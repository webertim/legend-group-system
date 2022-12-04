package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.model.ExpiringPlayer;
import com.github.webertim.legendgroupsystem.model.database.Group;
import com.github.webertim.legendgroupsystem.model.database.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class PlayerManagerTest extends BaseTest<UUID, PlayerInfo> {

    static MockedStatic<Bukkit> bukkitMockedStatic;
    PlayerManager playerManager;
    boolean taskRan = false;

    PlayerManagerTest() {
        super(PlayerInfo.class);
    }

    @BeforeAll
    static void setupStatic() {
        bukkitMockedStatic = mockStatic(Bukkit.class);
    }

    @BeforeEach
    @Order(0)
    @Override
    void setup() throws SQLException {
        super.setup();

        mockDb.replaceAll(group -> new PlayerInfo(UUID.randomUUID()));

        GroupManager groupManager = mock(GroupManager.class);
        when(groupManager.getDefaultGroup()).thenReturn(Group.DEFAULT);
        when(groupManager.get(anyString())).then(
                invocation -> {
                    // Pretending every group exists
                    String requestedGroupId = invocation.getArgument(0);

                    return new Group(requestedGroupId, requestedGroupId, "[" + requestedGroupId + "]", false);
                }
        );

        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        when(scheduler.runTaskTimer(any(Plugin.class), any(Runnable.class), anyLong(), anyLong())).then(
                invocation -> {
                    Runnable runnable = invocation.getArgument(1);

                    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
                    taskRan = true;
                    return null;
                }
        );
        when(Bukkit.getScheduler()).then(invocation -> scheduler);

        playerManager = new PlayerManager(legendGroupSystem, dao, groupManager);
    }

    @BeforeEach
    @Order(1)
    @Override
    void testInitialization() {
        super.testInitialization();
    }

    @Test
    void initialize() {
        Assertions.assertTrue(taskRan);
    }

    @Test
    void insert() throws InterruptedException {
        PlayerInfo newInfo = new PlayerInfo(UUID.randomUUID(), Group.DEFAULT, 10L);
        PlayerInfo permanentInfo = new PlayerInfo(UUID.randomUUID(), Group.DEFAULT, null);

        playerManager.insert(newInfo);
        playerManager.insert(permanentInfo);
        Assertions.assertNotNull(playerManager.get(newInfo.getId()));
        Assertions.assertNotNull(playerManager.get(permanentInfo.getId()));

        Assertions.assertTrue(playerManager.expiringPlayers.contains(ExpiringPlayer.fromPlayerInfo(newInfo)));
        Assertions.assertFalse(playerManager.expiringPlayers.contains(ExpiringPlayer.fromPlayerInfo(permanentInfo)));

        Thread.sleep(2000);

        Assertions.assertNull(playerManager.get(newInfo.getId()));
        Assertions.assertNotNull(playerManager.get(permanentInfo.getId()));

        Assertions.assertFalse(playerManager.expiringPlayers.contains(ExpiringPlayer.fromPlayerInfo(newInfo)));
    }

    @Test
    void edit() {
        PlayerInfo newInfo = new PlayerInfo(UUID.randomUUID(), Group.DEFAULT, 10_000L);

        playerManager.insert(newInfo);

        PlayerInfo updatedInfo = new PlayerInfo(newInfo.getId(), Group.DEFAULT, null);
        playerManager.edit(newInfo.getId(), updatedInfo);

        Assertions.assertNotNull(playerManager.get(newInfo.getId()));

        Assertions.assertFalse(playerManager.expiringPlayers.contains(ExpiringPlayer.fromPlayerInfo(newInfo)));
    }

    @Test
    void remove() {
        PlayerInfo newInfo = new PlayerInfo(UUID.randomUUID(), Group.DEFAULT, 10_000L);

        playerManager.insert(newInfo);
        playerManager.remove(newInfo);

        Assertions.assertNull(playerManager.get(newInfo.getId()));
        Assertions.assertFalse(playerManager.expiringPlayers.contains(ExpiringPlayer.fromPlayerInfo(newInfo)));
    }

    @Test
    void removePerformant() {
        PlayerInfo newInfo = new PlayerInfo(UUID.randomUUID(), Group.DEFAULT, 10_000L);

        playerManager.insert(newInfo);
        playerManager.removePerformant(newInfo);

        Assertions.assertNull(playerManager.get(newInfo.getId()));
        Assertions.assertTrue(playerManager.expiringPlayers.contains(ExpiringPlayer.fromPlayerInfo(newInfo)));
    }

    @Test
    void deletePerformant() {
        PlayerInfo newInfo = new PlayerInfo(UUID.randomUUID(), Group.DEFAULT, 10_000L);

        playerManager.create(
                newInfo,
                Assertions::assertTrue
        );
        playerManager.deletePerformant(newInfo);

        Assertions.assertNotNull(playerManager.get(newInfo.getId()));
        Assertions.assertTrue(playerManager.expiringPlayers.contains(ExpiringPlayer.fromPlayerInfo(newInfo)));
    }

    @Test
    void getGroupInfo() {
        PlayerInfo noGroupPlayer = new PlayerInfo(UUID.randomUUID(), null, null);

        playerManager.insert(noGroupPlayer);

        Assertions.assertEquals(Group.DEFAULT, playerManager.getGroupInfo(noGroupPlayer.getId()));

        String groupId = "test";
        Group newGroup =  new Group(groupId, null, null);
        PlayerInfo newGroupPlayer = new PlayerInfo(noGroupPlayer.getId(), newGroup, null);
        playerManager.edit(noGroupPlayer.getId(), newGroupPlayer);

        Assertions.assertEquals(groupId, playerManager.getGroupInfo(noGroupPlayer.getId()).getId());
    }

    @Test
    void buildPlayerName() {
        UUID playerUuid = UUID.randomUUID();
        String playerName = "TestPlayer";
        Player mockPlayer = mock(Player.class);
        when(mockPlayer.getUniqueId()).thenReturn(playerUuid);
        when(mockPlayer.getName()).thenReturn(playerName);

        PlayerInfo noGroupPlayer = new PlayerInfo(mockPlayer.getUniqueId(), null, null);

        playerManager.insert(noGroupPlayer);

        Assertions.assertEquals(
                Group.DEFAULT.getPrefix() + " " + mockPlayer.getName(),
                playerManager.buildPlayerName(mockPlayer)
        );

        String groupId = "test";
        Group newGroup =  new Group(groupId, null, null);
        PlayerInfo newGroupPlayer = new PlayerInfo(noGroupPlayer.getId(), newGroup, null);
        playerManager.edit(noGroupPlayer.getId(), newGroupPlayer);

        // This check is only valid because the GroupManager is mocked such that the prefix is always equal to
        // the group id surrounded by [ ]
        Assertions.assertEquals(
                "[" + groupId + "]" + " " + mockPlayer.getName(),
                playerManager.buildPlayerName(mockPlayer)
        );
    }

    @AfterAll
    static void after() {
        bukkitMockedStatic.close();
    }
}
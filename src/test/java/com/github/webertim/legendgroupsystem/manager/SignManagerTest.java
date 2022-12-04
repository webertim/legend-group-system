package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.model.database.PlayerGroupSign;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SignManagerTest extends BaseTest<Location, PlayerGroupSign> {
    static MockedStatic<Bukkit> bukkitMockedStatic;
    SignManager signManager;
    private List<Player> mockPlayers;
    private HashMap<UUID, String> playerNames;
    public SignManagerTest() {
        super(PlayerGroupSign.class);
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

        playerNames = new HashMap<>();

        mockPlayers = Arrays.asList(
                createMockPlayer(UUID.randomUUID(), "Tim"),
                createMockPlayer(UUID.randomUUID(), "Tom"),
                createMockPlayer(UUID.randomUUID(), "Till"),
                createMockPlayer(UUID.randomUUID(), "Thomas")
        );

        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        when(scheduler.runTaskAsynchronously(any(Plugin.class), any(Runnable.class))).then(
                invocation -> {
                    Runnable runnable = invocation.getArgument(1);

                    runnable.run();
                    return null;
                }
        );
        when(Bukkit.getScheduler()).then(invocation -> scheduler);

        when(Bukkit.getOnlinePlayers()).then(invocation -> mockPlayers);

        PlayerManager playerManager = mock(PlayerManager.class);
        signManager = new SignManager(legendGroupSystem, dao, playerManager);
    }

    @BeforeEach
    @Order(1)
    @Override
    void testInitialization() {
        super.testInitialization();
    }

    @Test
    void updateAllPlayersSingleSign() {
        int updateId = 100;
        Location updateLocation = new Location(null, updateId, 0, 0);
        this.signManager.updateAllPlayersSingleSign(new PlayerGroupSign(updateLocation));

        for (Player player : mockPlayers) {
            Assertions.assertTrue(player.getName().contains(updateId + ""));
        }
    }

    @Test
    void updatePlayerAllSigns() {
        for (int updateId = 0; updateId < 4; updateId++) {
            Location updateLocation = new Location(null, -1, 0, 0);
            PlayerGroupSign updateSign = new PlayerGroupSign(updateLocation);

            this.signManager.insert(updateSign);

            updateLocation.setX(updateId);
        }

        for (Player player : mockPlayers) {
            Assertions.assertTrue(player.getName().contains(-1 + ""));
        }

        Player updatePlayer = mockPlayers.get(0);

        this.signManager.updatePlayerAllSigns(updatePlayer);

        for (int updateId = 0; updateId < 4; updateId++) {
            Assertions.assertTrue(updatePlayer.getName().contains(updateId + ""));
        }
    }

    @Test
    void insert() {
        int updateId = 100;
        Location updateLocation = new Location(null, updateId, 0, 0);
        PlayerGroupSign updateSign = new PlayerGroupSign(updateLocation);

        this.signManager.insert(updateSign);

        for (Player player : mockPlayers) {
            Assertions.assertTrue(player.getName().contains(updateId + ""));
        }
    }

    @Test
    void edit() {
        int updateId = 100;
        Location updateLocation = new Location(null, updateId, 0, 0);
        PlayerGroupSign updateSign = new PlayerGroupSign(updateLocation);

        this.signManager.edit(updateSign.getId(), updateSign);

        for (Player player : mockPlayers) {
            Assertions.assertTrue(player.getName().contains(updateId + ""));
        }
    }

    @Test
    void remove() {
        int updateId = 100;
        Location updateLocation = new Location(null, updateId, 0, 0);
        PlayerGroupSign updateSign = new PlayerGroupSign(updateLocation);

        this.signManager.remove(updateSign);

        for (Player player : mockPlayers) {
            Assertions.assertTrue(player.getName().contains(updateId + ""));
        }
    }

    @AfterAll
    static void after() {
        bukkitMockedStatic.close();
    }

    private Player createMockPlayer(UUID uuid, String name) {
        Player mockPlayer = mock(Player.class);

        playerNames.put(uuid, name);

        when(mockPlayer.getUniqueId()).thenReturn(uuid);
        when(mockPlayer.getName()).then(
                invocation -> playerNames.get(uuid)
        );

        doAnswer(
                invocation -> {
                    Component newName = invocation.getArgument(0);
                    playerNames.put(uuid, PlainTextComponentSerializer.plainText().serialize(newName));
                    return null;
                }
        ).when(mockPlayer).customName(any(Component.class));

        doAnswer(
                invocation -> {
                    Location updateLocation = invocation.getArgument(0);
                    mockPlayer.customName(Component.text(updateLocation.getBlockX() + mockPlayer.getName()));
                    return null;
                }
        ).when(mockPlayer).sendSignChange(any(Location.class), anyList());


        return mockPlayer;
    }
}
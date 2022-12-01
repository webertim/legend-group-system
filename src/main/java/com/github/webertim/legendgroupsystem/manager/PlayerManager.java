package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.manager.tasks.CheckExpiredTask;
import com.github.webertim.legendgroupsystem.model.ExpiringPlayer;
import com.github.webertim.legendgroupsystem.model.Group;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import com.j256.ormlite.dao.Dao;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.PriorityQueue;
import java.util.UUID;

public class PlayerManager extends BaseManager<UUID, PlayerInfo> {

    private PriorityQueue<ExpiringPlayer> expiringPlayers;
    private CheckExpiredTask checkExpiredTask;
    private final GroupManager groupManager;

    public PlayerManager(LegendGroupSystem legendGroupSystem, Dao<PlayerInfo, UUID> dao, GroupManager groupManager) throws SQLException {
        super(legendGroupSystem, dao);
        this.groupManager = groupManager;
    }

    @Override
    public void initialize() throws SQLException {
        this.expiringPlayers = new PriorityQueue<>();

        this.checkExpiredTask = new CheckExpiredTask(expiringPlayers, this);
        this.checkExpiredTask.runTaskTimer(this.legendGroupSystem, 20, 20);

        super.initialize();
    }

    @Override
    void insert(PlayerInfo data) {
        super.insert(data);

        ExpiringPlayer expiringPlayer = new ExpiringPlayer(data.getId(), data.getExpirationTimeMillis());
        this.expiringPlayers.remove(expiringPlayer);

        if (data.getExpirationTimeMillis() != null) {
            this.expiringPlayers.add(expiringPlayer);
        }
    }

    @Override
    public void remove(PlayerInfo data) {
        super.remove(data);

        this.expiringPlayers.remove(data);
    }

    public void removePerformant(PlayerInfo data) {
        super.remove(data);
    }

    public void deletePerformant(PlayerInfo data) {
        this.createSuccessBasedTaskChain(
                () -> this.getDao().delete(data),
                () -> {},
                success -> {}
        );
    }

    public @NotNull Group getGroupInfo(UUID playerUuid) {

        PlayerInfo targetPlayer = this.get(playerUuid);

        if (targetPlayer == null) {
            return this.groupManager.getDefaultGroup();
        }

        Group targetGroup = this.groupManager.get(targetPlayer.getGroup().getId());

        if (targetGroup == null) {
            return this.groupManager.getDefaultGroup();
        }

        return targetGroup;
    }
}

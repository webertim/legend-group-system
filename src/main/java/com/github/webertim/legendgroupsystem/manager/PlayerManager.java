package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.manager.tasks.CheckExpiredTask;
import com.github.webertim.legendgroupsystem.model.ExpiringPlayer;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.PriorityQueue;
import java.util.UUID;

public class PlayerManager extends BaseManager<UUID, PlayerInfo> {

    private PriorityQueue<ExpiringPlayer> expiringPlayers;
    private CheckExpiredTask checkExpiredTask;
    public PlayerManager(LegendGroupSystem legendGroupSystem, Dao<PlayerInfo, UUID> dao) throws SQLException {
        super(legendGroupSystem, dao);
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
}

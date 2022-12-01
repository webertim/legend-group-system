package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.manager.tasks.CheckExpiredTask;
import com.github.webertim.legendgroupsystem.model.ExpiringPlayer;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import com.j256.ormlite.dao.Dao;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.UUID;

public class PlayerManager extends BaseManager<UUID, PlayerInfo> {

    public PlayerManager(LegendGroupSystem legendGroupSystem, Dao<PlayerInfo, UUID> dao) throws SQLException {
        super(legendGroupSystem, dao);
    }
}

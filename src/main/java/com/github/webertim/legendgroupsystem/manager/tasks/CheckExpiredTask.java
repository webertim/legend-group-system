package com.github.webertim.legendgroupsystem.manager.tasks;

import com.github.webertim.legendgroupsystem.model.ExpiringPlayer;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.PriorityQueue;

public class CheckExpiredTask extends BukkitRunnable {
    private final PriorityQueue<ExpiringPlayer> expiringPlayers;

    public CheckExpiredTask(PriorityQueue expiringPlayers) {
        this.expiringPlayers = expiringPlayers;
    }

    @Override
    public void run() {
        ExpiringPlayer nextExpiring = expiringPlayers.peek();
        while (nextExpiring != null && nextExpiring.getExpirationTimeMillis() < System.currentTimeMillis()) {
            expiringPlayers.poll();
            // TODO: Update player group
            nextExpiring = expiringPlayers.peek();
        }
    }
}

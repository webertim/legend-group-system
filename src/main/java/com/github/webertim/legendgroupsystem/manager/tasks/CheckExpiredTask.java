package com.github.webertim.legendgroupsystem.manager.tasks;

import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.model.ExpiringPlayer;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.PriorityQueue;

public class CheckExpiredTask extends BukkitRunnable {
    private final PriorityQueue<ExpiringPlayer> expiringPlayers;
    private final PlayerManager playerManager;

    public CheckExpiredTask(PriorityQueue<ExpiringPlayer> expiringPlayers, PlayerManager playerManager) {
        this.expiringPlayers = expiringPlayers;
        this.playerManager = playerManager;
    }

    @Override
    public void run() {
        ExpiringPlayer nextExpiring = expiringPlayers.peek();

        while (nextExpiring != null && nextExpiring.expirationTimeMillis() < System.currentTimeMillis()) {
            expiringPlayers.poll();
            PlayerInfo targetPlayer = new PlayerInfo(nextExpiring.uuid());

            /* This background task does not use the delete method of the BaseManager class but rather two more performant
             * methods. The first reason is the order of the operations. In this case it is more important, that the game
             * state is updated to remove the privileges from the player independent of the database operation.
             * Secondly the default remove() implementation of the PlayerManager is designed to mirror the insert() method.
             * Therefore, remove() also removes the player from the expiringPlayers - which this task already does - but
             * in O(n) because it must seek the playerInfo.
             *
             * Also, the default delete call calls the remove() method and is therefore also too expensive for this taks.
             */
            playerManager.removePerformant(targetPlayer);
            playerManager.deletePerformant(targetPlayer);

            nextExpiring = expiringPlayers.peek();
        }
    }
}

package com.github.webertim.legendgroupsystem.manager.tasks;

import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.model.ExpiringPlayer;
import com.github.webertim.legendgroupsystem.model.database.PlayerInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.PriorityQueue;

/**
 * A BukkitRunnable used to permanently check if player rights expired. Because this task is performed every second,
 * a fast implementation with an additional data structure (PriorityQueue) is used.
 *
 * As always performance is a tradeoff. Because the items in the PriorityQueue are sorted, it takes longer to enqueue
 * and dequeue them O(log(n)) and even longer to remove a 'random' element O(n).
 *
 * But since - at least in this job - most of the times only one peek() operation is necessary which only take O(1) time
 * this is the fastest implementation (i could come up with)
 *
 * Because performance matters we cannot use the default remove() and delete() operations of the PlayerManager
 * because they always mirror the insert() and create() operations and therefore access the PriorityQueue randomly which
 * takes O(n) time.
 *
 * Some performance numbers:
 * At 100.000 elements, adding a new item takes around 8-12ms (because a general remove is always executed in case an entry for the player already exists)
 * At 100.000 elements, removing all elements with the algorithm used in this task, takes 20-25ms
 * At 100.000 elements, removing 1.000 elements with the algorithm used in this task, takes 0ms
 */
public class CheckExpiredTask extends BukkitRunnable {
    private final PriorityQueue<ExpiringPlayer> expiringPlayers;
    private final PlayerManager playerManager;

    public CheckExpiredTask(PriorityQueue<ExpiringPlayer> expiringPlayers, PlayerManager playerManager) {
        this.expiringPlayers = expiringPlayers;
        this.playerManager = playerManager;
    }

    /**
     * The actual job implementation. Checks and removes expired player information.
     */
    @Override
    public void run() {
        ExpiringPlayer nextExpiring = expiringPlayers.peek();

        while (nextExpiring != null && nextExpiring.expirationTimeMillis() < System.currentTimeMillis()) {
            expiringPlayers.poll();
            PlayerInfo targetPlayer = new PlayerInfo(nextExpiring.uuid());

            playerManager.removePerformant(targetPlayer);
            playerManager.deletePerformant(targetPlayer);

            nextExpiring = expiringPlayers.peek();
        }
    }
}

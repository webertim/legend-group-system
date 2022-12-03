package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.manager.tasks.CheckExpiredTask;
import com.github.webertim.legendgroupsystem.model.ExpiringPlayer;
import com.github.webertim.legendgroupsystem.model.database.Group;
import com.github.webertim.legendgroupsystem.model.Identifiable;
import com.github.webertim.legendgroupsystem.model.database.PlayerInfo;
import com.j256.ormlite.dao.Dao;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.PriorityQueue;
import java.util.UUID;

/**
 * Manager class for managing players.
 */
public class PlayerManager extends BaseManager<UUID, PlayerInfo> {

    PriorityQueue<ExpiringPlayer> expiringPlayers;
    private final GroupManager groupManager;

    public PlayerManager(LegendGroupSystem legendGroupSystem, Dao<PlayerInfo, UUID> dao, GroupManager groupManager) throws SQLException {
        super(legendGroupSystem, dao);
        this.groupManager = groupManager;
    }

    /**
     * Overrides the base implementation of the initialize method to also create the PriorityQueue holding information
     * about expiring group rights and also start the task to check this queue every second.
     *
     * @throws SQLException If the database cannot be queried.
     */
    @Override
    public void initialize() throws SQLException {
        this.expiringPlayers = new PriorityQueue<>();

        CheckExpiredTask checkExpiredTask = new CheckExpiredTask(expiringPlayers, this);
        checkExpiredTask.runTaskTimer(this.legendGroupSystem, 20, 20);

        super.initialize();
    }

    /**
     * Insert a new resource into the internal hash map.
     * Since this manager also contains a second internal data structure, it is adjusted also.
     * This operation is relatively expensive because old entries in the priority queue for the same player are removed.
     * This operation notifies all change listeners.
     *
     * @param data The player info to insert.
     */
    @Override
    public void insert(PlayerInfo data) {
        super.insert(data);

        removeAndAddExpiringPlayer(data);
    }

    /**
     * Update an existing resource inside the internal hash map. Note that the only difference between this method and
     * {@link com.github.webertim.legendgroupsystem.manager.BaseManager#insert(Identifiable i)} is the way the change
     * listener is informed.
     * Since this manager also contains a second internal data structure, it is adjusted also.
     * This operation is relatively expensive because old entries in the priority queue for the same player are removed.
     * This operation notifies all change listeners.
     *
     * @param id The Id of the player info to update.
     * @param data The player info to update.
     */
    @Override
    public void edit(UUID id, PlayerInfo data) {
        super.edit(id, data);

        removeAndAddExpiringPlayer(data);
    }

    /**
     * Remove a resource from the internal hash map.
     * Since the remove() operation must mirror the insert() operation. Entries in the PriorityQueue are removed also.
     * Therefore this operation is relatively expensive.
     * This operation notifies all change listeners.
     *
     * @param data The player info to remove.
     */
    @Override
    public void remove(PlayerInfo data) {
        super.remove(data);

        ExpiringPlayer expiringPlayer = ExpiringPlayer.fromPlayerInfo(data);
        this.expiringPlayers.remove(expiringPlayer);
    }

    /**
     * This method is the equivalent of the BaseManager implementation of remove and only removes entries of the internal map.
     * Therefore, this should only be called if removing the corresponding entry in the PriorityQueue is handled elsewhere.
     *
     * @param data The player info to remove.
     */
    public void removePerformant(PlayerInfo data) {
        super.remove(data);
    }

    /**
     * This method only executes a database delete without modifying the internal data structure.
     * This should only be called if die removal of the value in the internal data structure is handled elsewhere.
     *
     * @param data The player info to delete from the database.
     */
    public void deletePerformant(PlayerInfo data) {
        this.createSuccessBasedTaskChain(
                () -> this.getDao().delete(data),
                () -> {},
                success -> {}
        );
    }

    /**
     * Retrieves the group info of the player with the provided Id. This method must be called because the Group object
     * inside the PlayerInfo object only contains the Id of the group.
     *
     * @param playerUuid The UUID of the player
     * @return The current group of this player.
     */
    public @NotNull Group getGroupInfo(UUID playerUuid) {

        PlayerInfo targetPlayer = get(playerUuid);

        if (targetPlayer == null) {
            return this.groupManager.getDefaultGroup();
        }

        Group playerGroupInfo = targetPlayer.getGroup();

        if (playerGroupInfo == null) {
            return this.groupManager.getDefaultGroup();
        }

        Group targetGroup = this.groupManager.get(playerGroupInfo.getId());

        if (targetGroup == null) {
            return this.groupManager.getDefaultGroup();
        }

        return targetGroup;
    }

    /**
     * Builds the prefixed player name based on its current group information.
     *
     * @param player A player object.
     * @return Prefixed player name.
     */
    public String buildPlayerName(Player player) {
        Group playerGroup = getGroupInfo(player.getUniqueId());

        return playerGroup.getPrefix() + " " + player.getName();
    }

    private void removeAndAddExpiringPlayer(PlayerInfo data) {
        ExpiringPlayer expiringPlayer = ExpiringPlayer.fromPlayerInfo(data);
        this.expiringPlayers.remove(expiringPlayer);

        if (data.getExpirationTimeMillis() != null) {
            this.expiringPlayers.add(expiringPlayer);
        }
    }
}

package com.github.webertim.legendgroupsystem.manager;

import co.aikar.taskchain.TaskChainTasks;
import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.database.DatabaseConnector;
import com.github.webertim.legendgroupsystem.manager.tasks.CheckExpiredTask;
import com.github.webertim.legendgroupsystem.model.ExpiringPlayer;
import com.github.webertim.legendgroupsystem.model.Group;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;

public class GroupManager {
    private final LegendGroupSystem legendGroupSystem;
    private final DatabaseConnector databaseConnector;
    private final HashMap<String, Group> groups = new HashMap<>();
    private final HashMap<UUID, PlayerInfo> players = new HashMap<>();
    private final PriorityQueue<ExpiringPlayer> expiringPlayers = new PriorityQueue<>();

    private final BukkitRunnable checkExpiredTask = new CheckExpiredTask(expiringPlayers);

    public GroupManager(DatabaseConnector databaseConnector, LegendGroupSystem legendGroupSystem) throws SQLException {
        this.databaseConnector = databaseConnector;
        this.legendGroupSystem = legendGroupSystem;

        this.initialize();
    }

    /**
     * Initializes the group manager, i.e. fetching all required data from the database
     * and starting the task to check expired groups of players.
     *
     * @throws SQLException If the database is not reachable this exception is thrown.
     */
    private void initialize() throws SQLException {
        this.databaseConnector.readGroups().forEach(this::insertGroup);
        this.databaseConnector.readPlayerInfos().forEach(this::insertPlayerInfo);

        this.checkExpiredTask.runTaskTimer(this.legendGroupSystem, 20, 20);
    }

    /**
     * Creates a new group based on the provided group object. The ID must be unique.
     * Otherwise, the operation is skipped silently. If you want to act based
     *
     * @param group A group object with am unique ID to create.
     * @param lastTask The task to execute once the insert finishes. This task is executed on the Main Thread.
     *                 The provided value of this task is a boolean telling if the operation was successful.
     */
    public void createGroup(Group group, TaskChainTasks.LastTask<Boolean> lastTask) {
        this.legendGroupSystem.createTaskChain()
                .asyncFirst(() -> this.databaseConnector.tryCreateGroup(group))
                .sync(success -> this.insertGroupIfSuccess(success, group))
                .syncLast(lastTask)
                .execute();
    }

    /**
     * Updates an existing group with the new values of the object. The group ID of the provided object must exist.
     * Otherwise, this operation is skipped silently.
     *
     * @param group A group object with the ID of the group to update.
     * @param lastTask The task to execute once the update finishes. This task is executed on the Main Thread.
     *                 The provided value of this task is a boolean telling if the operation was successful.
     */
    public void updateGroup(Group group, TaskChainTasks.LastTask<Boolean> lastTask) {
        this.legendGroupSystem.createTaskChain()
                .asyncFirst(() -> this.databaseConnector.tryUpdateGroup(group))
                .sync(success -> this.insertGroupIfSuccess(success, group))
                .syncLast(lastTask)
                .execute();
    }

    /**
     * Updates an existing group only based on set values of the new group.
     * If group prefix or name is null the values of the existing groups are used instead.
     * The group ID of the provided object must exist.
     * Otherwise, this operation is skipped silently.
     *
     * @param group A group object with the ID of the group to update.
     *              Prefix and name will only be updated if they are not null.
     * @param lastTask The task to execute once the update finishes. This task is executed on the Main Thread.
     *                 The provided value of this task is a boolean telling if the operation was successful.
     */
    public void updateGroupDiff(Group group, TaskChainTasks.LastTask<Boolean> lastTask) {
        Group targetGroup = this.groups.get(group.getId());

        if (group.getPrefix() == null) {
            group.setPrefix(targetGroup.getPrefix());
        }

        if (group.getName() == null) {
            group.setName(targetGroup.getName());
        }

        updateGroup(group, lastTask);
    }

    public void deleteGroup(Group group, TaskChainTasks.LastTask<Boolean> lastTask) {
        this.legendGroupSystem.createTaskChain()
                .asyncFirst(() -> this.databaseConnector.tryDeleteGroup(group))
                .sync(success -> this.removeGroupIfSuccess(success, group))
                .syncLast(lastTask)
                .execute();
    }

    /**
     * Gets the IDs of all currently managed groups.
     *
     * @return A set of group IDs currently existing.
     */
    public Set<String> getGroupIds() {
        return groups.keySet();
    }

    /**
     * Simple helper function used in a task chain,
     * which calls the {@link com.github.webertim.legendgroupsystem.manager.GroupManager#insertGroup(Group group)} method
     * based on a provided boolean value. Also, the provided boolean value is returned for following tasks in the chain.
     *
     * @param success Boolean deciding whether the group should actually be inserted.
     * @param group The group to potentially insert.
     * @return Boolean with the value of the success parameter.
     */
    private boolean insertGroupIfSuccess(boolean success, Group group) {
        if (success) {
            insertGroup(group);
        }

        return success;
    }

    /**
     * Simple helper function used in a task chain,
     * which calls the {@link com.github.webertim.legendgroupsystem.manager.GroupManager#removeGroup(Group group)} method
     * based on a provided boolean value. Also, the provided boolean value is returned for following tasks in the chain.
     *
     * @param success Boolean deciding whether the group should actually be removed.
     * @param group The group to potentially remove.
     * @return Boolean with the value of the success parameter.
     */
    private boolean removeGroupIfSuccess(Boolean success, Group group) {
        if (success) {
            removeGroup(group);
        }

        return success;
    }

    /**
     * This method is responsible for inserting new Group objects into the hashmap
     * Never use the put method of the group map directly.
     * A good example is {@link com.github.webertim.legendgroupsystem.manager.GroupManager#insertPlayerInfo(PlayerInfo playerInfo)}
     * where PlayerInfo objects with expiration time are also stored separately
     * (for performance reasons)
     *
     * @param group The group to insert in the HashMap
     */
    private void insertGroup(Group group) {
        this.groups.put(group.getId(), group);
    }

    private void removeGroup(Group group) {
        this.groups.remove(group.getId());
    }

    /**
     * This method is responsible for inserting new PlayerInfo objects into the hashmap
     * Never use the put method of the playerInfo map directly
     * because players with expiring group rights are also stored in another collection for performance reasons.
     *
     * Duration at 100.000 players is ~5ms
     *
     * @param playerInfo The player info to insert in the HashMap (and the PriorityQueue)
     */
    private void insertPlayerInfo(PlayerInfo playerInfo) {
        this.players.put(playerInfo.getUuid(), playerInfo);

        if (playerInfo.getExpirationTimeMillis() != null) {
            ExpiringPlayer expiringPlayer =
                    new ExpiringPlayer(playerInfo.getUuid(), playerInfo.getExpirationTimeMillis());

            // Since the equals method only compares the UUID of the object this check makes sure no duplicates exist.
            this.expiringPlayers.remove(expiringPlayer);

            this.expiringPlayers.add(expiringPlayer);
        }
    }
}

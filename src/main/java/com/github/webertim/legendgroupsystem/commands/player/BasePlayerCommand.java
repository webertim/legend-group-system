package com.github.webertim.legendgroupsystem.commands.player;

import com.github.webertim.legendgroupsystem.commands.BaseCommand;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.manager.PlayerManager;

/**
 * An abstract class used as a basis by the player commands. Provides a PlayerManager and GroupManager.
 */
public abstract class BasePlayerCommand extends BaseCommand {

    final PlayerManager playerManager;
    final GroupManager groupManager;

    /**
     * Constructor takes a PlayerManager and GroupManager to provide it to every child command.
     *
     * @param playerManager A PlayerManager instance.
     * @param groupManager A GroupManager instance.
     * @param config The Plugin config.
     */
    public BasePlayerCommand(PlayerManager playerManager, GroupManager groupManager, BaseConfiguration config) {
        super(config);
        this.playerManager = playerManager;
        this.groupManager = groupManager;
    }
}

package com.github.webertim.legendgroupsystem.commands.group;

import com.github.webertim.legendgroupsystem.commands.BaseCommand;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;

/**
 * An abstract class used as a basis by the group commands. Provides a GroupManager.
 */
public abstract class BaseGroupCommand extends BaseCommand {

    final GroupManager groupManager;

    /**
     * Constructor takes a GroupManager to provide it to every child command.
     *
     * @param groupManager A GroupManager instance.
     * @param config The Plugin config.
     */
    public BaseGroupCommand(GroupManager groupManager, BaseConfiguration config) {
        super(config);
        this.groupManager = groupManager;
    }
}

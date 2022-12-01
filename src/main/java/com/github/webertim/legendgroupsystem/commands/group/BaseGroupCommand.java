package com.github.webertim.legendgroupsystem.commands.group;

import com.github.webertim.legendgroupsystem.commands.CommandExecutorTabCompleter;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;

public abstract class BaseGroupCommand extends CommandExecutorTabCompleter {

    final GroupManager groupManager;
    final BaseConfiguration config;

    public BaseGroupCommand(GroupManager groupManager, BaseConfiguration config) {
        this.groupManager = groupManager;
        this.config = config;
    }
}

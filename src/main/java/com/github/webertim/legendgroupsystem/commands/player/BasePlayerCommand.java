package com.github.webertim.legendgroupsystem.commands.player;

import com.github.webertim.legendgroupsystem.commands.BaseCommand;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.manager.PlayerManager;

public abstract class BasePlayerCommand extends BaseCommand {

    final PlayerManager playerManager;
    final GroupManager groupManager;
    public BasePlayerCommand(PlayerManager playerManager, GroupManager groupManager, BaseConfiguration config) {
        super(config);
        this.playerManager = playerManager;
        this.groupManager = groupManager;
    }
}

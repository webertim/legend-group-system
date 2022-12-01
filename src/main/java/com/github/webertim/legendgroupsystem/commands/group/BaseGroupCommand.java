package com.github.webertim.legendgroupsystem.commands.group;

import com.github.webertim.legendgroupsystem.commands.BaseCommand;
import com.github.webertim.legendgroupsystem.commands.CommandExecutorTabCompleter;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

public abstract class BaseGroupCommand extends BaseCommand {

    final GroupManager groupManager;

    public BaseGroupCommand(GroupManager groupManager, BaseConfiguration config) {
        super(config);
        this.groupManager = groupManager;
    }
}

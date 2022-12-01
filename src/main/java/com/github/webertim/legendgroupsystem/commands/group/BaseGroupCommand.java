package com.github.webertim.legendgroupsystem.commands.group;

import com.github.webertim.legendgroupsystem.commands.CommandExecutorTabCompleter;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

public abstract class BaseGroupCommand extends CommandExecutorTabCompleter {

    final GroupManager groupManager;
    final BaseConfiguration config;

    public BaseGroupCommand(GroupManager groupManager, BaseConfiguration config) {
        this.groupManager = groupManager;
        this.config = config;
    }

    Consumer<Boolean> getSuccessCallback(String successKey, String errorKey, CommandSender sender) {
        return success -> {
            String message = success ?
                    this.config.getMessage(successKey) :
                    this.config.getMessage(errorKey);

            if (message == null) {
                message = "";
            }

            sender.sendMessage(message);
        };
    }
}

package com.github.webertim.legendgroupsystem.commands;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

public abstract class BaseCommand extends CommandExecutorTabCompleter {

    protected final BaseConfiguration config;

    public BaseCommand(BaseConfiguration config) {
        this.config = config;
    }
    protected Consumer<Boolean> getSuccessCallback(String successKey, String errorKey, CommandSender sender) {
        return success -> {
            String message = success ?
                    this.config.getMessage(successKey) :
                    this.config.getMessage(errorKey);

            sender.sendMessage(message);
        };
    }
}

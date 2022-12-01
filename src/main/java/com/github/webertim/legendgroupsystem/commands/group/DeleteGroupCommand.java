package com.github.webertim.legendgroupsystem.commands.group;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.model.Group;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeleteGroupCommand extends BaseGroupCommand {

    public DeleteGroupCommand(GroupManager groupManager, BaseConfiguration config) {
        super(groupManager, config);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }

        this.groupManager.deleteGroup(
                new Group(args[0]),
                success -> {
                    String message = success ?
                            this.config.getMessage("successDeleteGroup") :
                            this.config.getMessage("errorDeleteGroup");

                    sender.sendMessage(message);
                }
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return this.groupManager.getGroupIds().stream().toList();
        }
        return null;
    }
}

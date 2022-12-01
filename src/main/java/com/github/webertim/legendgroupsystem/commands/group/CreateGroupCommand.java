package com.github.webertim.legendgroupsystem.commands.group;

import com.github.webertim.legendgroupsystem.commands.CommandExecutorTabCompleter;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.model.Group;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreateGroupCommand extends BaseGroupCommand {

    public CreateGroupCommand(GroupManager groupManager, BaseConfiguration config) {
        super(groupManager, config);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 3) {
            return false;
        }

        String groupName = Arrays
                            .stream(args)
                            .skip(2)
                            .collect(Collectors.joining(" "));

        Group newGroup = new Group(args[0], groupName, args[1]);

        this.groupManager.createGroup(
                newGroup,
                success -> {
                    String message = success ?
                            ChatColor.GREEN + this.config.getMessage("successCreateGroup") :
                            ChatColor.RED + this.config.getMessage("errorCreateGroup");

                    sender.sendMessage(message);
                }
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length <= 0) {
            return Arrays.asList("<groupId>");
        } else if (args.length <= 1) {
            return Arrays.asList("<groupPrefix>");
        } else {
            return Arrays.asList("<groupName>");
        }
    }
}

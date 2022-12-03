package com.github.webertim.legendgroupsystem.commands.group;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.model.database.Group;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command used to create a new group.
 */
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

        this.groupManager.create(
                newGroup,
                this.getSuccessCallback("successCreateGroup", "errorCreateGroup", sender)
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length <= 0) {
            return List.of("<groupId>");
        } else if (args.length <= 1) {
            return List.of("<groupPrefix>");
        } else {
            return List.of("<groupName>");
        }
    }
}

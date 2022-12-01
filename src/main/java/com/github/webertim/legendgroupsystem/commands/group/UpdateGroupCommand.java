package com.github.webertim.legendgroupsystem.commands.group;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.model.Group;
import com.github.webertim.legendgroupsystem.util.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateGroupCommand extends BaseGroupCommand {

    private static final String UPDATE_NAME_KEYWORD = "name";
    private static final String UPDATE_PREFIX_KEYWORD = "prefix";
    public UpdateGroupCommand(GroupManager groupManager, BaseConfiguration config) {
        super(groupManager, config);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 3) {
            return false;
        }

        String groupName = null;
        String prefix = null;

        switch (args[0]) {
            case UPDATE_PREFIX_KEYWORD:
                prefix = args[2];
                break;
            case UPDATE_NAME_KEYWORD:
                groupName = Arrays
                        .stream(args)
                        .skip(2)
                        .collect(Collectors.joining(" "));
                break;
            default:
                return false;
        }

        Group updatedGroup = new Group(args[1], groupName, prefix);

        this.groupManager.updateGroupDiff(
            updatedGroup,
            success -> {
                String message = success ?
                        ChatColor.GREEN + this.config.getMessage("successUpdateGroup") :
                        ChatColor.RED + this.config.getMessage("errorUpdateGroup");

                sender.sendMessage(message);
            }
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return Arrays.asList(UPDATE_NAME_KEYWORD, UPDATE_PREFIX_KEYWORD);
        } else if (args.length == 1) {
            return groupManager.getGroupIds().stream().toList();
        } else if (args.length == 2) {
            Arrays.asList("<value>");
        }
        return null;
    }
}

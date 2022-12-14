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
 * Command used to update group name or prefix.
 */
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

        this.groupManager.update(
            updatedGroup,
            this.getSuccessCallback("successUpdateGroup", "errorUpdateGroup", sender)
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return Arrays.asList(UPDATE_NAME_KEYWORD, UPDATE_PREFIX_KEYWORD);
        } else if (args.length == 1) {
            return groupManager.getIds().stream().toList();
        } else if (args.length == 2) {
            return List.of("<value>");
        }
        return null;
    }
}

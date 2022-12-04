package com.github.webertim.legendgroupsystem.commands.group;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.manager.GroupPermissionsManager;
import com.github.webertim.legendgroupsystem.model.database.Group;
import com.github.webertim.legendgroupsystem.model.database.GroupPermissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PermissionCommand extends BaseGroupCommand {

    private static final String ADD_KEYWORD = "add";
    private static final String REMOVE_KEYWORD = "remove";
    private final GroupPermissionsManager groupPermissionsManager;
    public PermissionCommand(GroupManager groupManager, BaseConfiguration config, GroupPermissionsManager groupPermissionsManager) {
        super(groupManager, config);
        this.groupPermissionsManager = groupPermissionsManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 3) {
            return false;
        }

        Group targetGroup = this.groupManager.get(args[1]);

        if (targetGroup == null) {
            String noSuchGroup = this.config.getMessage("noSuchGroup");
            sender.sendMessage(noSuchGroup);
            return true;
        }

        GroupPermissions groupPermissions = this.groupPermissionsManager.get(targetGroup);
        if (groupPermissions == null) {
            groupPermissions = new GroupPermissions(targetGroup);
        }

        switch (args[0]) {
            case ADD_KEYWORD -> groupPermissions.addPermission(args[2]);
            case REMOVE_KEYWORD -> groupPermissions.removePermission(args[2]);
        }

        this.groupPermissionsManager.update(
            groupPermissions,
            getSuccessCallback("successGroupPermissions", "errorGroupPermissions", sender)
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return Arrays.asList(
                    ADD_KEYWORD,
                    REMOVE_KEYWORD
            );
        } else if (args.length == 1) {
            return this.groupManager.getIds().stream().toList();
        } else if (args.length == 2) {
            return List.of("<permission>");
        }

        return null;
    }
}

package com.github.webertim.legendgroupsystem.commands.group;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.model.Group;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultGroupCommand extends BaseGroupCommand {
    public DefaultGroupCommand(GroupManager groupManager, BaseConfiguration config) {
        super(groupManager, config);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Group targetGroup = new Group("");

        if (args.length >= 1) {
            targetGroup = new Group(args[0]);
        }

        this.groupManager.updateDefaultGroup(
                targetGroup,
                this.getSuccessCallback("successDefaultGroup", "errorDefaultGroup", sender)
        );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length <= 0) {
            return this.groupManager.getIds().stream().toList();
        }
        return null;
    }
}

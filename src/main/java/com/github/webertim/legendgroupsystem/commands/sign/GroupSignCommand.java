package com.github.webertim.legendgroupsystem.commands.sign;

import com.github.webertim.legendgroupsystem.commands.group.BaseGroupCommand;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.manager.SignManager;
import com.github.webertim.legendgroupsystem.model.PlayerGroupSign;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class GroupSignCommand extends BaseSignCommand {
    public GroupSignCommand(SignManager signManager, BaseConfiguration config) {
        super(signManager, config);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }

        String singName = args[0];

        if (!(sender instanceof Player player)) {
            String errorSignMessage = this.config.getMessage("errorSignCreate");
            sender.sendMessage(errorSignMessage);
            return true;
        }

        Location playerLocation = player.getLocation();
        /*Block blockAtPlayerLocation = playerLocation.getWorld().getBlockAt(playerLocation);

        if (!(blockAtPlayerLocation.getBlockData() instanceof Sign)) {
            String errorSignMessage = this.config.getMessage("errorSignCreate");
            sender.sendMessage(errorSignMessage);
            return true;
        }*/

        this.signManager.create(
                new PlayerGroupSign(singName, playerLocation),
                getSuccessCallback("successSignCreate", "errorSignCreate", sender)
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}

package com.github.webertim.legendgroupsystem.commands.player;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.model.Group;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetPlayerGroupCommand extends BasePlayerCommand {
    public GetPlayerGroupCommand(PlayerManager playerManager, GroupManager groupManager, BaseConfiguration config) {
        super(playerManager, groupManager, config);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            String noPlayer = this.config.getMessage("noPlayer");
            sender.sendMessage(noPlayer);
            return true;
        }

        PlayerInfo playerInfo = this.playerManager.get(player.getUniqueId());
        Group playerGroup = this.playerManager.getGroupInfo(player.getUniqueId());


        player.sendMessage(
                ChatColor.AQUA + playerGroup.getName() + " " + ChatColor.GREEN + playerGroup.getPrefix() + "\n"
                    + ((playerInfo != null) ? ("Expires: " + playerInfo.getExpirationDateString()) : "")
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}

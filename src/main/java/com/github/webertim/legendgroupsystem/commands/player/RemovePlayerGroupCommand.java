package com.github.webertim.legendgroupsystem.commands.player;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Command used to remove a group from a player.
 */
public class RemovePlayerGroupCommand extends BasePlayerCommand {
    public RemovePlayerGroupCommand(PlayerManager playerManager, GroupManager groupManager, BaseConfiguration config) {
        super(playerManager, groupManager, config);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }

        UUID targetPlayerUuid = Bukkit.getPlayerUniqueId(args[0]);

        if (targetPlayerUuid == null) {
            String noSuchPlayer = this.config.getMessage("noSuchPlayer");
            sender.sendMessage(noSuchPlayer);
            return true;
        }

        PlayerInfo targetPlayer = new PlayerInfo(targetPlayerUuid);

        playerManager.delete(
                targetPlayer,
                getSuccessCallback("successRemovePlayerGroup", "errorRemovePlayerGroup", sender)
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .toList();
        }

        return null;
    }
}

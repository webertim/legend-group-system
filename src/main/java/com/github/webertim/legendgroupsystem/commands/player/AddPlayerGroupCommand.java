package com.github.webertim.legendgroupsystem.commands.player;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.model.Group;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Command used to add a player to a group (permanently or for a limited time)
 */
public class AddPlayerGroupCommand extends BasePlayerCommand {
    public AddPlayerGroupCommand(PlayerManager playerManager, GroupManager groupManager, BaseConfiguration config) {
        super(playerManager, groupManager, config);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            return false;
        }

        UUID targetPlayerUuid = Bukkit.getPlayerUniqueId(args[0]);

        if (targetPlayerUuid == null) {
            String noSuchPlayer = this.config.getMessage("noSuchPlayer");
            sender.sendMessage(noSuchPlayer);
            return true;
        }

        Group targetGroup = this.groupManager.get(args[1]);

        if (targetGroup == null) {
            String noSuchGroup = this.config.getMessage("noSuchGroup");
            sender.sendMessage(noSuchGroup);
            return true;
        }

        Long expirationTimeMillis = null;

        if (args.length > 2) {
            try {
                Duration duration = parseDurationString(args[2]);
                expirationTimeMillis = System.currentTimeMillis() + duration.toMillis();
            } catch (NumberFormatException e) {
                String invalidDuration = this.config.getMessage("invalidDuration");
                sender.sendMessage(invalidDuration);
                return true;
            } catch (ArithmeticException e) {
                String durationTooLong = this.config.getMessage("durationTooLong");
                sender.sendMessage(durationTooLong);
                return true;
            }
        }

        PlayerInfo playerInfo = new PlayerInfo(targetPlayerUuid, targetGroup, expirationTimeMillis);

        this.playerManager.update(
                playerInfo,
                this.getSuccessCallback("successPlayerGroup", "errorPlayerGroup", sender)
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
        } else if (args.length == 1) {
            return groupManager
                    .getIds()
                    .stream()
                    .toList();
        } else if (args.length == 2) {
            return List.of("<DD:HH:MM:SS | HH:MM:SS | MM:SS | SS>");
        }

        return null;
    }

    private Duration parseDurationString(String durationString) {
        Duration duration = Duration.ZERO;
        String[] durations = durationString.split(":");

        int unit = 1;
        for (int i = Math.min(durations.length - 1, 3); i >= 0; i--) {
            duration = duration.plus(Duration.ofSeconds(Long.parseLong(durations[i]) * unit));

            if (durations.length >= 4 && i == 1) {
                unit *= 24;
            } else {
                unit *= 60;
            }

        }
        return duration;
    }
}

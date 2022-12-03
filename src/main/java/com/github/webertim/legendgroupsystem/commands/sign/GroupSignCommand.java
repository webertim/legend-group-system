package com.github.webertim.legendgroupsystem.commands.sign;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.SignManager;
import com.github.webertim.legendgroupsystem.model.database.PlayerGroupSign;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Command used to create a sign displaying the group of the viewing player.
 */
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

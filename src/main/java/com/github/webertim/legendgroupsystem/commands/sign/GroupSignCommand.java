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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Command used to create a sign displaying the group of the viewing player.
 */
public class GroupSignCommand extends BaseSignCommand {
    private static final String ADD_KEYWORD = "add";
    private static final String REMOVE_KEYWORD = "remove";
    public GroupSignCommand(SignManager signManager, BaseConfiguration config) {
        super(signManager, config);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            String errorSignMessage = this.config.getMessage("errorSignCreate");
            sender.sendMessage(errorSignMessage);
            return true;
        }

        Location playerLocation = player.getLocation();

        Location blockLocation = playerLocation.toBlockLocation();
        blockLocation.setPitch(0);
        blockLocation.setYaw(0);

        PlayerGroupSign targetSign = new PlayerGroupSign(blockLocation);

        if (args[0].equals(ADD_KEYWORD)) {
            this.signManager.create(
                    targetSign,
                    getSuccessCallback("successSignCreate", "errorSignCreate", sender)
            );
        } else if (args[0].equals(REMOVE_KEYWORD)) {
            this.signManager.delete(
                    targetSign,
                    getSuccessCallback("successSignRemove", "errorSignRemove", sender)
            );
        } else {
            return false;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return Arrays.asList(
                    ADD_KEYWORD,
                    REMOVE_KEYWORD
            );
        }
        return null;
    }
}

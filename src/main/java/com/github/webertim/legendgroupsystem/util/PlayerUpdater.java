package com.github.webertim.legendgroupsystem.util;

import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.manager.SignManager;
import com.github.webertim.legendgroupsystem.manager.enums.SignStatusEnum;
import com.github.webertim.legendgroupsystem.model.Group;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Arrays;

public class PlayerUpdater {

    private final PlayerManager playerManager;
    private final SignManager signManager;

    public PlayerUpdater(PlayerManager playerManager, SignManager signManager) {
        this.playerManager = playerManager;
        this.signManager = signManager;
    }

    public void updatePlayer(Player player) {
        long start = System.currentTimeMillis();
        String prefixedPlayerName = this.playerManager.buildPlayerName(player);
        Group playerGroup = this.playerManager.getGroupInfo(player.getUniqueId());
        PlayerInfo playerInfo = playerManager.get(player.getUniqueId());

        String expirationString = null;
        if (playerInfo != null && playerInfo.getExpirationTimeMillis() != null) {
            expirationString = ChatColor.YELLOW + "Expires: " + ChatColor.RESET + playerInfo.getExpirationDateString();
        }

        new ScoreboardBuilder(player.getName(), ChatColor.RED + "" + ChatColor.BOLD + "<Rank Information>")
                .addLine("")
                .addLine(ChatColor.BLUE + "Player: " + ChatColor.RESET + prefixedPlayerName)
                .addLine(ChatColor.GREEN + "Group: " + ChatColor.RESET + playerGroup.getName())
                .addLine(expirationString)
                .build()
                .setPlayer(player);

        this.signManager.updatePlayerAllSigns(player, SignStatusEnum.UPDATE);
        player.playerListName(Component.text(prefixedPlayerName));

        System.out.println(System.currentTimeMillis() - start);
    }
}

package com.github.webertim.legendgroupsystem.util;

import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.manager.SignManager;
import com.github.webertim.legendgroupsystem.manager.enums.SignStatusEnum;
import com.github.webertim.legendgroupsystem.model.database.Group;
import com.github.webertim.legendgroupsystem.model.database.PlayerInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerUpdater {

    private final PlayerManager playerManager;
    private final SignManager signManager;

    public PlayerUpdater(PlayerManager playerManager, SignManager signManager) {
        this.playerManager = playerManager;
        this.signManager = signManager;
    }

    public void updatePlayer(Player player) {
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
    }
}

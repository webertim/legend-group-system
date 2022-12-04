package com.github.webertim.legendgroupsystem.util;

import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.manager.SignManager;
import com.github.webertim.legendgroupsystem.model.database.Group;
import com.github.webertim.legendgroupsystem.model.database.PlayerInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Class carrying a method to update a player.
 * This includes updating its list name, its scoreboard and the sign information.
 */
public class PlayerUpdater {

    private final PlayerManager playerManager;
    private final SignManager signManager;

    public PlayerUpdater(PlayerManager playerManager, SignManager signManager) {
        this.playerManager = playerManager;
        this.signManager = signManager;
    }

    /**
     * Method handling all necessary updates of a player.
     *
     * @param player The player to update.
     */
    public void updatePlayer(Player player) {
        String prefixedPlayerName = this.playerManager.buildPlayerName(player);

        updateSigns(player);
        updatePermissions(player);
        updateScoreboard(player, prefixedPlayerName);
        updateTabList(player, prefixedPlayerName);
    }

    private void updateSigns(Player player) {
        this.signManager.updatePlayerAllSigns(player);
    }

    public void updatePermissions(Player player) {
        player.recalculatePermissions();
        player.updateCommands();
    }

    private void updateScoreboard(Player player, String prefixedPlayerName) {
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
    }

    private void updateTabList(Player player, String prefixedPlayerName) {
        player.playerListName(Component.text(prefixedPlayerName));
    }
}

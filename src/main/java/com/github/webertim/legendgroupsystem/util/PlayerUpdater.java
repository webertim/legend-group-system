package com.github.webertim.legendgroupsystem.util;

import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.manager.SignManager;
import com.github.webertim.legendgroupsystem.manager.enums.SignStatusEnum;
import net.kyori.adventure.text.Component;
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

        this.signManager.updatePlayerAllSigns(player, SignStatusEnum.UPDATE);
        player.playerListName(Component.text(prefixedPlayerName));
    }
}

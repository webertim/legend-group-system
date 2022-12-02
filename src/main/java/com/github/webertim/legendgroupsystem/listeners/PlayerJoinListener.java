package com.github.webertim.legendgroupsystem.listeners;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.manager.SignManager;
import com.github.webertim.legendgroupsystem.manager.enums.SignStatusEnum;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final PlayerManager playerManager;
    private final SignManager signManager;
    private final LegendGroupSystem legendGroupSystem;
    private final BaseConfiguration config;

    public PlayerJoinListener(LegendGroupSystem legendGroupSystem, PlayerManager playerManager, SignManager signManager, BaseConfiguration config) {
        this.legendGroupSystem = legendGroupSystem;
        this.playerManager = playerManager;
        this.signManager = signManager;
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String prefixedPlayerName = this.playerManager.buildPlayerName(e.getPlayer());

        e.joinMessage(Component.text(ChatColor.YELLOW
                + prefixedPlayerName
                + " "
                + this.config.getMessage("playerJoin"))
        );

        this.signManager.updatePlayerAllSigns(e.getPlayer(), SignStatusEnum.UPDATE);
    }
}

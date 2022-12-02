package com.github.webertim.legendgroupsystem.listeners;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.manager.SignManager;
import com.github.webertim.legendgroupsystem.manager.enums.SignStatusEnum;
import com.github.webertim.legendgroupsystem.util.PlayerUpdater;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final PlayerManager playerManager;
    private final PlayerUpdater playerUpdater;

    public PlayerJoinListener(PlayerManager playerManager, PlayerUpdater playerUpdater) {
        this.playerManager = playerManager;
        this.playerUpdater = playerUpdater;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String prefixedPlayerName = this.playerManager.buildPlayerName(e.getPlayer());

        e.joinMessage(
                e.joinMessage().replaceText(
                        builder -> builder
                                .match(e.getPlayer().getName())
                                .replacement(prefixedPlayerName)
                )
        );

        this.playerUpdater.updatePlayer(e.getPlayer());
    }
}

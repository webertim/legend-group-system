package com.github.webertim.legendgroupsystem.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.InvocationTargetException;

public class PlayerJoinListener implements Listener {

    private final PlayerManager playerManager;
    private final LegendGroupSystem legendGroupSystem;
    private final BaseConfiguration config;

    public PlayerJoinListener(LegendGroupSystem legendGroupSystem, PlayerManager playerManager, BaseConfiguration config) {
        this.legendGroupSystem = legendGroupSystem;
        this.playerManager = playerManager;
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
    }
}

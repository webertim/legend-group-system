package com.github.webertim.legendgroupsystem.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.github.webertim.legendgroupsystem.LegendGroupSystem;
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

    public PlayerJoinListener(LegendGroupSystem legendGroupSystem, PlayerManager playerManager) {
        this.legendGroupSystem = legendGroupSystem;
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

    }
}

package com.github.webertim.legendgroupsystem.listeners;

import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.util.PlayerUpdater;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Player Join listener used to modify the displayed player name.
 */
public class PlayerJoinListener implements Listener {

    private final PlayerManager playerManager;
    private final PlayerUpdater playerUpdater;

    public PlayerJoinListener(PlayerManager playerManager, PlayerUpdater playerUpdater) {
        this.playerManager = playerManager;
        this.playerUpdater = playerUpdater;
    }

    /**
     * The method handling the actual player join event.
     *
     * @param e Player join event data.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String prefixedPlayerName = this.playerManager.buildPlayerName(e.getPlayer());

        Component defaultJoinMessage = e.joinMessage();
        if (defaultJoinMessage == null) {
            return;
        }

        Component newJoinMessage = defaultJoinMessage.replaceText(
                builder -> builder
                        .match(e.getPlayer().getName())
                        .replacement(prefixedPlayerName)
        );

        e.joinMessage(newJoinMessage);

        this.playerUpdater.updatePlayer(e.getPlayer());
    }
}

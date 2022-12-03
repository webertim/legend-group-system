package com.github.webertim.legendgroupsystem.listeners;

import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Chat listener used to modify chat messages based on the senders group information.
 */
public class AsyncPlayerChatListener implements Listener {

    private final PlayerManager playerManager;

    /**
     * Internal class definition of a {@link io.papermc.paper.chat.ChatRenderer}.
     * Defines the way messages are rendered based on group information of the sending player.
     */
    private class PrefixRenderer implements ChatRenderer {
        @Override
        public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
            String playerName = playerManager.buildPlayerName(source);
            return Component.text(playerName + ": ").append(message);
        }
    }

    public AsyncPlayerChatListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    /**
     * The method handling the actual chat event.
     *
     * @param e Chat event data.
     */
    @EventHandler
    public void onPlayerChatEvent(AsyncChatEvent e) {
        e.renderer(new PrefixRenderer());
    }
}

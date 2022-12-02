package com.github.webertim.legendgroupsystem.listeners;

import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class AsyncPlayerChatListener implements Listener {

    private final PlayerManager playerManager;
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

    @EventHandler
    public void onPlayerChatEvent(AsyncChatEvent e) {
        e.renderer(new PrefixRenderer());
    }
}

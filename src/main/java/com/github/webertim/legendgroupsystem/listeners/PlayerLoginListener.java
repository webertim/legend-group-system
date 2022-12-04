package com.github.webertim.legendgroupsystem.listeners;

import com.github.webertim.legendgroupsystem.manager.GroupPermissionsManager;
import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.permissions.CustomPermissible;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.permissions.PermissibleBase;

import java.lang.reflect.Field;

public class PlayerLoginListener implements Listener {
    private final PlayerManager playerManager;
    private final GroupPermissionsManager groupPermissionsManager;

    public PlayerLoginListener(PlayerManager playerManager, GroupPermissionsManager groupPermissionsManager) {
        this.playerManager = playerManager;
        this.groupPermissionsManager = groupPermissionsManager;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) throws IllegalAccessException {
        Player player = e.getPlayer();
        Field permissibleField = getPermissableBaseField(player);
        if (permissibleField == null) {
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }

        permissibleField.setAccessible(true);
        permissibleField.set(player, new CustomPermissible(player, this.playerManager, this.groupPermissionsManager));
    }

    private Field getPermissableBaseField(Player player) {
        Field[] permissableField = player.getClass().getSuperclass().getDeclaredFields();
        for (Field f : permissableField) {
            if (f.getType().equals(PermissibleBase.class)) {
                return f;
            }
        }

        return null;
    }
}

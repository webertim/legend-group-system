package com.github.webertim.legendgroupsystem.permissions;

import com.github.webertim.legendgroupsystem.manager.GroupPermissionsManager;
import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.model.database.Group;
import com.github.webertim.legendgroupsystem.model.database.GroupPermissions;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

/**
 * Custom implementation of PermissibleBase to implement a custom way of checking permissions.
 */
public class CustomPermissible extends PermissibleBase {
    private final Player player;
    private final PlayerManager playerManager;
    private final GroupPermissionsManager groupPermissionsManager;
    public CustomPermissible(Player player, PlayerManager playerManager, GroupPermissionsManager groupPermissionsManager) {
        super(player);
        this.player = player;
        this.playerManager = playerManager;
        this.groupPermissionsManager = groupPermissionsManager;
    }

    @Override
    public boolean hasPermission(@NotNull String inName) {
        Group playerGroup = this.playerManager.getGroupInfo(this.player.getUniqueId());
        GroupPermissions groupPermissions = this.groupPermissionsManager.get(playerGroup);

        boolean hasGroupPermission = false;

        if (groupPermissions != null) {
            hasGroupPermission = groupPermissions.hasPermission(inName.toLowerCase());
        }

        return hasGroupPermission || super.hasPermission(inName);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return hasPermission(perm.getName());
    }
}

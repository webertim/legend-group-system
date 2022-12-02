package com.github.webertim.legendgroupsystem.model;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 * Reason being that the ordering is based on the expirationTime, but the removal is based on the UUID of the player.
 */
public record ExpiringPlayer(UUID uuid, Long expirationTimeMillis) implements Comparable<ExpiringPlayer> {

    public static ExpiringPlayer fromPlayerInfo(PlayerInfo playerInfo) {
        return new ExpiringPlayer(playerInfo.getId(), playerInfo.getExpirationTimeMillis());
    }

    @Override
    public int compareTo(@NotNull ExpiringPlayer o) {
        return this.expirationTimeMillis.compareTo(o.expirationTimeMillis);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ExpiringPlayer expiringPlayer)) {
            return false;
        }

        return this.uuid.equals(expiringPlayer.uuid);
    }
}

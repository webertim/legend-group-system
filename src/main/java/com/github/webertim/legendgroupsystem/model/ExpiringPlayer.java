package com.github.webertim.legendgroupsystem.model;

import com.github.webertim.legendgroupsystem.model.database.PlayerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 *       Reason being that the ordering is based on the expirationTime, but the removal is based on the UUID of the player.
 * <br>
 * This class is used to manage expiring group rights in a PriorityQueue.
 */
public record ExpiringPlayer(UUID uuid, Long expirationTimeMillis) implements Comparable<ExpiringPlayer> {

    /**
     * Create expiration information based on player information.
     *
     * @param playerInfo The player information to use
     * @return A new ExpiringPlayer object with Id and expiration equal to provided player info.
     */
    public static ExpiringPlayer fromPlayerInfo(PlayerInfo playerInfo) {
        return new ExpiringPlayer(playerInfo.getId(), playerInfo.getExpirationTimeMillis());
    }

    /**
     * Compares the expirationTimeMillis attribute of both objects.
     *
     * @param o the object to be compared.
     * @return Result of the comparison of expirationTimeMillis attribute of both objects.
     */
    @Override
    public int compareTo(@NotNull ExpiringPlayer o) {
        return this.expirationTimeMillis.compareTo(o.expirationTimeMillis);
    }

    /**
     * Compares the UUID of the represented player.
     *
     * @param obj   the reference object with which to compare.
     * @return true if the UUID of both objects are equal, false otherwise.
     */
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

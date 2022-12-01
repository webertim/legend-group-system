package com.github.webertim.legendgroupsystem.model;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 * Reason being that the ordering is based on the expirationTime, but the removal is based on the UUID of the player.
 *
 */
public class ExpiringPlayer implements Comparable {

    private final UUID uuid;
    private final Long expirationTimeMillis;

    public ExpiringPlayer(UUID uuid, Long expirationTimeMillis) {
        this.uuid = uuid;
        this.expirationTimeMillis = expirationTimeMillis;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getExpirationTimeMillis() {
        return expirationTimeMillis;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            throw new NullPointerException("Cannot compare with null");
        }
        if (!(o instanceof ExpiringPlayer expiringPlayer)) {
            throw new ClassCastException("Cannot compare with different Class");
        }
        return this.expirationTimeMillis.compareTo(expiringPlayer.expirationTimeMillis);
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

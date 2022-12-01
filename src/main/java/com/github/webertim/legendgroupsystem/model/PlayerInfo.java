package com.github.webertim.legendgroupsystem.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.annotation.Nullable;
import java.util.UUID;

@DatabaseTable(tableName = PlayerInfo.TABLE_NAME)
public class PlayerInfo implements Identifiable<UUID> {
    public static final String TABLE_NAME = "players";
    public static final String ID_COLUMN = "id";
    public static final String GROUP_COLUMN = "group_fk";
    public static final String EXPIRATION_TIME_MILLIS_COLUMN = "expiration_time_millis";

    @DatabaseField(id = true, columnName = ID_COLUMN)
    private UUID uuid;

    @DatabaseField(foreign = true, columnName = GROUP_COLUMN)
    private Group group;

    @DatabaseField(canBeNull = true, columnName = EXPIRATION_TIME_MILLIS_COLUMN)
    private Long expirationTimeMillis;

    public PlayerInfo() {}

    @Override
    public UUID getId() {
        return uuid;
    }

    public Group getGroup() {
        return group;
    }

    public @Nullable Long getExpirationTimeMillis() {
        return expirationTimeMillis;
    }
}

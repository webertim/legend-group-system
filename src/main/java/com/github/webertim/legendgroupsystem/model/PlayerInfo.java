package com.github.webertim.legendgroupsystem.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = PlayerInfo.TABLE_NAME)
public class PlayerInfo implements Identifiable<UUID> {
    public static final String TABLE_NAME = "players";
    public static final String ID_COLUMN = "id";
    public static final String GROUP_COLUMN = "group_fk";
    public static final String EXPIRATION_TIME_MILLIS_COLUMN = "expiration_time_millis";

    @DatabaseField(id = true, columnName = ID_COLUMN)
    private UUID uuid;

    @DatabaseField(foreign = true, columnName = GROUP_COLUMN, columnDefinition = "varchar(255) references groups(id)")
    private Group group;

    @DatabaseField(columnName = EXPIRATION_TIME_MILLIS_COLUMN)
    private Long expirationTimeMillis;

    public PlayerInfo() {}

    public PlayerInfo(UUID playerUuid) {
        this(playerUuid, null, null);
    }

    public PlayerInfo(UUID playerUuid, Group targetGroup, @Nullable Long expirationTimeMillis) {
        this.uuid = playerUuid;
        this.group = targetGroup;
        this.expirationTimeMillis = expirationTimeMillis;
    }

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

    public @Nullable String getExpirationDateString() {
        if (expirationTimeMillis == null) {
            return "";
        }

        Date expirationDate = new Date(expirationTimeMillis);
        return SimpleDateFormat
                .getDateTimeInstance()
                .format(expirationDate);
    }
}

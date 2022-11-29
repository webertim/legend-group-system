package com.github.webertim.legendgroupsystem.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "players")
public class PlayerInfo {

    @DatabaseField(id = true)
    private UUID uuid;

    /* Note: Because of the way ORMLight works, this object will only contain a group name
     * (which is the id of the group table)
     * Therefore no default getter should be created for this object.
     */
    @DatabaseField(foreign = true)
    private Group group;

    @DatabaseField
    private long expirationTimeMillis;

    public PlayerInfo() {}
}

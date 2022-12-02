package com.github.webertim.legendgroupsystem.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;

@DatabaseTable(tableName = PlayerGroupSign.TABLE_NAME)
public class PlayerGroupSign implements Identifiable<String> {
    public static final String TABLE_NAME = "signs";
    public static final String ID_COLUMN = "id";
    public static final String WORLD_COLUMN = "world";
    public static final String X_COLUMN = "x";
    public static final String Y_COLUMN = "y";
    public static final String Z_COLUMN = "z";

    @DatabaseField(id = true, columnName = ID_COLUMN)
    private final String id;
    @DatabaseField(columnName = WORLD_COLUMN)
    private final String world;
    @DatabaseField(columnName = X_COLUMN)
    private final Double x;
    @DatabaseField(columnName = Y_COLUMN)
    private final Double y;
    @DatabaseField(columnName = Z_COLUMN)
    private final Double z;

    public PlayerGroupSign() {
        this.id = null;
        this.world = null;
        this.x = null;
        this.y = null;
        this.z = null;
    }

    public PlayerGroupSign(String signName, Location location) {
        this.id = signName;
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    @Override
    public String getId() {
        return this.id;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
    }
}

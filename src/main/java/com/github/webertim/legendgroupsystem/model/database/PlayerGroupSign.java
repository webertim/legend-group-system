package com.github.webertim.legendgroupsystem.model.database;

import com.github.webertim.legendgroupsystem.database.persisters.LocationPersister;
import com.github.webertim.legendgroupsystem.model.Identifiable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Class representing a group sign.
 */
@DatabaseTable(tableName = PlayerGroupSign.TABLE_NAME)
public class PlayerGroupSign implements Identifiable<Location> {
    public static final String TABLE_NAME = "signs";

    public static final String LOCATION_COLUMN = "location";

    @DatabaseField(id = true, columnName = LOCATION_COLUMN, persisterClass = LocationPersister.class)
    private final Location location;

    public PlayerGroupSign() {
        this.location = null;
    }

    public PlayerGroupSign(Location location) {
        this.location = location;
    }

    @Override
    public Location getId() {
        return this.location;
    }

    /**
     * A location object of this sign.
     *
     * @return The location of this sign.
     */
    public Location getLocation() {
        return this.location;
    }
}

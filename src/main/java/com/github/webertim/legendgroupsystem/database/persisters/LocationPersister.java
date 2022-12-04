package com.github.webertim.legendgroupsystem.database.persisters;

import com.google.gson.Gson;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class LocationPersister extends BaseDataType {
    private static final String WORLD_KEY = "world";
    private static final String X_KEY = "X";
    private static final String Y_KEY = "Y";
    private static final String Z_KEY = "Z";

    private final Gson parser = new Gson();

    private static final LocationPersister instance = new LocationPersister();

    private LocationPersister() {
        super(SqlType.STRING);
    }

    public static LocationPersister getSingleton() {
        return instance;
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String s) {
        System.out.println(s);
        return new Location(Bukkit.getWorld("world"), 0, 0, 0);
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults databaseResults, int i) throws SQLException {
        return databaseResults.getString(i);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        String data = (String) sqlArg;
        Map map = parser.fromJson(data, Map.class);

        World world = Bukkit.getWorld((String) map.get(WORLD_KEY));
        double x = (Double) map.get(X_KEY);
        double y = (Double) map.get(Y_KEY);
        double z = (Double) map.get(Z_KEY);


        return new Location(world, x, y ,z);
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        Location location = (Location) javaObject;

        return stringifyLocation(location);
    }

    @Override
    public int getDefaultWidth() {
        return 255;
    }

    private String stringifyLocation(Location location) {
        Map<String, Object> locationMap = new HashMap<>();
        locationMap.put(WORLD_KEY, location.getWorld().getName());
        locationMap.put(X_KEY, location.getBlockX());
        locationMap.put(Y_KEY, location.getBlockY());
        locationMap.put(Z_KEY, location.getBlockZ());

        return parser.toJson(locationMap);
    }
}

package com.github.webertim.legendgroupsystem;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.database.DatabaseConnector;
import com.github.webertim.legendgroupsystem.database.DatabaseOptions;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class LegendGroupSystem extends JavaPlugin {
    private DatabaseConnector databaseConnector;

    @Override
    public void onEnable() {
        BaseConfiguration config = new BaseConfiguration(this);

        DatabaseOptions databaseOptions = config.getDatabaseOptions();
        try {
            this.databaseConnector = new DatabaseConnector(databaseOptions);
        } catch (SQLException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        if (databaseConnector != null) {
            try {
                databaseConnector.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

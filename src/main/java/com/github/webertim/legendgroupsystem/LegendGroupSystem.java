package com.github.webertim.legendgroupsystem;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class LegendGroupSystem extends JavaPlugin {

    @Override
    public void onEnable() {
        BaseConfiguration config = new BaseConfiguration(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

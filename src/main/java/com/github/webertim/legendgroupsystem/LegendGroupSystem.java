package com.github.webertim.legendgroupsystem;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.github.webertim.legendgroupsystem.commands.KeywordCommand;
import com.github.webertim.legendgroupsystem.commands.group.*;
import com.github.webertim.legendgroupsystem.commands.player.AddPlayerGroupCommand;
import com.github.webertim.legendgroupsystem.commands.player.GetPlayerGroupCommand;
import com.github.webertim.legendgroupsystem.commands.player.RemovePlayerGroupCommand;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.database.DatabaseConnector;
import com.github.webertim.legendgroupsystem.database.DatabaseOptions;
import com.github.webertim.legendgroupsystem.listeners.AsyncPlayerChatListener;
import com.github.webertim.legendgroupsystem.listeners.PlayerJoinListener;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class LegendGroupSystem extends JavaPlugin {
    private DatabaseConnector databaseConnector;
    private TaskChainFactory taskChainFactory;
    private BaseConfiguration config;
    private GroupManager groupManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        this.taskChainFactory = BukkitTaskChainFactory.create(this);
        this.config = new BaseConfiguration(this);

        try {
            DatabaseOptions databaseOptions = config.getDatabaseOptions();
            this.databaseConnector = new DatabaseConnector(databaseOptions);
        } catch (SQLException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        try {
            this.groupManager = new GroupManager(this, databaseConnector.getGroupDao());
            this.playerManager = new PlayerManager(this, databaseConnector.getPlayerInfoDao(), this.groupManager);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.registerListeners();
        this.registerCommands();
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

    private void registerCommands() {
        new KeywordCommand("group", new KeywordCommand[]{
            new KeywordCommand("create", new CreateGroupCommand(this.groupManager, this.config)),
            new KeywordCommand("update", new UpdateGroupCommand(this.groupManager, this.config)),
            new KeywordCommand("delete", new DeleteGroupCommand(this.groupManager, this.config)),
            new KeywordCommand("default", new DefaultGroupCommand(this.groupManager, this.config)),
            new KeywordCommand("sign", new GroupSignCommand(this.groupManager, this.config))
        }).register(this);

        new KeywordCommand("player", new KeywordCommand[]{
            new KeywordCommand("add", new AddPlayerGroupCommand(this.playerManager, this.groupManager, this.config)),
            new KeywordCommand("remove", new RemovePlayerGroupCommand(this.playerManager, this.groupManager, this.config))
        }).register(this);

        new KeywordCommand(
                "groupinfo", new GetPlayerGroupCommand(this.playerManager, this.groupManager, this.config)
        ).register(this);
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new AsyncPlayerChatListener(this.playerManager), this);
        pluginManager.registerEvents(new PlayerJoinListener(this, this.playerManager, this.config), this);
    }

    public <T> TaskChain<T> createTaskChain() {
        return taskChainFactory.newChain();
    }

    public <T> TaskChain<T> createTaskChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }
}

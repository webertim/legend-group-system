package com.github.webertim.legendgroupsystem;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.github.webertim.legendgroupsystem.commands.KeywordCommand;
import com.github.webertim.legendgroupsystem.commands.group.CreateGroupCommand;
import com.github.webertim.legendgroupsystem.commands.group.DefaultGroupCommand;
import com.github.webertim.legendgroupsystem.commands.group.DeleteGroupCommand;
import com.github.webertim.legendgroupsystem.commands.group.UpdateGroupCommand;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.database.DatabaseConnector;
import com.github.webertim.legendgroupsystem.database.DatabaseOptions;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class LegendGroupSystem extends JavaPlugin {
    private DatabaseConnector databaseConnector;
    private TaskChainFactory taskChainFactory;
    private BaseConfiguration config;
    private GroupManager groupManager;

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

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



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
            new KeywordCommand("default", new DefaultGroupCommand(this.groupManager, this.config))
        }).register(this);

        new KeywordCommand("player", new KeywordCommand[]{

        }).register(this);
    }

    public <T> TaskChain<T> createTaskChain() {
        return taskChainFactory.<T>newChain();
    }

    public <T> TaskChain<T> createTaskChain(String name) {
        return taskChainFactory.<T>newSharedChain(name);
    }
}

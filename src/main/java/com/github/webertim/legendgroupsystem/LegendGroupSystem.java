package com.github.webertim.legendgroupsystem;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.github.webertim.legendgroupsystem.commands.KeywordCommand;
import com.github.webertim.legendgroupsystem.commands.group.CreateGroupCommand;
import com.github.webertim.legendgroupsystem.commands.group.DefaultGroupCommand;
import com.github.webertim.legendgroupsystem.commands.group.DeleteGroupCommand;
import com.github.webertim.legendgroupsystem.commands.group.UpdateGroupCommand;
import com.github.webertim.legendgroupsystem.commands.player.AddPlayerGroupCommand;
import com.github.webertim.legendgroupsystem.commands.player.GetPlayerGroupCommand;
import com.github.webertim.legendgroupsystem.commands.player.RemovePlayerGroupCommand;
import com.github.webertim.legendgroupsystem.commands.sign.GroupSignCommand;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.database.DatabaseConnector;
import com.github.webertim.legendgroupsystem.database.DatabaseOptions;
import com.github.webertim.legendgroupsystem.listeners.AsyncPlayerChatListener;
import com.github.webertim.legendgroupsystem.listeners.PlayerJoinListener;
import com.github.webertim.legendgroupsystem.manager.GroupManager;
import com.github.webertim.legendgroupsystem.manager.PlayerManager;
import com.github.webertim.legendgroupsystem.manager.SignManager;
import com.github.webertim.legendgroupsystem.model.database.Group;
import com.github.webertim.legendgroupsystem.util.PlayerUpdater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class LegendGroupSystem extends JavaPlugin {
    private DatabaseConnector databaseConnector;
    private TaskChainFactory taskChainFactory;
    private BaseConfiguration config;
    private GroupManager groupManager;
    private PlayerManager playerManager;
    private SignManager signManager;
    private PlayerUpdater playerUpdater;

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
            this.signManager = new SignManager(this, databaseConnector.getSignDao(), this.playerManager);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.playerUpdater = new PlayerUpdater(this.playerManager, this.signManager);

        this.registerManagerCallbacks();
        this.registerListeners();
        this.registerCommands();
    }

    private void registerManagerCallbacks() {
        this.groupManager.registerOnChangeListener(
                (group, operation) -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Group playerGroup = this.playerManager.getGroupInfo(player.getUniqueId());
                        // Players only need to be updated if
                        //  - they are in a group which is being updated
                        //  - they are in a default group
                        //      (because the DEFAULT group will be removed from them if a group is set to default in the database)
                        if (playerGroup.getId().equals(group.getId())
                                || playerGroup.isDefault()) {

                            this.playerUpdater.updatePlayer(player);
                        }
                    }
                }
        );

        this.playerManager.registerOnChangeListener(
                (playerInfo, operation) -> {
                    Player targetPlayer = Bukkit.getPlayer(playerInfo.getId());
                    if (targetPlayer == null) {
                        return;
                    }

                    this.playerUpdater.updatePlayer(targetPlayer);
                }
        );
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new AsyncPlayerChatListener(this.playerManager), this);
        pluginManager.registerEvents(new PlayerJoinListener(this.playerManager, this.playerUpdater), this);
    }

    private void registerCommands() {
        new KeywordCommand("group", new KeywordCommand[]{
            new KeywordCommand("create", new CreateGroupCommand(this.groupManager, this.config)),
            new KeywordCommand("update", new UpdateGroupCommand(this.groupManager, this.config)),
            new KeywordCommand("delete", new DeleteGroupCommand(this.groupManager, this.config)),
            new KeywordCommand("default", new DefaultGroupCommand(this.groupManager, this.config)),
        }).register(this);

        new KeywordCommand("player", new KeywordCommand[]{
            new KeywordCommand("add", new AddPlayerGroupCommand(this.playerManager, this.groupManager, this.config)),
            new KeywordCommand("remove", new RemovePlayerGroupCommand(this.playerManager, this.groupManager, this.config))
        }).register(this);

        new KeywordCommand(
                "groupinfo", new GetPlayerGroupCommand(this.playerManager, this.groupManager, this.config)
        ).register(this);

        new KeywordCommand(
                "groupsign", new GroupSignCommand(this.signManager, this.config)
        ).register(this);
    }

    public <T> TaskChain<T> createTaskChain() {
        return taskChainFactory.newChain();
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

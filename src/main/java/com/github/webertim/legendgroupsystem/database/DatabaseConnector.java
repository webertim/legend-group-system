package com.github.webertim.legendgroupsystem.database;

import com.github.webertim.legendgroupsystem.model.Group;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseConnector {
    private final Dao<Group, String> groupDao;
    private final Dao<PlayerInfo, Byte> playerInfoDao;
    private final JdbcPooledConnectionSource connectionSource;

    public DatabaseConnector(String url, String user, String password, String databaseName) throws SQLException {
        this.connectionSource = new JdbcPooledConnectionSource(url + databaseName, user, password);
        this.connectionSource.setCheckConnectionsEveryMillis(5 * 60 * 1000);
        this.connectionSource.setTestBeforeGet(true);

        groupDao = DaoManager.createDao(connectionSource, Group.class);
        playerInfoDao = DaoManager.createDao(connectionSource, PlayerInfo.class);

        TableUtils.createTableIfNotExists(this.connectionSource, Group.class);
        TableUtils.createTableIfNotExists(this.connectionSource, PlayerInfo.class);
    }

    public DatabaseConnector(DatabaseOptions databaseOptions) throws SQLException {
        this(databaseOptions.getUrl(), databaseOptions.getUsername(), databaseOptions.getPassword(), databaseOptions.getName());
    }

    public void close() throws Exception {
        connectionSource.close();
    }
}

package com.github.webertim.legendgroupsystem.database;

import com.github.webertim.legendgroupsystem.model.Group;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

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

    /**
     * This method tries to insert a group into the 'groups' table of the database.
     * If this operation fails, no exception is thrown but rather a boolean of false is returned.
     * If you need to act based on the success of the operation make sure to check the return value.
     * @param group A Group object to be created in the database.
     * @return <code>true</code> if the operation is successful i.e. the group object was added to the database, <code>false</code> otherwise
     */
    public boolean tryCreateGroup(Group group) {
        try {
            return this.groupDao.create(group) == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean tryUpdateGroup(Group group) {
        try {
            return this.groupDao.update(group) == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Group> readGroups() throws SQLException {
        return this.groupDao.queryForAll();
    }

    public List<PlayerInfo> readPlayerInfos() throws SQLException {
        return this.playerInfoDao.queryForAll();
    }

    public void close() throws Exception {
        connectionSource.close();
    }
}

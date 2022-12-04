package com.github.webertim.legendgroupsystem.database;

import com.github.webertim.legendgroupsystem.model.database.Group;
import com.github.webertim.legendgroupsystem.model.database.PlayerGroupSign;
import com.github.webertim.legendgroupsystem.model.database.PlayerInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.UUID;

/**
 * A class used for establishing a database connection as well as initializing necessary tables if they do not exist.
 * Provides a DAO for every manager.
 */
public class DatabaseConnector {
    private final Dao<Group, String> groupDao;
    private final Dao<PlayerInfo, UUID> playerInfoDao;
    private final Dao<PlayerGroupSign, Location> signDao;
    private final JdbcPooledConnectionSource connectionSource;

    /**
     * Initializes the connection pool, creates required DAOs and creates all tables if they do not exist.
     * If one of the above interactions fails an SQLException is thrown and the plugin should exit.
     * Currently, only Postgres is supported, because only the JDBC driver for postgres is included in this plugins pom.xml
     * Could be easily fixed if necessary.
     *
     * @param url URL of the prostgres database.
     * @param user User of the prostgres database.
     * @param password Password of the provided user.
     * @param databaseName Name of the database to write to.
     * @throws SQLException If one of the database interactions fail an SQLException is thrown which should result in
     *                      the plugin exiting (because it doesn't work without a database)
     */
    public DatabaseConnector(String url, String user, String password, String databaseName) throws SQLException {
        this.connectionSource = new JdbcPooledConnectionSource(url + databaseName, user, password);
        this.connectionSource.setCheckConnectionsEveryMillis(5 * 60 * 1000);
        this.connectionSource.setTestBeforeGet(true);

        groupDao = DaoManager.createDao(connectionSource, Group.class);
        playerInfoDao = DaoManager.createDao(connectionSource, PlayerInfo.class);
        signDao = DaoManager.createDao(connectionSource, PlayerGroupSign.class);

        TableUtils.createTableIfNotExists(this.connectionSource, Group.class);
        TableUtils.createTableIfNotExists(this.connectionSource, PlayerInfo.class);
        TableUtils.createTableIfNotExists(this.connectionSource, PlayerGroupSign.class);
    }

    /**
     * Initializes the connection pool, creates required DAOs and creates all tables if they do not exist.
     * If one of the above interactions fails an SQLException is thrown and the plugin should exit.
     * Currently, only Postgres is supported, because only the JDBC driver for postgres is included in this plugins pom.xml
     * Could be easily fixed if necessary.
     *
     * @param databaseOptions Database options.
     * @throws SQLException If one of the database interactions fail an SQLException is thrown which should result in
     *                      the plugin exiting (because it doesn't work without a database)
     */
    public DatabaseConnector(DatabaseOptions databaseOptions) throws SQLException {
        this(databaseOptions.url(), databaseOptions.username(), databaseOptions.password(), databaseOptions.name());
    }

    /**
     * Get the DAO for interacting with the groups table. Currently used in the group manager.
     *
     * @return The DAO for the groups table
     */
    public Dao<Group, String> getGroupDao() {
        return groupDao;
    }

    /**
     * Get the DAO for interacting with the players table. Currently used in the player manager.
     *
     * @return The DAO for the players table
     */
    public Dao<PlayerInfo, UUID> getPlayerInfoDao() {
        return playerInfoDao;
    }

    /**
     * Get the DAO for interacting with the signs table. Currently used in the sign manager.
     *
     * @return The DAO for the signs table
     */
    public Dao<PlayerGroupSign, Location> getSignDao() {
        return signDao;
    }

    /**
     * Close the database connection.
     *
     * @throws Exception
     */
    public void close() throws Exception {
        connectionSource.close();
    }
}

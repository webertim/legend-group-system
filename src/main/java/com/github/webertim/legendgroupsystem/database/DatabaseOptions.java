package com.github.webertim.legendgroupsystem.database;

/**
 * Class for packaging all the required database options.
 *
 * @param url URL of the prostgres database.
 * @param name User of the prostgres database.
 * @param username Password of the provided user.
 * @param password Name of the database to write to.
 */
public record DatabaseOptions(String url, String name, String username, String password) {
}

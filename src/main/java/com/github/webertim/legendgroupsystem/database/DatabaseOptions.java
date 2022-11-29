package com.github.webertim.legendgroupsystem.database;

public class DatabaseOptions {
    private final String url;
    private final String name;
    private final String username;
    private final String password;

    public DatabaseOptions(String url, String name, String username, String password) {
        this.url = url;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

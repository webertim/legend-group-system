package com.github.webertim.legendgroupsystem.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

@DatabaseTable(tableName = "groups")
public class Group {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String prefix;

    private List<String> permissions;

    public Group() {}

    public Group(String id) {
        this(id, null, null);
    }

    public Group(String id, String name, String prefix) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

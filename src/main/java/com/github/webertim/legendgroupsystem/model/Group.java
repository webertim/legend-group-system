package com.github.webertim.legendgroupsystem.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

@DatabaseTable(tableName = "groups")
public class Group {

    @DatabaseField(id = true)
    private String name;

    @DatabaseField
    private String prefix;

    private List<String> permissions;

    public Group() {}

    public Group(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }
}

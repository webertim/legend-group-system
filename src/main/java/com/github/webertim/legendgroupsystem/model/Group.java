package com.github.webertim.legendgroupsystem.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

@DatabaseTable(tableName = Group.TABLE_NAME)
public class Group implements Identifiable<String> {
    public static final String TABLE_NAME = "groups";
    public static final String ID_COLUMN = "id";
    public static final String NAME_COLUMN = "name";
    public static final String PREFIX_COLUMN = "prefix";
    public static final String IS_DEFAULT_COLUMN = "is_default";

    @DatabaseField(id = true, columnName = ID_COLUMN)
    private String id;

    @DatabaseField(columnName = NAME_COLUMN)
    private String name;

    @DatabaseField(columnName = PREFIX_COLUMN)
    private String prefix;

    @DatabaseField(defaultValue = "false", canBeNull = false, columnName = IS_DEFAULT_COLUMN)
    private boolean isDefault;

    private List<String> permissions;

    public Group() {}

    public Group(String id) {
        this(id, null, null);
    }

    public Group(String id, String name, String prefix) {
        this(id, name, prefix, false);
    }

    public Group(String id, String name, String prefix, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.isDefault = isDefault;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}

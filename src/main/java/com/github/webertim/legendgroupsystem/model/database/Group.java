package com.github.webertim.legendgroupsystem.model.database;


import com.github.webertim.legendgroupsystem.model.Identifiable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Class representing a group.
 */
@DatabaseTable(tableName = Group.TABLE_NAME)
public class Group implements Identifiable<String> {
    public static final String TABLE_NAME = "groups";
    public static final String ID_COLUMN = "id";
    public static final String NAME_COLUMN = "name";
    public static final String PREFIX_COLUMN = "prefix";
    public static final String IS_DEFAULT_COLUMN = "is_default";
    public static final Group DEFAULT = new Group("", "DEFAULT", "[DEFAULT]", true);

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

    /**
     * Creates a new group with isDefault being false.
     *
     * @param id Group id.
     * @param name Group name.
     * @param prefix Group prefix.
     */
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

    /**
     * Creates a copy of this group only modifying the isDefault value with the provided value.
     *
     * @param isDefault The new value of isDefault for the group copy.
     * @return A copy of this group with a new isDefault value.
     */
    public Group copyWithDefault(boolean isDefault) {
        return new Group(
                this.id,
                this.name,
                this.prefix,
                isDefault
        );
    }
}

package com.github.webertim.legendgroupsystem.model.database;

import com.github.webertim.legendgroupsystem.database.persisters.HashSetPersister;
import com.github.webertim.legendgroupsystem.model.Identifiable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.HashSet;
import java.util.UUID;

@DatabaseTable(tableName = GroupPermissions.TABLE_NAME)
public class GroupPermissions implements Identifiable<Group> {
    public static final String TABLE_NAME = "group_permissions";
    public static final String ID_COLUMN = "id";
    public static final String GROUP_COLUMN = "group";
    public static final String PERMISSIONS_COLUMN = "permissions";

    @DatabaseField(generatedId = true, columnName = ID_COLUMN)
    private final Integer id;
    @DatabaseField(foreign = true, columnName = GROUP_COLUMN)
    private final Group group;

    @DatabaseField(columnName = PERMISSIONS_COLUMN, persisterClass = HashSetPersister.class)
    private HashSet<String> permissions;

    public GroupPermissions() {
        this(null);
    }
    public GroupPermissions(Group group) {
        this.id = null;
        this.group = group;
        this.permissions = new HashSet<>();
    }

    public HashSet<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(HashSet<String> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(String permission) {
        if (permissions.contains("*")) {
            return true;
        }

        return permissions.contains(permission);
    }

    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    public void removePermission(String permission) {
        this.permissions.remove(permission);
    }

    @Override
    public Group getId() {
        return group;
    }
}

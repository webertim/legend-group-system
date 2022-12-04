package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.model.database.Group;
import com.github.webertim.legendgroupsystem.model.database.GroupPermissions;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Manager class for group permissions.
 */
public class GroupPermissionsManager extends BaseManager<Group, GroupPermissions> {
    public GroupPermissionsManager(LegendGroupSystem legendGroupSystem, Dao<GroupPermissions, Group> dao) throws SQLException {
        super(legendGroupSystem, dao);
    }
}

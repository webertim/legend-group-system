package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.model.Group;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.function.Consumer;

public class GroupManager extends BaseManager<String, Group> {


    public GroupManager(LegendGroupSystem legendGroupSystem, Dao<Group, String> dao) throws SQLException {
        super(legendGroupSystem, dao);
    }

    @Override
    public void update(Group data, Consumer<Boolean> finalTask) {
        Group targetGroup = this.get(data.getId());

        if (data.getPrefix() == null) {
            data.setPrefix(targetGroup.getPrefix());
        }

        if (data.getName() == null) {
            data.setName(targetGroup.getName());
        }

        super.update(data, finalTask);
    }

    public void updateDefaultGroup(Group group, Consumer<Boolean> lastTask) {
        this.createSuccessBasedTaskChain(
                () -> {
                    if (!(this.dataMap.containsKey(group.getId()))) {
                        return 0;
                    }
                    UpdateBuilder<Group, String> updateBuilder = this.getDao().updateBuilder();
                    updateBuilder.updateColumnExpression(
                            Group.IS_DEFAULT_COLUMN,
                            "(? = %s)".formatted(updateBuilder.escapeColumnName(Group.ID_COLUMN)));

                    return this.getDao().updateRaw(updateBuilder.prepareStatementString(), group.getId());
                },
                () -> setDefaultGroup(group),
                lastTask
        );
    }

    private void setDefaultGroup(Group group) {
        this.getValues().forEach(
                (Group prevGroup) ->
                    prevGroup.setDefault(prevGroup.getId().equals(group.getId()))
        );
    }
}

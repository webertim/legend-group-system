package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.model.Group;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;

public class GroupManager extends BaseManager<String, Group> {


    public GroupManager(LegendGroupSystem legendGroupSystem, Dao<Group, String> dao) throws SQLException {
        super(legendGroupSystem, dao);
    }

    @Override
    public void update(Group data, Consumer<Boolean> finalTask) {
        Group targetGroup = this.get(data.getId());

        if (targetGroup == null) {
            finalTask.accept(false);
            return;
        }

        if (data.getPrefix() == null) {
            data.setPrefix(targetGroup.getPrefix());
        }

        if (data.getName() == null) {
            data.setName(targetGroup.getName());
        }

        data.setDefault(targetGroup.isDefault());

        super.update(data, finalTask);
    }

    public void updateDefaultGroup(Group group, Consumer<Boolean> lastTask) {
        this.createSuccessBasedTaskChain(
                () -> {
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
        // This may seem overcomplicated, but mapping over this.getValues() would become a problem
        // because the onChange would not fire in this case.

        Optional<Group> currentDefault = this.findDefault();

        if (!(currentDefault.isEmpty())) {
            this.edit(
                    currentDefault.get().getId(),
                    currentDefault.get().copyWithDefault(false)
            );
        }

        Group targetGroup = this.get(group.getId());
        if (targetGroup != null) {
            this.edit(
                    targetGroup.getId(),
                    targetGroup.copyWithDefault(true)
            );
        }
    }

    public Group getDefaultGroup() {
        return this
                .findDefault()
                .orElse(Group.DEFAULT);
    }

    private Optional<Group> findDefault() {
        return this
                .getValues()
                .stream()
                .filter(Group::isDefault)
                .findFirst();
    }
}

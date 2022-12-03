package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.model.database.Group;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Manager class for managing groups.
 */
public class GroupManager extends BaseManager<String, Group> {


    public GroupManager(LegendGroupSystem legendGroupSystem, Dao<Group, String> dao) throws SQLException {
        super(legendGroupSystem, dao);
    }

    /**
     * Adds some logic on top of the implementation of the {@link com.github.webertim.legendgroupsystem.manager.BaseManager}
     * implementation. <br>
     * 1. Checks if the group exists (the base implementation executes a craeteOrUpdate operation) <br>
     * 2. Replaces null values of the new data with fields of the existing data point <br>
     * 3. Ignores changes to the isDefault value, since this needs more logic and is therefore handled with a special method <br>
     *
     * @param data The data to update the database and the local map with (the Id of the object determines the updated object).
     * @param finalTask The callback to execute after the update and edit.
     */
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

    /**
     * Updates the default group by updating all entries of the group table and afterwards modifying the old default
     * group and setting the new one.
     *
     * @param group Group object with an Id matching the new target default group. Other attributes are ignored.
     * @param lastTask Sync callback to execute after the database interaction and update of the local datastructure
     *                 Receives a boolean value representing the success of the operation.
     */
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

        currentDefault.ifPresent(value -> this.edit(
                value.getId(),
                value.copyWithDefault(false)
        ));

        Group targetGroup = this.get(group.getId());
        if (targetGroup == null) {
            return;
        }

        this.edit(
                targetGroup.getId(),
                targetGroup.copyWithDefault(true)
        );
    }

    /**
     * Searches the current default group and returns it. In case not group inside the database is marked as default
     * the static default group is returned.
     *
     * @return The current default group.
     */
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

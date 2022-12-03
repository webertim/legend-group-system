package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.model.database.Group;
import com.j256.ormlite.stmt.UpdateBuilder;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class GroupManagerTest extends BaseTest<String, Group> {

    GroupManager groupManager;

    GroupManagerTest() {
        super(Group.class);
    }

    @BeforeEach
    @Order(0)
    @Override
    void setup() throws SQLException {
        super.setup();

        UpdateBuilder updateBuilder = mock(UpdateBuilder.class);
        when(updateBuilder.updateColumnExpression(anyString(), anyString())).then(invocation -> null);
        when(updateBuilder.prepareStatementString()).then(invocation -> "");
        when(updateBuilder.escapeColumnName(anyString())).then(invocation -> "");

        when(dao.updateBuilder()).then(invocation -> updateBuilder);
        when(dao.updateRaw(anyString(), anyString())).then(
                invocation -> {
                    String groupId = invocation.getArgument(1);
                    mockDb.replaceAll(group -> group.copyWithDefault(group.getId().equals(groupId)));
                    return 1;
                }
        );

        mockDb.replaceAll(group -> new Group(UUID.randomUUID().toString(), "", "", false));

        groupManager = new GroupManager(legendGroupSystem, dao);
    }

    @BeforeEach
    @Order(1)
    @Override
    void testInitialization() {
        super.testInitialization();
    }

    @Test
    void update() {
        Group updatedGroup = mockDb.get(0);
        Group newGroup = new Group(updatedGroup.getId(), null, null, true);

        groupManager.update(
                newGroup,
                Assertions::assertTrue
        );

        Group newGroupUpdated = groupManager.get(newGroup.getId());
        assertEquals("", newGroupUpdated.getName());
        assertEquals("", newGroupUpdated.getPrefix());
        assertFalse(newGroupUpdated.isDefault());

    }

    @Test
    void updateDefaultGroup() {
        Group newDefault = mockDb.get(1);

        groupManager.updateDefaultGroup(
                newDefault,
                Assertions::assertTrue
        );

        for (Group group : groupManager.getValues()) {
            assertEquals(group.getId().equals(newDefault.getId()), group.isDefault());
        }

        groupManager.updateDefaultGroup(
                new Group(),
                Assertions::assertTrue
        );

        for (Group group : groupManager.getValues()) {
            assertEquals(false, group.isDefault());
        }
    }

    @Test
    void getDefaultGroup() {
        assertEquals(Group.DEFAULT, groupManager.getDefaultGroup());

        for (int i = 0; i < mockDb.size(); i++) {
            Group newDefault = mockDb.get(i);

            groupManager.updateDefaultGroup(
                    newDefault,
                    Assertions::assertTrue
            );

            assertEquals(newDefault.getId(), groupManager.getDefaultGroup().getId());
        }

    }
}
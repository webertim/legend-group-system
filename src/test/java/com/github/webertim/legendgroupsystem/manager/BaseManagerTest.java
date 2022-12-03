package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.model.Identifiable;
import com.j256.ormlite.field.DatabaseField;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

class BaseManagerTest extends BaseTest<UUID, BaseManagerTest.TestData> {
    static final int INITIAL_DB_SIZE = 3;

    BaseManagerTest() {
        super(TestData.class);
    }

    static class TestData implements Identifiable<UUID> {

        @DatabaseField(id = true)
        public UUID id;

        @DatabaseField
        public String data;

        public TestData() {
            this.id = UUID.randomUUID();
            this.data = "data";
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestData data)) return false;
            return id.equals(data.id);
        }
    }

    BaseManager<UUID, TestData> baseManager;

    @BeforeEach
    @Order(0)
    @Override
    void setup() throws SQLException {
        super.setup();
        baseManager = new BaseManager<UUID, TestData>(legendGroupSystem, dao) {};
    }

    @BeforeEach
    @Order(1)
    @Override
    void testInitialization() {
        super.testInitialization();
    }

    @Test
    void create() {
        // Create new object.
        baseManager.create(
            new TestData(),
            Assertions::assertTrue
        );

        // Try to create existing object.
        baseManager.create(
            mockDb.get(0),
            Assertions::assertFalse
        );

        Assertions.assertEquals(mockDb.size(), baseManager.getValues().size());
    }

    @Test
    void update() {
        baseManager.update(
                new TestData(),
                Assertions::assertTrue
        );

        baseManager.update(
                mockDb.get(0),
                Assertions::assertTrue
        );

        Assertions.assertEquals(mockDb.size(), baseManager.getValues().size());
    }

    @Test
    void delete() {
        baseManager.delete(
                new TestData(),
                Assertions::assertFalse
        );

        baseManager.delete(
                mockDb.get(0),
                Assertions::assertTrue
        );

        Assertions.assertEquals(mockDb.size(), baseManager.getValues().size());
    }

    @Test
    void insert() {
        baseManager.insert(new TestData());
        baseManager.insert(new TestData());
        baseManager.insert(new TestData());

        Assertions.assertEquals(INITIAL_DB_SIZE + 3, baseManager.getValues().size());
    }

    @Test
    void edit() {
        Assertions.assertThrows(
                AssertionError.class,
                () -> baseManager.edit(mockDb.get(0).getId(), new TestData())
        );

        TestData data = mockDb.get(0);
        baseManager.edit(data.getId(), data);
        Assertions.assertEquals(
                INITIAL_DB_SIZE,
                baseManager.getValues().size()
        );

        String newData = "NewData";
        data.data = newData;
        baseManager.edit(data.getId(), data);
        Assertions.assertEquals(
                newData,
                baseManager.get(data.getId()).data
        );
    }

    @Test
    void remove() {
        baseManager.remove(mockDb.get(0));
        baseManager.remove(mockDb.get(0));
        baseManager.remove(mockDb.get(1));
        baseManager.remove(mockDb.get(1));
        baseManager.remove(mockDb.get(2));
        baseManager.remove(mockDb.get(2));

        Assertions.assertEquals(0, baseManager.getValues().size());
    }

    @Test
    void registerOnChangeListener() {
        AtomicInteger called = new AtomicInteger();
        final String newData = "newData";
        final String updatedData = "updatedData";
        final String removedData = "removedData";

        TestData newTestData = new TestData();
        newTestData.data = newData;

        TestData editTestData = new TestData();
        editTestData.data = updatedData;

        TestData removeTestData = new TestData();
        removeTestData.id = newTestData.getId();
        removeTestData.data = removedData;

        baseManager.registerOnChangeListener(
                (testData, operation) -> {
                    called.getAndIncrement();
                    Assertions.assertEquals(0, 0);
                    switch (operation) {
                        case INSERT -> Assertions.assertEquals(newData, testData.data);
                        case REMOVE -> Assertions.assertEquals(removedData, testData.data);
                        case EDIT -> Assertions.assertEquals(updatedData, testData.data);
                    }
                }
        );

        baseManager.insert(newTestData);
        baseManager.edit(editTestData.getId(), editTestData);
        baseManager.remove(removeTestData);

        Assertions.assertEquals(3, called.get());
    }
}
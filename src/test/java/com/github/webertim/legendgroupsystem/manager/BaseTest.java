package com.github.webertim.legendgroupsystem.manager;

import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainTasks;
import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.model.Identifiable;
import com.j256.ormlite.dao.Dao;
import org.junit.jupiter.api.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class BaseTest<K, V extends Identifiable<K>> {
    int INITIAL_DB_SIZE = 3;
    LegendGroupSystem legendGroupSystem;
    List<V> mockDb;
    List<TaskChainTasks.Task> mockTasks;
    Dao dao;
    Class<V> clazz;

    public BaseTest(Class<V> clazz) {
        this.clazz = clazz;
    }

    @BeforeEach
    @Order(0)
    void setup() throws SQLException {
        mockDb = new ArrayList<>(Arrays
                .stream(new Object[INITIAL_DB_SIZE])
                .map(o -> {
                    try {
                        return clazz.getConstructor().newInstance();
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList());

        mockTasks = new ArrayList<>();

        legendGroupSystem = mock(LegendGroupSystem.class);
        dao = mock(Dao.class);
        TaskChain taskChain = mock(TaskChain.class);

        when(legendGroupSystem.createTaskChain()).then(invocation -> {
            mockTasks = new ArrayList();
            return taskChain;
        });

        when(taskChain.asyncFirst(any(TaskChainTasks.FirstTask.class))).then(invocation -> {
            mockTasks.add((TaskChainTasks.Task) invocation.getArguments()[0]);
            return taskChain;
        });

        when(taskChain.sync(any(TaskChainTasks.Task.class))).then(invocation -> {
            mockTasks.add((TaskChainTasks.Task) invocation.getArguments()[0]);
            return taskChain;
        });

        when(taskChain.syncLast(any(TaskChainTasks.LastTask.class))).then(invocation -> {
            mockTasks.add((TaskChainTasks.Task) invocation.getArguments()[0]);
            return taskChain;
        });

        doAnswer(invocation -> {
            Object r = null;
            for (TaskChainTasks.Task t : mockTasks) {
                r = t.run(r);
            }
            return null;
        }).when(taskChain).execute();

        when(dao.queryForAll()).thenReturn(mockDb);

        when(dao.delete(any(BaseManagerTest.TestData.class))).then(
                invocation -> {
                    BaseManagerTest.TestData data = (BaseManagerTest.TestData) invocation.getArguments()[0];
                    if (mockDb.contains(data)) {
                        mockDb.remove(data);
                        return 1;
                    }
                    return 0;
                }
        );

        when(dao.createOrUpdate(any(clazz))).then(
                invocation -> {
                    V data = (V) invocation.getArguments()[0];
                    mockDb.remove(data);
                    mockDb.add(data);
                    Dao.CreateOrUpdateStatus createOrUpdateStatus = mock(Dao.CreateOrUpdateStatus.class);
                    when(createOrUpdateStatus.getNumLinesChanged()).thenReturn(1);
                    return createOrUpdateStatus;
                }
        );

        when(dao.create(any(clazz))).then(
                invocation -> {
                    V data = (V) invocation.getArguments()[0];
                    if (mockDb.contains(data)) {
                        throw new Exception("");
                    }

                    mockDb.add(data);
                    return 1;
                }
        );
    }

    @Test
    @BeforeEach
    @Order(1)
    @DisplayName("Test if the mocking is successful and the internal list is initialized correctly.")
    void testInitialization() {
        Assertions.assertEquals(INITIAL_DB_SIZE, mockDb.size());
    }
}

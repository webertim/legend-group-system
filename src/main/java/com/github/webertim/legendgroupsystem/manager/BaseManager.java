package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.model.Identifiable;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseManager<T, V extends Identifiable<T>> {
    final HashMap<T, V> dataMap = new HashMap();
    private final Dao<V, T> dataDao;
    private final LegendGroupSystem legendGroupSystem;

    public BaseManager(LegendGroupSystem legendGroupSystem, Dao<V, T> dao) throws SQLException {
        this.dataDao = dao;
        this.legendGroupSystem = legendGroupSystem;

        this.initialize();
    }

    public void initialize() throws SQLException {
        this.dataDao.queryForAll().forEach(this::insert);
    }

    public void create(V data, Consumer<Boolean> finalTask) {
        createSuccessBasedTaskChain(
                () -> this.dataDao.create(data),
                () -> this.insert(data),
                finalTask
        );
    }

    public void update(V data, Consumer<Boolean> finalTask) {
        createSuccessBasedTaskChain(
                () -> this.dataDao.update(data),
                () -> this.insert(data),
                finalTask
        );
    }

    public void delete(V data, Consumer<Boolean> finalTask) {
        createSuccessBasedTaskChain(
                () -> this.dataDao.delete(data),
                () -> this.remove(data),
                finalTask
        );
    }

    void createSuccessBasedTaskChain(
            Callable<Integer> operationCallback,
            Runnable successfulCallback,
            Consumer<Boolean> finalCallback) {
        this.legendGroupSystem.createTaskChain()
                .asyncFirst(() -> tryOperation(operationCallback))
                .sync(success -> ifSuccess(success, successfulCallback))
                .syncLast(success -> finalCallback.accept(success))
                .execute();
    }

    private boolean ifSuccess(boolean success, Runnable callback) {
        if (success) {
            callback.run();
        }

        return success;
    }

    private boolean tryOperation(Callable<Integer> operationCallback) {
        try {
            return operationCallback.call() > 0;
        } catch (Exception e) {
            legendGroupSystem.getLogger().warning(e.getMessage());
            return false;
        }
    }

    void insert(V data) {
        this.dataMap.put(data.getId(), data);
    }

    void remove(V data) {
        this.dataMap.remove(data.getId());
    }

    V get(T dataId) {
        return this.dataMap.get(dataId);
    }

    public Set<T> getIds() {
        return dataMap.keySet();
    }

    public Collection<V> getValues() {
        return dataMap.values();
    }

    Dao<V, T> getDao() {
        return this.dataDao;
    }
}

package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.manager.enums.Operation;
import com.github.webertim.legendgroupsystem.model.Identifiable;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This is an abstract class for managing database bound objects.
 * Because in some cases special operations are required when interacting with the underlying data structure,
 * no direct access is possible. Instead, the most important features of the underlying HashMap are provided with custom
 * methods.
 *
 * If special operations are required, those methods should be overridden. For example the PlayerManager needs to also
 * make an entry in a separate data structure for every player with expiring group rights. This can easily be done
 * by overriding the {@link com.github.webertim.legendgroupsystem.manager.BaseManager#insert(Identifiable identifiable)}
 * method which is also used internally by every method.
 *
 * @param <T>
 * @param <V>
 */
public abstract class BaseManager<T, V extends Identifiable<T>> {
    private final HashMap<T, V> dataMap = new HashMap();
    private final Dao<V, T> dataDao;
    private final List<BiConsumer<V, Operation>> changeListeners = new ArrayList<>();
    protected final LegendGroupSystem legendGroupSystem;

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
                () -> this.dataDao.createOrUpdate(data).getNumLinesChanged(),
                () -> this.update(data.getId(), data),
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

    public void insert(V data) {
        this.dataMap.put(data.getId(), data);
        onChange(data, Operation.INSERT);
    }

    public void update(T id, V data) {
        assert id.equals(data.getId());
        assert this.dataMap.containsKey(data.getId());

        this.dataMap.put(id, data);
        onChange(data, Operation.UPDATE);
    }

    public void remove(V data) {
        this.dataMap.remove(data.getId());
        onChange(data, Operation.REMOVE);
    }

    /**
     * Get resource with specified id.
     * Note: Resource should never be changed directly because change listeners will not be informed.
     *
     * @param dataId The id of the requested resource.
     * @return The managed resource with the provided id or null if no such resource exists.
     */
    public V get(T dataId) {
        return this.dataMap.get(dataId);
    }

    public Set<T> getIds() {
        return dataMap.keySet();
    }

    /**
     * Get all resources managed by this manager.
     * Note: Resources should never be changed directly because change listeners will not be informed.
     *
     * @return Collection of currently managed resources.
     */
    public Collection<V> getValues() {
        return dataMap.values();
    }

    public boolean contains(T id) {
        return dataMap.containsKey(id);
    }

    Dao<V, T> getDao() {
        return this.dataDao;
    }

    private void onChange(V data, Operation operation) {
        changeListeners.forEach(
                listener -> listener.accept(data, operation)
        );
    }

    public void registerOnChangeListener(BiConsumer<V, Operation> listener) {
        this.changeListeners.add(listener);
    }
}


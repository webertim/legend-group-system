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
 * method which is also used internally every time a PlayerInfo object is added.
 *
 * @param <T> Data type of the ID column of the managed data.
 * @param <V> Data type of the managed data.
 */
public abstract class BaseManager<T, V extends Identifiable<T>> {
    private final HashMap<T, V> dataMap = new HashMap<>();
    private final Dao<V, T> dataDao;
    private final List<BiConsumer<V, Operation>> changeListeners = new ArrayList<>();
    protected final LegendGroupSystem legendGroupSystem;


    public BaseManager(LegendGroupSystem legendGroupSystem, Dao<V, T> dao) throws SQLException {
        this.dataDao = dao;
        this.legendGroupSystem = legendGroupSystem;

        this.initialize();
    }

    /**
     * Initialize this manager. Fetches the corresponding table and inserts the data into the local data structure
     * using the internal {@link com.github.webertim.legendgroupsystem.manager.BaseManager#insert(Identifiable i)}
     * method. If additional code needs to be executed when objects are inserted either override this method, if this code
     * only is relevant on plugin start, or override the mentioned insert() method, if the code is relevant for every
     * insertion of data into the local map.
     *
     * @throws SQLException If the table cannot be read from the database.
     */
    protected void initialize() throws SQLException {
        this.dataDao.queryForAll().forEach(this::insert);
    }

    /**
     * Create a data object in the database and - if successful - insert it into the local map.
     * This method also takes a callback which gets passed a boolean based on the success of the database operation.
     *
     * @param data The data to insert into the database and the local map.
     * @param finalTask The callback to execute after the creation and insertion.
     */
    public void create(V data, Consumer<Boolean> finalTask) {
        createSuccessBasedTaskChain(
                () -> this.dataDao.create(data),
                () -> this.insert(data),
                finalTask
        );
    }

    /**
     * Update a data object in the database and - if successful - in the local map.
     * This method also takes a callback which gets passed a boolean based on the success of the database operation.
     *
     * @param data The data to update the database and the local map with (the Id of the object determines the updated object).
     * @param finalTask The callback to execute after the update and edit.
     */
    public void update(V data, Consumer<Boolean> finalTask) {
        createSuccessBasedTaskChain(
                () -> this.dataDao.createOrUpdate(data).getNumLinesChanged(),
                () -> this.edit(data.getId(), data),
                finalTask
        );
    }

    /**
     * Delete a data object in the database and - if successful - in the local map.
     * This method also takes a callback which gets passed a boolean based on the success of the database operation.
     *
     * @param data The data to delete in the database and the local map (the Id of the object determines the deleted object).
     * @param finalTask The callback to execute after the deletion and removal.
     */
    public void delete(V data, Consumer<Boolean> finalTask) {
        createSuccessBasedTaskChain(
                () -> this.dataDao.delete(data),
                () -> this.remove(data),
                finalTask
        );
    }

    /**
     * An internal helper function creating a task chain swapping threads.
     * The first operation is an asynchronously executed operation which returns a boolean based on the success of
     * the operation. This means that Exceptions thrown are caught and represented with a return value of false.
     * Also if the operationCallback returns 0 or less false is returned (because in case of database operations it means
     * no rows have been affected).
     * The second operation is only executed if the first operation was successful, i.e. returned a value > 0 and did not
     * throw an Exception.
     * The last Callback is always executed and receives a boolean representing the success of the first operation
     *
     * @param operationCallback An async operation which should return a value > 0 if successful. Exceptions are caught and lead to a false as return value.
     * @param successfulCallback A sync operation which should only be called if the first operation was successful.
     * @param finalCallback A sync operation which should always be called and receives a boolean representing the success of the first operation.
     */
    void createSuccessBasedTaskChain(
            Callable<Integer> operationCallback,
            Runnable successfulCallback,
            Consumer<Boolean> finalCallback) {
        this.legendGroupSystem.createTaskChain()
                .asyncFirst(() -> tryOperation(operationCallback))
                .sync(success -> ifSuccess(success, successfulCallback))
                .syncLast(finalCallback::accept)
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

    /**
     * Insert a new resource into the internal hash map.
     * This operation notifies all change listeners.
     *
     * @param data The data to insert.
     */
    public void insert(V data) {
        this.dataMap.put(data.getId(), data);
        onChange(data, Operation.INSERT);
    }

    /**
     * Update an existing resource inside the internal hash map. Note that the only difference between this method and
     * {@link com.github.webertim.legendgroupsystem.manager.BaseManager#insert(Identifiable i)} is the way the change
     * listener is informed.
     * This operation notifies all change listeners.
     *
     * @param id The Id of the data to update.
     * @param data The data to update.
     */
    public void edit(T id, V data) {
        assert id.equals(data.getId());

        this.dataMap.put(id, data);
        onChange(data, Operation.EDIT);
    }

    /**
     * Remove a resource from the internal hash map.
     * This operation notifies all change listeners.
     *
     * @param data The data to remove.
     */
    public void remove(V data) {
        this.dataMap.remove(data.getId());
        onChange(data, Operation.REMOVE);
    }

    /**
     * Get resource with specified id.
     * Note: Resource should never be changed directly because change listeners will not be informed.
     *       It is therefore recommended to either modify the resource and insert it again (which takes constant time
     *       and is therefore no performance issue) or create a copy with the same Id and insert it
     *       (which automatically overrides the old entry)
     *
     * @param dataId The id of the requested resource.
     * @return The managed resource with the provided id or null if no such resource exists.
     */
    public V get(T dataId) {
        return this.dataMap.get(dataId);
    }

    /**
     * Get the keys of the internal map
     *
     * @return A set of keys of the internal map.
     */
    public Set<T> getIds() {
        return dataMap.keySet();
    }

    /**
     * Get all resources managed by this manager.
     * Note: Resources should never be changed directly because change listeners will not be informed.
     *       It is therefore recommended to only modify the relevant elements and reinsert them. This takes constant time
     *       per element and makes sure all change listeners are called for every change.
     *
     * @return Collection of currently managed resources.
     */
    public Collection<V> getValues() {
        return dataMap.values();
    }

    /**
     * Checks whether the underlying map contains the provided key.
     *
     * @param id Key to check
     * @return true if an element with the provided Id exists, false otherwise.
     */
    public boolean contains(T id) {
        return dataMap.containsKey(id);
    }

    /**
     * Returns the DAO of this manager. Can be used to create custom queries.
     * Only use this if none of the existing methods (create, update, delete) fits your needs.
     *
     * @return The DAO to perform SQL Statements against the underlying table of this manager.
     */
    Dao<V, T> getDao() {
        return this.dataDao;
    }


    private void onChange(V data, Operation operation) {
        changeListeners.forEach(
                listener -> listener.accept(data, operation)
        );
    }

    /**
     * Registers a listener which gets informed if one of the following methods are called:<br>
     *  - {@link com.github.webertim.legendgroupsystem.manager.BaseManager#insert(Identifiable i)}<br>
     *  - {@link com.github.webertim.legendgroupsystem.manager.BaseManager#edit(Object data, Identifiable i)}<br>
     *  - {@link com.github.webertim.legendgroupsystem.manager.BaseManager#remove(Identifiable i)}<br>
     * <br>
     *  The listener receives the new data point as well as the type of the operation which corresponds to the executed
     *  operation.
     *
     * @param listener The listener to register.
     */
    public void registerOnChangeListener(BiConsumer<V, Operation> listener) {
        this.changeListeners.add(listener);
    }
}


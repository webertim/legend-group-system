package com.github.webertim.legendgroupsystem.model;

/**
 * Interface marking an identifiable class with an Id of Type T.
 *
 * @param <T> The data type of the Id value of the class.
 */
public interface Identifiable<T> {
    /**
     * Get the Id of this object.
     *
     * @return The Id of this object.
     */
    T getId();
}

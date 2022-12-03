package com.github.webertim.legendgroupsystem.manager.enums;

/**
 * Enum representing possible operations performed on a managers internal data.
 * Used to pass the performed operation to the change listeners registered on the manager instances.
 */
public enum Operation {
    REMOVE,
    INSERT,
    EDIT
}

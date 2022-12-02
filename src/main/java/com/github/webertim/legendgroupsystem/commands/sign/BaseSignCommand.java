package com.github.webertim.legendgroupsystem.commands.sign;

import com.github.webertim.legendgroupsystem.commands.BaseCommand;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.SignManager;

/**
 * An abstract class used as a basis by the sign commands. Provides a SignManager.
 */
public abstract class BaseSignCommand extends BaseCommand {
    final SignManager signManager;

    /**
     * Constructor takes a SignManager to provide it to every child command.
     *
     * @param signManager A SignManager instance.
     * @param config The Plugin config.
     */
    public BaseSignCommand(SignManager signManager, BaseConfiguration config) {
        super(config);
        this.signManager = signManager;
    }
}

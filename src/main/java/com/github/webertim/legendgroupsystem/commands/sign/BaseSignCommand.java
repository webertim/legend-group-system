package com.github.webertim.legendgroupsystem.commands.sign;

import com.github.webertim.legendgroupsystem.commands.BaseCommand;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.SignManager;

public abstract class BaseSignCommand extends BaseCommand {
    final SignManager signManager;

    public BaseSignCommand(SignManager signManager, BaseConfiguration config) {
        super(config);
        this.signManager = signManager;
    }
}

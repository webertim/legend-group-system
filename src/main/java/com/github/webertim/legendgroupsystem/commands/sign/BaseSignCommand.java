package com.github.webertim.legendgroupsystem.commands.sign;

import com.github.webertim.legendgroupsystem.commands.BaseCommand;
import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import com.github.webertim.legendgroupsystem.manager.SignManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseSignCommand extends BaseCommand {
    final SignManager signManager;

    public BaseSignCommand(SignManager signManager, BaseConfiguration config) {
        super(config);
        this.signManager = signManager;
    }
}

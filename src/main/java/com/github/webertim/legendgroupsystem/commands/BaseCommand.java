package com.github.webertim.legendgroupsystem.commands;

import com.github.webertim.legendgroupsystem.configuration.BaseConfiguration;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

/**
 * An abstract class representing the basis for every defined command. This class requires an instance of the plugin config
 * and therefore provides a callback creator used in most of the commands when interacting with the manager classes.
 *
 * Also, because commands give feedback it is necessary that every command has access to the config (providing the messages).
 */
public abstract class BaseCommand extends CommandExecutorTabCompleter {

    protected final BaseConfiguration config;

    /**
     * Constructor used by every child class.
     *
     * @param config Plugin config object.
     */
    public BaseCommand(BaseConfiguration config) {
        this.config = config;
    }

    /**
     * A method which is used to create a callback sending messages defined in the config based on the success of an operation.
     * This method is by commands for asynchronous database operations which take a callback to react to the outcome of the
     * database interaction.
     *
     * @param successKey The key used in the config for a successful message
     * @param errorKey The key used in the config for an unsuccessful message
     * @param sender The receiver of the message
     * @return The callback function taking a boolean representing the success of the operation.
     */
    protected Consumer<Boolean> getSuccessCallback(String successKey, String errorKey, CommandSender sender) {
        return success -> {
            String message = success ?
                    this.config.getMessage(successKey) :
                    this.config.getMessage(errorKey);

            sender.sendMessage(message);
        };
    }
}

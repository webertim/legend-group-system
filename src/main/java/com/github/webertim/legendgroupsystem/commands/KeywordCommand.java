package com.github.webertim.legendgroupsystem.commands;

import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal command "framework" for hierarchical command definitions.
 * Provides automatic tab completion of sub commands.
 */
public class KeywordCommand implements CommandExecutor, TabCompleter {

    private final String keyword;
    private CommandExecutor executor;
    private TabCompleter completer;
    private Map<String, KeywordCommand> subCommands;

    /**
     * Create a command containing multiple subcommands also defined as
     * {@link com.github.webertim.legendgroupsystem.commands.KeywordCommand}
     *
     * @param keyword The keyword of this (sub)command
     * @param subCommands A list of subcommand definitions.
     */
    public KeywordCommand(String keyword, KeywordCommand[] subCommands){
        this.keyword = keyword;
        this.subCommands = new HashMap<>();
        for (KeywordCommand subCommand : subCommands) {
            this.subCommands.put(subCommand.getKeyword(), subCommand);
        }
    }

    /**
     * Create a command with a simple CommandExecutor. This is typically passed as a last layer and contains
     * the actual command execution definition for the subcommand.
     *
     * @param keyword The keyword of this (sub)command
     * @param executor The execution definition for this (sub)command.
     */
    public KeywordCommand(String keyword, CommandExecutor executor) {
        this.keyword = keyword;
        this.executor = executor;
    }

    /**
     * Create a command with a simple CommandExecutor and a TabCompleter.
     * This is typically passed as a last layer and contains the actual command execution definition for the subcommand
     * as well as the tab completion definition.
     *
     * @param keyword The keyword of this (sub)command
     * @param executor The execution definition for this (sub)command.
     * @param completer The tab completion definition for this (sub)command.
     */
    public KeywordCommand(String keyword, CommandExecutor executor, TabCompleter completer) {
        this(keyword, executor);
        this.completer = completer;
    }

    /**
     * Create a command with a class containing both the CommandExecutor and the TabCompleter.
     * This is typically passed as a last layer and contains the actual command execution definition for the subcommand
     * as well as the tab completion definition.
     *
     * @param keyword The keyword of this (sub)command
     * @param executorListener A class implementing both the execution definition and the tab completion definition.
     *                         This is a custom abstract class because the constructors would otherwise clash.
     */
    public KeywordCommand(String keyword, CommandExecutorTabCompleter executorListener) {
        this(keyword, executorListener, executorListener);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (this.executor != null) {
            return executor.onCommand(commandSender, command, label, args);
        }

        if (args.length < 1) {
            return false;
        }

        KeywordCommand subCommand = this.subCommands.get(args[0]);

        if (subCommand == null) {
            return false;
        }

        return subCommand.onCommand(commandSender, command, label, subArray(args));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return null;
        }

        if (this.executor != null) {
            if (this.completer != null) {
                return this.completer.onTabComplete(commandSender, command, label, subArray(args));
            } else {
                return null;
            }
        }

        String arg = args[0];

        if (arg.isBlank()) {
            return this.subCommands.keySet().stream().toList();
        } else {
            KeywordCommand subCommand = this.subCommands.get(arg);
            if (subCommand == null) {
                return this.subCommands
                        .keySet()
                        .stream()
                        .filter(s -> s.startsWith(arg))
                        .toList();
            }

            return subCommand.onTabComplete(commandSender, command, label, subArray(args));
        }
    }

    /**
     * Register the defined hierarchical command structure. Because only the top level command must be registered,
     * this method should only be called on the top level KeywordCommand instance.
     *
     * @param plugin The plugin providing the defined command.
     */
    public void register(JavaPlugin plugin) {
        PluginCommand command = plugin.getCommand(this.keyword);

        if (command == null) {
            return;
        }

        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    public String getKeyword() {
        return keyword;
    }

    /**
     * Helper function returning an array with all but the first element of the provided array.
     *
     * @param args Array which should be shortened.
     * @return The passed array without the first element.
     */
    private String[] subArray(@NotNull String @NotNull [] args) {
        return Arrays.stream(args).toList().subList(1, args.length).toArray(String[]::new);
    }
}

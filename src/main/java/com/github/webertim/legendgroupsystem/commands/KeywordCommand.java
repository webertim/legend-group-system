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

    public KeywordCommand(String keyword, KeywordCommand[] subCommands){
        this.keyword = keyword;
        this.subCommands = new HashMap<>();
        for (KeywordCommand subCommand : subCommands) {
            this.subCommands.put(subCommand.getKeyword(), subCommand);
        }
    }

    public KeywordCommand(String keyword, CommandExecutor executor) {
        this.keyword = keyword;
        this.executor = executor;
    }

    public KeywordCommand(String keyword, CommandExecutor executor, TabCompleter completer) {
        this(keyword, executor);
        this.completer = completer;
    }

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

    private String[] subArray(@NotNull String @NotNull [] args) {
        return Arrays.stream(args).toList().subList(1, args.length).toArray(String[]::new);
    }
}

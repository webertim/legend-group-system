package com.github.webertim.legendgroupsystem.configuration;

import com.github.webertim.legendgroupsystem.database.DatabaseOptions;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class BaseConfiguration {
    private static final String DATABASE_OPTIONS = "database";
    private static final String USER_OPTION = "user";
    private static final String PASSWORD_OPTION = "password";
    private static final String URL_OPTION = "url";
    private static final String NAME_OPTION = "name";
    private static final String I18N_OPTIONS = "i18n";
    private static final String LANGUAGE_OPTIONS = "languages";
    private static final String DEFAULT_LANGUAGE_OPTION = "defaultLanguage";


    Plugin plugin;
    FileConfiguration configFile;

    public BaseConfiguration(Plugin plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();

        this.configFile = plugin.getConfig();
        this.configFile.options().copyDefaults(true);

        this.plugin.saveConfig();
    }

    public DatabaseOptions getDatabaseOptions() {
        return new DatabaseOptions(
                configFile.getString(buildOptionPath(
                        DATABASE_OPTIONS,
                        URL_OPTION
                )),
                configFile.getString(buildOptionPath(
                        DATABASE_OPTIONS,
                        NAME_OPTION
                )),
                configFile.getString(buildOptionPath(
                        DATABASE_OPTIONS,
                        USER_OPTION
                )),
                configFile.getString(buildOptionPath(
                        DATABASE_OPTIONS,
                        PASSWORD_OPTION
                ))
        );
    }

    public String getMessage(String languageKey, String messageKey) {
        return this.configFile.getString(buildOptionPath(
            I18N_OPTIONS, LANGUAGE_OPTIONS, languageKey, messageKey
        ));
    }

    public String getMessage(String messageKey) {
        return getMessage(
            this.configFile.getString(buildOptionPath(
                I18N_OPTIONS, DEFAULT_LANGUAGE_OPTION
            )),
            messageKey
        );
    }

    private String buildOptionPath(String... options) {
        return String.join(".", options);
    }
}

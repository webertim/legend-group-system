package com.github.webertim.legendgroupsystem.configuration;

import com.github.webertim.legendgroupsystem.database.DatabaseOptions;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Class for managing/using the default plugin configuration file.
 * Currently, only used for reading database config and internationalized messages.
 */
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

    /**
     * Creates a new BaseConfiguration instance. Thereby reading the config file, setting missing default values and
     * saving potential changes.
     *
     * @param plugin The Plugin instance.
     */
    public BaseConfiguration(Plugin plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();

        this.configFile = plugin.getConfig();
        this.configFile.options().copyDefaults(true);

        this.plugin.saveConfig();
    }

    /**
     * Get the provided database configuration containing information about the url, name, user and password to use.
     *
     * @return All necessary database information packed into an object.
     */
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

    /**
     * Get a message from the configuration based on a languageKey as well as on a message key.
     * The language key has to match one of the used keys in the configuration. Otherwise, an empty message is returned.
     *
     * @param languageKey The language key representing one of the defined languages of the config.
     * @param messageKey The message key representing one of the defined messages of the config.
     * @return Returns the matching message or an empty string.
     */
    public String getMessage(String languageKey, String messageKey) {
        return this.configFile.getString(buildOptionPath(
            I18N_OPTIONS, LANGUAGE_OPTIONS, languageKey, messageKey
        ), "");
    }

    /**
     * Get a message from the configuration based on a message key as well as the default language.
     * The default language key has to match one of the used keys in the configuration. Otherwise, an empty message is returned.
     *
     * @param messageKey The message key representing one of the defined messages of the config.
     * @return Returns the matching message or an empty string.
     */
    public String getMessage(String messageKey) {
        return getMessage(
            this.configFile.getString(buildOptionPath(
                I18N_OPTIONS, DEFAULT_LANGUAGE_OPTION
            )),
            messageKey
        );
    }

    /**
     * Helper method to build an option path based on a list of strings.
     *
     * @param options List of strings representing the path to read.
     * @return The joined list of strings.
     */
    private String buildOptionPath(String... options) {
        return String.join(".", options);
    }
}

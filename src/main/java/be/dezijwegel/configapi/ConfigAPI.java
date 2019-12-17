package be.dezijwegel.configapi;

import be.dezijwegel.configapi.utility.Logger;
import jdk.internal.jline.internal.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class ConfigAPI{

    // Some terminology for the reader:
    //      Default config: The config file which is provided by the plugin (default)
    //      Live config: The config file on the server which server admins can edit

    // Expansion ideas:
    //      allow message customisation
    //      allow writing to the live config (ex: a new option) which should include its comments

    private JavaPlugin plugin;              // An instance of which plugin's config files are being managed
    private String fileName;                // The name of the config file being managed

    private Map<String, Object> contents;   // Will contain the loaded values

    private Settings settings;              // An instance that handles all settings



    /**
     * Creates an instance of ConfigAPI which will manage your config files. Multiple files will require multiple instances
     * Some terminology used in the documentation:
     *      Default config: The config file which is provided by the plugin (default)
     *      Live config: The config file on the server which server admins can edit
     * @param fileName the name of the file you wish (config.yml, lang.yml, etc.)
     * @param plugin your plugin
     */
    public ConfigAPI(String fileName, JavaPlugin plugin) {
        this.plugin = plugin;           // Keep track of an instance of this plugin
        this.fileName = fileName;       // Store the filename of this config file

        contents = new HashMap<String, Object>();       // Create a Map that will contain all paths and values
        settings = new Settings();                      // Initialize the default settings

        if ( settings.getAutoLoadValues() )             // Check if auto loading is enabled
        {
            reloadContents();                           // Load contents into memory
        }

        copyDefConfigIfNeeded();    // Checks if a live config is available, if not: a new default config file is generated
    }



    // ------------------- //
    // Settings management //
    // ------------------- //



    /**
     * Get the object that handles all ConfigAPI settings
     * @return the Settings object
     */
    public Settings getSettings()
    {
        return settings;
    }


    /**
     * Overwrite the used settings
     * @param settings the new settings
     */
    public void setSettings( Settings settings )
    {
        this.settings = settings;
    }



    // ------------------- //
    // Config file loading //
    // ------------------- //



    /**
     * Get a FileConfiguration containing all live configuration values
     * @return a YamlConfiguration, created from the live config file
     */
    public YamlConfiguration getLiveConfiguration()
    {
        File file = new File(plugin.getDataFolder(), fileName);     // Get the file from the plugin's datafolder
        return YamlConfiguration.loadConfiguration(file);           // Create a YAMLConfiguration from the found file
    }


    /**
     * Create a FileConfiguration of the default config file within the resources folder of the jar file
     * @return YamlConfiguration
     */
    public YamlConfiguration getDefaultConfiguration()
    {
        YamlConfiguration fileConfig = new YamlConfiguration();

        Reader defaultStream;
        try {
            defaultStream = new InputStreamReader(plugin.getResource(fileName), "UTF8");
            if (defaultStream != null) {
                fileConfig = YamlConfiguration.loadConfiguration(defaultStream);
            }
        } catch (Exception ex) {
            if (settings.getDoDebugLogging())
            {
                Logger.log("An error occurred while loading the default config values (see below)");
                Logger.log(ex.getMessage());
            }
        }

        return fileConfig;
    }


    // ------------------- //
    // Loading into memory //
    // ------------------- //



    /**
     * Load all values from both provided configurations into memory
     * The previous contents will be removed from memory
     * @param liveConfig should be a YamlConfiguration of the config on the server
     * @param defaultConfig should be a YamlConfiguration of the default config values. If null, no default values will be loaded. If not null, there will be no missing options
     */
    private void loadFromConfigurations(YamlConfiguration liveConfig, @Nullable YamlConfiguration defaultConfig)
    {
        // If the default config should not be considered (for missing options): only load from the live values

        if (defaultConfig == null)
        {
            loadFromConfigurations(liveConfig);
            return;
        }

        // Empty the contents

        contents = new HashMap<String, Object>();

        // Load the new contents

        for (String path : defaultConfig.getKeys(true))
        {
            if ( ! defaultConfig.isConfigurationSection( path ))
            {
                if ( liveConfig.contains(path) )
                {
                    contents.put(path, liveConfig.get(path) );
                }
                else
                {
                    contents.put(path, defaultConfig.get(path));
                }
            }
        }
    }


    /**
     * Load all values from the provided configuration into memory
     * Earlier loaded values will be deleted
     * @param liveConfig should be a YamlConfiguration of the config on the server
     */
    private void loadFromConfigurations(YamlConfiguration liveConfig)
    {

        // Reset contents
        contents = new HashMap<String, Object>();

        // Load new contents
        for (String path : liveConfig.getKeys(true))
        {
            if ( ! liveConfig.isConfigurationSection( path ))
            {
                contents.put( path, liveConfig.get( path ) );
            }
        }
    }


    /**
     * (re)Load all values from the live config
     * If loadDefaults is enabled, default values will be used wherever the live config is missing options
     * When reportMissingOptions is enabled:     missing options will be reported to the console
     * When reportRedundantOptions is enabled:   redundant options will be reported to the console
     * When reportNewConfig is enabled:          creating a new config will be reported to the console (meaning: when no live config exists yet)
     */
    public void reloadContents() {

        copyDefConfigIfNeeded();        // Copy a new config if none exists yet

        YamlConfiguration liveConfiguration;        // Live config values
        YamlConfiguration defConfiguration = null;  // Default config values

        // Get the live config
        liveConfiguration = getLiveConfiguration();

        // If enabled: add the default values to the live configuration where options are missing
        if ( settings.getLoadDefaults() ) {
            defConfiguration = getDefaultConfiguration();
        }

        if (settings.getReportMissingOptions())
            reportMissingOptions();

        loadFromConfigurations( liveConfiguration, defConfiguration);

    }



    // --------------- //
    // File management //
    // --------------- //



    /**
     * This method will reset the live config to the default config
     * ALL LIVE CONFIG CONTENTS WILL BE OVERWRITTEN!
     * If you only want to copy the default config if no live config is present, please use ConfigAPI#copyDefConfigIfNeeded()
     */
    public void forceDefaultConfig()
    {
        if (settings.getDoDebugLogging())
            Logger.log("Forcing default config! The live config will be overwritten");

        if (settings.getReportNewConfig())
            Logger.sendToConsole("Forcing a fresh " + fileName + " ...", plugin);

        plugin.saveResource(fileName, true);
    }


    /**
     * Checks whether a live config exists
     * If it does not exist, the default contents are copied and a message is sent to the console
     * If you want to force-copy the default config to the server, please use ConfigAPI#forceDefaultConfig()
     */
    public void copyDefConfigIfNeeded() {

        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists())
        {
            if (settings.getReportNewConfig())
                Logger.sendToConsole("Copying a new " + fileName + " ...", plugin);

            plugin.saveResource(fileName, false);       // saves if the file does not exist but will not overwrite

            //Make sure the Configuration is up to date. Otherwise missing options may be reported!
            //this.configuration = YamlConfiguration.loadConfiguration(file);
        }
    }



    // --------------- //
    // Missing options //
    // --------------- //



    /**
     * This option will find all missing options and provide the default value for each missing option in a Map
     * Missing options will NOT be reported to the console, even if enabled
     * To report missing options to the console, please use ConfigAPI#reportMissingOptions()
     * @return A Map<String, Object> with all Strings being paths of missing options and the Object being its default value
     */
    public Map<String, Object> getMissingOptions()
    {
        Map<String, Object> missingOptions = new HashMap<String, Object>();

        // Get the current config on the server

        YamlConfiguration liveConfig = getLiveConfiguration();

        // Get the missing configuration options that are not configuration sections

        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(plugin.getResource(fileName), "UTF8");
        } catch (UnsupportedEncodingException ex) {}

        if (defConfigStream != null) {

            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);

            for (String path : defConfig.getKeys(true))
            {
                if ( ! defConfig.isConfigurationSection(path) )
                {
                    if ( !liveConfig.contains(path) ) {
                        missingOptions.put(path, defConfig.get( path ));
                    }
                }
            }

        }

        return missingOptions;
    }


    /**
     * Compare the default file with the one on the server and report every missing option
     */
    public void reportMissingOptions()
    {

        Map<String, Object> missingOptions = getMissingOptions();

        if (missingOptions.size() > 0)
        {

            if (missingOptions.size() == 1)
                Logger.sendToConsole(ChatColor.RED + "A missing option has been found in " + fileName + "!", plugin);
            else
                Logger.sendToConsole("" + ChatColor.RED + missingOptions.size() + " Missing options have been found in " + fileName + "!", plugin);

            Logger.sendToConsole("" + ChatColor.RED + "Please add the missing option(s) manually or delete this file and perform a reload (/bs reload)", plugin);
            Logger.sendToConsole("" + ChatColor.RED + "The default values will be used until then", plugin);

            YamlConfiguration defaultConfig = getDefaultConfiguration();

            for (Map.Entry<String, Object> entry : missingOptions.entrySet())
            {
                String path = entry.getKey();

                // Handle value
                Object value = entry.getValue();

                // Change formatting if the setting is a String
                if (value instanceof String)
                    value = "\"" + value + "\"";


                // Handle path
                String[] sections = path.split("\\.");  // Split the path in its sections
                path = "";                                     // Reset the path variable

                for (int i = sections.length-1; i > 0; i--)    // Loop each subsection in reversed order
                {
                    String part = sections[i];
                    path += "'" + part + "'" + " in section ";             // Print each section in reversed order
                }
                path += "'" + sections[0] + "'";                           // Add the actual setting name

                // Send message
                Logger.sendToConsole("" + ChatColor.DARK_RED + "Missing option: " + path + " with default value: " + value, plugin);
            }
        } else {
            Logger.sendToConsole("No missing options were found in " + fileName + "!", plugin);
        }
    }

}
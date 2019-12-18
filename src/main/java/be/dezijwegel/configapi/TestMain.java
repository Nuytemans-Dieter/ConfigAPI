package be.dezijwegel.configapi;

import be.dezijwegel.configapi.utility.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TestMain extends JavaPlugin {

    @Override
    public void onEnable()
    {
        /*
        checkMissingOptions();

        checkRedundantOptions();

        checkReportNewConfig();

        checkColor();

        checkAutoLoadValues();

        checkLoadDefaults();

        
         */

    }

    public void checkMissingOptions()
    {
        Logger.log("CHECKING MISSING OPTIONS SETTING");
        Logger.log("-----");
        Logger.log("Output: 2x missing options reported");
        Logger.log("-----");
        ConfigAPI config = new ConfigAPI("exampleConfig.yml", this);
        config.reportMissingOptions();
        Logger.log("-----");

        Logger.log("-----");
        Logger.log("Output: None");
        Logger.log("-----");
        Settings settings = new Settings();
        settings.setReportMissingOptions(false);
        config = new ConfigAPI("exampleConfig.yml", settings, this);
        Logger.log("-----");
    }


    public void checkRedundantOptions()
    {
        Logger.log("CHECKING REDUNDANT OPTIONS SETTING");
        Logger.log("-----");
        Logger.log("Output: 2x redundant options reported");
        Logger.log("-----");
        Settings settings = new Settings();
        settings.setReportRedundantOptions(true);
        ConfigAPI config = new ConfigAPI("exampleConfig.yml", settings, this);
        config.reportMissingOptions();
        Logger.log("-----");

        Logger.log("-----");
        Logger.log("Output: None");
        Logger.log("-----");
        settings = new Settings();
        settings.setReportRedundantOptions(false);
        config = new ConfigAPI("exampleConfig.yml", settings, this);
        Logger.log("-----");
    }


    public void checkReportNewConfig()
    {
        Logger.log("CHECKING NEW CONFIG REPORT");
        Logger.log("-----");
        Logger.log("Output: Report new config");
        Logger.log("-----");
        Settings settings = new Settings();
        settings.setReportNewConfig(true);
        ConfigAPI config = new ConfigAPI("exampleConfig.yml", settings, this);
        config.forceDefaultConfig();
        Logger.log("-----");

        Logger.log("-----");
        Logger.log("Output: None");
        Logger.log("-----");
        settings = new Settings();
        settings.setReportNewConfig(false);
        config = new ConfigAPI("exampleConfig.yml", settings, this);
        config.forceDefaultConfig();
        Logger.log("-----");
    }


    public void checkColor()
    {
        Logger.log("CHECKING TEXT COLORING");
        Logger.log("-----");
        Logger.log("Output: Coloured (default)");
        Logger.log("-----");
        ConfigAPI config = new ConfigAPI("exampleConfig.yml", this);
        Logger.log("-----");

        Logger.log("-----");
        Logger.log("Output: Not coloured (set)");
        Logger.log("-----");
        Settings settings = new Settings();
        settings.setUseColors(false);
        config = new ConfigAPI("exampleConfig.yml", settings, this);
        Logger.log("-----");

        Logger.log("-----");
        Logger.log("Output: Coloured (set)");
        Logger.log("-----");
        settings = new Settings();
        settings.setUseColors(true);
        config = new ConfigAPI("exampleConfig.yml", settings, this);
        Logger.log("-----");
    }


    public void checkAutoLoadValues()
    {
        Logger.log("CHECKING AUTO LOAD VALUES");
        Logger.log("-----");
        Logger.log("Output: true (auto load values)");
        Logger.log("-----");
        Settings settings = new Settings();
        ConfigAPI config = new ConfigAPI("exampleConfig.yml", settings, this);
        Logger.log(config.getObject("affirm").toString());
        Logger.log("-----");

        Logger.log("-----");
        Logger.log("Output: false (no auto load)");
        Logger.log("-----");
        settings = new Settings();
        settings.setAutoLoadValues(false);
        config = new ConfigAPI("exampleConfig.yml", settings, this);
        Logger.log(config.getObject("affirm").toString() == null? "This works correctly" : "This does not work as expected");
        Logger.log("-----");
    }


    public void checkLoadDefaults()
    {
        Logger.log("CHECKING LOAD DEFAULTS");
        Logger.log("Run this seperate from the other tests!");
        Logger.log("Delete field 'affirm' in the config before running this code");
        Logger.log("-----");
        Logger.log("Output: true (load default)");
        Logger.log("-----");
        Settings settings = new Settings();
        ConfigAPI config = new ConfigAPI("exampleConfig.yml", settings, this);
        Logger.log(config.getObject("affirm").toString());
        Logger.log("-----");

        Logger.log("-----");
        Logger.log("Output: false (don't load default)");
        Logger.log("-----");
        settings = new Settings();
        settings.setLoadDefaults(false);
        config = new ConfigAPI("exampleConfig.yml", settings, this);
        Logger.log(config.getObject("affirm") == null ? "This works correctly" : "This does not work as expected");
        Logger.log("-----");
    }

}

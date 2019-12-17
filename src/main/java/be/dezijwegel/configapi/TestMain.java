package be.dezijwegel.configapi;

import be.dezijwegel.configapi.utility.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TestMain extends JavaPlugin {

    @Override
    public void onEnable()
    {
        ConfigAPI config = new ConfigAPI("exampleConfig.yml", this);
        config.reportMissingOptions();
    }

}

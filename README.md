# SpigotConfigAPI
 A config file API created for Spigot plugins and only supports YAML files.
 This API is still a work in progress but will have several features:
 - Allow the developer to focus on plugin functionality instead of config files
 - Load the values from the default config file where the file on the server misses a value
   - Automatically `replace missing options`
   - Useful when adding new features in a new update: The config file won't have to be reset!
 - `Report missing options` to the console
 - Forcefully copy a config file from the plugin's resources folder in the jar-file to the server
 - `Copy a config file from jar to the server` if no file exists yet

And above all, a [complete and clear documentation is provided through the wiki](https://github.com/Nuytemans-Dieter/ConfigAPI/wiki)!

![ConfigAPI banner](https://github.com/Nuytemans-Dieter/ConfigAPI/blob/master/visuals/banner.png?raw=true)
# ConfigAPI
Looking for the wiki? [Click here](https://github.com/Nuytemans-Dieter/ConfigAPI/wiki)!

 This is a config file library (technically not an API) created for Spigot plugins. It should be compatible with Spigot 1.12 and newer but was programmed against the latest version of Spigot 1.15. Compatibility with versions prior to Spigot 1.12 is likely but has not been tested.
 The first release version is now available in [the versions folder](https://github.com/Nuytemans-Dieter/ConfigAPI/tree/master/versions). There may be future updates but they will be few and mainly focused on maintaining compatibility through Spigot versions.
 - Allow the developer to focus on plugin functionality instead of config files
 - Easily work with `several config files`
 - Load the values from the default config file where the file on the server misses a value
   - Automatically `replace missing options`
   - Useful when adding new features in a new update: The config file won't have to be reset!
 - `Report missing options` to the console
 - Forcefully `copy a config` file from the plugin's resources folder in the jar-file to the server
 - `Copy a config file from jar to the server` if no file exists yet

And above all, a [complete and clear documentation is provided through the wiki](https://github.com/Nuytemans-Dieter/ConfigAPI/wiki)!

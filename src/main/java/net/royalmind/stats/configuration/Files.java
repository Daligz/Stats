package net.royalmind.stats.configuration;

import org.bukkit.plugin.java.JavaPlugin;

public class Files {

    private final JavaPlugin plugin;

    private ConfigurationFile config;

    public Files(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.loadDirectory();
    }

    private void loadDirectory() {
        this.config = new ConfigurationFile(this.plugin, "config");
    }

    public ConfigurationFile getConfig() {
        return config;
    }
}

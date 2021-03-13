package net.royalmind.stats.configuration;

import org.bukkit.plugin.java.JavaPlugin;

public class Files {

    private final JavaPlugin plugin;

    private ConfigurationFile config;
    private ConfigurationFile configLeaderboard;

    public Files(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.loadDirectory();
    }

    public long reload() {
        final long currentTimeMillis = System.currentTimeMillis();
        loadDirectory();
        loadBefore();
        return (System.currentTimeMillis() - currentTimeMillis);
    }

    private void loadDirectory() {
        this.config = new ConfigurationFile(this.plugin, "config");
    }

    public void loadBefore() {
        this.configLeaderboard = new ConfigurationFile(this.plugin, "leaderboard");
    }

    public ConfigurationFile getConfig() {
        return config;
    }

    public ConfigurationFile getConfigLeaderboard() {
        return configLeaderboard;
    }
}

package net.royalmind.stats.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigurationFile {

    private JavaPlugin plugin;

    private final String name;
    private File file;
    private FileConfiguration fileConfiguration;

    public ConfigurationFile(final JavaPlugin plugin, final String name) {
        this.plugin = plugin;
        this.name = name + ".yml";
        this.load();
    }

    private void load() {
        this.file = new File(this.plugin.getDataFolder(), this.name);
        if (!(this.file.exists())) {
            file.getParentFile().mkdirs();
            this.plugin.saveResource(this.name, false);
        }
        this.fileConfiguration = new YamlConfiguration();
        try {
            this.fileConfiguration.load(this.file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void set(final String path, final Object object) {
        this.fileConfiguration.set(path, object);
        try {
            this.fileConfiguration.save(this.file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public File getFile() {
        return file;
    }
}

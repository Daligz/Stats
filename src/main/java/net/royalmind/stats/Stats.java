package net.royalmind.stats;

import net.royalmind.stats.configuration.Files;
import net.royalmind.stats.data.containers.stats.StatsContainerImpl;
import org.bukkit.plugin.java.JavaPlugin;

public final class Stats extends JavaPlugin {

    private Files files;

    //Containers
    private StatsContainerImpl statsContainer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.files = new Files(this);
        this.statsContainer = new StatsContainerImpl();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

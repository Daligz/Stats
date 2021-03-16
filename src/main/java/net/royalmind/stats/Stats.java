package net.royalmind.stats;

import net.royalmind.stats.commands.StatsCommand;
import net.royalmind.stats.configuration.Files;
import net.royalmind.stats.data.DataSource;
import net.royalmind.stats.data.containers.leaderboards.LeaderboardContainerImpl;
import net.royalmind.stats.data.containers.stats.StatsContainerImpl;
import net.royalmind.stats.data.containers.threads.ThreadsContainerImpl;
import net.royalmind.stats.data.containers.top.TopsContainerImpl;
import net.royalmind.stats.handlers.PlayerDataHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Stats extends JavaPlugin {

    private Files files;
    private DataSource dataSource;

    //Containers
    private StatsContainerImpl statsContainer;
    private ThreadsContainerImpl threadsContainer;
    private LeaderboardContainerImpl leaderboardContainer;
    private TopsContainerImpl topsContainer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        final JavaPlugin instance = this;
        this.files = new Files(this);
        this.dataSource = new DataSource(this);
        this.threadsContainer = new ThreadsContainerImpl();
        this.statsContainer = new StatsContainerImpl();
        this.topsContainer = new TopsContainerImpl(this, this.dataSource,
                this.files.getConfig().getFileConfiguration(), this.statsContainer);
        registerEvents();
        registerCommands();
        new BukkitRunnable() {
            @Override
            public void run() {
                files.loadBefore();
                leaderboardContainer = new LeaderboardContainerImpl(files.getConfigLeaderboard(), topsContainer, instance);
            }
        }.runTaskLater(this, 5L);
    }

    private void registerEvents() {
        final PluginManager pluginManager = getServer().getPluginManager();
        final FileConfiguration config = this.files.getConfig().getFileConfiguration();
        pluginManager.registerEvents(new PlayerDataHandler(this.dataSource, this.statsContainer,
                this.threadsContainer, config, this), this);
    }

    private void registerCommands() {
        getCommand("rstats").setExecutor(new StatsCommand(this.files, this.leaderboardContainer));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.dataSource.close();
    }
}

package net.royalmind.stats.handlers;

import net.royalmind.stats.data.DataSource;
import net.royalmind.stats.data.containers.stats.StatsContainerImpl;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PlayerDataHandler implements Listener {

    private DataSource dataSource;
    private StatsContainerImpl statsContainer;
    private JavaPlugin plugin;
    private FileConfiguration config;

    public PlayerDataHandler(final DataSource dataSource, final StatsContainerImpl statsContainer,
                             final FileConfiguration config, final JavaPlugin plugin) {
        this.dataSource = dataSource;
        this.statsContainer = statsContainer;
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        load(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangeWorld(final PlayerChangedWorldEvent event) {
        load(event.getPlayer());
    }

    private void load(final Player player) {
        final List<String> worlds = this.config.getStringList("Enable-worlds");
        this.statsContainer.close(player, this.dataSource, this.plugin);
        this.statsContainer.loadData(player, this.dataSource, worlds, this.plugin);
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        this.statsContainer.addDeath(event.getEntity().getPlayer());
    }

    @EventHandler
    public void onPlayerKill(final PlayerDeathEvent event) {
        final Player killer = event.getEntity().getKiller();
        if (killer == null || killer == event.getEntity().getPlayer()) return;
        this.statsContainer.addKill(killer);
    }

    @EventHandler
    public void onPlayerGetKillStreak(final PlayerDeathEvent event) {
        final Player killer = event.getEntity().getKiller();
        final Player victim = event.getEntity().getPlayer();
        if (killer == null || killer == victim) return;
        this.statsContainer.updateBestKillStreak(killer, false);
        this.statsContainer.updateBestKillStreak(victim, true);
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event) {
        this.statsContainer.close(event.getPlayer(), this.dataSource, this.plugin);
    }

    @EventHandler
    public void onWorldLoad(final WorldLoadEvent event) {
        this.statsContainer.loadWorld(event.getWorld(), this.dataSource, this.plugin);
    }
}

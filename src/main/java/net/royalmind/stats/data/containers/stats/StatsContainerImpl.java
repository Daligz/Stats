package net.royalmind.stats.data.containers.stats;

import net.royalmind.stats.data.DataSource;
import net.royalmind.stats.data.containers.AbstractDataMap;
import net.royalmind.stats.data.containers.threads.ThreadsContainerImpl;
import net.royalmind.stats.data.containers.threads.ThreadsDataContainer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

public class StatsContainerImpl extends AbstractDataMap<UUID, StatsDataContainer> {

    public StatsContainerImpl() { }

    private void loadData(final Player player, final DataSource dataSource, final ArrayList<String> worlds, final JavaPlugin plugin) {
        if (!(worlds.contains(player.getWorld().getName()))) {
            remove(player.getUniqueId());
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                dataSource.execute(conn -> {
                    String query = "CALL addPlayer(?, ?, ?);";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, player.getName());
                    statement.setString(2, player.getUniqueId().toString());
                    statement.setString(3, player.getLocation().getWorld().getName());
                    statement.execute();

                    query = "CALL getPlayerData(?, ?, ?);";
                    statement = conn.prepareStatement(query);
                    statement.setString(1, "*");
                    statement.setString(2, player.getUniqueId().toString());
                    statement.setString(2, player.getLocation().getWorld().getName());
                    final ResultSet resultSet = statement.executeQuery();

                    query = "CALL getWorldByID(?)";
                    statement = conn.prepareStatement(query);
                    statement.setInt(1, resultSet.getInt("idWorld"));
                    final String worldName = statement.executeQuery().getString("name");

                    set(
                            player.getUniqueId(),
                            new StatsDataContainer(
                                    player.getUniqueId(),
                                    resultSet.getInt("kills"),
                                    resultSet.getInt("deaths"),
                                    resultSet.getInt("bestKillStreak"),
                                    worldName
                            )
                    );
                    return null;
                });
            }
        }.runTaskAsynchronously(plugin);
    }

    private void sendUpdateToDatabase(final Player player, final DataSource dataSource, final JavaPlugin plugin) {
        final UUID uniqueId = player.getUniqueId();
        final StatsDataContainer dataContainer = get(uniqueId);
        if (dataContainer == null) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                dataSource.execute(conn -> {
                    final String query = "CALL updatePlayerData(?, ?, ?, ?, ?);";
                    final PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, uniqueId.toString());
                    statement.setInt(2, dataContainer.getKills());
                    statement.setInt(3, dataContainer.getDeaths());
                    statement.setInt(4, dataContainer.getBestKillStreak());
                    statement.setString(5, dataContainer.getWorld());
                    statement.execute();
                    return null;
                });
            }
        }.runTaskAsynchronously(plugin);
    }

    public void close(final Player player, final DataSource dataSource, final JavaPlugin plugin) {
        sendUpdateToDatabase(player, dataSource, plugin);
        remove(player.getUniqueId());
    }

    public void closeAndKeep(final Player player, final DataSource dataSource, final JavaPlugin plugin,
                             final FileConfiguration config, final ThreadsContainerImpl threadsContainer) {
        final int minutes = config.getInt("Data.Keep.Time-Minutes");
        final int time = (20 * 60) * minutes;
        final UUID uniqueId = player.getUniqueId();
        sendUpdateToDatabase(player, dataSource, plugin);
        if (threadsContainer.contains(uniqueId)) return;
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                final Player player = Bukkit.getPlayer(uniqueId);
                if (player != null) return;
                remove(uniqueId);
                if (!(threadsContainer.contains(uniqueId))) return;
                threadsContainer.remove(uniqueId);
            }
        };
        runnable.runTaskLaterAsynchronously(plugin, time);
        threadsContainer.set(uniqueId, new ThreadsDataContainer(runnable));
    }

    public void addKill(final Player player) {
        final UUID uniqueId = player.getUniqueId();
        final StatsDataContainer statsDataContainer = get(uniqueId);
        statsDataContainer.setKills(statsDataContainer.getKills() + 1);
    }

    public void addDeath(final Player player) {
        final UUID uniqueId = player.getUniqueId();
        final StatsDataContainer statsDataContainer = get(uniqueId);
        statsDataContainer.setDeaths(statsDataContainer.getDeaths() + 1);
    }

    public void updateBestKillStreak(final Player player, final int killStreak) {
        final UUID uniqueId = player.getUniqueId();
        final StatsDataContainer statsDataContainer = get(uniqueId);
        if (killStreak > statsDataContainer.getBestKillStreak()) {
            statsDataContainer.setBestKillStreak(killStreak);
        }
    }
}

package net.royalmind.stats.data.containers.stats;

import net.royalmind.stats.data.DataSource;
import net.royalmind.stats.data.containers.AbstractDataMap;
import net.royalmind.stats.data.containers.threads.ThreadsContainerImpl;
import net.royalmind.stats.data.containers.threads.ThreadsDataContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

public class StatsContainerImpl extends AbstractDataMap<UUID, StatsDataContainer> {

    public StatsContainerImpl() { }

    public void loadWorld(final World world, final DataSource dataSource, final JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                dataSource.execute(conn -> {
                    final String query = "CALL addWorld(?);";
                    final PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, world.getName());
                    statement.execute();
                    return null;
                });
            }
        }.runTaskAsynchronously(plugin);
    }

    public void loadData(final Player player, final DataSource dataSource, final List<String> worlds, final JavaPlugin plugin) {
        final UUID uniqueId = player.getUniqueId();
        if (contains(uniqueId)) {
            remove(uniqueId);
        }
        if (!(worlds.contains(player.getWorld().getName()))) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                dataSource.execute(conn -> {
                    String query = "CALL addPlayer(?, ?, ?, ?);";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, player.getName());
                    statement.setString(2, player.getUniqueId().toString());
                    statement.setDate(3, new Date(System.currentTimeMillis()));
                    statement.setString(4, player.getLocation().getWorld().getName());
                    statement.execute();

                    query = "CALL getPlayerData(?, ?, ?, ?);";
                    statement = conn.prepareStatement(query);
                    statement.setString(1, "kills, deaths, bestKillStreak");
                    statement.setString(2, player.getUniqueId().toString());
                    statement.setDate(3, new Date(System.currentTimeMillis()));
                    statement.setString(4, player.getLocation().getWorld().getName());
                    ResultSet resultSet = statement.executeQuery();

                    if (!(resultSet.next())) return null;

                    final StatsDataContainer dataContainer = new StatsDataContainer(
                            player.getUniqueId(),
                            resultSet.getInt("kills"),
                            resultSet.getInt("deaths"),
                            resultSet.getInt("bestKillStreak"),
                            player.getLocation().getWorld().getName()
                    );

                    query = "CALL getPlayerDataExceptWithDate(?, ?, ?, ?);";
                    statement = conn.prepareStatement(query);
                    statement.setString(1, "kills, deaths, bestKillStreak");
                    statement.setString(2, player.getUniqueId().toString());
                    statement.setDate(3, new Date(System.currentTimeMillis()));
                    statement.setString(4, player.getLocation().getWorld().getName());
                    resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        dataContainer.setMemKills(resultSet.getInt("kills"));
                        dataContainer.setMemDeaths(resultSet.getInt("deaths"));
                        dataContainer.setMemBestKillStreak(resultSet.getInt("bestKillStreak"));
                    }
                    set(player.getUniqueId(), dataContainer);
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
                    final String query = "CALL updatePlayerData(?, ?, ?, ?, ?, ?);";
                    final PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, uniqueId.toString());
                    statement.setInt(2, dataContainer.getKills());
                    statement.setInt(3, dataContainer.getDeaths());
                    statement.setInt(4, dataContainer.getBestKillStreak());
                    statement.setDate(5, new Date(System.currentTimeMillis()));
                    statement.setString(6, dataContainer.getWorld());
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
        if (!(contains(uniqueId))) return;
        final StatsDataContainer statsDataContainer = get(uniqueId);
        statsDataContainer.setKills(statsDataContainer.getKills() + 1);
    }

    public void addDeath(final Player player) {
        final UUID uniqueId = player.getUniqueId();
        if (!(contains(uniqueId))) return;
        final StatsDataContainer statsDataContainer = get(uniqueId);
        statsDataContainer.setDeaths(statsDataContainer.getDeaths() + 1);
    }

    public void updateBestKillStreak(final Player player, final Boolean isDead) {
        final UUID uniqueId = player.getUniqueId();
        if (!(contains(uniqueId))) return;
        final StatsDataContainer statsDataContainer = get(uniqueId);
        if (isDead) {
            statsDataContainer.setCurrentKillStreak(0);
            return;
        }
        final int currentKillStreak = statsDataContainer.getCurrentKillStreak() + 1;
        statsDataContainer.setCurrentKillStreak(currentKillStreak);
        if (currentKillStreak> statsDataContainer.getBestKillStreak()) {
            statsDataContainer.setBestKillStreak(currentKillStreak);
        }
    }
}

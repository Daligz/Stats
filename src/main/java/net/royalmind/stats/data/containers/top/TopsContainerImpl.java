package net.royalmind.stats.data.containers.top;

import net.royalmind.stats.data.DataSource;
import net.royalmind.stats.data.containers.AbstractConcurrentDataMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

public class TopsContainerImpl extends AbstractConcurrentDataMap<String, TopsDataContainer> {

    private JavaPlugin plugin;
    private DataSource dataSource;
    private FileConfiguration config;

    public TopsContainerImpl(final JavaPlugin plugin, final DataSource dataSource, final FileConfiguration config) {
        this.plugin = plugin;
        this.dataSource = dataSource;
        this.config = config;
        update();
    }

    private void load(final String worldName, final boolean isLifetime) {
        new BukkitRunnable() {
            @Override
            public void run() {
                dataSource.execute(conn -> {
                    try {
                        final TopsDataContainer[] topsDataContainers = getValues().toArray(new TopsDataContainer[0]);
                        final int size = getValues().size();
                        for (int i = 0; i < size; i++) {
                            final String identifier = topsDataContainers[i].getIdentifier();
                            if (isLifetime) {
                                if (identifier.contains("lifetime")) {
                                    remove(topsDataContainers[i].getIdentifier());
                                }
                            } else {
                                if (identifier.contains("monthly")) {
                                    remove(topsDataContainers[i].getIdentifier());
                                }
                            }
                        }
                    } catch (final Exception ex) { ex.printStackTrace(); }
                    finally {
                        final String query = (isLifetime) ? "CALL getLifetimeTop(?, ?, ?, ?);" : "CALL getMonthlyTop(?, ?, ?, ?, ?)";
                        final String dataToGet = (isLifetime) ? "name, uuid" : "kills, uuid, name";
                        final int limit = config.getInt("Data.Top.Limit-To-Show");
                        final PreparedStatement statement = conn.prepareStatement(query);
                        statement.setString(1, dataToGet);
                        statement.setString(2, "kills");
                        statement.setInt(3, limit);
                        if (isLifetime) {
                            statement.setString(4, worldName);
                        } else {
                            statement.setDate(4, new Date(System.currentTimeMillis()));
                            statement.setString(5, worldName);
                        }
                        final ResultSet resultSet = statement.executeQuery();
                        int place = 1;
                        while (resultSet.next()) {
                            final String name = resultSet.getString("name");
                            final String killsColumnLabel = (isLifetime) ? "total" : "kills";
                            final int kills = resultSet.getInt(killsColumnLabel) ;
                            final String identifier = createIdentifier(worldName, place, isLifetime);
                            final UUID dataUUID = UUID.nameUUIDFromBytes(identifier.getBytes());
                            /*final boolean simulateRealtime = config.getBoolean("Data.Top.Simulate-RealTime");
                            final UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                            if (simulateRealtime && statsContainer.contains(uuid)) {
                                final StatsDataContainer dataContainer = statsContainer.get(uuid);
                                kills += dataContainer.getKills();
                            }*/
                            set(
                                    identifier,
                                    new TopsDataContainer(
                                            dataUUID,
                                            identifier,
                                            name,
                                            kills
                                    )
                            );
                            place++;
                        }
                    }
                    return null;
                });
            }
        }.runTaskAsynchronously(this.plugin);
    }

    public String createIdentifier(final String worldName, final int place, final boolean isLifetime) {
        final String suffix = (isLifetime) ? "lifetime" : "monthly";
        return worldName + "-" + place + "-" + suffix;
    }

    private void update() {
        final int time = this.config.getInt("Data.Top.Time-Seconds");
        final List<String> worlds = this.config.getStringList("Enable-worlds");
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final String world : worlds) {
                    load(world, true);
                    load(world, false);
                }
            }
        }.runTaskTimer(this.plugin, 0L, 20 * time);
    }
}
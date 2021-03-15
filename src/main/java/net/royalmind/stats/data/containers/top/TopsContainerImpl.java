package net.royalmind.stats.data.containers.top;

import net.royalmind.stats.data.DataSource;
import net.royalmind.stats.data.containers.AbstractDataMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class TopsContainerImpl extends AbstractDataMap<String, TopsDataContainer> {

    private JavaPlugin plugin;
    private DataSource dataSource;
    private FileConfiguration config;

    public TopsContainerImpl(final JavaPlugin plugin, final DataSource dataSource, final FileConfiguration config) {
        this.plugin = plugin;
        this.dataSource = dataSource;
        this.config = config;
        update();
    }

    private void load(final String worldName) {
        new BukkitRunnable() {
            @Override
            public void run() {
                dataSource.execute(conn -> {
                    try {
                        final Collection<TopsDataContainer> values = getValues();
                        if (!(values.isEmpty())) {
                            for (final TopsDataContainer value : values) {
                                remove(value.getIdentifier());
                            }
                        }
                    } catch (final Exception ex) { }
                    final String query = "CALL getTop(?, ?, ?, ?);";
                    final PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, "kills, name");
                    statement.setString(2, "kills");
                    statement.setInt(3, 5);
                    statement.setString(4, worldName);
                    final ResultSet resultSet = statement.executeQuery();
                    int place = 1;
                    while (resultSet.next()) {
                        final String name = resultSet.getString("name");
                        final int kills = resultSet.getInt("kills");
                        final String identifier = createIdentifier(worldName, place);
                        final UUID uuid = UUID.nameUUIDFromBytes(identifier.getBytes());
                        set(
                                identifier,
                                new TopsDataContainer(
                                        uuid,
                                        identifier,
                                        name,
                                        kills
                                )
                        );
                        place++;
                    }
                    return null;
                });
            }
        }.runTaskAsynchronously(this.plugin);
    }

    public String createIdentifier(final String worldName, final int place) {
        return worldName + "-" + place;
    }

    public void update() {
        final int time = this.config.getInt("Data.Top.Time-Seconds");
        final List<String> worlds = this.config.getStringList("Enable-worlds");
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final String world : worlds) {
                    load(world);
                }
            }
        }.runTaskTimer(this.plugin, 0L, 20 * time);
    }
}
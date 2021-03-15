package net.royalmind.stats.data.containers.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import net.royalmind.stats.configuration.ConfigurationFile;
import net.royalmind.stats.data.containers.AbstractDataMap;
import net.royalmind.stats.data.containers.top.TopsContainerImpl;
import net.royalmind.stats.data.containers.top.TopsDataContainer;
import net.royalmind.stats.utils.Chat;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class LeaderboardContainerImpl extends AbstractDataMap<UUID, LeaderboardDataContainer> {

    private ConfigurationFile configLeaderboard;
    private TopsContainerImpl topsContainer;
    private JavaPlugin plugin;

    public LeaderboardContainerImpl(final ConfigurationFile configLeaderboard, final TopsContainerImpl topsContainer, final JavaPlugin plugin) {
        this.configLeaderboard = configLeaderboard;
        this.topsContainer = topsContainer;
        this.plugin = plugin;
        loadAll();
        update();
    }

    private void update() {
        final FileConfiguration fileConfiguration = this.configLeaderboard.getFileConfiguration();
        final int timeLifetime = fileConfiguration.getInt("Leaderboard.Lifetime.TimeToUpdate");
        final int timeMonthly = fileConfiguration.getInt("Leaderboard.Monthly.TimeToUpdate");
        if (timeLifetime > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (final LeaderboardDataContainer value : getValues()) {
                        configuration(fileConfiguration, value.getHologramLifetime(), value.getUuid(), true, true);
                    }
                }
            }.runTaskTimer(this.plugin, 0L, 20 * timeLifetime);
        }

        if (timeMonthly > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (final LeaderboardDataContainer value : getValues()) {
                        configuration(fileConfiguration, value.getHologramMonthly(), value.getUuid(), false, true);
                    }
                }
            }.runTaskTimer(this.plugin, 0L, 20 * timeMonthly);
        }
        }

    public void loadAll() {
        final FileConfiguration fileConfiguration = this.configLeaderboard.getFileConfiguration();
        try {
            for (final String key : fileConfiguration.getConfigurationSection("Holograms").getKeys(false)) {
                load(null, key, fileConfiguration, false);
            }
        } catch (final Exception ex) { }
    }

    public void load(final Location playerLocation, final String key, final FileConfiguration fileConfiguration, final Boolean save) {
        final UUID uuid = (key == null || key.isEmpty()) ? UUID.randomUUID() : UUID.fromString(key);
        if (contains(uuid)) return;
        final String rootPath = "Holograms." + key;
        final Location location = (playerLocation != null) ? playerLocation : ((Location) fileConfiguration.get(rootPath));
        final Hologram hologramLifetime = HologramsAPI.createHologram(this.plugin, location);
        final Hologram hologramMonthly = HologramsAPI.createHologram(this.plugin, location);
        configuration(fileConfiguration, hologramLifetime, uuid, true, false);
        configuration(fileConfiguration, hologramMonthly, uuid, false, false);
        set(
                uuid,
                new LeaderboardDataContainer(
                        uuid, hologramLifetime, hologramMonthly, location
                )
        );
        if (save) {
            this.configLeaderboard.set("Holograms." + uuid, location);
        }
    }

    private void configuration(final FileConfiguration fileConfiguration, final Hologram hologram,
                               final UUID uuid, final Boolean isLifetime, final Boolean cache) {
        if (!(isLifetime) && !(cache)) {
            hologram.getVisibilityManager().setVisibleByDefault(false);
        }
        final String key = (isLifetime) ? "Lifetime" : "Monthly";
        final String rootPath = "Leaderboard." + key + ".";
        final List<String> text = fileConfiguration.getStringList(rootPath + "Text");
        int place = 1;
        boolean nextLine = false;
        int posLine = 0;
        if (cache) {
            hologram.clearLines();
        }
        final String worldName = hologram.getWorld().getName();
        for (String line : text) {
            /*
             * %top_name% = name
             * %top_kills% = kills
             */
            final String identifier = this.topsContainer.createIdentifier(worldName, place);
            final Boolean contains = this.topsContainer.contains(identifier);
            final TopsDataContainer topsDataContainer = this.topsContainer.get(identifier);
            if (line.contains("%top_name%")) {
                if (contains) {
                    line = line.replace("%top_name%", topsDataContainer.getName());
                    nextLine = true;
                } else {
                    line = line.replace("%top_name%", "-/-/-");
                }
            }
            if (line.contains("%top_kills%")) {
                if (contains) {
                    line = line.replace("%top_kills%", String.valueOf(topsDataContainer.getKills()));
                    nextLine = true;
                } else {
                    line = line.replace("%top_kills%", "-/-/-");
                }
            }

            if (nextLine) {
                place++;
                nextLine = false;
            }

            if (line.contains("%clickable_switch%")) {
                final String replace = line.replace("%clickable_switch%", "");
                final TextLine textLine = hologram.appendTextLine(Chat.translate(replace));
                textLine.setTouchHandler(player -> {
                    final LeaderboardDataContainer leaderboardDataContainer = get(uuid);
                    hologram.getVisibilityManager().hideTo(player);
                    if (isLifetime) {
                        leaderboardDataContainer.getHologramMonthly().getVisibilityManager().showTo(player);
                    } else {
                        leaderboardDataContainer.getHologramLifetime().getVisibilityManager().showTo(player);
                    }
                });
            } else {
                hologram.insertTextLine(posLine, Chat.translate(line));
            }
            posLine++;
        }
    }

    public void delete(final String strUUID) {
        final UUID uuid = UUID.fromString(strUUID);
        if (!(contains(uuid))) return;
        final LeaderboardDataContainer leaderboardDataContainer = get(uuid);
        leaderboardDataContainer.getHologramLifetime().delete();
        leaderboardDataContainer.getHologramMonthly().delete();
        remove(uuid);
        this.configLeaderboard.set("Holograms." + uuid, null);
    }

    public void deleteAll() {
        for (final LeaderboardDataContainer value : getValues()) {
            value.getHologramLifetime().delete();
            value.getHologramMonthly().delete();
            remove(value.getUuid());
        }
    }

    public void reload() {
        deleteAll();
        loadAll();
    }
}

package net.royalmind.stats.data.containers.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import net.royalmind.stats.configuration.ConfigurationFile;
import net.royalmind.stats.data.containers.AbstractDataMap;
import net.royalmind.stats.utils.Chat;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class LeaderboardContainerImpl extends AbstractDataMap<UUID, LeaderboardDataContainer> {

    private ConfigurationFile configLeaderboard;
    private JavaPlugin plugin;

    public LeaderboardContainerImpl(final ConfigurationFile configLeaderboard, final JavaPlugin plugin) {
        this.configLeaderboard = configLeaderboard;
        this.plugin = plugin;
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
        final int timeLifetime = fileConfiguration.getInt("Leaderboard.Lifetime.TimeToUpdate");
        final int timeMonthly = fileConfiguration.getInt("Leaderboard.Monthly.TimeToUpdate");
        configuration(fileConfiguration, hologramLifetime, uuid, true);
        configuration(fileConfiguration, hologramMonthly, uuid, false);
        set(
                uuid,
                new LeaderboardDataContainer(
                        uuid, hologramLifetime, timeLifetime, hologramMonthly, timeMonthly, location
                )
        );
        if (save) {
            this.configLeaderboard.set("Holograms." + uuid, location);
        }
    }

    private void configuration(final FileConfiguration fileConfiguration, final Hologram hologram,
                               final UUID uuid, final Boolean isLifetime) {
        if (!(isLifetime)) {
            hologram.getVisibilityManager().setVisibleByDefault(false);
        }
        final String key = (isLifetime) ? "Lifetime" : "Monthly";
        final String rootPath = "Leaderboard." + key + ".";
        final List<String> text = fileConfiguration.getStringList(rootPath + "Text");
        for (final String line : text) {
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
                continue;
            }
            hologram.appendTextLine(Chat.translate(line));
        }
    }

    public void delete(final String strUUID) {
        final UUID uuid = UUID.fromString(strUUID);
        if (!(contains(uuid))) return;
        final LeaderboardDataContainer leaderboardDataContainer = get(uuid);
        leaderboardDataContainer.getHologramLifetime().delete();
        leaderboardDataContainer.getHologramMonthly().delete();
        remove(uuid);
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

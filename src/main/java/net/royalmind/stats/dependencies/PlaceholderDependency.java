package net.royalmind.stats.dependencies;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.royalmind.stats.data.containers.stats.StatsContainerImpl;
import net.royalmind.stats.data.containers.stats.StatsDataContainer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlaceholderDependency extends PlaceholderExpansion {

    private StatsContainerImpl statsContainer;

    public PlaceholderDependency(final StatsContainerImpl statsContainer) {
        this.statsContainer = statsContainer;
    }

    @Override
    public String onPlaceholderRequest(final Player player, final String identifier) {
        final UUID uniqueId = player.getUniqueId();
        if (!(this.statsContainer.contains(uniqueId))) return null;
        final StatsDataContainer dataContainer = this.statsContainer.get(uniqueId);
        if (identifier.equalsIgnoreCase("kills")) {
            return String.valueOf(dataContainer.getKills());
        } else if (identifier.equalsIgnoreCase("deaths")) {
            return String.valueOf(dataContainer.getDeaths());
        } else if (identifier.equalsIgnoreCase("currentks")) {
            return String.valueOf(dataContainer.getCurrentKillStreak());
        } else if (identifier.equalsIgnoreCase("bestks")) {
            return String.valueOf(dataContainer.getBestKillStreak());
        }
        return null;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "rstats";
    }

    @Override
    public String getAuthor() {
        return "ImSrPanda";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }
}
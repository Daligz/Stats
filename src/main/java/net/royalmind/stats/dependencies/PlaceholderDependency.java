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
        if (identifier.equalsIgnoreCase("monthly_kills")) {
            return String.valueOf(dataContainer.getKills());
        } else if (identifier.equalsIgnoreCase("monthly_deaths")) {
            return String.valueOf(dataContainer.getDeaths());
        } else if (identifier.equalsIgnoreCase("monthly_bestks")) {
            return String.valueOf(dataContainer.getBestKillStreak());
        } else if (identifier.equalsIgnoreCase("lifetime_kills")) {
            return String.valueOf(dataContainer.getAllKills());
        } else if (identifier.equalsIgnoreCase("lifetime_deaths")) {
            return String.valueOf(dataContainer.getAllDeaths());
        } else if (identifier.equalsIgnoreCase("lifetime_bestks")) {
            return String.valueOf(dataContainer.getAllBestKillstreak());
        } else if (identifier.equalsIgnoreCase("current_ks")) {
            return String.valueOf(dataContainer.getCurrentKillStreak());
        } else if (identifier.equalsIgnoreCase("lifetime_kd")) {
            return String.valueOf(dataContainer.getAllKills() / dataContainer.getAllDeaths());
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
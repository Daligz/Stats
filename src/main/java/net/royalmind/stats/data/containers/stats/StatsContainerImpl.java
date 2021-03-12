package net.royalmind.stats.data.containers.stats;

import net.royalmind.stats.data.containers.AbstractDataMap;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatsContainerImpl extends AbstractDataMap<UUID, StatsDataContainer> {

    public StatsContainerImpl() { }

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

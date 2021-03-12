package net.royalmind.stats.data.containers.stats;

import java.util.UUID;

public class StatsDataContainer {

    private UUID uuid;
    private int kills;
    private int deaths;
    private int bestKillStreak;

    public StatsDataContainer(final UUID uuid, final int kills, final int deaths, final int bestKillStreak) {
        this.uuid = uuid;
        this.kills = kills;
        this.deaths = deaths;
        this.bestKillStreak = bestKillStreak;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getBestKillStreak() {
        return bestKillStreak;
    }
}

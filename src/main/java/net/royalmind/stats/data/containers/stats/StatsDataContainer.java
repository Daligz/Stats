package net.royalmind.stats.data.containers.stats;

import java.util.UUID;

public class StatsDataContainer {

    private UUID uuid;
    private int kills;
    private int deaths;
    private int bestKillStreak;
    private String world;

    public StatsDataContainer(final UUID uuid, final int kills, final int deaths, final int bestKillStreak, final String world) {
        this.uuid = uuid;
        this.kills = kills;
        this.deaths = deaths;
        this.bestKillStreak = bestKillStreak;
        this.world = world;
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

    public String getWorld() {
        return world;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setBestKillStreak(int bestKillStreak) {
        this.bestKillStreak = bestKillStreak;
    }
}

package net.royalmind.stats.data.containers.stats;

import java.util.UUID;

public class StatsDataContainer {

    private UUID uuid;
    private int kills;
    private int deaths;
    private int bestKillStreak;
    private int currentKillStreak = 0;
    private String world;
    private int memKills = 0;
    private int memDeaths = 0;
    private int memBestKillStreak = 0;

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

    public int getCurrentKillStreak() {
        return currentKillStreak;
    }

    public String getWorld() {
        return world;
    }

    public int getMemKills() {
        return memKills;
    }

    public int getMemDeaths() {
        return memDeaths;
    }

    public int getMemBestKillStreak() {
        return memBestKillStreak;
    }

    public int getAllKills() {
        return this.kills + this.memKills;
    }

    public int getAllDeaths() {
        return this.deaths + this.memDeaths;
    }

    public int getAllBestKillstreak() {
        return (this.bestKillStreak > this.memBestKillStreak) ? this.bestKillStreak : this.memBestKillStreak;
    }

    public void setKills(final int kills) {
        this.kills = kills;
    }

    public void setDeaths(final int deaths) {
        this.deaths = deaths;
    }

    public void setBestKillStreak(final int bestKillStreak) {
        this.bestKillStreak = bestKillStreak;
    }

    public void setCurrentKillStreak(final int currentKillStreak) {
        this.currentKillStreak = currentKillStreak;
    }

    public void setMemKills(final int memKills) {
        this.memKills = memKills;
    }

    public void setMemDeaths(final int memDeaths) {
        this.memDeaths = memDeaths;
    }

    public void setMemBestKillStreak(final int memBestKillStreak) {
        this.memBestKillStreak = memBestKillStreak;
    }
}

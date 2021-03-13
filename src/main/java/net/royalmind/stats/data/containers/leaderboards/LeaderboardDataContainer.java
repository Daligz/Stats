package net.royalmind.stats.data.containers.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;

import java.util.UUID;

public class LeaderboardDataContainer {

    private UUID uuid;
    private Hologram hologramLifetime;
    private int timeToUpdateLifetime;
    private Hologram hologramMonthly;
    private int timeToUpdateMonthly;
    private Location location;

    public LeaderboardDataContainer(final UUID uuid, final Hologram hologramLifetime, final int timeToUpdateLifetime,
                                    final Hologram hologramMonthly, final int timeToUpdateMonthly, final Location location) {
        this.uuid = uuid;
        this.hologramLifetime = hologramLifetime;
        this.timeToUpdateLifetime = timeToUpdateLifetime;
        this.hologramMonthly = hologramMonthly;
        this.timeToUpdateMonthly = timeToUpdateMonthly;
        this.location = location;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Hologram getHologramLifetime() {
        return hologramLifetime;
    }

    public int getTimeToUpdateLifetime() {
        return timeToUpdateLifetime;
    }

    public Hologram getHologramMonthly() {
        return hologramMonthly;
    }

    public int getTimeToUpdateMonthly() {
        return timeToUpdateMonthly;
    }

    public Location getLocation() {
        return location;
    }
}

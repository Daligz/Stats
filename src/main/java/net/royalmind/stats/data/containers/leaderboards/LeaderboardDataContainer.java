package net.royalmind.stats.data.containers.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;

import java.util.UUID;

public class LeaderboardDataContainer {

    private UUID uuid;
    private Hologram hologramLifetime;
    private Hologram hologramMonthly;
    private Location location;

    public LeaderboardDataContainer(final UUID uuid, final Hologram hologramLifetime,
                                    final Hologram hologramMonthly, final Location location) {
        this.uuid = uuid;
        this.hologramLifetime = hologramLifetime;
        this.hologramMonthly = hologramMonthly;
        this.location = location;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Hologram getHologramLifetime() {
        return hologramLifetime;
    }

    public Hologram getHologramMonthly() {
        return hologramMonthly;
    }

    public Location getLocation() {
        return location;
    }
}

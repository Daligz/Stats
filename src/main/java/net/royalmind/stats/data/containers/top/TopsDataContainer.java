package net.royalmind.stats.data.containers.top;

import java.util.UUID;

public class TopsDataContainer {

    private UUID uuid;
    private String identifier;
    private String name;
    private int kills;

    public TopsDataContainer(final UUID uuid, final String identifier, final String name, final int kills) {
        this.uuid = uuid;
        this.identifier = identifier;
        this.name = name;
        this.kills = kills;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getKills() {
        return kills;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "TopsDataContainer{" +
                "uuid=" + uuid +
                ", identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                ", kills=" + kills +
                '}';
    }
}

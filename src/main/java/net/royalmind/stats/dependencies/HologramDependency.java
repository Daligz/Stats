package net.royalmind.stats.dependencies;

import org.bukkit.Bukkit;

public class HologramDependency {

    private final Boolean enable;
    private static final String PLUGIN_ID = "HolographicDisplays";
    private static HologramDependency instance;

    private HologramDependency() {
        this.enable = Bukkit.getPluginManager().isPluginEnabled(PLUGIN_ID);
    }

    public static HologramDependency getInstance() {
        return (instance == null) ? new HologramDependency() : instance;
    }

    public Boolean isEnable() {
        return enable;
    }
}

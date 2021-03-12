package net.royalmind.stats.data.containers.threads;

import org.bukkit.scheduler.BukkitRunnable;

public class ThreadsDataContainer {

    private BukkitRunnable runnable;

    public ThreadsDataContainer(final BukkitRunnable runnable) {
        this.runnable = runnable;
    }

    public BukkitRunnable getRunnable() {
        return runnable;
    }
}

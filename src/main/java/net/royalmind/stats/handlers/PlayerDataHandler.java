package net.royalmind.stats.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerDataHandler implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) { }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) { }

    @EventHandler
    public void onPlayerKill(final PlayerDeathEvent event) { }

    @EventHandler
    public void onPlayerGetKillStreak(final PlayerDeathEvent event) { }
}

package net.royalmind.stats.handlers;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.royalmind.stats.data.DataSource;
import net.royalmind.stats.data.containers.stats.StatsContainerImpl;
import net.royalmind.stats.data.containers.stats.StatsDataContainer;
import net.royalmind.stats.data.containers.threads.ThreadsContainerImpl;
import net.royalmind.stats.utils.Chat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PlayerDataHandler implements Listener {

    private DataSource dataSource;
    private StatsContainerImpl statsContainer;
    private ThreadsContainerImpl threadsContainer;
    private JavaPlugin plugin;
    private FileConfiguration config;

    public PlayerDataHandler(final DataSource dataSource, final StatsContainerImpl statsContainer,
                             final ThreadsContainerImpl threadsContainer, final FileConfiguration config, final JavaPlugin plugin) {
        this.dataSource = dataSource;
        this.statsContainer = statsContainer;
        this.threadsContainer = threadsContainer;
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        load(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangeWorld(final PlayerChangedWorldEvent event) {
        load(event.getPlayer());
    }

    private void load(final Player player) {
        final List<String> worlds = this.config.getStringList("Enable-worlds");
        this.statsContainer.close(player, this.dataSource, this.plugin);
        this.statsContainer.loadData(player, this.dataSource, worlds, this.plugin);
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        this.statsContainer.addDeath(event.getEntity().getPlayer());
    }

    @EventHandler
    public void onPlayerKill(final PlayerDeathEvent event) {
        final Player killer = event.getEntity().getKiller();
        if (killer == null || killer == event.getEntity().getPlayer()) return;
        this.statsContainer.addKill(killer);
    }

    @EventHandler
    public void onPlayerGetKillStreak(final PlayerDeathEvent event) {
        final Player killer = event.getEntity().getKiller();
        final Player victim = event.getEntity().getPlayer();
        if (killer == null || killer == victim) return;
        this.statsContainer.updateBestKillStreak(killer, false);
        this.statsContainer.updateBestKillStreak(victim, true);
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final boolean isKeepDataEnable = this.config.getBoolean("Data.Keep.Enable");
        if (isKeepDataEnable) {
            this.statsContainer.closeAndKeep(player, this.dataSource, this.plugin, this.config, this.threadsContainer);
        } else {
            this.statsContainer.close(player, this.dataSource, this.plugin);
        }
    }

    @EventHandler
    public void onWorldLoad(final WorldLoadEvent event) {
        this.statsContainer.loadWorld(event.getWorld(), this.dataSource, this.plugin);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        String message = event.getMessage();
        event.setCancelled(true);
        if (message.contains("/")) return;
        final StatsDataContainer dataContainer = this.statsContainer.get(player.getUniqueId());
        final StringBuilder textBuilder = new StringBuilder();
        for (String text : this.config.getStringList("Chat.Hover")) {
            text = text.replace("%rs_name%", player.getName());
            text = text.replace("%rs_kills%", String.valueOf(dataContainer.getKills()));
            text = text.replace("%rs_deaths%", String.valueOf(dataContainer.getDeaths()));
            text = text.replace("%rs_bestkillstreak%", String.valueOf(dataContainer.getBestKillStreak()));
            text = text.replace("%rs_currentkillstreak%", String.valueOf(dataContainer.getCurrentKillStreak()));
            if (text.contains("_n")) {
                text = text.replace("_n", "");
                textBuilder.append(Chat.translate(text));
            } else {
                textBuilder.append(Chat.translate(text) + "\n");
            }
        }
        final String coloredPermission = this.config.getString("Chat.ColoredMessage-Permission");
        if (player.hasPermission(coloredPermission)) {
            message = Chat.translate(message);
        }
        final String chatFormat = this.config.getString("Chat.Message")
                .replace("%player%", player.getName())
                .replace("%message%", message);

        TextComponent textComponent = new TextComponent(chatFormat);
        textComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(textBuilder.toString()).create()
        ));
        this.plugin.getServer().spigot().broadcast(textComponent);
    }
}

package net.royalmind.stats.commands;

import net.royalmind.stats.configuration.Files;
import net.royalmind.stats.data.containers.leaderboards.LeaderboardContainerImpl;
import net.royalmind.stats.data.containers.leaderboards.LeaderboardDataContainer;
import net.royalmind.stats.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    private Files files;
    private LeaderboardContainerImpl leaderboardContainer;

    public StatsCommand(final Files files, final LeaderboardContainerImpl leaderboardContainer) {
        this.files = files;
        this.leaderboardContainer = leaderboardContainer;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.translate("&cNo puedes ejecutar esto en consola."));
            return true;
        }
        final Player player = (Player) sender;
        if (!(player.hasPermission("royalstats.admin"))) {
            player.sendMessage(Chat.translate("&cQue paso?, este plugin es de &eRoyalMind &cno sea chismoso."));
        } else if (args.length <= 0) {
            help(player);
        } else if (args[0].equalsIgnoreCase("reload")) {
            final long reloadTime = this.files.reload();
            player.sendMessage(Chat.translate("&aPlugin recargado en &e" + reloadTime + " &ams"));
        } else if (args[0].equalsIgnoreCase("leaderboard")) {
            if (args.length <= 1 || args[1].isEmpty()) {
                player.sendMessage(Chat.translate("&c/stats leaderboard (list/set/delete)"));
                return true;
            }
            if (args[1].equalsIgnoreCase("list")) {
                player.sendMessage(Chat.translate("&e&lLeaderboard"));
                for (final LeaderboardDataContainer value : this.leaderboardContainer.getValues()) {
                    player.sendMessage(Chat.translate("&7- &f" + value.getUuid() + " | " + value.getLocation().serialize()));
                }
            } else if (args[1].equalsIgnoreCase("set")) {
                this.leaderboardContainer.load(player.getLocation(), null, this.files.getConfigLeaderboard().getFileConfiguration(), true);
                player.sendMessage(Chat.translate("&eLeaderboard colocado"));
            } else if (args[1].equalsIgnoreCase("delete")) {
                if (args[2].isEmpty()) {
                    player.sendMessage(Chat.translate("&c/stats leaderboard delete (UUID)"));
                    return true;
                }
                this.leaderboardContainer.delete(args[2]);
                player.sendMessage(Chat.translate("&eLeaderboard eliminado"));
            } else {
                player.sendMessage(Chat.translate("&c/stats leaderboard (list/set/delete)"));
            }
        } else {
            help(player);
        }
        return true;
    }

    private void help(final Player player) {
        player.sendMessage(Chat.translate("&c/stats (reload/leaderboard)"));
    }
}

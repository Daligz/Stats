package net.royalmind.stats.commands;

import net.royalmind.stats.configuration.Files;
import net.royalmind.stats.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    private Files files;

    public StatsCommand(final Files files) {
        this.files = files;
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
        } else if (args.length < 1) {
            help(player);
        } else if (args[0].equalsIgnoreCase("reload")) {
            final long reloadTime = this.files.reload();
            player.sendMessage(Chat.translate("&aPlugin recargado en &e" + reloadTime + " &ams"));
        } else {
            help(player);
        }
        return true;
    }

    private void help(final Player player) {
        player.sendMessage(Chat.translate("&c/stats (reload)"));
    }
}

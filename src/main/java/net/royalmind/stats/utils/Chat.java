package net.royalmind.stats.utils;

import net.md_5.bungee.api.ChatColor;

public class Chat {

    public static String translate(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}

package me.youngermax.survivalserver;

import org.bukkit.ChatColor;

public class Messages {

	public static String c(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public static final String RESTORED_POSITION =  c("&aRestored your last position in survival mode.");
	public static final String POSITION_SAVED = c("&aYour position was saved and will be restored the next time survival opens. In the meantime, you'll be in Spectator Mode.");
	public static final String ERROR_RESTORING_POSITION = c("&cThere was an error while trying to restore your last position. You've been teleported to a safe location.");
}

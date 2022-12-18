package me.youngermax.survivalserver;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;

public class ServerPingListener implements Listener {
	public static final DateTimeFormatter MESSAGE_FORMATTER = new DateTimeFormatterBuilder()
			.appendText(ChronoField.DAY_OF_WEEK)
			.appendLiteral(" at ")
			.appendText(ChronoField.CLOCK_HOUR_OF_AMPM)
			.appendText(ChronoField.AMPM_OF_DAY)
			.toFormatter();

	private final CachedServerIcon icon;
	private final SurvivalServer server;

	public ServerPingListener(SurvivalServer server) {
		File iconFile = new File("server-icon.png");
		CachedServerIcon icon;

		try {
			icon = iconFile.exists() ? server.getServer().loadServerIcon(iconFile) : null;
		} catch (Exception e) {
			server.getLogger().log(Level.SEVERE, "Failed to load server icon!", e);
			icon = null;
		}

		this.icon = icon;
		this.server = server;
	}

	public static String getTimeUntil(LocalDateTime dt) {
		LocalDateTime now = LocalDateTime.now();

		return String.format(
				"%02d:%02d:%02d",
				now.until(dt, ChronoUnit.HOURS),
				now.until(dt, ChronoUnit.MINUTES) % 60,
				now.until(dt, ChronoUnit.SECONDS) % 60
		);
	}

	private String getMessage() {
		LocalDateTime timeOfNextOpen = this.server.serverStateManager.getNextOpenTime();
		LocalDateTime timeOfNextClose = this.server.serverStateManager.getCloseTime();

		String fromFormat = timeOfNextOpen.format(MESSAGE_FORMATTER);
		String toFormat = timeOfNextClose.format(MESSAGE_FORMATTER);

		StringBuilder b = new StringBuilder("&6Weekly Survival: ")
				.append(fromFormat).append(" to ").append(toFormat).append("\n");

		if (this.server.serverStateManager.isOpenRightNow()) {
			b.append("&bOPEN RIGHT NOW! Closes in ").append(getTimeUntil(timeOfNextClose)).append("!");
		} else {
			b.append("&aSurvival will open in ").append(getTimeUntil(timeOfNextOpen)).append("!");
		}

		return ChatColor.translateAlternateColorCodes('&', b.toString());
	}

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		event.setMotd(getMessage());
		event.setServerIcon(this.icon);
	}
}

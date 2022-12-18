package me.youngermax.survivalserver;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
	private final SurvivalServer server;

	public PlayerJoinListener(SurvivalServer s) {
		this.server = s;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (this.server.serverStateManager.isOpenRightNow()) {
			this.server.serverStateManager.openSurvival(event.getPlayer());
		} else {
			this.server.serverStateManager.closeSurvival(event.getPlayer());
		}
	}
}


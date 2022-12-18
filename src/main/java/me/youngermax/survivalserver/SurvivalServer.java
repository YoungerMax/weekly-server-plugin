package me.youngermax.survivalserver;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public final class SurvivalServer extends JavaPlugin {
	public ServerStateManager serverStateManager;

	public void closeSurvivalLoop() {
		Arrays.stream(this.getServer().getOfflinePlayers())
			.filter(offlinePlayer -> offlinePlayer.getPlayer() != null)
			.forEach(offlinePlayer -> this.serverStateManager.closeSurvival(offlinePlayer.getPlayer()));

		this.serverStateManager.save();

		this.getLogger().info("OPEN: " + this.serverStateManager.getOpenTime());
		this.getLogger().info("NEXT OPEN: " + this.serverStateManager.getNextOpenTime());

		this.getServer().getScheduler().runTaskLater(
				this,
				this::openSurvivalLoop,
				LocalDateTime.now().until(this.serverStateManager.getNextOpenTime(), ChronoUnit.SECONDS) * 20
		);
	}

	public void openSurvivalLoop() {
		Arrays.stream(this.getServer().getOfflinePlayers())
				.filter(offlinePlayer -> offlinePlayer.getPlayer() != null)
				.forEach(offlinePlayer -> this.serverStateManager.openSurvival(offlinePlayer.getPlayer()));

		this.serverStateManager.save();

		this.getLogger().info("CLOSE: " + this.serverStateManager.getCloseTime());

		this.getServer().getScheduler().runTaskLater(
				this,
				this::closeSurvivalLoop,
				LocalDateTime.now().until(this.serverStateManager.getCloseTime(), ChronoUnit.SECONDS) * 20
		);
	}

	public void updateTimeLeft() {
		BaseComponent[] str = TextComponent.fromLegacyText(Messages.c(
				"&b" + (this.serverStateManager.isOpenRightNow() ? "Closes" : "Opens") +
				" in &6" +
				ServerPingListener.getTimeUntil(this.serverStateManager.isOpenRightNow() ? this.serverStateManager.getCloseTime() : this.serverStateManager.getNextOpenTime())
		));

		Arrays.stream(this.getServer().getOfflinePlayers())
				.filter(offlinePlayer -> offlinePlayer.getPlayer() != null)
				.forEach(offlinePlayer -> {
					offlinePlayer.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, str);
				});
	}

	@Override
	public void onEnable() {
		this.serverStateManager = new ServerStateManager(new File("player-position-data.javaobject"), this);

		this.getServer().getWorlds().forEach(world -> world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false));

		this.getServer().getPluginManager().registerEvents(new ServerPingListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

		if (this.serverStateManager.isOpenRightNow()) {
			this.openSurvivalLoop();
		} else {
			this.closeSurvivalLoop();
		}

		this.getServer().getScheduler().runTaskTimerAsynchronously(this, this::updateTimeLeft, 0, 20);
	}

	@Override
	public void onDisable() {
		this.serverStateManager.save();
	}
}

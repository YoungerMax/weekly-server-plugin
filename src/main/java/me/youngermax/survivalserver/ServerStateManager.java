package me.youngermax.survivalserver;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class ServerStateManager {
	public static final DayOfWeek OPEN_DAY = DayOfWeek.FRIDAY;
	public static final LocalTime OPEN_TIME = LocalTime.of(5, 0);
	public static final DayOfWeek CLOSE_DAY = DayOfWeek.SUNDAY;
	public static final LocalTime CLOSE_TIME = LocalTime.of(12 + 8, 0);


	private final File posFile;
	private final SurvivalServer server;
	public HashMap<UUID, SerializableLocation> locationHashMap = new HashMap<>();

	public ServerStateManager(File playerPositionsFile, SurvivalServer s) {
		this.posFile = playerPositionsFile;
		this.server = s;

		this.load();
	}

	public void openSurvival(Player player) {
		SerializableLocation lastLocation = this.locationHashMap.remove(player.getUniqueId());

		player.setGameMode(GameMode.SURVIVAL);

		if (lastLocation == null) {
			player.teleport(player.getBedSpawnLocation() == null ? player.getWorld().getSpawnLocation() : player.getBedLocation());
			player.sendMessage(Messages.ERROR_RESTORING_POSITION);

			return;
		}

		player.teleport(lastLocation.deserialize(this.server));
		player.sendMessage(Messages.RESTORED_POSITION);
	}

	public void closeSurvival(Player player) {
		if (this.locationHashMap.containsKey(player.getUniqueId())) return;

		this.locationHashMap.put(player.getUniqueId(), new SerializableLocation(player.getLocation()));

		player.setGameMode(GameMode.SPECTATOR);
		player.sendMessage(Messages.POSITION_SAVED);
	}

	@SuppressWarnings("unchecked")
	public void load() {
		if (!this.posFile.exists()) return;

		try (FileInputStream f = new FileInputStream(this.posFile); ObjectInputStream o = new ObjectInputStream(f)) {
			this.locationHashMap = (HashMap<UUID, SerializableLocation>) o.readObject();
		} catch (IOException | ClassNotFoundException e) {
			this.server.getLogger().log(Level.SEVERE, "Failed to load locations", e);

			this.locationHashMap = new HashMap<>();
		}
	}

	public void save() {
		try (FileOutputStream f = new FileOutputStream(this.posFile); ObjectOutputStream o = new ObjectOutputStream(f)) {
			o.writeObject(this.locationHashMap);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public LocalDateTime getOpenTime() {
		return LocalDate.now()
				.with(OPEN_DAY)
				.atTime(OPEN_TIME);
	}

	public LocalDateTime getNextOpenTime() {
		LocalDateTime d = LocalDate.now()
				.atTime(OPEN_TIME)
				.with(TemporalAdjusters.nextOrSame(OPEN_DAY));

		if (LocalDateTime.now().isAfter(d)) {
			d = d.plusWeeks(1);
		}

		return d;
	}

	public LocalDateTime getCloseTime() {
		return LocalDate.now()
				.with(CLOSE_DAY)
				.atTime(CLOSE_TIME);
	}

	public boolean isOpenRightNow() {
		LocalDateTime now = LocalDateTime.now();

		return now.isAfter(getOpenTime()) && now.isBefore(getCloseTime());
	}
}

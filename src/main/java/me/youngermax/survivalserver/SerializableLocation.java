package me.youngermax.survivalserver;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class SerializableLocation implements Serializable {
	private UUID world;
	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;

	public SerializableLocation(World world, double x, double y, double z, float pitch, float yaw) {
		this.world = world.getUID();
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public SerializableLocation(Location location) {
		this(Objects.requireNonNull(location.getWorld()), location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
	}

	public Location deserialize(SurvivalServer server) {
		return new Location(server.getServer().getWorld(world), x, y, z, yaw, pitch);
	}
}

package com.hotmail.AdrianSR.core.util.configurable.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSR.core.util.TextUtils;
import com.hotmail.AdrianSR.core.util.configurable.Configurable;
import com.hotmail.AdrianSR.core.util.file.YmlUtils;
import com.hotmail.AdrianSR.core.util.initializable.Initializable;

public class ConfigurableLocation extends org.bukkit.Location implements Configurable, Initializable {
	
	public static final String WORLD_KEY = "world";
	public static final String     X_KEY = "x";
	public static final String     Y_KEY = "y";
	public static final String     Z_KEY = "z";
	public static final String   YAW_KEY = "yaw";
	public static final String PITCH_KEY = "pitch";
	
	private boolean initialized;

	public ConfigurableLocation() { // uninitialized
		super(null, -1D, -1D, -1D, -1F, -1F);
	}
	
	public ConfigurableLocation(World world, double x, double y, double z, float yaw, float pitch) {
		super(world, x, y, z, yaw, pitch);
		this.initialized = true;
	}
	
	public ConfigurableLocation(World world, double x, double y, double z) {
		this(world, x, y, z, 0.0F, 0.0F);
	}
	
	public ConfigurableLocation(Location copy) {
		this(copy.getWorld(), copy.getX(), copy.getY(), copy.getZ(), copy.getYaw(), copy.getPitch());
	}

	@Override
	public ConfigurableLocation load(ConfigurationSection section) {
		this.setWorld(Bukkit.getWorld(TextUtils.getNotNull(section.getString(WORLD_KEY, null), "")));
		this.setX(section.getDouble(X_KEY, 0));
		this.setY(section.getDouble(Y_KEY, 0));
		this.setZ(section.getDouble(Z_KEY, 0));
		this.setYaw((float) section.getDouble(YAW_KEY, 0));
		this.setPitch((float) section.getDouble(PITCH_KEY, 0));
		this.initialized = true;
		return this;
	}
	
	@Override
	public int save(ConfigurationSection section) {
		return 	YmlUtils.setNotEqual(section, WORLD_KEY, ( getWorld() != null ? getWorld().getName() : "unknown" ) )
			+	YmlUtils.setNotEqual(section, X_KEY, getX())
			+	YmlUtils.setNotEqual(section, Y_KEY, getY())
			+	YmlUtils.setNotEqual(section, Z_KEY, getZ())
			+	YmlUtils.setNotEqual(section, YAW_KEY, getYaw())
			+	YmlUtils.setNotEqual(section, PITCH_KEY, getPitch());
	}
	
	@Override
	public boolean isInitialized() {
		return initialized;
	}
	
	@Override
	public boolean isValid() {
		return isInitialized() && getWorld() != null;
	}

	@Override
	public boolean isInvalid() {
		return !isValid();
	}
}
package com.hotmail.AdrianSR.core.util.localization;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.NumberConversions;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.hotmail.AdrianSR.core.util.TextUtils;
import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;
import com.hotmail.AdrianSR.core.util.file.YmlUtils;

/**
 * Represets a Localization that can be saved to config.
 * 
 * @author AdrianSR.
 */
public class Loc implements Cloneable {
	
	// fields.
	private Number x, y, z;
	private float pitch, yaw;
	private String world;
	private BlockFace direction;

	/**
	 * Construct a Loc from a location.
	 * 
	 * @param loc the location.
	 * @param precise if is false the location will be from the location block.
	 */
	public Loc(Location loc, boolean precise) {
		world = loc == null ? null : (loc.getWorld() != null ? loc.getWorld().getName() : null);
		if (precise) {
			x     = loc.getX();
			y     = loc.getY();
			z     = loc.getZ();
			pitch = loc.getPitch();
			yaw   = loc.getYaw();
		} else {
			x     = loc.getBlockX();
			y     = loc.getBlockY();
			z     = loc.getBlockZ();
			pitch = 0;
			yaw   = 0;
		}
	}
	
	/**
	 * Construct a Loc from a location.
	 * 
	 * @param loc the location.
	 */
	public Loc(Location loc) {
		this(loc, false);
	}

	/**
	 * Construct a Loc from world name, and coords.
	 * 
	 * @param world the world name.
	 * @param x is the coord X.
	 * @param y is the coord Y.
	 * @param z is the coord Z.
	 */
	public Loc(String world, Number x, Number y, Number z) {
		this(world, x, y, z, 0, 0);
	}

	/**
	 * Construct a Loc from world name, and coords.
	 * 
	 * @param world the world name.
	 * @param x is the coord X.
	 * @param y is the coord Y.
	 * @param z is the coord Z.
	 * @param pitch is the coord pitch.
	 * @param yaw is the coord yaw.
	 */
	public Loc(String world, Number x, Number y, Number z, float pitch, float yaw) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	/**
	 * Load Loc from config.
	 * 
	 * @param section the saved Loc section.
	 */
	public Loc(ConfigurationSection section) {
		// check section.
		assert section != null : "Cannot load a Loc from this section!";

		// load values.
		world     = section.getString("World");
		pitch     = (float) section.getDouble("Pitch");
		yaw       = (float) section.getDouble("Yaw");
		direction = ReflectionUtils.getEnumConstant(BlockFace.class, TextUtils.getNotNull(section.getString("FaceDirection"), ""));
		
		// load coords.
		for (String prf : new String[] {"X", "Y", "Z"}) {
			// get numb.
			Number numb = section.isDouble(prf) ? section.getDouble(prf) : section.getInt(prf);
			
			// set value.
			switch(prf) {
			case "X":
				x = numb;
				break;
			case "Y":
				y = numb;
				break;
			case "Z":
				z = numb;
				break;
			}
		}
	}
	
	/**
	 * @return x.
	 */
	public double getX() {
		return x.doubleValue();
	}

	/**
	 * @return block X.
	 */
	public int getBlockX() {
		return x.intValue();
	}
	
	/**
	 * @return get y.
	 */
	public double getY() {
		return y.doubleValue();
	}

	/**
	 * @return block Y.
	 */
	public int getBlockY() {
		return y.intValue();
	}
	
	/**
	 * @return get z.
	 */
	public double getZ() {
		return z.doubleValue();
	}

	/**
	 * @return block Z.
	 */
	public int getBlockZ() {
		return z.intValue();
	}
	
	/**
	 * @return location pitch.
	 */
	public float getPitch() {
		return pitch;
	}
	
	/**
	 * @return location yaw.
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Get world name.
	 * 
	 * @return the world name.
	 */
	public String getWorld() {
		return world;
	}

	/**
	 * Get the bukkit world.
	 * 
	 * @return this bukkit world.
	 */
	public World getBukkitWorld() {
		// check world name.
		if (world != null) {
			// get world from bukkit.
			final World bwrl = Bukkit.getWorld(world);
			
			// if the world is null, check world folder addres.
			if (bwrl == null) {
				return Bukkit.getWorld(YmlUtils.getVerifyUniversalPCAddres(world));
			} else { // if the world is not null, return it.
				return bwrl;
			}
		}
		return null;
	}
	
	/**
	 * Get direction.
	 * 
	 * @return the direction.
	 */
	public BlockFace getDirection() {
		return direction;
	}
	
    public double distance(Loc o) {
        return Math.sqrt(distanceSquared(o));
    }
    
    public double distanceExcludingX(Loc o) {
        return Math.sqrt(distanceSquaredExcludingX(o));
    }
    
    public double distanceExcludingY(Loc o) {
        return Math.sqrt(distanceSquaredExcludingY(o));
    }
    
    public double distanceExcludingZ(Loc o) {
        return Math.sqrt(distanceSquaredExcludingZ(o));
    }
	
	public double distanceSquared(Loc o) {
		if (o == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null location");
		}

		return NumberConversions.square(x.doubleValue() - o.x.doubleValue())
				+ NumberConversions.square(y.doubleValue() - o.y.doubleValue())
				+ NumberConversions.square(z.doubleValue() - o.z.doubleValue());
	}
    
	public double distanceSquaredExcludingX(Loc o) {
		if (o == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null location");
		}

		return NumberConversions.square(y.doubleValue() - o.y.doubleValue())
				+ NumberConversions.square(z.doubleValue() - o.z.doubleValue());
	}

	public double distanceSquaredExcludingY(Loc o) {
		if (o == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null location");
		}

		return NumberConversions.square(x.doubleValue() - o.x.doubleValue())
				+ NumberConversions.square(z.doubleValue() - o.z.doubleValue());
	}

	public double distanceSquaredExcludingZ(Loc o) {
		if (o == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null location");
		}

		return NumberConversions.square(x.doubleValue() - o.x.doubleValue())
				+ NumberConversions.square(y.doubleValue() - o.y.doubleValue());
	}
	
	/**
	 * Get a bukkit location from this.
	 * 
	 * @return a bukkit location from this coords.
	 */
	public Location toLocation() {
		// check ejes.
		if (world == null || x == null || y == null || z == null) {
			return null;
		}
		return new Location(getBukkitWorld(), x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw, pitch);
	}
	
	public Vector3d toVector3d() {
		return new Vector3d(x.doubleValue(), y.doubleValue(), z.doubleValue());
	}

	public Vector3i toVector3i() {
		return new Vector3i(getBlockX(), getBlockY(), getBlockZ());
	}
	
	/**
	 * Set world.
	 * 
	 * @param newWorld the world
	 */
	public void setWorld(final World newWorld) {
		if (newWorld != null && newWorld.getName() != null) {
			this.world = newWorld.getName();
		}
	}

	/**
	 * Set world from his name.
	 * 
	 * @param name the world name.
	 * @return this.
	 */
	public Loc setWorld(final String name) {
		this.world = name;
		return this;
	}
	
	/**
	 * Set direction.
	 * 
	 * @param face the direction.
	 * @return this.
	 */
	public Loc setDirection(final BlockFace face) {
		direction = face;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj instanceof Loc) {
			Loc l = (Loc) obj;
			return world.equals(l.world) && this.getBlockX() == l.getBlockX() && this.getBlockY() == l.getBlockY()
					&& this.getBlockZ() == l.getBlockZ();
		} else if (obj instanceof Location) {
			Location l = (Location) obj;
			return world.equals(l.getWorld().getName()) && this.getBlockX() == l.getBlockX()
					&& this.getBlockY() == l.getBlockY() && this.getBlockZ() == l.getBlockZ();
		}

		return false;
	}

	/**
	 * Save this loc.
	 * 
	 * @param section the section to save.
	 * @return the number of changes.
	 */
	public int saveToConfig(ConfigurationSection section) {
		int save = 0;
		save += YmlUtils.setNotEqual(section, "World", world);
		save += YmlUtils.setNotEqual(section, "X", x.doubleValue());
		save += YmlUtils.setNotEqual(section, "Y", y.doubleValue());
		save += YmlUtils.setNotEqual(section, "Z", z.doubleValue());
		save += YmlUtils.setNotEqual(section, "Pitch", (double) pitch);
		save += YmlUtils.setNotEqual(section, "Yaw",   (double) yaw);
		save += (direction != null ? YmlUtils.setNotSet(section, "FaceDirection", direction.name()) : 0);
		return save > 0 ? 1 : 0;
	}
	
	/**
	 * Save this loc.
	 * 
	 * @param section the section to save.
	 * @return the number of changes.
	 */
	public int saveToConfig(ConfigurationSection section, boolean saveWorld, boolean savePitch, boolean saveYaw) {
		int save = 0;
		save += saveWorld ? YmlUtils.setNotEqual(section, "World", world) : 0;
		save += YmlUtils.setNotEqual(section, "X", x.doubleValue());
		save += YmlUtils.setNotEqual(section, "Y", y.doubleValue());
		save += YmlUtils.setNotEqual(section, "Z", z.doubleValue());
		save += savePitch ? YmlUtils.setNotEqual(section, "Pitch", (double) pitch) : 0;
		save += saveYaw ? YmlUtils.setNotEqual(section, "Yaw",   (double) yaw) : 0;
		save += (direction != null ? YmlUtils.setNotSet(section, "FaceDirection", direction.name()) : 0);
		return save > 0 ? 1 : 0;
	}

	/**
	 * Clone this.
	 */
	public Loc clone() {
		try {
			return (Loc) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	@Override
	public String toString() {
		return "World: " + this.getWorld() + " X:" + x + " Y:" + y + " Z:" + z;
	}
}
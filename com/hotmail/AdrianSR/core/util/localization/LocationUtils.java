package com.hotmail.AdrianSR.core.util.localization;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.riding.util.StandBlockFace;
import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;

/**
 * Represents a Localization util class.
 * 
 * @author AdrianSR
 */
public class LocationUtils {
	
	/**
	 * Check if a Loc is valid.
	 * 
	 * @param loc the Loc to check.
	 * @return true if is valid.
	 */
	public static boolean isValidLoc(final Loc loc) {
		return loc != null && isValidLocation(loc.toLocation());
	}
	
	/**
	 * Check if a Location is valid.
	 * 
	 * @param location the Location to check.
	 * @return true if is valid.
	 */
	public static boolean isValidLocation(final Location location) {
		return location != null && location.getWorld() != null;
	}

	public static String format(Location location, boolean append_yaw, boolean append_pitch, boolean append_world) {
		return (         location.getBlockX() 
				+ ", " + location.getBlockY() 
				+ ", " + location.getBlockZ())
				+ ( append_yaw   ? ", " + location.getYaw()             : "" )
				+ ( append_pitch ? ", " + location.getPitch()           : "" )
				+ ( append_world ? ", (at '" + location.getWorld().getName() + "')" : "" );
	}
	
	public static String format(Location location, boolean append_yaw, boolean append_pitch) {
		return format(location, append_yaw, append_pitch, true);
	}

	public static String format(Location location) {
		return format(location, true, true);
	}
	
	/**
	 * Rotate an {@link ArmorStand}.
	 * 
	 * @param s the armor stand.
	 * @param amount the rotating amount.
	 * @param left rotate to left? (if left, the armor will be rotated to the left, else will be rotated to the right)
	 */
//	public static void rotateStand(final ArmorStand s, final float amount, boolean left) {
//		final Location from = s.getLocation().clone();
//		final Location   to = from;
//		to.setYaw(left ? (to.getYaw() - amount) : (to.getYaw() + amount)); // - 90, and  + 90
//		moveStand(s, to);
//	}
//	
//	/**
//	 * Move an {@link ArmorStand}.
//	 * 
//	 * @param s the armor stand.
//	 * @param to where to.
//	 */
//	public static void moveStand(final ArmorStand s, final Location to) {
//		final EntityArmorStand nms = ((CraftArmorStand)s).getHandle();
//		nms.setPositionRotation(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
//	}
//	
//	/**
//	 * Get nms entity Pitch.
//	 * 
//	 * @param s the entity.
//	 * @return the nms entity pitch.
//	 */
//	public static float getDeepYaw(final ArmorStand s) {
//		final EntityArmorStand nms = ((CraftArmorStand)s).getHandle();
//		return nms.yaw;
//	}
//	
//	/**
//	 * Get nms entity Pitch.
//	 * 
//	 * @param s the entity.
//	 * @return the nms entity pitch.
//	 */
//	public static float getDeepPitch(final ArmorStand s) {
//		final EntityArmorStand nms = ((CraftArmorStand)s).getHandle();
//		return nms.pitch;
//	}
	
	/**
	 * @return a Location with cleaned Yaw.
	 */
	public static Location clearYaw(final Location loc) {
		loc.setYaw(0.0F);
		return loc;
	}
	
	/**
	 * @return a Location with changed Yaw.
	 */
	public static Location setYaw(final Location loc, final float yaw) {
		loc.setYaw(yaw);
		return loc;
	}
	
	/**
	 * @return a Location with cleaned Pitch.
	 */
	public static Location clearPitch(final Location loc) {
		loc.setPitch(0.0F);
		return loc;
	}
	
	/**
	 * @return a Location with changed Pitch.
	 */
	public static Location setPitch(final Location loc, final float pitch) {
		loc.setPitch(pitch);
		return loc;
	}
	
	/**
	 * Get a block list between two points.
	 * 
	 * @param loc1 the corner 1.
	 * @param loc2 the corner 2.
	 * @return the block list.
	 */
	public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
		List<Block> blocks = new ArrayList<Block>();
		int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
		int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
		int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
		int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
		int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
		int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Block block = loc1.getWorld().getBlockAt(x, y, z);
					blocks.add(block);
				}
			}
		}
		return blocks;
	}
	
	/**
	 * Get circle around of location.
	 * 
	 * @param center the center location.
	 * @param radius the circle radius.
	 * @param amount the circle definition.
	 * @return a list with circle locations.
	 */
	public static ArrayList<Location> getCircle(Location center, double radius, int amount) {
		World world = center.getWorld();
		double increment = (2 * Math.PI) / amount;
		ArrayList<Location> locations = new ArrayList<Location>();
		for (int i = 0; i < amount; i++) {
			double angle = i * increment;
			double x = center.getX() + (radius * Math.cos(angle));
			double z = center.getZ() + (radius * Math.sin(angle));
			locations.add(new Location(world, x, center.getY(), z));
		}
		return locations;
	}
	
	/**
	 * Get cuboid around of location.
	 * 
	 * @param center the center location.
	 * @param radius the cuboid radius.
	 * @param amount the cuboid definition.
	 * @return a list with circle locations.
	 */
	public static ArrayList<Location> getCuboid(Location center, double radius, int amount) {
		// make list.
		ArrayList<Location> locations = new ArrayList<Location>();
		
		/* check location */
		if (!LocationUtils.isValidLocation(center)) {
			return locations;
		}

		// get main ejes.
		final double mainX = center.getX();
		final double mainY = center.getY();
		final double mainZ = center.getZ();

		// get cuboid.
		World world = center.getWorld();
		for (int z = 0; z < 2; z++) {
			double newZ = z == 0 ? (mainZ - radius) : (mainZ + radius);
			Location corner = new Location(world, mainX - radius, mainY, newZ);
			Location corner2 = new Location(world, mainX + radius, mainY, newZ);
			double increment = (corner.distance(corner2) / amount);
			for (int i = 0; i < (amount + 1); i++) {
				locations.add(corner.clone().add(increment * i, 0.0D, 0.0D));
			}
		}

		for (int x = 0; x < 2; x++) {
			double newX = x == 0 ? (mainX - radius) : (mainX + radius);
			Location corner = new Location(world, newX, mainY, mainZ - radius);
			Location corner2 = new Location(world, newX, mainY, mainZ + radius);
			double increment = (corner.distance(corner2) / amount);
			for (int i = 0; i < (amount + 1); i++) {
				locations.add(corner.clone().add(0.0D, 0.0D, increment * i));
			}
		}
		return locations;
	}
	
	/**
	 * Point a location to another location.
	 * <p>
	 * @param fromLocation the location from.
	 * @param to the target location.
	 * @return the calculated Yaw.
	 */
	public static float pointLocationTo(final Location fromLocation, final Location to) {
		double xDiff = ( to.getX() - fromLocation.getX() ); // the difference between the X axis
		double zDiff = ( to.getZ() - fromLocation.getZ() ); // the difference between the Z axis

		// distance between the two locations, but excluding the distance in the axis Y
		double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff); 
		double        yaw = Math.toDegrees(Math.acos(xDiff / distanceXZ));
		
		if (zDiff < 0.0D) {
			yaw += ( Math.abs(180.0D - yaw) * 2.0D );
		}
		return (float) (yaw - 90.0F);
	}
	
	/**
	 * Make entity look to location.
	 * 
	 * @param entity the entity to make.
	 * @param to the target location.
	 * @return the location to look.
	 */
	public static Location entityLookToLocation(Entity entity, Location to) {
		if (entity.getWorld() != to.getWorld()) {
			return null;
		}

		final Location fromLocation = entity.getLocation();
		double xDiff = (to.getX() - fromLocation.getX());
		double yDiff = (to.getY() - fromLocation.getY());
		double zDiff = (to.getZ() - fromLocation.getZ());

		double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		double distanceY = Math.sqrt(distanceXZ * distanceXZ + yDiff * yDiff);

		double yaw = Math.toDegrees(Math.acos(xDiff / distanceXZ));
		double pitch = (Math.toDegrees(Math.acos(yDiff / distanceY)) - 90.0D);
		if (zDiff < 0.0D) {
			yaw += (Math.abs(180.0D - yaw) * 2.0D);
		}

		Location loc = entity.getLocation();
		loc.setYaw((float) (yaw - 90.0F));
		loc.setPitch((float) (pitch - 90.0F));
		return loc;
	}
	
	public static boolean isInsideOfBorder(final Player p, final WorldBorder border) {
		return isInsideOfBorder(p.getLocation(), border);
	}
	
	public static boolean isInsideOfBorder(final Location location, final WorldBorder border) {
		try {
			/* get world of the world border */
			final World world = border.getCenter().getWorld();
			
			/* load reflection */
			final Class<?> craft_world_border_class         = border.getClass();
//			final Class<?> nms_world_border                 = ReflectionUtils.getCraftClass("WorldBorder");
			final Class<?> block_position_class             = ReflectionUtils.getCraftClass("BlockPosition");
			final Constructor<?> block_position_constructor = block_position_class.getConstructor(double.class, double.class, double.class);
			
			/* get instances */
			final Object craft = craft_world_border_class.cast(border);
			final Object handle = FieldUtils.readField(craft, "handle", true);
			final Object block_position = block_position_constructor.newInstance(location.getX(), location.getY(), location.getZ());
			
			// check is inside conparing world and invoking method "a".
			final Method a = handle.getClass().getMethod("a", block_position_class);
			return (location.getWorld().equals(world)) && (boolean) (a.invoke(handle, block_position));
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Add a {@link Blockface} mod
	 * axis to {@link Location}.
	 * <p>
	 * @param location The {@link Location}.
	 * @param face The {@link BlockFace}.
	 * @return modified location.
	 */
	public static Location add(final Location location, final BlockFace face) {
		add(location, face, 1);
		return location;
	}

	/**
	 * Add a {@link BlockFace} mod
	 * axis to {@link Location}.
	 * <p>
	 * @param location The {@link Location}.
	 * @param face The {@link BlockFace}.
	 * @param num The blocks amount.
	 * @return modified location.
	 */
	public static Location add(final Location location, final BlockFace face, double num) {
//		location.add((face.getModX() != 0 ? (face.getModX() * Math.max(num, 1)) : face.getModX()),
//				(face.getModY() != 0 ? (face.getModY() * Math.max(num, 1)) : face.getModY()),
//				(face.getModZ() != 0 ? (face.getModZ() * Math.max(num, 1)) : face.getModZ()));
		location.add(face.getModX() * num, face.getModY() * num, face.getModZ() * num);
		return location;
	}
	
	/**
	 * Add a {@link StandBlockFace} mod
	 * axis to {@link Location}.
	 * <p>
	 * @param location The {@link Location}.
	 * @param face The {@link StandBlockFace}.
	 */
	public static void add(final Location location, final StandBlockFace face) {
		add(location, face, 1);
	}
	
	/**
	 * Add a {@link StandBlockFace} mod
	 * axis to {@link Location}.
	 * <p>
	 * @param location The {@link Location}.
	 * @param face The {@link StandBlockFace}.
	 * @param num The blocks amount.
	 */
	public static void add(final Location location, final StandBlockFace face, double num) {
		location.add(face.getModX() * num, face.getModY() * num, face.getModZ() * num);
	}
	
	/**
	 * Rotate a {@link Vector}.
	 * 
	 * @param vector the Vector to rotate.
	 * @param yawDegrees the yaw degress.
	 * @param pitchDegrees the pitch degress.
	 * @return a new rotated Vector.
	 */
	public static final Vector rotateVector(Vector vector, float yawDegrees, float pitchDegrees) {
		// get radians.
        double   yaw = Math.toRadians(-yawDegrees);
        double pitch = Math.toRadians(-pitchDegrees);

        // get yaw/pitch sine and cosine.
        double cosYaw   = Math.cos(yaw);
        double cosPitch = Math.cos(pitch);
        double sinYaw   = Math.sin(yaw);
        double sinPitch = Math.sin(pitch);

        // axis.
        double initialX, initialY, initialZ;
        double x, y, z;

        // Z_axis rotation (Pitch)
        initialX = vector.getX();
        initialY = vector.getY();
        x        = initialX * cosPitch - initialY * sinPitch;
        y        = initialX * sinPitch + initialY * cosPitch;

        // Y_axis rotation (Yaw)
        initialZ = vector.getZ();
        initialX = x;
        z        = initialZ * cosYaw - initialX * sinYaw;
        x        = initialZ * sinYaw + initialX * cosYaw;
        
        // return a new vector with calculated axis.
        return new Vector(x, y, z);
    }
}
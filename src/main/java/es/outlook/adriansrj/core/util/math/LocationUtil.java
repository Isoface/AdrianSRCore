package es.outlook.adriansrj.core.util.math;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;

/**
 * Useful class for dealing with locations.
 * <p>
 * @author AdrianSR / Thursday 16 April, 2020 / 01:55 PM
 */
public class LocationUtil {
	
	/**
	 * Formats the desired {@link location}.
	 * <p>
	 * @param location the location to format.
	 * @param append_yaw append yaw?
	 * @param append_pitch append pitch?
	 * @param append_world append world name?
	 * @return the formatted location.
	 */
	public static String format(Location location, boolean append_yaw, boolean append_pitch, boolean append_world) {
		return (         location.getBlockX() 
				+ ", " + location.getBlockY() 
				+ ", " + location.getBlockZ())
				+ ( append_yaw   ? ", " + location.getYaw()             : "" )
				+ ( append_pitch ? ", " + location.getPitch()           : "" )
				+ ( append_world ? ", (at '" + location.getWorld().getName() + "')" : "" );
	}
	
	/**
	 * Rounds up the yaw angle of the provided location.
	 * <p>
	 * @param location the location to round.
	 * @return the location with its yaw angle rounded up.
	 */
	public static Location roundUpYaw ( Location location ) {
		location.setYaw ( DirectionUtil.getYaw ( DirectionUtil.getBlockFace90 ( location.getYaw ( ) ) ) );
		return location;
	}
	
	/**
	 * Rounds up the pitch angle of the provided location.
	 * <p>
	 * @param location the location to round.
	 * @return the location with its pitch angle rounded up.
	 */
	public static Location roundUpPitch ( Location location ) {
		location.setPitch ( location.getPitch ( ) != 0F ? ( location.getPitch ( ) < 0F ? -90F : 90F ) : 0F );
		return location;
	}

	/**
	 * Gets all the blocks between the provided location corners.
	 * <p>
	 * @param corner_a the first corner.
	 * @param corner_b the second corner.
	 * @return a set containing the blocks between the provided locations.
	 * @throws IllegalArgumentException for differing worlds.
	 */
	public static Set < Block > getBlocksBetween ( Location corner_a , Location corner_b ) {
		if ( corner_a.getWorld ( ) != corner_b.getWorld ( ) ) {
			throw new IllegalArgumentException ( "cannot get blocks between " + corner_a.getWorld().getName ( ) + " and " 
					+ corner_b.getWorld().getName ( ) );
		}
		
		final Set < Block > blocks = new HashSet<>();
		
		int min_x = Math.min ( corner_a.getBlockX ( ) , corner_b.getBlockX ( ) );
		int min_y = Math.min ( corner_a.getBlockY ( ) , corner_b.getBlockY ( ) );
		int min_z = Math.min ( corner_a.getBlockZ ( ) , corner_b.getBlockZ ( ) );
		int max_x = Math.max ( corner_a.getBlockX ( ) , corner_b.getBlockX ( ) );
		int max_y = Math.max ( corner_a.getBlockY ( ) , corner_b.getBlockY ( ) );
		int max_z = Math.max ( corner_a.getBlockZ ( ) , corner_b.getBlockZ ( ) );
		
		for ( int x = min_x ; x <= max_x ; x ++ ) {
			for ( int y = min_y ; y <= max_y ; y ++ ) {
				for ( int z = min_z ; z <= max_z ; z ++ ) {
					blocks.add ( corner_a.getWorld().getBlockAt ( x , y , z ) );
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

	public static boolean isInsideWorldBorder(final Player p, final WorldBorder border) {
		return isInsideWorldBorder(p.getLocation(), border);
	}

	public static boolean isInsideWorldBorder(final Location location, final WorldBorder border) {
		try {
			/* get world of the world border */
			final World world = border.getCenter().getWorld();

			/* load reflection */
			final Class<?> craft_world_border_class = border.getClass();
			final Class<?> block_position_class = ClassReflection.getNmsClass("BlockPosition");
			final Constructor<?> block_position_constructor = block_position_class.getConstructor(double.class,
					double.class, double.class);

			/* get instances */
			final Object craft = craft_world_border_class.cast(border);
			final Object handle = FieldUtils.readField(craft, "handle", true);
			final Object block_position = block_position_constructor.newInstance(location.getX(), location.getY(),
					location.getZ());

			// check is inside conparing world and invoking method "a".
			final Method a = handle.getClass().getMethod("a", block_position_class);
			return (location.getWorld().equals(world)) && (boolean) (a.invoke(handle, block_position));
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}

	/**
	 * Add a {@link Blockface} mod axis to {@link Location}.
	 * <p>
	 * @param location The {@link Location}.
	 * @param face     The {@link BlockFace}.
	 * @return modified location.
	 */
	public static Location add(final Location location, final BlockFace face) {
		add(location, face, 1);
		return location;
	}

	/**
	 * Add a {@link BlockFace} mod axis to {@link Location}.
	 * <p>
	 * @param location The {@link Location}.
	 * @param face     The {@link BlockFace}.
	 * @param num      The blocks amount.
	 * @return modified location.
	 */
	public static Location add(final Location location, final BlockFace face, double num) {
		location.add(face.getModX() * num, face.getModY() * num, face.getModZ() * num);
		return location;
	}
}
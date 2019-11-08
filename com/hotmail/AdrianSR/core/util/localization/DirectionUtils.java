package com.hotmail.AdrianSR.core.util.localization;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Represents a util class to get the block face directions.
 * 
 * @author AdrianSR
 */
public class DirectionUtils {
	
	/**
	 * The Global 90° BlockFaces.
	 */
	public static final BlockFace[] FACES_90 = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	
	/**
	 * Get {@link BlockFace} from yaw.
	 * 
	 * @param yaw the yaw.
	 * @return the yaw BlockFace direction.
	 */
	public static BlockFace getFacingDirection(float yaw) {
		int index = (int) Math.round((wrapAngle(yaw) + 180.0F) / 90.0D);
		if (index > 3) {
			index = 0;
		}
		return FACES_90[index];
	}
	
	/**
	 * Get left {@link Block}.
	 * 
	 * @param block the Block.
	 * @param direction the base BlockFace.
	 * @param numBlocks the number of blocks.
	 * @return the left Block.
	 */
	public static Block getLeft(Block block, BlockFace direction, int numBlocks) {
		BlockFace bf = getLeftFace(direction);
		return block.getRelative( 
			 bf.getModX() * numBlocks, 
			 bf.getModY() * numBlocks,
			 bf.getModZ() * numBlocks);
	}

	/**
	 * Get rigth {@link Block}.
	 * 
	 * @param block the Block.
	 * @param direction the base BlockFace.
	 * @param numBlocks the number of blocks.
	 * @return the rigth Block.
	 */
	public static Block getRight(Block block, BlockFace direction, int numBlocks) {
		BlockFace bf = getRightFace(direction);
		return block.getRelative(
				bf.getModX() * numBlocks, 
				bf.getModY() * numBlocks, 
				bf.getModZ() * numBlocks);
	}

	/**
	 * Get the {@link BlockFace} at the right of other BlockFace.
	 * 
	 * @param direction the base BlockFace.
	 * @return the right BlockFace.
	 */
	public static BlockFace getRightFace(BlockFace direction) {
		return getLeftFace(direction).getOppositeFace();
	}

	/**
	 * Get the {@link BlockFace} at the left of other BlockFace.
	 * 
	 * @param direction the base BlockFace.
	 * @return the left BlockFace.
	 */
	public static BlockFace getLeftFace(BlockFace direction) {
		switch (direction) {
		case SOUTH:
			return BlockFace.EAST;
		case EAST:
			return BlockFace.NORTH;
		case NORTH:
			return BlockFace.WEST;
		case WEST:
			return BlockFace.SOUTH;
		default:
			break;
		}
		return BlockFace.NORTH;
	}
	
	/**
	 * Get yaw from a {@link BlockFace}
	 * 
	 * @param face the BlockFace
	 * @return the BlockFace yaw.
	 */
	public static float getYaw(final BlockFace face) {
		switch(face) {
		case EAST:
			return -90.0F;
		case NORTH:
			return 180.0F;
		case SOUTH:
			return 0.0F;
		case WEST:
			return 90.0F;
		default:
			return 0.0F;
		}
	}

	public static float wrapAngle(float angle) {
		float wrappedAngle = angle;
		while (wrappedAngle <= -180.0F) {
			wrappedAngle += 360.0F;
		}

		while (wrappedAngle > 180.0F) {
			wrappedAngle -= 360.0F;
		}

		return wrappedAngle;
	}
	
	/**
	 * Resolve a yaw, setting
	 * it between 0 and 360.
	 * <p>
	 * @param yaw The yaw to resolve.
	 * @return resolved yaw.
	 */
	public static float resolveTo360(final float yaw) {
		return (yaw + 360) % 360;
	}
	
	/**
	 * Resolve a yaw, setting
	 * it between 0 and 360. (raw)
	 * <p>
	 * @param yaw The yaw to resolve.
	 * @return resolved yaw.
	 */
	public static float resolveTo360Raw(final float yaw) {
		return yaw % 360;
	}
	
	/**
	 * Round up a {@link Location}
	 * yaw.
	 * <p>
	 * @param loc the Location.
	 * @return the Location with a rounded up yaw.
	 */
	public static Location RoundUpYaw(final Location loc) {
		// get original yaw.
		final float oriYaw = loc.getYaw();
		
		// get face.
		final BlockFace face = DirectionUtils.getFacingDirection(oriYaw);
		
		// get and set the new yaw.
		loc.setYaw(DirectionUtils.getYaw(face));
		return loc;
	}
	
	/**
	 * Get player in a player
	 * sight.
	 * <p>
	 * @param player the player that is looking at.
	 * @param distance the max distance that the target player can be detected.
	 * @return the lplayer in sight or null if could not be found.
	 */
	public static Player getPlayerInSight(Player player, int distance) {
		final Entity inSight = getEntityInSight(player, distance);
		return inSight instanceof Player ? (Player) inSight : null;
	}
	
	public static Entity getEntityInSight(Player player, int distance) {
		Location playerLoc = player.getLocation();
		Vector3D playerDirection = new Vector3D(playerLoc.getDirection());
		Vector3D start = new Vector3D(playerLoc);
		Vector3D end = start.add(playerDirection.multiply(distance));
		Entity inSight = null;
		for (Entity nearbyEntity : player.getNearbyEntities(distance, distance, distance)) {
			if (nearbyEntity != null) {
				Vector3D nearbyLoc = new Vector3D(nearbyEntity.getLocation());

				// Bounding box
				Vector3D min = nearbyLoc.subtract(0.5D, 1.6D, 0.5D);
				Vector3D max = nearbyLoc.add(0.5D, 0.3D, 0.5D);

				if (hasIntersection(start, end, min, max)) {
					if (inSight == null || inSight.getLocation().distanceSquared(playerLoc) > nearbyEntity.getLocation().distanceSquared(playerLoc)) {
						inSight = nearbyEntity;
						return inSight;
					}
				}
			}
		}
		return inSight;
	}
	
	public static Player getTargetPlayer(Player player, double range) {
		// get entity.
		Entity target = getTargetEntity(player, range);
		
		// check entity
		return target instanceof Player ? (Player) target : null;
	}
	
	public static Entity getTargetEntity(Player player, double range) {
		return getTargetEntity(player, 0.15D, player.getEyeHeight() * 0.07, 0.15D, range);
	}
	
	public static Entity getTargetEntity(Player player, double near_x, double near_y, double near_z, double range) {
		// get list.
		final List<Entity> targets = getTargetEntities(player, near_x, near_y,near_z, range);
		
		// get first entity in list.
		return targets.size() > 0 ? targets.get(0) : null;
	}
	
	public static List<Entity> getTargetEntities(Player player, double range) {
		return getTargetEntities(player, 0.15D, player.getEyeHeight() * 0.07, 0.15D, range);
	}
	
	public static List<Entity> getTargetEntities(Player player, double near_x, double near_y, double near_z, double range) {
		// make list.
		final List<Entity> targets = new ArrayList<Entity>();
		
		// get the player's direction.
		Vector dir = player.getLocation().getDirection();
		// get the player's eye location.
		Location loc = player.getEyeLocation();

		// look entities in range.
		for (double i = 1; i <= range; i++) {
			// move the location forward.
			loc.add(dir.getX() * i, dir.getY() * i, dir.getZ() * i);

			// get entities around 'loc'.
			for (Entity ent : loc.getWorld().getNearbyEntities(loc, near_x, near_y, near_z)) {
				// check is not it'self.
				if (!ent.getUniqueId().equals(player.getUniqueId())) {
					// entity found!
					targets.add(ent);
				}
			}
			
			// move loc back.
			loc.subtract(dir.getX() * i, dir.getY() * i, dir.getZ() * i);
		}
		return targets;
	}
	
	private static boolean hasIntersection(Vector3D start, Vector3D end, Vector3D min, Vector3D max) {
		final double epsilon = 0.0001f;

		Vector3D d = end.subtract(start).multiply(0.5);
		Vector3D e = max.subtract(min).multiply(0.5);
		Vector3D c = start.add(d).subtract(min.add(max).multiply(0.5));
		Vector3D ad = d.abs();

		if (Math.abs(c.getX()) > e.getX() + ad.getX()) {
			return false;
		}

		if (Math.abs(c.getY()) > e.getY() + ad.getY()) {
			return false;
		}

		if (Math.abs(c.getZ()) > e.getX() + ad.getZ()) {
			return false;
		}

		if (Math.abs(d.getY() * c.getZ() - d.getZ() * c.getY()) > e.getY() * ad.getZ() + e.getZ() * ad.getY()
				+ epsilon) {
			return false;
		}

		if (Math.abs(d.getZ() * c.getX() - d.getX() * c.getZ()) > e.getZ() * ad.getX() + e.getX() * ad.getZ()
				+ epsilon) {
			return false;
		}

		if (Math.abs(d.getX() * c.getY() - d.getY() * c.getX()) > e.getX() * ad.getY() + e.getY() * ad.getX()
				+ epsilon) {
			return false;
		}
		return true;
	}
	
	public static Vector getDirection(final float yaw, final float pitch) {
		// make new vector.
		final Vector vector = new Vector();

		// set y.
		vector.setY(-Math.sin(Math.toRadians(pitch)));

		// set x and z.
		double xz = Math.cos(Math.toRadians(pitch));
		vector.setX(-xz * Math.sin(Math.toRadians(yaw)));
		vector.setZ(xz * Math.cos(Math.toRadians(yaw)));
		return vector;
	}
}

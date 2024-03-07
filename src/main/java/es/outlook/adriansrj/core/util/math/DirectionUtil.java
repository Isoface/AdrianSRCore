package es.outlook.adriansrj.core.util.math;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * Useful class for dealing with direction {@link Vector}s and EULER angles.
 * <p>
 * @author AdrianSR / Thursday 16 April, 2020 / 02:07 PM
 */
public class DirectionUtil {
	
	/**
	 * BlockFace each 90°.
	 */
	public static final BlockFace[] FACES_90 = {
			BlockFace.SOUTH , BlockFace.WEST , BlockFace.NORTH , BlockFace.EAST
	};
	
	/**
	 * BlockFace each 45°.
	 */
	public static final BlockFace[] FACES_45 = {
			BlockFace.SOUTH , BlockFace.SOUTH_WEST ,
			BlockFace.WEST , BlockFace.NORTH_WEST ,
			BlockFace.NORTH , BlockFace.NORTH_EAST ,
			BlockFace.EAST , BlockFace.SOUTH_EAST
	};
	
	/**
	 * Normalizes the provided angle to a value between 0° and 360°.
	 * <p>
	 * @param angle the angle to normalize.
	 * @return the normalized angle.
	 */
	public static float normalize ( float angle ) {
		return ( angle + 360F ) % 360F;
	}
	
	/**
	 * Normalizes the provided angle to a value between +180° and -180°.
	 * <p>
	 * @param angle the angle to normalize.
	 * @return the normalized angle.
	 */
	public static float normalize2 ( float angle ) {
		angle = normalize ( angle );
		return angle >= 180F
				? ( angle - 360F )
				: ( angle < -180F ? ( angle + 360F ) : angle );
	}
	
	/**
	 * Gets the equivalent {@link BlockFace} of the provided yaw angle (<b>each 90°</b>).
	 * <p>
	 * @param yaw     the yaw angle.
	 * @param precise true for a precise calculation.
	 * @return the {@link BlockFace} equivalent of the provided yaw angle.
	 */
	public static BlockFace getBlockFace90 ( float yaw , boolean precise ) {
		if ( precise ) {
			return FACES_90[ ( int ) Math.floor ( normalize ( yaw ) / 90F ) % FACES_90.length ];
		} else {
			return FACES_90[ ( NumberConversions.floor ( ( double ) ( yaw * 4.0F / 360.0F ) + 0.5D ) & 3 )
					% FACES_90.length ];
		}
	}
	
	/**
	 * Gets the equivalent {@link BlockFace} of the provided yaw angle (<b>each 95°</b>).
	 * <p>
	 * @param yaw     the yaw angle.
	 * @return the {@link BlockFace} equivalent of the provided yaw angle.
	 */
	public static BlockFace getBlockFace45 ( float yaw ) {
		return FACES_45[ ( NumberConversions.floor (
				( double ) ( yaw * ( ( float ) FACES_45.length ) / 360.0F ) + 0.5D ) & ( FACES_45.length - 1 ) )
				% FACES_45.length ];
	}
	
	/**
	 * Gets the equivalent {@link BlockFace} of the provided yaw angle.
	 * <p>
	 * @param yaw the yaw angle.
	 * @return the {@link BlockFace} equivalent of the provided yaw angle.
	 */
	public static BlockFace getBlockFace90 ( float yaw ) {
		return getBlockFace90 ( yaw , true );
	}
	
	/**
	 * Gets the equivalent yaw angle of the provided {@link BlockFace}.
	 * <p>
	 * Also <strong>{@code NaN}</strong> will be returned if the provided block face
	 * is not a 45° face. It means that only the following block faces are
	 * supported:
	 * <ul>
	 * <li> {@link BlockFace#SOUTH}
	 * <li> {@link BlockFace#SOUTH_WEST}
	 * <li> {@link BlockFace#WEST}
	 * <li> {@link BlockFace#NORTH_WEST}
	 * <li> {@link BlockFace#NORTH}
	 * <li> {@link BlockFace#NORTH_EAST}
	 * <li> {@link BlockFace#EAST}
	 * <li> {@link BlockFace#SOUTH_EAST}
	 * <ul>
	 * <p>
	 * @param blockface the desired block face.
	 * @return the equivalent yaw angle of the provided {@link BlockFace}.
	 */
	public static float getYaw ( BlockFace blockface ) {
		switch ( blockface ) {
			case SOUTH:
				return 0.0F;
			case SOUTH_WEST:
				return 45.0F;
			case WEST:
				return 90F;
			case NORTH_WEST:
				return 135.0F;
			case NORTH:
				return 180F;
			case NORTH_EAST:
				return 225.0F;
			case EAST:
				return 270.0F;
			case SOUTH_EAST:
				return 315.0F;
			
			default:
				return Float.NaN;
		}
	}
	
	/**
	 * Gets the euler angles from the direction vector result of the subtraction of
	 * {@code to} and {@code from}.
	 * <p>
	 * <ul>
	 * <li> lookAt ( from , to ) [ 0 ] = yaw angle.
	 * <li> lookAt ( from , to ) [ 1 ] = pitch angle.
	 * </ul>
	 * <p>
	 * @param from the position from.
	 * @param to   the position to.
	 * @return the euler angles of the result direction vector.
	 */
	public static float[] lookAt ( Vector from , Vector to ) {
		final double dx = ( to.getX ( ) - from.getX ( ) );
		final double dy = ( to.getY ( ) - from.getY ( ) );
		final double dz = ( to.getZ ( ) - from.getZ ( ) );
		
		final double dst_xz = Math.sqrt ( NumberConversions.square ( dx ) + NumberConversions.square ( dz ) );
		final double dst_y  = Math.sqrt ( NumberConversions.square ( dst_xz ) + NumberConversions.square ( dy ) );
		
		double yaw   = Math.toDegrees ( Math.acos ( dx / dst_xz ) );
		double pitch = Math.toDegrees ( Math.acos ( dy / dst_y ) ) - 90D;
		
		if ( dz < 0D ) {
			yaw += Math.abs ( 180D - yaw ) * 2D;
		}
		
		yaw -= 90D;
		pitch -= 90D;
		return new float[] { ( float ) yaw , ( float ) pitch };
	}
	
	/**
	 * Gets the euler angles from the direction vector result of the subtraction of
	 * {@code to} and {@code from}.
	 * <p>
	 * <ul>
	 * <li> lookAt ( from , to ) [ 0 ] = yaw angle.
	 * <li> lookAt ( from , to ) [ 1 ] = pitch angle.
	 * </ul>
	 * <p>
	 * @param from the position from.
	 * @param to   the position to.
	 * @return the euler angles of the result direction vector.
	 * @see DirectionUtil#lookAt(Vector , Vector)
	 */
	public static float[] lookAt ( Location from , Location to ) {
		return lookAt ( from.toVector ( ) , to.toVector ( ) );
	}
	
	/**
	 * Gets the euler angles from the direction vector result of the subtraction of
	 * {@code to} and {@code from}.
	 * <p>
	 * <ul>
	 * <li> lookAt ( from , to ) [ 0 ] = yaw angle.
	 * <li> lookAt ( from , to ) [ 1 ] = pitch angle.
	 * </ul>
	 * <p>
	 * @param from the position from.
	 * @param to   the position to.
	 * @return the euler angles of the result direction vector.
	 */
	public static float[] lookAt2 ( Vector from , Vector to ) {
		return getEulerAngles ( to.clone ( ).subtract ( from ).normalize ( ) );
	}
	
	/**
	 * Gets the euler angles from the direction vector result of the subtraction of
	 * {@code to} and {@code from}.
	 * <p>
	 * <ul>
	 * <li>lookAt ( from , to ) [ 0 ] = yaw angle.
	 * <li>lookAt ( from , to ) [ 1 ] = pitch angle.
	 * </ul>
	 * <p>
	 * @param from the position from.
	 * @param to   the position to.
	 * @return the euler angles of the result direction vector.
	 * @see DirectionUtil#lookAt2(Vector , Vector).
	 */
	public static float[] lookAt2 ( Location from , Location to ) {
		return lookAt2 ( from.toVector ( ) , to.toVector ( ) );
	}
	
	/**
	 * Gets a unit-vector pointing in the direction represented by the specified
	 * euler angles ( {@code yaw} and {@code pitch} ).
	 * <p>
	 * @param yaw   the yaw angle ( rotation around axis X ).
	 * @param pitch the pitch angle ( rotation around axis Y ).
	 * @return a vector pointing the direction represented by the specified euler
	 *         angles.
	 */
	public static Vector getDirection ( float yaw , float pitch ) {
		Vector vector = new Vector ( );
		
		double rotX = yaw;
		double rotY = pitch;
		
		vector.setY ( -Math.sin ( Math.toRadians ( rotY ) ) );
		
		double xz = Math.cos ( Math.toRadians ( rotY ) );
		
		vector.setX ( -xz * Math.sin ( Math.toRadians ( rotX ) ) );
		vector.setZ ( xz * Math.cos ( Math.toRadians ( rotX ) ) );
		return vector;
	}
	
	/**
	 * Gets the euler angles from the provided direction {@link Vector}.
	 * <p>
	 * <ul>
	 * <li> getEulerAngles ( direction ) [ 0 ] = yaw angle.
	 * <li> getEulerAngles ( direction ) [ 1 ] = pitch angle.
	 * </ul>
	 * <p>
	 * @param direction the direction vector.
	 * @return the yaw and pitch angles within an array.
	 */
	public static float[] getEulerAngles ( Vector direction ) {
		float yaw   = 0F;
		float pitch = 0F;
		
		final double _2PI = 2 * Math.PI;
		final double x    = direction.getX ( );
		final double z    = direction.getZ ( );
		
		if ( x == 0 && z == 0 ) {
			pitch = direction.getY ( ) > 0 ? -90 : 90;
			return new float[] { yaw , pitch };
		}
		
		double theta = Math.atan2 ( -x , z );
		yaw = ( float ) Math.toDegrees ( ( theta + _2PI ) % _2PI );
		
		double x2 = NumberConversions.square ( x );
		double z2 = NumberConversions.square ( z );
		double xz = Math.sqrt ( x2 + z2 );
		
		pitch = ( float ) Math.toDegrees ( Math.atan ( -direction.getY ( ) / xz ) );
		return new float[] { yaw , pitch };
	}
	
	/**
	 * Get left {@link Block}.
	 * <p>
	 * @param block the Block.
	 * @param direction the base BlockFace.
	 * @param numBlocks the number of blocks.
	 * @return the left Block.
	 */
	public static Block getLeft ( Block block , BlockFace direction , int numBlocks ) {
		BlockFace bf = getLeftFace ( direction );
		return block.getRelative (
				bf.getModX ( ) * numBlocks ,
				bf.getModY ( ) * numBlocks ,
				bf.getModZ ( ) * numBlocks );
	}
	
	/**
	 * Get rigth {@link Block}.
	 * <p>
	 * @param block the Block.
	 * @param direction the base BlockFace.
	 * @param numBlocks the number of blocks.
	 * @return the rigth Block.
	 */
	public static Block getRight ( Block block , BlockFace direction , int numBlocks ) {
		BlockFace bf = getRightFace ( direction );
		return block.getRelative (
				bf.getModX ( ) * numBlocks ,
				bf.getModY ( ) * numBlocks ,
				bf.getModZ ( ) * numBlocks );
	}
	
	/**
	 * Get the {@link BlockFace} at the right of other BlockFace.
	 * <p>
	 * @param direction the base BlockFace.
	 * @return the right BlockFace.
	 */
	public static BlockFace getRightFace ( BlockFace direction ) {
		return getLeftFace ( direction ).getOppositeFace ( );
	}
	
	/**
	 * Get the {@link BlockFace} at the left of other BlockFace.
	 *
	 * @param direction the base BlockFace.
	 * @return the left BlockFace.
	 */
	public static BlockFace getLeftFace ( BlockFace direction ) {
		switch ( direction ) {
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
}
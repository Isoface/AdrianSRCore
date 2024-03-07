package es.outlook.adriansrj.core.util.math;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Encapsulates a immutable Vector. Methods like {@link #normalize()} will not
 * modify this Vector, and a new Vector containing the result will be returned
 * instead.
 * <p>
 * Also values like {@link #lenght()} are cached for a better performance.
 * <p>
 * @author AdrianSR / Monday 16 March, 2020 / 11:23 AM
 */
public interface Vector extends ConfigurationSerializable {
	
	/**
	 * A "close to zero" float epsilon value for use
	 */
	float FLOAT_EPSILON = Float.intBitsToFloat ( 0x34000000 );

	/**
	 * Gets the magnitude of the Vector, defined as sqrt ( x^2 + y^2 + z^2 ). The
	 * value of this method is cached, so repeatedly calling this method to get the
	 * vector's magnitude will not re-calculate it. NaN will be returned if the
	 * inner result of the sqrt() function overflows, which will be caused if the
	 * length is too long.
	 * <p>
	 * This is the equivalent of using:
	 * <dd><code>Math.sqrt ( {@link #lengthSquared()} )</code></dd>
	 * <p>
	 * @return the magnitude.
	 */
	float length ( );
	
	/**
     * Gets the magnitude of the vector squared.
     * <p>
     * @return the magnitude.
     */
	float lengthSquared ( );
	
	/**
	 * Gets the unit equivalent of this vector. ( a Vector with length of 1 ).
	 * <p>
	 * @return the created unit vector from this.
	 */
	Vector normalize ( );
	
	/**
	 * Gets the 2D equivalent of this vector.
	 * <p>
	 * @return the equivalent, or the same vector if called from an instance of the
	 *         same class.
	 */
	Vector2D toVector2D ( );
	
	/**
	 * Gets the 3D equivalent of this vector.
	 * <p>
	 * Note that calling this method from {@link Vector2D} will result in an
	 * {@link UnsupportedOperationException}. The method
	 * {@link Vector2D#toVector3D(double)} must be used instead.
	 * <p>
	 * @return the equivalent, or the same vector if called from an instance of the
	 *         same class.
	 */
	Vector3D toVector3D ( );
	
	/**
	 * Gets the Bukkit equivalent of this vector.
	 * <p>
	 * Note that calling this method from {@link Vector2D} will result in an
	 * {@link UnsupportedOperationException}. The method
	 * {@link Vector2D#toBukkit(double)} must be used instead.
	 * <p>
	 * @return the equivalent.
	 */
	org.bukkit.util.Vector toBukkit ( );
	
	/**
	 * Gets a Location version of this Vector.
	 * <p>
	 * Note that calling this method from {@link Vector2D} will result in an
	 * {@link UnsupportedOperationException}. The method
	 * {@link Vector2D#toLocation(World, double, float, float)} must be used instead.
	 * <p>
	 * @param world the world to link the location to.
	 * @param yaw the desired yaw.
	 * @param pitch the desired pitch.
	 * @return the location.
	 */
	Location toLocation ( final World world , final float yaw , final float pitch );
	
	/**
	 * Gets a Location version of this Vector with yaw and pitch being 0.
	 * <p>
	 * Note that calling this method from {@link Vector2D} will result in an
	 * {@link UnsupportedOperationException}. The method
	 * {@link Vector2D#toLocation(World, double)} must be used instead.
	 * <p>
	 * @param world the world to link the location to.
	 * @return the location.
	 */
	Location toLocation ( final World world );
}
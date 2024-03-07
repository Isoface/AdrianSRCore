package es.outlook.adriansrj.core.util.math.collision;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Encapsulates an immutable ray having a starting position and a unit length
 * direction.
 * <p>
 * @author AdrianSR / Thursday 16 April, 2020 / 03:17 PM
 */
public class Ray {

	protected final Vector    origin;
	protected final Vector direction;
	
	/**
	 * Constructs the ray.
	 * <p>
	 * @param origin    the starting position.
	 * @param direction the direction.
	 */
	public Ray ( final Vector origin , final Vector direction ) {
		this.origin    = origin.clone();
		this.direction = direction.clone().normalize();
	}
	
	/**
	 * Constructs the ray.
	 * <p>
	 * @param origin    the starting position.
	 * @param direction the direction.
	 */
	public Ray ( final Location origin , final Vector direction ) {
		this ( origin.toVector ( ) , direction );
	}
	
	/**
	 * Constructs the ray from the provided {@link Location}, setting the
	 * {@link #origin} from its position coordinates (x, y and z), and the
	 * {@link #direction} from its direction.
	 * <p>
	 * @param origin    the starting position.
	 * @param direction the direction.
	 */
	public Ray ( final Location data ) {
		this ( data , data.getDirection ( ) );
	}

	/**
	 * Gets the ray's origin location vector ( a vector representing a location ).
	 * <p>
	 * @return a new copy of the ray's origin.
	 */
	public Vector getOrigin() {
		return origin.clone();
	}

	/**
	 * Gets the ray's direction vector.
	 * <p>
	 * @return a new copy of the ray's origin.
	 */
	public Vector getDirection() {
		return direction.clone();
	}
	
	/**
	 * Returns the endpoint given the distance. This is calculated as startpoint +
	 * distance * direction.
	 * <p>
	 * @param distance the distance from the end point to the start point.
	 * @return the endpoint result.
	 */
	public Vector getEndPoint ( final double distance ) {
		return direction.clone().multiply ( distance ).add ( origin );
	}
	
	@Override
	public boolean equals ( Object obj ) {
		if ( obj == this ) {
			return true;
		}
		
		if ( obj instanceof Ray ) {
			return Objects.equals ( ((Ray) obj).origin , origin ) 
					&& Objects.equals ( ((Ray) obj).direction , direction );
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 73;
		int result = 1;
		result = prime * result + this.direction.hashCode();
		result = prime * result + this.origin.hashCode();
		return result;
	}
}
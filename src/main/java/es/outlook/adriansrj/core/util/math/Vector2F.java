package es.outlook.adriansrj.core.util.math;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates an immutable cached Vector of two dimensions ( x and y ).
 * <p>
 * The values of {@link #length()}, {@link #lengthSquared()}, {@link #normalize()} and {@link #hashCode()} are cached
 * for a better performance.
 * <p>
 *
 * @author AdrianSR / Monday 16 March, 2020 / 11:34 AM
 */
public class Vector2F implements Vector {
	
	public static final Vector2F ZERO = new Vector2F ( 0F , 0F );
	public static final Vector2F ONE  = new Vector2F ( 1F , 1F );
	public static final Vector2F X    = new Vector2F ( 1F , 0F );
	public static final Vector2F Y    = new Vector2F ( 0F , 1F );
	
	protected final float x;
	protected final float y;
	
	/* cached values */
	private float length_squared = Float.NaN;
	private float length         = Float.NaN;
	
	private Vector2F normalized = null;
	
	private boolean hashed   = false;
	private int     hashcode = 0;
	
	private int     x_bits = 0;
	private int     y_bits = 0;
	private boolean bitset = false;
	
	public Vector2F ( final float x , final float y ) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2F ( final int x , final int y ) {
		this ( ( float ) x , ( float ) y );
	}
	
	public Vector2F ( final double x , final double y ) {
		this ( ( float ) x , ( float ) y );
	}
	
	public float getX ( ) {
		return x;
	}
	
	public float getY ( ) {
		return y;
	}
	
	public Vector2F add ( final Vector2F other ) {
		return new Vector2F ( ( this.x + other.x ) , ( this.y + other.y ) );
	}
	
	public Vector2F add ( final float x , final float y ) {
		return new Vector2F ( ( this.x + x ) , ( this.y + y ) );
	}
	
	public Vector2F subtract ( final Vector2F other ) {
		return new Vector2F ( ( this.x - other.x ) , ( this.y - other.y ) );
	}
	
	public Vector2F subtract ( final float x , final float y ) {
		return new Vector2F ( ( this.x - x ) , ( this.y - y ) );
	}
	
	public Vector2F multiply ( final Vector2F other ) {
		return new Vector2F ( ( this.x * other.x ) , ( this.y * other.y ) );
	}
	
	public Vector2F multiply ( final float x , final float y ) {
		return new Vector2F ( ( this.x * x ) , ( this.y * y ) );
	}
	
	public Vector2F divide ( final Vector2F other ) {
		return new Vector2F ( ( this.x / other.x ) , ( this.y / other.y ) );
	}
	
	public Vector2F divide ( final float x , final float y ) {
		return new Vector2F ( ( this.x / x ) , ( this.y / y ) );
	}
	
	@Override
	public float length ( ) {
		if ( Float.isNaN ( length ) ) {
			this.length = ( float ) Math.sqrt ( lengthSquared ( ) );
		}
		return length;
	}
	
	@Override
	public float lengthSquared ( ) {
		if ( Float.isNaN ( length_squared ) ) {
			this.length_squared = ( x * x ) + ( y * y );
		}
		return length_squared;
	}
	
	public float distance ( final Vector2F other ) {
		return ( float ) Math.sqrt ( distanceSquared ( other ) );
	}
	
	public float distanceSquared ( final Vector2F other ) {
		final float x_d = ( this.x - other.x );
		final float y_d = ( this.y - other.y );
		
		return ( ( x_d * x_d ) + ( y_d * y_d ) );
	}
	
	/**
	 * Gets the angle between this vector and another in degrees.
	 * <p>
	 *
	 * @param other the other vector.
	 *
	 * @return the angle in degrees.
	 */
	public float angle ( final Vector2F other ) {
		return ( float ) Math.toDegrees ( Math.atan2 ( crossProduct ( other ) , dotProduct ( other ) ) );
	}
	
	public Vector2F midpoint ( final Vector2F other ) {
		final float x = ( this.x + other.x ) / 2;
		final float y = ( this.y + other.y ) / 2;
		
		return new Vector2F ( x , y );
	}
	
	public float dotProduct ( final Vector2F other ) {
		return ( this.x * other.x ) + ( this.y * other.y );
	}
	
	public float crossProduct ( final Vector2F other ) {
		return ( this.x * other.y ) - ( this.y * other.x );
	}
	
	@Override
	public Vector2F normalize ( ) {
		if ( this.normalized == null ) {
			if ( Math.abs ( length ( ) ) < FLOAT_EPSILON ) {
				this.normalized = Vector2F.ZERO;
			} else {
				this.normalized = new Vector2F ( ( this.x / length ( ) ) , ( this.y / length ( ) ) );
			}
		}
		return normalized;
	}
	
	@Override
	public Vector2D toVector2D ( ) {
		return new Vector2D ( this.x , this.y );
	}
	
	@Override
	public Vector3D toVector3D ( ) {
		throw new UnsupportedOperationException ( "not supported ( missing z component )" );
	}
	
	public Vector3D toVector3D ( final float z ) {
		return new Vector3D ( this.x , this.y , z );
	}
	
	/**
	 * Creates and returns a new {@link Vector3D} by specifying the components to provide, following the rules explain
	 * below:
	 * <dd><strong>-1</strong> represents the value of {@link #getX()} in this
	 * vector.</dd>
	 * <dd><strong>0</strong> represents the value of {@link #getY()} in this
	 * vector.</dd>
	 * <dd><strong>1</strong> represents 0.
	 * <p>
	 *
	 * @param x Providing <strong>-1</strong>, will construct the Vector3D with its x component having the same
	 *             value as
	 *          the x component of this vector. Providing
	 *          <strong>0</strong>, will construct the Vector3D with its x component having the same
	 *          value as the y component of this vector. Providing <strong>1</strong>, will construct the Vector3D with
	 *          its x being 0.
	 * @param y Providing <strong>-1</strong>, will construct the Vector3D with its y component having the same
	 *             value as
	 *          the x component of this vector. Providing
	 *          <strong>0</strong>, will construct the Vector3D with its y component having the same
	 *          value as the y component of this vector. Providing <strong>1</strong>, will construct the Vector3D with
	 *          its y being 0.
	 * @param z Providing <strong>-1</strong>, will construct the Vector3D with its z component having the same
	 *             value as
	 *          the x component of this vector. Providing
	 *          <strong>0</strong>, will construct the Vector3D with its z component having the same
	 *          value as the y component of this vector. Providing <strong>1</strong>, will construct the Vector3D with
	 *          its z being 0.
	 *
	 * @return the 3D version.
	 */
	public Vector3D toVector3D ( final int x , final int y , final int z ) {
		// -1 = x
		//  0 = y
		//  1 = 0
		
		final float vector_x = x < 0 ? this.x : ( x == 0 ? this.y : 0F );
		final float vector_y = y < 0 ? this.x : ( x == 0 ? this.y : 0F );
		final float vector_z = z < 0 ? this.x : ( x == 0 ? this.y : 0F );
		
		return new Vector3D ( vector_x , vector_y , vector_z );
	}
	
	@Override
	public org.bukkit.util.Vector toBukkit ( ) {
		throw new UnsupportedOperationException ( "not supported ( missing z component )" );
	}
	
	public org.bukkit.util.Vector toBukkit ( final double z ) {
		return new org.bukkit.util.Vector ( this.x , this.y , z );
	}
	
	@Override
	public Location toLocation ( final World world , final float yaw , final float pitch ) {
		throw new UnsupportedOperationException ( "not supported ( missing z component )" );
	}
	
	public Location toLocation ( final World world , final double z , final float yaw , final float pitch ) {
		return new Location ( world , this.x , this.y , z , yaw , pitch );
	}
	
	@Override
	public Location toLocation ( final World world ) {
		throw new UnsupportedOperationException ( "not supported ( missing z component )" );
	}
	
	public Location toLocation ( final World world , final double z ) {
		return toLocation ( world , z , 0F , 0F );
	}
	
	@Override
	public String toString ( ) {
		return "(" + x + ", " + y + ")";
	}
	
	@Override
	public Map < String, Object > serialize ( ) {
		final Map < String, Object > serialized = new HashMap <> ( );
		
		serialized.put ( "x" , this.x );
		serialized.put ( "y" , this.y );
		
		return serialized;
	}
	
	@Override
	public int hashCode ( ) {
		if ( !hashed ) {
			this.hashcode = 7;
			this.hashcode = 79 * hashcode + ( Float.floatToIntBits ( this.x ) ^ ( Float.floatToIntBits (
					this.x ) >>> 32 ) );
			this.hashcode = 79 * hashcode + ( Float.floatToIntBits ( this.y ) ^ ( Float.floatToIntBits (
					this.y ) >>> 32 ) );
			
			this.hashed = true;
		}
		return hashcode;
	}
	
	@Override
	public boolean equals ( final Object obj ) {
		if ( obj == this ) {
			return true;
		}
		
		if ( !( obj instanceof Vector2F ) ) {
			return false;
		}
		
		Vector2F other = ( Vector2F ) obj;
		this.bitset ( );
		other.bitset ( );
		
		return ( this.x_bits == other.x_bits )
				&& ( this.y_bits == other.y_bits );
	}
	
	protected void bitset ( ) {
		if ( !bitset ) {
			this.x_bits = Float.floatToIntBits ( this.x );
			this.y_bits = Float.floatToIntBits ( this.y );
			
			this.bitset = true;
		}
	}
}
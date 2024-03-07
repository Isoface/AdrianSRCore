package es.outlook.adriansrj.core.util.math;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates an immutable cached integer Vector of three dimensions ( x, y and z ).
 * <br>
 * The values of {@link #length()}, {@link #lengthSquared()}, {@link #normalize()} and {@link #hashCode()} are cached
 * for a better performance.
 *
 * @author AdrianSR / Monday 16 March, 2020 / 11:34 AM
 */
public class Vector3I implements Vector {
	
	public static final Vector3I ZERO = new Vector3I ( 0F , 0F , 0F );
	public static final Vector3I ONE  = new Vector3I ( 1F , 1F , 1F );
	public static final Vector3I X    = new Vector3I ( 1F , 0F , 0F );
	public static final Vector3I Y    = new Vector3I ( 0F , 1F , 0F );
	public static final Vector3I Z    = new Vector3I ( 0F , 0F , 1F );
	
	protected final int x;
	protected final int y;
	protected final int z;
	
	/* cached values */
	// length
	protected float    length_squared = Float.NaN;
	protected float    length         = Float.NaN;
	// normalized version of this vector
	protected Vector3I normalized     = null;
	// hashcode
	protected boolean  hashed         = false;
	protected int      hashcode       = 0;
	
	public Vector3I ( int x , int y , int z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3I ( final float x , final float y , final float z ) {
		this ( ( int ) x , ( int ) y , ( int ) z );
	}
	
	public Vector3I ( final double x , final double y , final double z ) {
		this ( ( int ) x , ( int ) y , ( int ) z );
	}
	
	public int getX ( ) {
		return x;
	}
	
	public int getY ( ) {
		return y;
	}
	
	public int getZ ( ) {
		return z;
	}
	
	public Vector3I add ( final Vector3I other ) {
		return new Vector3I ( ( this.x + other.x ) , ( this.y + other.y ) , ( this.z + other.z ) );
	}
	
	public Vector3I add ( final int x , final int y , final int z ) {
		return new Vector3I ( ( this.x + x ) , ( this.y + y ) , ( this.z + z ) );
	}
	
	public Vector3I subtract ( final Vector3I other ) {
		return new Vector3I ( ( this.x - other.x ) , ( this.y - other.y ) , ( this.z - other.z ) );
	}
	
	public Vector3I subtract ( final int x , final int y , final int z ) {
		return new Vector3I ( ( this.x - x ) , ( this.y - y ) , ( this.z - z ) );
	}
	
	public Vector3I multiply ( final Vector3I other ) {
		return new Vector3I ( ( this.x * other.x ) , ( this.y * other.y ) , ( this.z * other.z ) );
	}
	
	public Vector3I multiply ( final int x , final int y , final int z ) {
		return new Vector3I ( ( this.x * x ) , ( this.y * y ) , ( this.z * z ) );
	}
	
	public Vector3I multiply ( final int factor ) {
		return new Vector3I ( ( this.x * factor ) , ( this.y * factor ) , ( this.z * factor ) );
	}
	
	public Vector3I divide ( final Vector3I other ) {
		return new Vector3I ( ( this.x / other.x ) , ( this.y / other.y ) , ( this.z / other.z ) );
	}
	
	public Vector3I divide ( final int x , final int y , final int z ) {
		return new Vector3I ( ( this.x / x ) , ( this.y / y ) , ( this.z / z ) );
	}
	
	public Vector3I divide ( final int factor ) {
		return new Vector3I ( ( this.x / factor ) , ( this.y / factor ) , ( this.z / factor ) );
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
			this.length_squared = ( float ) ( ( x * x ) + ( y * y ) + ( z * z ) );
		}
		return length_squared;
	}
	
	public float distance ( final Vector3I other ) {
		return ( float ) Math.sqrt ( distanceSquared ( other ) );
	}
	
	public int distanceSquared ( final Vector3I other ) {
		final int x_d = ( this.x - other.x );
		final int y_d = ( this.y - other.y );
		final int z_d = ( this.z - other.z );
		
		return ( x_d * x_d ) + ( y_d * y_d ) + ( z_d * z_d );
	}
	
	/**
	 * Gets the angle between this vector and another in degrees.
	 *
	 * @param other the other vector.
	 *
	 * @return the angle in degrees.
	 */
	public double angle ( final Vector3I other ) {
		return Math.toDegrees ( Math.acos ( dotProduct ( other ) / ( length ( ) * other.length ( ) ) ) );
	}
	
	public Vector3I midpoint ( final Vector3I other ) {
		final int x = ( this.x + other.x ) / 2;
		final int y = ( this.y + other.y ) / 2;
		final int z = ( this.z + other.z ) / 2;
		
		return new Vector3I ( x , y , z );
	}
	
	public int dotProduct ( final Vector3I other ) {
		return ( this.x * other.x ) +
				( this.y * other.y ) +
				( this.z * other.z );
	}
	
	public Vector3I crossProduct ( final Vector3I other ) {
		final int x = ( this.y * other.z ) - ( other.y * this.z );
		final int y = ( this.z * other.x ) - ( other.z * this.x );
		final int z = ( this.x * other.y ) - ( other.x * this.y );
		
		return new Vector3I ( x , y , z );
	}
	
	@Override
	public Vector3I normalize ( ) {
		if ( this.normalized == null ) {
			if ( Math.abs ( length ( ) ) < FLOAT_EPSILON ) {
				this.normalized = Vector3I.ZERO;
			} else {
				this.normalized = new Vector3I ( ( this.x / length ( ) ) , ( this.y / length ( ) ) ,
												 ( this.z / length ( ) ) );
			}
		}
		return normalized;
	}
	
	@Override
	public Vector2D toVector2D ( ) {
		return toVector2D ( -1 , 0 );
	}
	
	/**
	 * Creates and returns a new {@link Vector2D} by specifying the components to provide, following the rules explain
	 * below:
	 * <dd><strong>-1</strong> represents the value of {@link #getX()} in this
	 * vector.</dd>
	 * <dd><strong>0</strong> represents the value of {@link #getY()} in this
	 * vector.</dd>
	 * <dd><strong>1</strong> represents the value of {@link #getZ()} in this
	 * vector.</dd>
	 * <p>
	 *
	 * @param x Providing <strong>-1</strong>, will construct the Vector2D with its x component having the same
	 *             value as
	 *          the x component of this vector. Providing
	 *          <strong>0</strong>, will construct the Vector2D with its x component having the same
	 *          value as the y component of this vector. Providing <strong>1</strong>, will construct the Vector2D with
	 *          its x component having the same value as the z component of this vector.
	 * @param y Providing <strong>-1</strong>, will construct the Vector2D with its y component having the same
	 *             value as
	 *          the x component of this vector. Providing
	 *          <strong>0</strong>, will construct the Vector2D with its y component having the same
	 *          value as the y component of this vector. Providing <strong>1</strong>, will construct the Vector2D with
	 *          its y component having the same value as the z component of this vector.
	 *
	 * @return the 2D version.
	 */
	public Vector2D toVector2D ( final int x , final int y ) {
		// -1 = x
		//  0 = y
		//  1 = z
		
		final int vector_x = x < 0 ? this.x : ( x == 0 ? this.y : this.z );
		final int vector_y = y < 0 ? this.x : ( x == 0 ? this.y : this.z );
		
		return new Vector2D ( vector_x , vector_y );
	}
	
	@Override
	public Vector3D toVector3D ( ) {
		return new Vector3D ( x , y , z );
	}
	
	@Override
	public org.bukkit.util.Vector toBukkit ( ) {
		return new org.bukkit.util.Vector ( this.x , this.y , this.z );
	}
	
	@Override
	public Location toLocation ( final World world , final float yaw , final float pitch ) {
		return new Location ( world , this.x , this.y , this.z , yaw , pitch );
	}
	
	public Location toLocation ( final World world ) {
		return toLocation ( world , 0F , 0F );
	}
	
	@Override
	public String toString ( ) {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	@Override
	public Map < String, Object > serialize ( ) {
		final Map < String, Object > serialized = new HashMap <> ( );
		
		serialized.put ( "x" , this.x );
		serialized.put ( "y" , this.y );
		serialized.put ( "z" , this.z );
		
		return serialized;
	}
	
	@Override
	public int hashCode ( ) {
		if ( !hashed ) {
			this.hashcode = 7;
			this.hashcode += 79 * hashcode + x ^ ( x >>> 32 );
			this.hashcode += 79 * hashcode + y ^ ( y >>> 32 );
			this.hashcode += 79 * hashcode + z ^ ( z >>> 32 );
			
			this.hashed = true;
		}
		return hashcode;
	}
	
	@Override
	public boolean equals ( final Object obj ) {
		if ( obj == this ) {
			return true;
		}
		
		if ( !( obj instanceof Vector3I ) ) {
			return false;
		}
		
		Vector3I other = ( Vector3I ) obj;
		return ( this.x == other.x ) && ( this.y == other.y ) && ( this.z == other.z );
	}
}
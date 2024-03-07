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
public class Vector2I implements Vector {
	
	public static final Vector2I ZERO = new Vector2I ( 0 , 0 );
	public static final Vector2I ONE  = new Vector2I ( 1 , 1 );
	public static final Vector2I X    = new Vector2I ( 1 , 0 );
	public static final Vector2I Y    = new Vector2I ( 0 , 1 );
	
	protected final int x;
	protected final int y;
	
	/* cached values */
	private float length_squared = Float.NaN;
	private float length         = Float.NaN;
	
	private Vector2I normalized = null;
	
	private boolean hashed   = false;
	private int     hashcode = 0;
	
	public Vector2I ( final int x , final int y ) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2I ( final float x , final float y ) {
		this ( ( int ) x , ( int ) y );
	}
	
	public Vector2I ( final double x , final double y ) {
		this ( ( int ) x , ( int ) y );
	}
	
	public int getX ( ) {
		return x;
	}
	
	public int getY ( ) {
		return y;
	}
	
	public Vector2I add ( final Vector2I other ) {
		return new Vector2I ( ( this.x + other.x ) , ( this.y + other.y ) );
	}
	
	public Vector2I add ( final int x , final int y ) {
		return new Vector2I ( ( this.x + x ) , ( this.y + y ) );
	}
	
	public Vector2I subtract ( final Vector2I other ) {
		return new Vector2I ( ( this.x - other.x ) , ( this.y - other.y ) );
	}
	
	public Vector2I subtract ( final int x , final int y ) {
		return new Vector2I ( ( this.x - x ) , ( this.y - y ) );
	}
	
	public Vector2I multiply ( final Vector2I other ) {
		return new Vector2I ( ( this.x * other.x ) , ( this.y * other.y ) );
	}
	
	public Vector2I multiply ( final int x , final int y ) {
		return new Vector2I ( ( this.x * x ) , ( this.y * y ) );
	}
	
	public Vector2I divide ( final Vector2I other ) {
		return new Vector2I ( ( this.x / other.x ) , ( this.y / other.y ) );
	}
	
	public Vector2I divide ( final int x , final int y ) {
		return new Vector2I ( ( this.x / x ) , ( this.y / y ) );
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
	
	public int distance ( final Vector2I other ) {
		return ( int ) Math.sqrt ( distanceSquared ( other ) );
	}
	
	public int distanceSquared ( final Vector2I other ) {
		final int x_d = ( this.x - other.x );
		final int y_d = ( this.y - other.y );
		
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
	public int angle ( final Vector2I other ) {
		return ( int ) Math.toDegrees ( Math.atan2 ( crossProduct ( other ) , dotProduct ( other ) ) );
	}
	
	public Vector2I midpoint ( final Vector2I other ) {
		final int x = ( this.x + other.x ) / 2;
		final int y = ( this.y + other.y ) / 2;
		
		return new Vector2I ( x , y );
	}
	
	public int dotProduct ( final Vector2I other ) {
		return ( this.x * other.x ) + ( this.y * other.y );
	}
	
	public int crossProduct ( final Vector2I other ) {
		return ( this.x * other.y ) - ( this.y * other.x );
	}
	
	@Override
	public Vector2I normalize ( ) {
		if ( this.normalized == null ) {
			if ( Math.abs ( length ( ) ) < FLOAT_EPSILON ) {
				this.normalized = Vector2I.ZERO;
			} else {
				this.normalized = new Vector2I ( ( this.x / length ( ) ) , ( this.y / length ( ) ) );
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
	
	public Vector3D toVector3D ( final int z ) {
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
		
		final int vector_x = x < 0 ? this.x : ( x == 0 ? this.y : 0 );
		final int vector_y = y < 0 ? this.x : ( x == 0 ? this.y : 0 );
		final int vector_z = z < 0 ? this.x : ( x == 0 ? this.y : 0 );
		
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
			this.hashcode += 79 * hashcode + x ^ ( x >>> 32 );
			this.hashcode += 79 * hashcode + y ^ ( y >>> 32 );
			
			this.hashed = true;
		}
		return hashcode;
	}
	
	@Override
	public boolean equals ( final Object obj ) {
		if ( obj == this ) {
			return true;
		}
		
		if ( !( obj instanceof Vector2I ) ) {
			return false;
		}
		
		Vector2I other = ( Vector2I ) obj;
		
		return ( this.x == other.x ) && ( this.y == other.y );
	}
}
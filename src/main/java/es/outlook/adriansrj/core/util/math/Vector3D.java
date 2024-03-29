package es.outlook.adriansrj.core.util.math;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Encapsulates an immutable cached Vector of three dimensions ( x, y and z ).
 * <p>
 * The values of {@link #length()}, {@link #lengthSquared()}, {@link #normalize()} and {@link #hashCode()} are cached
 * for a better performance.
 * <p>
 *
 * @author AdrianSR / Monday 16 March, 2020 / 11:34 AM
 */
public class Vector3D implements Vector {
	
	public static final Vector3D ZERO = new Vector3D ( 0F , 0F , 0F );
	public static final Vector3D ONE  = new Vector3D ( 1F , 1F , 1F );
	public static final Vector3D X    = new Vector3D ( 1F , 0F , 0F );
	public static final Vector3D Y    = new Vector3D ( 0F , 1F , 0F );
	public static final Vector3D Z    = new Vector3D ( 0F , 0F , 1F );
	
	protected final double x;
	protected final double y;
	protected final double z;
	
	/* cached values */
	protected float length_squared = Float.NaN;
	protected float length         = Float.NaN;
	
	protected Vector3D normalized = null;
	
	protected boolean hashed   = false;
	protected int     hashcode = 0;
	
	protected long    x_bits = 0;
	protected long    y_bits = 0;
	protected long    z_bits = 0;
	protected boolean bitset = false;
	
	public Vector3D ( double x , double y , double z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3D ( final float x , final float y , final float z ) {
		this ( x , y , ( double ) z );
	}
	
	public Vector3D ( final int x , final int y , final int z ) {
		this ( x , y , ( double ) z );
	}
	
	public double getX ( ) {
		return x;
	}
	
	public double getY ( ) {
		return y;
	}
	
	public double getZ ( ) {
		return z;
	}
	
	public Vector3D add ( final Vector3D other ) {
		return new Vector3D ( ( this.x + other.x ) , ( this.y + other.y ) , ( this.z + other.z ) );
	}
	
	public Vector3D add ( final double x , final double y , final double z ) {
		return new Vector3D ( ( this.x + x ) , ( this.y + y ) , ( this.z + z ) );
	}
	
	public Vector3D subtract ( final Vector3D other ) {
		return new Vector3D ( ( this.x - other.x ) , ( this.y - other.y ) , ( this.z - other.z ) );
	}
	
	public Vector3D subtract ( final double x , final double y , final double z ) {
		return new Vector3D ( ( this.x - x ) , ( this.y - y ) , ( this.z - z ) );
	}
	
	public Vector3D multiply ( final Vector3D other ) {
		return new Vector3D ( ( this.x * other.x ) , ( this.y * other.y ) , ( this.z * other.z ) );
	}
	
	public Vector3D multiply ( final double x , final double y , final double z ) {
		return new Vector3D ( ( this.x * x ) , ( this.y * y ) , ( this.z * z ) );
	}
	
	public Vector3D multiply ( final double factor ) {
		return new Vector3D ( ( this.x * factor ) , ( this.y * factor ) , ( this.z * factor ) );
	}
	
	public Vector3D divide ( final Vector3D other ) {
		return new Vector3D ( ( this.x / other.x ) , ( this.y / other.y ) , ( this.z / other.z ) );
	}
	
	public Vector3D divide ( final double x , final double y , final double z ) {
		return new Vector3D ( ( this.x / x ) , ( this.y / y ) , ( this.z / z ) );
	}
	
	public Vector3D divide ( final double factor ) {
		return new Vector3D ( ( this.x / factor ) , ( this.y / factor ) , ( this.z / factor ) );
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
	
	public double distance ( final Vector3D other ) {
		return Math.sqrt ( distanceSquared ( other ) );
	}
	
	public double distanceSquared ( final Vector3D other ) {
		final double x_d = ( this.x - other.x );
		final double y_d = ( this.y - other.y );
		final double z_d = ( this.z - other.z );
		
		return ( x_d * x_d ) + ( y_d * y_d ) + ( z_d * z_d );
	}
	
	/**
	 * Gets the angle between this vector and another in degrees.
	 * <p>
	 *
	 * @param other the other vector.
	 *
	 * @return the angle in degrees.
	 */
	public double angle ( final Vector3D other ) {
		return Math.toDegrees ( Math.acos ( dotProduct ( other ) / ( length ( ) * other.length ( ) ) ) );
	}
	
	public Vector3D midpoint ( final Vector3D other ) {
		final double x = ( this.x + other.x ) / 2;
		final double y = ( this.y + other.y ) / 2;
		final double z = ( this.z + other.z ) / 2;
		
		return new Vector3D ( x , y , z );
	}
	
	public double dotProduct ( final Vector3D other ) {
		return ( this.x * other.x ) +
				( this.y * other.y ) +
				( this.z * other.z );
	}
	
	public Vector3D crossProduct ( final Vector3D other ) {
		final double x = ( this.y * other.z ) - ( other.y * this.z );
		final double y = ( this.z * other.x ) - ( other.z * this.x );
		final double z = ( this.x * other.y ) - ( other.x * this.y );
		
		return new Vector3D ( x , y , z );
	}
	
	@Override
	public Vector3D normalize ( ) {
		if ( this.normalized == null ) {
			if ( Math.abs ( length ( ) ) < FLOAT_EPSILON ) {
				this.normalized = Vector3D.ZERO;
			} else {
				this.normalized = new Vector3D ( ( this.x / length ( ) ) , ( this.y / length ( ) ) ,
												 ( this.z / length ( ) ) );
			}
		}
		return normalized;
	}
	
	@Override
	public Vector2D toVector2D ( ) {
		return toVector2D ( - 1 , 0 );
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
		
		final double vector_x = x < 0 ? this.x : ( x == 0 ? this.y : this.z );
		final double vector_y = y < 0 ? this.x : ( x == 0 ? this.y : this.z );
		
		return new Vector2D ( vector_x , vector_y );
	}
	
	@Override
	public Vector3D toVector3D ( ) {
		return this;
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
		if ( ! hashed ) {
			this.hashcode = 7;
			this.hashcode = 79 * hashcode + ( int ) ( Double.doubleToLongBits ( this.x ) ^ ( Double.doubleToLongBits (
					this.x ) >>> 32 ) );
			this.hashcode = 79 * hashcode + ( int ) ( Double.doubleToLongBits ( this.y ) ^ ( Double.doubleToLongBits (
					this.y ) >>> 32 ) );
			this.hashcode = 79 * hashcode + ( int ) ( Double.doubleToLongBits ( this.z ) ^ ( Double.doubleToLongBits (
					this.z ) >>> 32 ) );
			
			this.hashed = true;
		}
		return hashcode;
	}
	
	@Override
	public boolean equals ( final Object obj ) {
		if ( obj == this ) {
			return true;
		}
		
		if ( ! ( obj instanceof Vector3D ) ) {
			return false;
		}
		
		Vector3D other = ( Vector3D ) obj;
		this.bitset ( );
		other.bitset ( );
		
		return ( this.x_bits == other.x_bits )
				&& ( this.y_bits == other.y_bits )
				&& ( this.z_bits == other.z_bits );
	}
	
	protected void bitset ( ) {
		if ( ! bitset ) {
			this.x_bits = Double.doubleToLongBits ( this.x );
			this.y_bits = Double.doubleToLongBits ( this.y );
			this.z_bits = Double.doubleToLongBits ( this.z );
			
			this.bitset = true;
		}
	}
}
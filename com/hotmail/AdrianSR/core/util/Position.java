package com.hotmail.AdrianSR.core.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Utility;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.riding.util.StandBlockFace;
import com.hotmail.AdrianSR.core.util.file.YmlSerializable;
import com.hotmail.AdrianSR.core.util.localization.DirectionUtils;

/**
 * Represents a 3-dimensional position of a ComplexEntityPart. <br>
 * No constraints are placed on any angular values other than that they be
 * specified in degrees. This means that negative angles or angles of greater
 * magnitude than 360 are valid, but may be normalized to any other equivalent
 * representation by the implementation.
 */
public class Position implements Cloneable, ConfigurationSerializable, YmlSerializable {

	/**
	 * Class values.
	 */
	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;

	/**
	 * Construct a new {@link Position}
	 * loading its coords from a {@link ConfigurationSection}
	 * <p>
	 * @param section to load from.
	 */
	public Position(ConfigurationSection section) {
		this(section.getDouble("X"), section.getDouble("Y"), section.getDouble("Z"), (float) section.getDouble("Yaw"),
				(float) section.getDouble("Pitch"));
	}
	
	/**
	 * Constructs a new {@link Position} 
	 * based on a Location.
	 *<p>
	 * @param Location The base {@link Location}.
	 * @return New Position containing the coordinates represented by the given location.
	 */
	public Position(final Location location) {
		this(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	/**
	 * Constructs a new Position with the given coordinates
	 *<p>
	 * @param x The x-coordinate of this new Position
	 * @param y The y-coordinate of this new Position
	 * @param z The z-coordinate of this new Position
	 */
	public Position(final double x, final double y, final double z) {
		this(x, y, z, 0, 0);
	}

	/**
	 * Constructs a new Position with the given coordinates and direction
	 *<p>
	 * @param x The x-coordinate of this new Position
	 * @param y The y-coordinate of this new Position
	 * @param z The z-coordinate of this new Position
	 * @param yaw The absolute rotation on the x-plane, in degrees
	 * @param pitch The absolute rotation on the y-plane, in degrees
	 */
	public Position(final double x, final double y, final double z, final float yaw, final float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}
	
	/**
	 * Sets the x-coordinate of this Position
	 *<p>
	 * @param x X-coordinate
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Gets the x-coordinate of this Position
	 *<p>
	 * @return x-coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the y-coordinate of this Position
	 *<p>
	 * @param y y-coordinate
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Gets the y-coordinate of this Position
	 *<p>
	 * @return y-coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the z-coordinate of this Position
	 *<p>
	 * @param z z-coordinate
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Gets the z-coordinate of this Position
	 *<p>
	 * @return z-coordinate
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Sets the yaw of this Position, measured in degrees.
	 * <ul>
	 * <li>A yaw of 0 or 360 represents the positive z direction.
	 * <li>A yaw of 180 represents the negative z direction.
	 * <li>A yaw of 90 represents the negative x direction.
	 * <li>A yaw of 270 represents the positive x direction.
	 * </ul>
	 * Increasing yaw values are the equivalent of turning to your right-facing,
	 * increasing the scale of the next respective axis, and decreasing the scale of
	 * the previous axis.
	 *<p>
	 * @param yaw new rotation's yaw
	 */
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	/**
	 * Gets the yaw of this Position, measured in degrees.
	 * <ul>
	 * <li>A yaw of 0 or 360 represents the positive z direction.
	 * <li>A yaw of 180 represents the negative z direction.
	 * <li>A yaw of 90 represents the negative x direction.
	 * <li>A yaw of 270 represents the positive x direction.
	 * </ul>
	 * Increasing yaw values are the equivalent of turning to your right-facing,
	 * increasing the scale of the next respective axis, and decreasing the scale of
	 * the previous axis.
	 *<p>
	 * @return the rotation's yaw
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Sets the pitch of this Position, measured in degrees.
	 * <ul>
	 * <li>A pitch of 0 represents level forward facing.
	 * <li>A pitch of 90 represents downward facing, or negative y direction.
	 * <li>A pitch of -90 represents upward facing, or positive y direction.
	 * </ul>
	 * Increasing pitch values the equivalent of looking down.
	 *<p>
	 * @param pitch new incline's pitch
	 */
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	/**
	 * Gets the pitch of this Position, measured in degrees.
	 * <ul>
	 * <li>A pitch of 0 represents level forward facing.
	 * <li>A pitch of 90 represents downward facing, or negative y direction.
	 * <li>A pitch of -90 represents upward facing, or positive y direction.
	 * </ul>
	 * Increasing pitch values the equivalent of looking down.
	 *<p>
	 * @return the incline's pitch
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Gets a unit-vector pointing in the direction that this Position is facing.
	 *<p>
	 * @return a vector pointing the direction of this Position's {@link #getPitch()
	 *         pitch} and {@link #getYaw() yaw}
	 */
	public Vector getDirection() {
		// make vector
		Vector vector = new Vector();

		// get yaw and pitch rotations.
		double rotX = this.getYaw();
		double rotY = this.getPitch();

		// set Y direction.
		vector.setY(-Math.sin(Math.toRadians(rotY)));

		// get x and z direction.
		double xz = Math.cos(Math.toRadians(rotY));

		// set x and z.
		vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
		vector.setZ(xz  * Math.cos(Math.toRadians(rotX)));
		return vector;
	}

	/**
	 * Adds the Position by another.
	 *<p>
	 * @see Vector
	 * @param vec The other Position
	 * @return the same Position
	 */
	public Position add(Position vec) {
		x += vec.x;
		y += vec.y;
		z += vec.z;
		return this;
	}

	/**
	 * Adds the Position by a vector.
	 *<p>
	 * @see Vector.
	 * @param vec Vector to use.
	 * @return The same Position.
	 */
	public Position add(Vector vec) {
		this.x += vec.getX();
		this.y += vec.getY();
		this.z += vec.getZ();
		return this;
	}
	
	/**
	 * Add a {@link BlockFace} axis mods.
	 * <p>
	 * @param blockface The block face to add.
	 * @return The same Position.
	 */
	public Position add(BlockFace blockface) {
		add(blockface, 1);
		return this;
	}
	
	/**
	 * Add a {@link BlockFace} axis mods.
	 * <p>
	 * @param blockface The block face to add.
	 * @param multiplier The multiplier of axis mods.
	 * @return The same Position.
	 */
	public Position add(BlockFace blockface, double multiplier) {
		this.x += blockface.getModX() * multiplier;
		this.y += blockface.getModY() * multiplier;
		this.z += blockface.getModZ() * multiplier;
		return this;
	}
	
	/**
	 * Add a {@link StandBlockFace} axis mods.
	 * <p>
	 * @param standblockface The block face to add.
	 * @return The same Position.
	 */
	public Position add(StandBlockFace standblockface) {
		add(standblockface, 1);
		return this;
	}
	
	/**
	 * Add a {@link StandBlockFace} axis mods.
	 * <p>
	 * @param standblockface The block face to add.
	 * @param multiplier The multiplier of axis mods.
	 * @return The same Position.
	 */
	public Position add(StandBlockFace face, double multiplier) {
		this.x += face.getModX() * multiplier;
		this.y += face.getModY() * multiplier;
		this.z += face.getModZ() * multiplier;
		return this;
	}
	
	/**
	 * Add to front direction that
	 * this Position is facing.
	 * <p>
	 * @param amount The amount to add.
	 * @return The same Position.
	 */
	public Position addToFront(final double amount) {
		// add to front that this is facing.
		add(DirectionUtils.getFacingDirection(yaw), amount);
		return this;
	}
	
	/**
	 * Add to back direction that
	 * this Position is facing.
	 * <p>
	 * @param amount The amount to add.
	 * @return The same Position.
	 */
	public Position addToBack(final double amount) {
		// add to back from this is facing.
		add(DirectionUtils.getFacingDirection(yaw).getOppositeFace(), amount);
		return this;
	}
	
	/**
	 * Add at left direction that
	 * this Position is facing.
	 * <p>
	 * @param amount The amount to add.
	 * @return The same Position.
	 */
	public Position addAtLeft(final double amount) {
		// add to left from this is facing.
		add(DirectionUtils.getLeftFace(DirectionUtils.getFacingDirection(yaw)), amount);
		return this;
	}
	
	/**
	 * Add at right direction that
	 * this Position is facing.
	 * <p>
	 * @param amount The amount to add.
	 * @return The same Position.
	 */
	public Position addAtRight(final double amount) {
		// add to right from this is facing.
		add(DirectionUtils.getRightFace(DirectionUtils.getFacingDirection(yaw)), amount);
		return this;
	}
	
//	private double negativeToPositiveToNegative(final double d) {
//		return d < 0 ? Math.abs(d) : -d;
//	}
	
	/**
	 * Adds the Position by another. Not world-aware.
	 *<p>
	 * @see Vector
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return the same Position
	 */
	public Position add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	/**
	 * Subtracts the Position by another.
	 *<p>
	 * @see Vector
	 * @param vec The other Position
	 * @return the same Position
	 */
	public Position subtract(Position vec) {
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
		return this;
	}

	/**
	 * Subtracts the Position by a vector.
	 *<p>
	 * @see Vector
	 * @param vec The vector to use
	 * @return the same Position
	 */
	public Position subtract(Vector vec) {
		this.x -= vec.getX();
		this.y -= vec.getY();
		this.z -= vec.getZ();
		return this;
	}

	/**
	 * Subtracts the Position by another. Not world-aware and orientation
	 * independent.
	 *<p>
	 * @see Vector
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return the same Position
	 */
	public Position subtract(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	/**
	 * Gets the magnitude of the Position, defined as sqrt(x^2+y^2+z^2). The value
	 * of this method is not cached and uses a costly square-root function, so do
	 * not repeatedly call this method to get the Position's magnitude. NaN will be
	 * returned if the inner result of the sqrt() function overflows, which will be
	 * caused if the length is too long. Not world-aware and orientation
	 * independent.
	 *<p>
	 * @see Vector
	 * @return the magnitude
	 */
	public double length() {
		return Math.sqrt(NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z));
	}

	/**
	 * Gets the magnitude of the Position squared. Not world-aware and orientation
	 * independent.
	 *
	 * @see Vector
	 * @return the magnitude
	 */
	public double lengthSquared() {
		return NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z);
	}

	/**
	 * Get the distance between this Position and another. The value of this method
	 * is not cached and uses a costly square-root function, so do not repeatedly
	 * call this method to get the Position's magnitude. NaN will be returned if the
	 * inner result of the sqrt() function overflows, which will be caused if the
	 * distance is too long.
	 *<p>
	 * @see Vector
	 * @param o The other Position
	 * @return the distance
	 */
	public double distance(Position o) {
		return Math.sqrt(distanceSquared(o));
	}

	/**
	 * Get the squared distance between this Position and another.
	 *<p>
	 * @see Vector
	 * @param o The other Position
	 * @return the distance
	 */
	public double distanceSquared(Position o) {
		return NumberConversions.square(x - o.x) + NumberConversions.square(y - o.y)
				+ NumberConversions.square(z - o.z);
	}

	/**
	 * Get the distance between this Position and another excluding Y axis. 
	 * The value of this method is not cached and uses a costly square-root function, 
	 * so do not repeatedly call this method to get the Position's magnitude. 
	 * NaN will be returned if the inner result of the sqrt() function overflows, 
	 * which will be caused if the distance is too long.
	 *<p>
	 * @see Vector
	 * @param o The other Position
	 * @return the distance
	 */
	public double distance2D(Position o) {
		return Math.sqrt(distanceSquared2D(o));
	}

	/**
	 * Get the squared distance between this Position and another,
	 * excluding Y axis.
	 *<p>
	 * @see Vector
	 * @param o The other Position
	 * @return the distance
	 */
	public double distanceSquared2D(Position o) {
		return NumberConversions.square(x - o.x) + NumberConversions.square(y - o.y)
				+ NumberConversions.square(z - o.z);
	}

	/**
	 * Performs scalar multiplication, multiplying all components with a scalar.
	 *<p>
	 * @param m The factor
	 * @see Vector
	 * @return the same Position
	 */
	public Position multiply(double m) {
		x *= m;
		y *= m;
		z *= m;
		return this;
	}

	/**
	 * Zero this Position's components.
	 *<p>
	 * @see Vector
	 * @return the same Position
	 */
	public Position zero() {
		x = 0;
		y = 0;
		z = 0;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final Position other = (Position) obj;

		if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
			return false;
		}
		if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
			return false;
		}
		if (Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch)) {
			return false;
		}
		if (Float.floatToIntBits(this.yaw) != Float.floatToIntBits(other.yaw)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;

		hash = 19 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
		hash = 19 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
		hash = 19 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
		hash = 19 * hash + Float.floatToIntBits(this.pitch);
		hash = 19 * hash + Float.floatToIntBits(this.yaw);
		return hash;
	}

	@Override
	public String toString() {
		return "X: " + x + ", Y: " + y + ", Z: " + z + ", Yaw: " + yaw + ", Pitch: " + pitch;
	}
	
	/**
	 * Constructs a new {@link Location} 
	 * based on this Position.
	 *<p>
	 * @return New Position containing the coordinates represented by this Position
	 */
	public Location toLocation(final World world) {
		return new Location(world, x, y, z, yaw, pitch);
	}

	/**
	 * Constructs a new {@link Vector} based on this Position
	 *<p>
	 * @return New Vector containing the coordinates represented by this Position
	 */
	public Vector toVector() {
		return new Vector(x, y, z);
	}

	@Override
	public Position clone() {
		try {
			return (Position) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	/**
	 * Check if each component of this Position is finite.
	 *<p>
	 * @throws IllegalArgumentException
	 *             if any component is not finite
	 */
	public void checkFinite() throws IllegalArgumentException {
		NumberConversions.checkFinite(x, "x not finite");
		NumberConversions.checkFinite(y, "y not finite");
		NumberConversions.checkFinite(z, "z not finite");
		NumberConversions.checkFinite(pitch, "pitch not finite");
		NumberConversions.checkFinite(yaw, "yaw not finite");
	}

	/**
	 * Safely converts a double (Position coordinate) to an int (block coordinate)
	 *<p>
	 * @param loc Precise coordinate
	 * @return Block coordinate
	 */
	public static int locToBlock(double loc) {
		return NumberConversions.floor(loc);
	}

	@Utility
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("x", this.x);
		data.put("y", this.y);
		data.put("z", this.z);

		data.put("yaw", this.yaw);
		data.put("pitch", this.pitch);

		return data;
	}

	/**
	 * Required method for deserialization
	 *<p>
	 * @param args map to deserialize
	 * @return deserialized Position
	 * @see ConfigurationSerializable
	 */
	public static Position deserialize(Map<String, Object> args) {
		return new Position(NumberConversions.toDouble(args.get("x")), NumberConversions.toDouble(args.get("y")),
				NumberConversions.toDouble(args.get("z")), NumberConversions.toFloat(args.get("yaw")),
				NumberConversions.toFloat(args.get("pitch")));
	}

	@Override
	public void save(ConfigurationSection section) {
		section.set("X", x);
		section.set("Y", y);
		section.set("Z", z);
		section.set("Yaw", yaw);
		section.set("Pitch", pitch);
	}
}
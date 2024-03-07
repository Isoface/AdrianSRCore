package es.outlook.adriansrj.core.util.reflection.bukkit;

import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import es.outlook.adriansrj.core.util.reflection.general.ConstructorReflection;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Useful class for dealing with the reflection of Bukkit entities.
 * <p>
 * @author AdrianSR / Monday 13 April, 2020 / 10:50 AM
 */
public class EntityReflection {
	
	/**
	 * Gets the ID of the provided {@code entity}.
	 * <p>
	 * @param entity the entity to get.
	 * @return the ID of the provided {@code entity}.
	 */
	public static int getEntityID ( Entity entity ) {
		try {
			return ( int ) MethodReflection.get ( getEntityClass ( ) , "getId" )
					.invoke ( BukkitReflection.getHandle ( entity ) );
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException
				| ClassNotFoundException | NoSuchMethodException ex ) {
			ex.printStackTrace ( );
			return 0;
		}
	}
	
	/**
	 * Gets the {@link BoundingBox} for the provided {@link Entity}.
	 * <p>
	 * @param entity the entity to get its BoundingBox.
	 * @return the BoundingBox for the entity, or null if couldn't get.
	 */
	public static BoundingBox getBoundingBox ( Entity entity ) {
		Vector[] corners = getBoundingBoxCorners ( entity );
		
		return corners != null ? new BoundingBox ( corners[ 0 ] , corners[ 1 ] ) : null;
	}
	
	/**
	 * Gets the minimum and maximum corners of the bounding box for the provided
	 * {@link Entity}.
	 * <p>
	 * @param entity the entity to get its BoundingBox.
	 * @return the minimum and maximum corners of the respective bounding box.
	 */
	public static Vector[] getBoundingBoxCorners ( Entity entity ) {
		try {
			Object handle = BukkitReflection.getHandle ( entity );
			Object nms_bb = handle.getClass ( ).getMethod ( "getBoundingBox" ).invoke ( handle );
			
			Field[] fields = nms_bb.getClass ( ).getDeclaredFields ( );
			
			double min_x = FieldReflection.getValue ( nms_bb , fields[ 0 ].getName ( ) );
			double min_y = FieldReflection.getValue ( nms_bb , fields[ 1 ].getName ( ) );
			double min_z = FieldReflection.getValue ( nms_bb , fields[ 2 ].getName ( ) );
			
			double max_x = FieldReflection.getValue ( nms_bb , fields[ 3 ].getName ( ) );
			double max_y = FieldReflection.getValue ( nms_bb , fields[ 4 ].getName ( ) );
			double max_z = FieldReflection.getValue ( nms_bb , fields[ 5 ].getName ( ) );
			
			return new Vector[] {
					new Vector ( min_x , min_y , min_z ) , new Vector ( max_x , max_y , max_z )
			};
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException
				| NoSuchMethodException | NoSuchFieldException ex ) {
			ex.printStackTrace ( );
			return null;
		}
	}
	
	/**
	 * Gets the {@link BoundingBox} for the provided {@link Entity}.
	 * <p>
	 * @param entity the entity to get its BoundingBox.
	 * @param the entity's height.
	 * @return the BoundingBox for the entity, or null if couldn't get.
	 */
	//	public static BoundingBox getBoundingBox ( Entity entity , float height ) {
	//		try {
	//			final Object handle = BukkitReflection.getHandle ( entity );
	//			final Object nms_bb = handle.getClass().getMethod ( "getBoundingBox" ).invoke ( handle ); // NMS
	//			bounding box
	//
	//			final Field[] fields = nms_bb.getClass().getDeclaredFields();
	//
	//			final double min_x = (double) FieldReflection.getValue ( nms_bb, fields [ 0 ].getName() );
	//			final double min_y = (double) FieldReflection.getValue ( nms_bb, fields [ 1 ].getName() ) - height;
	//			final double min_z = (double) FieldReflection.getValue ( nms_bb, fields [ 2 ].getName() );
	//
	//			final double max_x = (double) FieldReflection.getValue ( nms_bb, fields [ 3 ].getName() );
	//			final double max_y = (double) FieldReflection.getValue ( nms_bb, fields [ 4 ].getName() ) - height;
	//			final double max_z = (double) FieldReflection.getValue ( nms_bb, fields [ 5 ].getName() );
	//
	//			return new BoundingBox ( new Vector ( min_x , min_y , min_z ), new Vector ( max_x , max_y , max_z ) );
	//		} catch ( Throwable ex ) {
	//			ex.printStackTrace();
	//			return null;
	//		}
	//	}
	
	/**
	 * Gets the {@link BoundingBox} for the provided {@link Entity}. The accuracy is
	 * not guaranteed when calculating the entity's height.
	 * <p>
	 * @param entity the entity to get its BoundingBox.
	 * @return the BoundingBox for the entity, or null if couldn't get.
	 */
	//	public static BoundingBox getBoundingBox ( Entity entity ) {
	//		try {
	//			final Object     handle = BukkitReflection.getHandle ( entity );
	//			final float head_height = (float) handle.getClass().getMethod ( "getHeadHeight" ).invoke ( handle );
	//
	//			return getBoundingBox ( entity , head_height );
	//		} catch (IllegalAccessException | IllegalArgumentException
	//				| InvocationTargetException | SecurityException | NoSuchMethodException ex ) {
	//			ex.printStackTrace();
	//			return null;
	//		}
	//	}
	
	/**
	 * This method makes the provided {@code entity} invisible to the desired target
	 * players.
	 * <p>
	 * Note that after calling this, the entity cannot be made visible. Instead the
	 * entity will be invisible if the player left the server and joins it again.
	 * <p>
	 * @param entity the entity to make invisible to the {@code targets} players.
	 * @param targets the players that will not can see the entity.
	 */
	public static void setInvisibleTo ( Entity entity , Collection < Player > targets ) {
		// TODO: FIX
		try {
			Object packet = ConstructorReflection.get ( ClassReflection.getNmsClass ( "PacketPlayOutEntityDestroy" ) ,
														int[].class )
					.newInstance ( new int[] { entity.getEntityId ( ) } );
			
			for ( Player target : targets ) {
				if ( target.isOnline ( ) ) {
					BukkitReflection.sendPacket ( target , packet );
				}
			}
		} catch ( InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * This method makes the provided {@code entity} invisible to the desired target
	 * players.
	 * <p>
	 * Note that after calling this, the entity cannot be made visible. Instead the
	 * entity will be invisible if the player left the server and joins it again.
	 * <p>
	 * @param entity the entity to make invisible to the {@code targets} players.
	 * @param targets the players that will not can see the entity.
	 */
	public static void setInvisibleTo ( Entity entity , Player... targets ) {
		setInvisibleTo ( entity , Arrays.asList ( targets ) );
	}
	
	/**
	 * Sets whether the provided {@code entity} will have AI.
	 * <p>
	 * @param entity the target entity.
	 * @param ai whether the entity will have AI or not.
	 */
	public static void setAI ( LivingEntity entity , boolean ai ) {
		// TODO: FIX
		try {
			if ( Version.getServerVersion ( ).isOlder ( Version.v1_9_R2 ) ) {
				Class < ? > nbt_class = ClassReflection.getNmsClass ( "NBTTagCompound" );
				
				Object handle = BukkitReflection.getHandle ( entity );
				Object ntb    = ConstructorReflection.newInstance ( nbt_class , new Class < ? >[ 0 ] );
				//				Object    ntb = ConstructorReflection.newInstance ( nbt_class );
				
				Method m0 = MethodReflection.get ( handle.getClass ( ) , "c" , nbt_class );
				Method m1 = MethodReflection.get ( nbt_class , "setInt" , String.class , int.class );
				Method m2 = MethodReflection.get ( handle.getClass ( ) , "f" , nbt_class );
				
				MethodReflection.invoke ( m0 , handle , ntb );
				MethodReflection.invoke ( m1 , ntb , "NoAI" , ( ai ? 0 : 1 ) );
				MethodReflection.invoke ( m2 , handle , ntb );
			} else {
				MethodReflection.invoke ( MethodReflection.get ( entity.getClass ( ) , "setAI" , boolean.class ) ,
										  entity , ai );
			}
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | ClassNotFoundException | InstantiationException ex ) {
			ex.printStackTrace ( );
		}
	}
	
	/**
	 * Sets whether the provided {@code entity} is will have collisions.
	 * <p>
	 * @param entity the target entity.
	 * @param collidable whether to enable collisions for the entity.
	 */
	public static void setCollidable ( LivingEntity entity , boolean collidable ) {
		try {
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_9_R2 ) ) {
				MethodReflection.invoke (
						MethodReflection.get ( entity.getClass ( ) , "setCollidable" , boolean.class ) , entity ,
						collidable );
			}
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e ) {
			e.printStackTrace ( );
		}
	}
	
	public static Vector getPositionDirty ( Object entity_handle ) {
		double x = 0.0D;
		double y = 0.0D;
		double z = 0.0D;
		
		try {
			Class < ? > entity_class = ClassReflection.getMinecraftClass (
					"Entity" , "world.entity" );
			
			// v1_17_R1 +
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
				x = ( double ) MethodReflection.get ( entity_class , "locX" ).invoke ( entity_handle );
				y = ( double ) MethodReflection.get ( entity_class , "locY" ).invoke ( entity_handle );
				z = ( double ) MethodReflection.get ( entity_class , "locZ" ).invoke ( entity_handle );
			} else {
				// v1_16_R1 - v1_16_R3
				if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_16_R1 ) ) {
					x = ( double ) MethodReflection.get ( entity_class , "locX" ).invoke ( entity_handle );
					y = ( double ) MethodReflection.get ( entity_class , "locY" ).invoke ( entity_handle );
					z = ( double ) MethodReflection.get ( entity_class , "locZ" ).invoke ( entity_handle );
				}
				// v1_8_R3 - v1_15_R1
				else {
					x = FieldReflection.get ( entity_class , "locX" ).getDouble ( entity_handle );
					y = FieldReflection.get ( entity_class , "locY" ).getDouble ( entity_handle );
					z = FieldReflection.get ( entity_class , "locZ" ).getDouble ( entity_handle );
				}
			}
		} catch ( ClassNotFoundException | InvocationTargetException
				| IllegalAccessException | NoSuchMethodException | NoSuchFieldException e ) {
			e.printStackTrace ( );
			return null;
		}
		
		return new Vector ( x , y , z );
	}
	
	public static Vector getPositionDirty ( Entity entity ) {
		try {
			return getPositionDirty ( BukkitReflection.getHandle ( entity ) );
		} catch ( IllegalAccessException | InvocationTargetException e ) {
			e.printStackTrace ( );
			return null;
		}
	}
	
	public static float getYawDirty ( Object entity_handle ) {
		try {
			// v1_17_R1 +
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
				return ( float ) invokeEntityClassMethod ( entity_handle , "getYRot" , new Class[ 0 ] );
			}
			// v1_8_R3 - v1_16_R3
			else {
				return ( float ) getEntityClassFieldValue ( entity_handle , "yaw" );
			}
		} catch ( ClassNotFoundException | InvocationTargetException
				| IllegalAccessException | NoSuchMethodException | NoSuchFieldException e ) {
			e.printStackTrace ( );
			return 0.0F;
		}
	}
	
	public static float getYawDirty ( Entity entity ) {
		try {
			return getYawDirty ( BukkitReflection.getHandle ( entity ) );
		} catch ( IllegalAccessException | InvocationTargetException e ) {
			e.printStackTrace ( );
			return 0.0F;
		}
	}
	
	public static float getPitchDirty ( Object entity_handle ) {
		try {
			// v1_17_R1 +
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
				return ( float ) invokeEntityClassMethod ( entity_handle , "getXRot" , new Class[ 0 ] );
			}
			// v1_8_R3 - v1_16_R3
			else {
				return ( float ) getEntityClassFieldValue ( entity_handle , "pitch" );
			}
		} catch ( ClassNotFoundException | InvocationTargetException
				| IllegalAccessException | NoSuchMethodException | NoSuchFieldException e ) {
			e.printStackTrace ( );
			return 0.0F;
		}
	}
	
	public static float getPitchDirty ( Entity entity ) {
		try {
			return getPitchDirty ( BukkitReflection.getHandle ( entity ) );
		} catch ( IllegalAccessException | InvocationTargetException e ) {
			e.printStackTrace ( );
			return 0.0F;
		}
	}
	
	public static Location getLocationDirty ( World world , Object entity_handle ) {
		return Objects.requireNonNull ( getPositionDirty ( entity_handle ) ).toLocation (
				world , getYawDirty ( entity_handle ) , getPitchDirty ( entity_handle ) );
	}
	
	/**
	 * Gets the entity's current position.
	 * <p>
	 * @param entity the entity to get.
	 * @return a new copy of Location containing the position of the desired entity, or null if couldn't get.
	 */
	public static Location getLocationDirty ( Entity entity ) {
		try {
			return getLocationDirty ( entity.getWorld ( ) , BukkitReflection.getHandle ( entity ) );
		} catch ( IllegalAccessException | InvocationTargetException e ) {
			e.printStackTrace ( );
			return null;
		}
	}
	
	public static void setPositionDirty ( Object handle , Vector position ) {
		try {
			Class < ? > entity_class = ClassReflection.getMinecraftClass (
					"Entity" , "world.entity" );
			Class < ? > vector_class = ClassReflection.getMinecraftClass (
					"Vec3D" , "world.phys" );
			
			// v1_17_R1 +
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
				Field position_field = FieldReflection.getAccessible ( entity_class , "av" );
				
				position_field.set ( handle , vector_class.getConstructor (
								double.class , double.class , double.class )
						.newInstance ( position.getX ( ) , position.getY ( ) , position.getZ ( ) ) );
			} else {
				// v1_16_R1 - v1_16_R3
				if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_16_R1 ) ) {
					Field position_field = FieldReflection.getAccessible ( entity_class , "loc" );
					
					position_field.set ( handle , vector_class.getConstructor (
									double.class , double.class , double.class )
							.newInstance ( position.getX ( ) , position.getY ( ) , position.getZ ( ) ) );
				}
				// v1_8_R3 - v1_15_R1
				else {
					FieldReflection.getAccessible ( entity_class ,
													"locX" ).setDouble ( handle , position.getX ( ) );
					FieldReflection.getAccessible ( entity_class ,
													"locY" ).setDouble ( handle , position.getY ( ) );
					FieldReflection.getAccessible ( entity_class ,
													"locZ" ).setDouble ( handle , position.getZ ( ) );
				}
			}
		} catch ( ClassNotFoundException | InvocationTargetException | IllegalAccessException
				| NoSuchMethodException | NoSuchFieldException | InstantiationException e ) {
			e.printStackTrace ( );
		}
	}
	
	public static void setPositionDirty ( Entity entity , Vector position ) {
		try {
			setPositionDirty ( BukkitReflection.getHandle ( entity ) , position );
		} catch ( IllegalAccessException | InvocationTargetException e ) {
			e.printStackTrace ( );
		}
	}
	
	public static void setYawDirty ( Object handle , float yaw ) {
		try {
			Class < ? > entity_class = ClassReflection.getMinecraftClass (
					"Entity" , "world.entity" );
			
			// v1_17_R1 +
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
				MethodReflection.getAccessible ( entity_class ,
												 "setYRot" , float.class ).invoke ( handle , yaw );
			} else {
				// x , y , z -> v1_8_R3 - v1_16_R3
				FieldReflection.getAccessible ( entity_class ,
												"yaw" ).setFloat ( handle , yaw );
			}
		} catch ( ClassNotFoundException | InvocationTargetException | IllegalAccessException
				| NoSuchMethodException | NoSuchFieldException e ) {
			e.printStackTrace ( );
		}
	}
	
	public static void setYawDirty ( Entity entity , float yaw ) {
		try {
			setYawDirty ( BukkitReflection.getHandle ( entity ) , yaw );
		} catch ( IllegalAccessException | InvocationTargetException e ) {
			e.printStackTrace ( );
		}
	}
	
	public static void setPitchDirty ( Object handle , float pitch ) {
		try {
			Class < ? > entity_class = ClassReflection.getMinecraftClass (
					"Entity" , "world.entity" );
			
			// v1_17_R1 +
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
				MethodReflection.getAccessible ( entity_class ,
												 "setXRot" , float.class ).invoke ( handle , pitch );
			} else {
				// x , y , z -> v1_8_R3 - v1_16_R3
				FieldReflection.getAccessible ( entity_class ,
												"pitch" ).setFloat ( handle , pitch );
			}
		} catch ( ClassNotFoundException | InvocationTargetException | IllegalAccessException
				| NoSuchMethodException | NoSuchFieldException e ) {
			e.printStackTrace ( );
		}
	}
	
	public static void setPitchDirty ( Entity entity , float pitch ) {
		try {
			setPitchDirty ( BukkitReflection.getHandle ( entity ) , pitch );
		} catch ( IllegalAccessException | InvocationTargetException e ) {
			e.printStackTrace ( );
		}
	}
	
	public static void setLocationDirty ( Object handle , Location location ) {
		setPositionDirty ( handle , location.toVector ( ) );
		setYawDirty ( handle , location.getYaw ( ) );
		setYawDirty ( handle , location.getPitch ( ) );
	}
	
	public static void setLocationDirty ( Entity entity , Location location ) {
		try {
			setLocationDirty ( BukkitReflection.getHandle ( entity ) , location );
		} catch ( IllegalAccessException | InvocationTargetException e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * Sets the location of the provided nms entity. Note that this method doesn't teleport the entity.
	 * <p>
	 * @param entity the nms entity to set.
	 * @param x location's x.
	 * @param y location's y.
	 * @param z location's z.
	 * @param yaw rotation around axis y.
	 * @param pitch rotation around axis x.
	 */
	public static void setLocation ( Object entity , double x , double y , double z , float yaw , float pitch ) {
		try {
			MethodReflection.invoke ( MethodReflection.getAccessible ( entity.getClass ( ) , "setLocation" , double.class ,
																	   double.class , double.class , float.class ,
																	   float.class ) ,
									  entity , x , y , z , yaw , pitch );
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * Sets the location of the provided entity. Note that this method doesn't teleport the entity.
	 * <p>
	 * @param entity the entity to set.
	 * @param x location's x.
	 * @param y location's y.
	 * @param z location's z.
	 * @param yaw rotation around axis y.
	 * @param pitch rotation around axis x.
	 */
	public static void setLocation ( Entity entity , double x , double y , double z , float yaw , float pitch ) {
		try {
			Object handle = BukkitReflection.getHandle ( entity );
			
			MethodReflection.invoke (
					MethodReflection.get ( getEntityClass ( ) , "setLocation" , double.class ,
										   double.class , double.class , float.class ,
										   float.class ) ,
					handle , x , y , z , yaw , pitch );
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * Sets the location of the provided entity. Note that this method doesn't teleport the entity.
	 * <p>
	 * @param entity the entity to set.
	 * @param location the new location for the entity.
	 */
	public static void setLocation ( Entity entity , Location location ) {
		setLocation ( entity , location.getX ( ) , location.getY ( ) , location.getZ ( ) , location.getYaw ( ) ,
					  location.getPitch ( ) );
	}
	
	/**
	 * Sets the location of the provided entity. Note that this method doesn't teleport the entity.
	 * <p>
	 * @param entity the entity to set.
	 * @param location the new location for the entity.
	 */
	public static void setLocation ( Entity entity , Vector location ) {
		setLocation ( entity , location.toLocation ( entity.getWorld ( ) ) );
	}
	
	/**
	 * Sets the location of the provided nms entity. Note that this method doesn't teleport the entity.
	 * <p>
	 * @param entity the nms entity to set.
	 * @param location the new location for the entity.
	 */
	public static void setLocation ( Object entity , Location location ) {
		setLocation ( entity , location.getX ( ) , location.getY ( ) , location.getZ ( ) , location.getYaw ( ) ,
					  location.getPitch ( ) );
	}
	
	/**
	 * Sets the location of the provided nms entity. Note that this method doesn't teleport the entity.
	 * <p>
	 * @param entity the nms entity to set.
	 * @param location the new location for the entity.
	 * @param world the world.
	 */
	public static void setLocation ( Object entity , Vector location , World world ) {
		setLocation ( entity , location.toLocation ( world ) );
	}
	
	/**
	 * Sets the yaw and pitch of the provided entity. Note that this method doesn't
	 * teleport the entity.
	 * <p>
	 * Also {@link #setLocation(Entity , double , double , double , float , float)} is
	 * recommended to be used instead.
	 * <p>
	 * @param entity entity the entity to set.
	 * @param yaw    the new yaw.
	 * @param pitch  the new pitch.
	 */
	public static void setYawPitch ( Entity entity , float yaw , float pitch ) {
		try {
			Object handle = BukkitReflection.getHandle ( entity );
			
			MethodReflection.invoke (
					MethodReflection.getAccessible ( getEntityClass ( ) , "setYawPitch" , float.class , float.class ) ,
					handle , yaw , pitch );
		} catch ( IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * Gets whether the provided entity is visible or not.
	 * <p>
	 * @param entity the entity to check
	 * @return true if visible.
	 */
	public static boolean isVisible ( Entity entity ) {
		try {
			Object handle = BukkitReflection.getHandle ( entity );
			
			return !( boolean ) MethodReflection.invoke (
					MethodReflection.get ( getEntityClass ( ) , "isInvisible" ) , handle );
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e ) {
			e.printStackTrace ( );
			return false;
		}
	}
	
	/**
	 * Sets whether the provided entity is visible or not.
	 * <p>
	 * @param entity the entity to set.
	 * @param visible {@code true} = visible, {@code false} = invisible.
	 */
	public static void setVisible ( Entity entity , boolean visible ) {
		try {
			Object handle = BukkitReflection.getHandle ( entity );
			
			MethodReflection.invoke ( MethodReflection.get (
					getEntityClass ( ) , "setInvisible" , boolean.class ) , handle , !visible );
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * Gets whether the provided entity is silent or not.
	 * <p>
	 * @param entity the entity to check.
	 * @return true if silent.
	 */
	public static boolean isSilent ( Entity entity ) {
		// TODO: FIX
		
		try {
			Object handle = BukkitReflection.getHandle ( entity );
			Method getter = null;
			
			switch ( Version.getServerVersion ( ) ) {
				case v1_8_R1:
				case v1_8_R2:
				case v1_8_R3:
					getter = MethodReflection.get ( handle.getClass ( ) , "R" );
					break;
				
				case v1_9_R1:
				case v1_9_R2:
					getter = MethodReflection.get ( handle.getClass ( ) , "ad" );
					break;
				
				default:
					return ( boolean ) MethodReflection.invoke (
							MethodReflection.get ( entity.getClass ( ) , "isSilent" ) , entity );
			}
			return ( boolean ) MethodReflection.invoke ( getter , handle );
		} catch ( NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e ) {
			e.printStackTrace ( );
			return false;
		}
	}
	
	/**
	 * Sets whether the provided entity is silent or not.
	 * <p>
	 * @param entity the entity to set.
	 * @param silent true = silent, false = not silent.
	 */
	public static void setSilent ( Entity entity , boolean silent ) {
		try {
			Object handle = BukkitReflection.getHandle ( entity );
			
			if ( Version.getServerVersion ( ).isOlderEquals ( Version.v1_9_R2 ) ) {
				MethodReflection.invoke ( MethodReflection.get (
						handle.getClass ( ) , "b" , boolean.class ) , handle , silent );
			} else {
				MethodReflection.invoke ( MethodReflection.get (
						getEntityClass ( ) , "setSilent" , boolean.class ) , handle , silent );
			}
		} catch ( NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | ClassNotFoundException e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * Gets whether or not the provided entity is invulnerable.
	 * <p>
	 * @param entity the entity to check.
	 * @return true if invulnerable.
	 */
	public static boolean isInvulnerable ( Entity entity ) {
		try {
			Object handle = BukkitReflection.getHandle ( entity );
			
			if ( Version.getServerVersion ( ).isOlderEquals ( Version.v1_9_R2 ) ) {
				Class < ? > damage_class   = ClassReflection.getNmsClass ( "DamageSource" );
				Object      generic_damage = FieldReflection.getValue ( damage_class , "GENERIC" );
				Method getter = MethodReflection.get ( handle.getClass ( ) , "isInvulnerable" ,
													   damage_class );
				
				return ( boolean ) MethodReflection.invoke ( getter , handle , generic_damage );
			} else {
				return ( boolean ) MethodReflection.invoke (
						MethodReflection.get ( getEntityClass ( ) , "isInvulnerable" ) , entity );
			}
		} catch ( NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | ClassNotFoundException | NoSuchFieldException e ) {
			e.printStackTrace ( );
			return false;
		}
	}
	
	/**
	 * Sets whether the provided entity is invulnerable or not.
	 * <p>
	 * When an entity is invulnerable it can only be damaged by players in
	 * creative mode.
	 * <p>
	 * @param entity the entity to set.
	 * @param invulnerable true = invulnerable, false = vulnerable.
	 */
	public static void setInvulnerable ( Entity entity , boolean invulnerable ) {
		try {
			Object handle = BukkitReflection.getHandle ( entity );
			
			if ( Version.getServerVersion ( ).isOlderEquals ( Version.v1_9_R2 ) ) {
				Class < ? > nms_entity_class = ClassReflection.getNmsClass ( "Entity" );
				Field       field            = FieldReflection.getAccessible ( nms_entity_class , "invulnerable" );
				
				field.set ( handle , invulnerable );
			} else {
				MethodReflection.invoke ( MethodReflection.get (
						getEntityClass ( ) , "setInvulnerable" , boolean.class ) , handle , invulnerable );
			}
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NoSuchFieldException | ClassNotFoundException e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * Sets the provided {@link ArmorStand} as invulnerable. This method is to be
	 * used instead of {@link #setInvulnerable(Entity , boolean)}.
	 * <p>
	 * @param stand the armor stand to set.
	 */
	public static void setInvulnerable ( ArmorStand stand , boolean invulnerable ) {
		// TODO: FIX
		
		String field_name = "h";
		switch ( Version.getServerVersion ( ) ) {
			case v1_9_R1:
				field_name = "by";
				break;
			case v1_9_R2:
			case v1_11_R1:
				field_name = "bz";
				break;
			case v1_10_R1:
			case v1_12_R1:
				field_name = "bA";
				break;
			case v1_13_R1:
			case v1_13_R2:
				field_name = "bG";
				break;
			case v1_14_R1:
				field_name = "bD";
				break;
			default:
				field_name = "h";
				break;
		}
		
		try {
			FieldReflection.setValue ( BukkitReflection.getHandle ( stand ) , field_name , !invulnerable );
		} catch ( SecurityException | NoSuchFieldException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException ex ) {
			ex.printStackTrace ( );
		}
	}
	
	/**
	 * Gets the height of the provided {@link Entity}.
	 *
	 * @param entity the entity to get.
	 * @return the height of the provided {@link Entity}.
	 */
	public static double getHeight ( Entity entity ) {
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_12_R1 ) ) {
			return entity.getHeight ( );
		} else {
			try {
				Class < ? > entity_class = ClassReflection.getMinecraftClass ( "Entity" , "world.entity" );
				Field       height_field = FieldReflection.getAccessible ( entity_class , "length" );
				
				return height_field.getFloat ( BukkitReflection.getHandle ( entity ) );
			} catch ( IllegalAccessException | InvocationTargetException
					| ClassNotFoundException | NoSuchFieldException ex ) {
				throw new IllegalStateException ( ex );
			}
		}
	}
	
	/**
	 * Plays a sound for the provided player.
	 * <p>
	 * This method will fail silently if Location or Sound are null. No
	 * sound will be heard by the player if their client does not have the
	 * respective sound for the value passed.
	 * <p>
	 * @param sound the internal sound name to play.
	 * @param volume the volume of the sound.
	 * @param pitch the pitch of the sound.
	 */
	public static void playNamedSound ( Player player , String sound , float volume , float pitch ) {
		// TODO: FIX
		
		try {
			Class < ? > category_enum = ClassReflection.getNmsClass ( "SoundCategory" );
			Object master = MethodReflection.invoke (
					MethodReflection.get ( category_enum , "valueOf" , String.class ) , category_enum , "MASTER" );
			
			Location location = player.getEyeLocation ( );
			double   x        = location.getX ( );
			double   y        = location.getY ( );
			double   z        = location.getZ ( );
			
			Object packet = ConstructorReflection.newInstance (
					ClassReflection.getNmsClass ( "PacketPlayOutCustomSoundEffect" ) ,
					new Class < ? >[] { String.class ,
							category_enum , double.class , double.class , double.class , float.class , float.class
					} ,
					sound , master , x , y , z , volume , pitch );
			
			BukkitReflection.sendPacket ( player , packet );
		} catch ( ClassNotFoundException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * Plays a Sound at the provided Location in the World.
	 * <p>
	 * This method will fail silently if Location or Sound are null. No
	 * sound will be heard by the players if their clients do not have the
	 * respective sound for the value passed.
	 * <p>
	 * @param location the location to play the sound.
	 * @param sound the internal sound name to play.
	 * @param volume the volume of the sound.
	 * @param pitch the pitch of the sound.
	 */
	public static void playNameSoundAt ( Location location , String sound , float volume , float pitch ) {
		// TODO: FIX
		World  w = location.getWorld ( );
		double x = location.getX ( );
		double y = location.getY ( );
		double z = location.getZ ( );
		
		try {
			Class < ? > category_enum = ClassReflection.getNmsClass ( "SoundCategory" );
			Object master = MethodReflection.invoke (
					MethodReflection.get ( category_enum , "valueOf" , String.class ) ,
					category_enum , "MASTER" );
			
			Object packet = ConstructorReflection.newInstance (
					ClassReflection.getNmsClass ( "PacketPlayOutCustomSoundEffect" ) ,
					new Class < ? >[] { String.class ,
							category_enum , double.class , double.class , double.class , float.class , float.class
					} ,
					sound , master , x , y , z , volume , pitch );
			
			Object world_server = FieldReflection.getValue ( w.getClass ( ) , "world" );
			Object minecraft_server = MethodReflection.invoke (
					MethodReflection.get ( world_server.getClass ( ) , "getMinecraftServer" ) ,
					world_server );
			Object player_list = MethodReflection.invoke (
					MethodReflection.get ( minecraft_server.getClass ( ) , "getPlayerList" ) ,
					minecraft_server );
			
			Class < ? > human_class  = ClassReflection.getNmsClass ( "EntityHuman" );
			Class < ? > packet_class = ClassReflection.getNmsClass ( "Packet" );
			
			int dimension = world_server.getClass ( ).getField ( "dimension" ).getInt ( world_server );
			
			MethodReflection.invoke (
					MethodReflection.get ( player_list.getClass ( ) , "sendPacketNearby" , human_class , double.class ,
										   double.class ,
										   double.class , double.class , int.class , packet_class ) ,
					player_list , null , x , y , z , ( volume > 1.0F ? 16.0F * volume : 16.0D ) , dimension , packet );
		} catch ( SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException
				| ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException e ) {
			e.printStackTrace ( );
		}
	}
	
	protected static Class < ? > getEntityClass ( ) throws ClassNotFoundException {
		return ClassReflection.getMinecraftClass ( "Entity" , "world.entity" );
	}
	
	protected static Object getEntityClassFieldValue ( Object instance , String name )
			throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
		return FieldReflection.getAccessible ( getEntityClass ( ) , name ).get ( instance );
	}
	
	protected static Object invokeEntityClassMethod ( Object instance , String name ,
			Class < ? >[] types , Object... args )
			throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		return MethodReflection.getAccessible ( getEntityClass ( ) , name , types ).invoke ( instance , args );
	}
}

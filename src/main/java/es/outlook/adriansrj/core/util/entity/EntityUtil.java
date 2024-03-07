package es.outlook.adriansrj.core.util.entity;

import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Useful class for dealing with Bukkit {@link Entity}s.
 * <p>
 * @author AdrianSR / Saturday 23 November, 2019 / 10:44 PM
 */
public class EntityUtil {
	
	/**
	 * TODO: Description
	 * <p>
	 * @author AdrianSR / Friday 11 June, 2021 / 10:50 AM
	 */
	public enum EnumForceCondition {
		
		/**
		 * Force or not based on whether the potion effect to be added <strong>lasts
		 * less</strong> than the current potion effect the player has.
		 * <p>
		 * <strong>Note that this condition is considered fulfilled if the player does
		 * not yet have the potion effect.</strong>
		 */
		SHORTER_DURATION {
			@Override
			public boolean test ( LivingEntity entity , PotionEffectType type , Duration duration , int amplifier ) {
				PotionEffect current = entity.getActivePotionEffects ( ).stream ( )
						.filter ( potion -> Objects.equals ( potion.getType ( ) , type ) ).findAny ( ).orElse ( null );
				
				if ( current != null ) {
					return !duration.isInfinite ( ) && duration.toSeconds ( ) < ( current.getDuration ( ) / 20 );
				} else {
					return true;
				}
			}
		},
		
		/**
		 * Force or not based on whether the potion effect to be added <strong>lasts
		 * longer</strong> than the current potion effect the player has.
		 * <p>
		 * <strong>Note that this condition is considered fulfilled if the player does
		 * not yet have the potion effect.</strong>
		 */
		LONGER_DURATION {
			@Override
			public boolean test ( LivingEntity entity , PotionEffectType type , Duration duration , int amplifier ) {
				PotionEffect current = entity.getActivePotionEffects ( ).stream ( )
						.filter ( potion -> Objects.equals ( potion.getType ( ) , type ) ).findAny ( ).orElse ( null );
				
				if ( current != null ) {
					return duration.isInfinite ( ) || duration.toSeconds ( ) > ( current.getDuration ( ) / 20 );
				} else {
					return true;
				}
			}
		},
		
		/**
		 * Force or not based on whether the potion effect to be added <strong>has a
		 * lower amplifier</strong> than the current potion effect the player has.
		 * <p>
		 * <strong>Note that this condition is considered fulfilled if the player does
		 * not yet have the potion effect.</strong>
		 */
		LOWER_AMPLIFIER {
			@Override
			public boolean test ( LivingEntity entity , PotionEffectType type , Duration duration , int amplifier ) {
				PotionEffect current = entity.getActivePotionEffects ( ).stream ( )
						.filter ( potion -> Objects.equals ( potion.getType ( ) , type ) ).findAny ( ).orElse ( null );
				
				if ( current != null ) {
					return amplifier < current.getAmplifier ( );
				} else {
					return true;
				}
			}
		},
		
		/**
		 * Force or not based on whether the potion effect to be added <strong>has a
		 * higher amplifier</strong> than the current potion effect the player has.
		 * <p>
		 * <strong>Note that this condition is considered fulfilled if the player does
		 * not yet have the potion effect.</strong>
		 */
		HIGHER_AMPLIFIER {
			@Override
			public boolean test ( LivingEntity entity , PotionEffectType type , Duration duration , int amplifier ) {
				PotionEffect current = entity.getActivePotionEffects ( ).stream ( )
						.filter ( potion -> Objects.equals ( potion.getType ( ) , type ) ).findAny ( ).orElse ( null );
				
				if ( current != null ) {
					return amplifier > current.getAmplifier ( );
				} else {
					return true;
				}
			}
		},
		
		/**
		 * Force or not based on whether the potion effect to be added <strong>lasts
		 * less or has a lower amplifier</strong> than the current potion effect the
		 * player has.
		 * <p>
		 * <strong>Note that this condition is considered fulfilled if the player does
		 * not yet have the potion effect.</strong>
		 */
		SHORTER_DURATION_OR_LOWER_AMPLIFIER {
			@Override
			public boolean test ( LivingEntity entity , PotionEffectType type , Duration duration , int amplifier ) {
				return EnumForceCondition.SHORTER_DURATION.test ( entity , type , duration , amplifier )
						|| EnumForceCondition.LOWER_AMPLIFIER.test ( entity , type , duration , amplifier );
			}
		},
		
		/**
		 * Force or not based on whether the potion effect to be added <strong>lasts
		 * longer or has a higher amplifier</strong> than the current potion effect the
		 * player has.
		 * <p>
		 * <strong>Note that this condition is considered fulfilled if the player does
		 * not yet have the potion effect.</strong>
		 */
		LONGER_DURATION_OR_HIGHER_AMPLIFIER {
			@Override
			public boolean test ( LivingEntity entity , PotionEffectType type , Duration duration , int amplifier ) {
				return EnumForceCondition.LONGER_DURATION.test ( entity , type , duration , amplifier )
						|| EnumForceCondition.HIGHER_AMPLIFIER.test ( entity , type , duration , amplifier );
			}
		},
		
		;
		
		/**
		 * Tests if this condition is met or not.
		 * <p>
		 * @param entity    the entity to test.
		 * @param type      the type of the potion effect that is to be added.
		 * @param duration  the duration of the potion effect that is to be added.
		 * @param amplifier the amplifier of the potion effect that is to be added.
		 * @return if this condition is met or not.
		 */
		public abstract boolean test ( LivingEntity entity , PotionEffectType type , Duration duration ,
				int amplifier );
	}
	
	/**
	 * Returns the {@link Entity} associated with the given {@link UUID} and with
	 * the given type.
	 * <p>
	 * @param <T>   entity type.
	 * @param world the world in which the entity is.
	 * @param type  the class of the entity type.
	 * @param id    the UUID of the entity to find.
	 * @return the {@link Entity} associated with the given {@link UUID} and with
	 *         the given type, or null if could not be found.
	 */
	@SuppressWarnings ( "unchecked" )
	public static < T extends Entity > T getEntity ( World world , Class < ? extends Entity > type , UUID id ) {
		return ( T ) world.getEntities ( ).stream ( )
				.filter ( entity ->
								  type.isAssignableFrom ( entity.getClass ( ) ) &&
										  id.equals ( entity.getUniqueId ( ) ) ).findAny ( ).orElse ( null );
	}
	
	/**
	 * Returns the {@link Entity} associated with the given {@link UUID}.
	 * @param world the world in which the entity is.
	 * @param id the UUID of the entity to find.
	 * @return the {@link Entity} associated with the given {@link UUID}, or null if could not be found.
	 */
	public static Entity getEntity ( World world , UUID id ) {
		return world.getEntities ( ).stream ( ).filter ( entity -> id.equals ( entity.getUniqueId ( ) ) ).findAny ( )
				.orElse ( null );
	}
	
	public static PotionEffect getActivePotionEffect ( LivingEntity entity , PotionEffectType type ) {
		return entity.getActivePotionEffects ( ).stream ( )
				.filter ( potion -> Objects.equals ( potion.getType ( ) , type ) ).findAny ( ).orElse ( null );
	}
	
	public static boolean addPotionEffectForcing ( LivingEntity entity , PotionEffectType type , Duration duration ,
			int amplifier ) {
		long duration_seconds = duration.isInfinite ( ) ? 0 : duration.toSeconds ( );
		
		if ( ( duration.isInfinite ( ) || duration_seconds > 0L ) && amplifier >= 0 ) {
			return entity.addPotionEffect ( new PotionEffect ( type ,
															   duration.isInfinite ( ) ? Integer.MAX_VALUE :
																	   ( int ) ( duration_seconds * 20 ) ,
															   amplifier ) ,
											true );
		} else {
			return false;
		}
	}
	
	public static boolean addPotionEffect ( LivingEntity entity , PotionEffectType type , Duration duration ,
			int amplifier , EnumForceCondition... force_conditions ) {
		long    duration_seconds = duration.isInfinite ( ) ? 0 : duration.toSeconds ( );
		boolean force            = force_conditions.length > 0;
		
		if ( ( duration.isInfinite ( ) || duration_seconds > 0L ) && amplifier >= 0 ) {
			if ( force_conditions != null ) {
				for ( EnumForceCondition condition : force_conditions ) {
					force &= condition.test ( entity , type , duration , amplifier );
				}
			}
			
			return entity.addPotionEffect (
					new PotionEffect ( type ,
									   duration.isInfinite ( ) ? Integer.MAX_VALUE :
											   ( int ) ( duration_seconds * 20 ) ,
									   amplifier ) ,
					force );
		} else {
			return false;
		}
	}
	
	public static void clearPotionEffects ( LivingEntity entity ) {
		entity.getActivePotionEffects ( ).stream ( ).map ( PotionEffect :: getType )
				.forEach ( entity :: removePotionEffect );
	}
	
	public static void setMaxHealth ( LivingEntity entity , double max_health ) {
		if ( Version.getServerVersion ( ).isOlder ( Version.v1_9_R1 ) ) {
			entity.setMaxHealth ( max_health );
		} else {
			// TODO: TEST
			AttributeInstance max_health_att = entity.getAttribute ( Attribute.GENERIC_MAX_HEALTH );
			
			max_health_att.setBaseValue ( max_health );
		}
	}
	
	public static double getMaxHealth ( LivingEntity entity ) {
		if ( Version.getServerVersion ( ).isOlder ( Version.v1_9_R1 ) ) {
			return entity.getMaxHealth ( );
		} else {
			// TODO: TEST
			return entity.getAttribute ( Attribute.GENERIC_MAX_HEALTH ).getBaseValue ( );
		}
	}
	
	@SuppressWarnings ( "deprecation" )
	public static List < Entity > getPassengers ( Entity entity ) {
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_12_R1 ) ) {
			return entity.getPassengers ( );
		} else {
			return Arrays.asList ( entity.getPassenger ( ) );
		}
	}
}
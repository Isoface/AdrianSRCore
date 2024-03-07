package es.outlook.adriansrj.core.util.entity;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents an entity that is identified by using an {@link UUID unique id}.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 10:48 AM
 */
public class UUIDEntity < T extends Entity > {
	
	/**
	 * The entity's unique id.
	 */
	protected final UUID uuid;
	
	/**
	 * The entity's type class.
	 */
	protected final Class < T > clazz;
	
	/**
	 * Construct the {@code UUIDEntity} from its {@link UUID unique id} and its
	 * type class.
	 * <p>
	 * @param uuid  the entity's {@link UUID unique id}.
	 * @param clazz the type class of the entity.
	 */
	public UUIDEntity ( final UUID uuid , final Class < T > clazz ) {
		this.uuid  = Objects.requireNonNull ( uuid , "uuid cannot be null" );
		this.clazz = Objects.requireNonNull ( clazz , "class cannot be null" );
	}
	
	/**
	 * Construct the {@code UUIDEntity} from its respective {@link Entity entity}.
	 * <p>
	 * @param entity the respective entity.
	 */
	@SuppressWarnings ( "unchecked" )
	public UUIDEntity ( final Entity entity ) {
		this ( entity.getUniqueId ( ) , ( Class < T > ) entity.getClass ( ) );
	}
	
	/**
	 * Gets the entity's {@link UUID unique id}.
	 * <p>
	 * @return the entity's {@link UUID unique id}.
	 */
	public UUID getUniqueId ( ) {
		return uuid;
	}
	
	/**
	 * Gets the entity's type class.
	 * <p>
	 * @return entity's type class.
	 */
	public Class < T > getTypeClass ( ) {
		return clazz;
	}
	
	/**
	 * Gets the respective entity associated with the entity's {@link #uuid}. This
	 * will look in all the worlds for an entity whose its {@link UUID unique id} is
	 * the same as this.
	 * <p>
	 * @return the respective entity.
	 */
	@SuppressWarnings ( "unchecked" )
	public T get ( ) {
		if ( Player.class.equals ( getTypeClass ( ) ) ) {
			return ( T ) Bukkit.getPlayer ( getUniqueId ( ) );
		}
		
		for ( World world : Bukkit.getWorlds ( ) ) {
			Optional < T > filter = world.getEntitiesByClass ( getTypeClass ( ) ).stream ( )
					.filter ( entity -> entity.getUniqueId ( ).equals ( getUniqueId ( ) ) ).findAny ( );
			if ( filter.isPresent ( ) ) {
				return filter.get ( );
			}
			
			//			// we do this to avoid ConcurrentModificationException
			//			synchronized ( world ) {
			//				List < Entity > entities = world.getEntities ( );
			//				for ( int i = 0 ; i < entities.size ( ) ; i ++ ) {
			//					Entity entity = entities.get ( i );
			//					if ( entity.getClass ( ).isAssignableFrom ( getTypeClass ( ) )
			//							&& entity.getUniqueId ( ).equals ( getUniqueId ( ) ) ) {
			//						return (T) entity;
			//					}
			//				}
			//			}
		}
		return null;
	}
	
	/**
	 * Wraps the returned value from {@link #get()} in a {@link Optional}.
	 * <p>
	 * @return the returned value from {@link #get()} in a {@link Optional}.
	 * @see #get()
	 */
	public Optional < T > getOptional ( ) {
		return Optional.ofNullable ( get ( ) );
	}
	
	@Override
	public boolean equals ( Object obj ) {
		if ( obj instanceof UUIDEntity ) {
			return getUniqueId ( ).equals ( ( ( UUIDEntity < ? > ) obj ).getUniqueId ( ) );
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode ( ) {
		final int prime  = 31;
		int       result = 1;
		result = prime * result + ( ( uuid == null ) ? 0 : uuid.hashCode ( ) );
		return result;
	}
}
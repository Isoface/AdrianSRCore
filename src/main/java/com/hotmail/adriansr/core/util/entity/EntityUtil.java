package com.hotmail.adriansr.core.util.entity;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Useful class for dealing with Bukkit {@link Entity}s.
 * <p>
 * @author AdrianSR / Saturday 23 November, 2019 / 10:44 PM
 */
public class EntityUtil {

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
	@SuppressWarnings("unchecked")
	public static <T extends Entity> T getEntity(World world, Class<? extends Entity> type, UUID id) {
		return (T) world.getEntities().stream()
				.filter(entity -> 
				type.isAssignableFrom(entity.getClass()) &&
				id.equals(entity.getUniqueId())).findAny().orElse(null);
	}
	
	/**
	 * Returns the {@link Entity} associated with the given {@link UUID}.
	 * @param world the world in which the entity is.
	 * @param id the UUID of the entity to find.
	 * @return the {@link Entity} associated with the given {@link UUID}, or null if could not be found.
	 */
	public static Entity getEntity(World world, UUID id) {
		return world.getEntities().stream().filter(entity -> id.equals(entity.getUniqueId())).findAny().orElse(null);
	}
}
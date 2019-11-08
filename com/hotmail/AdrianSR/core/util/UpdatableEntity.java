package com.hotmail.AdrianSR.core.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;

/**
 * Represents a class that
 * get the current entity from
 * his UUID, or null if is dead
 * or does not exists.
 * <p>
 * @author AdrianSR
 */
public class UpdatableEntity implements Cloneable {

	/**
	 * All Updatable Entities.
	 */
	private static final Map<UUID, UpdatableEntity> UPDATABLE_ENTITIES = new HashMap<UUID, UpdatableEntity>();

	/**
	 * Returns all initialized
	 * {@link UpdatableEntity}s.
	 * <p>
	 * @return
	 */
	public static Map<UUID, UpdatableEntity> getUpdatableEntities() {
		return Collections.unmodifiableMap(UPDATABLE_ENTITIES);
	}
	
	/**
	 * Returns the {@link UpdatableEntity}
	 * registered with the giving {@link UUID}.
	 * <p>
	 * @param uuid
	 * @return
	 */
	public static UpdatableEntity getForUUID(UUID uuid) {
		return UPDATABLE_ENTITIES.get(uuid);
	}

	/**
	 * Class values.
	 */
	private final UUID         id;
	private final EntityType type;
	private final World     world;
	
	/**
	 * Construct a new Updatable 
	 * Entity.
	 * <p>
	 * @param ent the entity.
	 */
	public UpdatableEntity(final Entity ent) {
		this.id    = ent.getUniqueId();
		this.type  = ent.getType();
		this.world = ent.getWorld();
		
		/* register */
		UPDATABLE_ENTITIES.put(id, this);
	}
	
	/**
	 * Construct a new Updatable Entity.
	 * <p>
	 * @param id the entity id.
	 * @param world the entity world.
	 * @param type the entity Type.
	 */
	public UpdatableEntity(final UUID id, final World world, final EntityType type) {
		this.id    = id;
		this.type  = type;
		this.world = world;
		
		/* register */
		UPDATABLE_ENTITIES.put(id, this);
	}
	
	/**
	 * Get the entity registered
	 * in the world (server if is instanceof player), 
	 * from his id.
	 * <p>
	 * @return the current registered 
	 * entity in the world with the UUID
	 * or the registered player in the 
	 * server with the UUID.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> T get() {
		final Class<? extends Entity> clazz = type.getEntityClass();
		
		/* return as player */
		if (type == EntityType.PLAYER) {
			return (T) Bukkit.getPlayer(id);
		}
		
		/* return as entity */
		return (T) ReflectionUtils.getEntityByClass(clazz, world, id);
	}
	
	/**
	 * Get the entity {@link UUID}.
	 * <p>
	 * @return entity Unique UUID.
	 */
	public UUID getUniqueId() {
		return id;
	}
	
	/**
	 * Get the entity type.
	 * <p>
	 * @return the entity {@link EntityType}.
	 */
	public EntityType getType() {
		return type;
	}

	/**
	 * Get the entity class.
	 * <p>
	 * @return the entity {@link EntityType#getEntityClass()}
	 */
	public Class<? extends Entity> getTypeClass() {
		return type.getEntityClass();
	}
	
	/**
	 * Get the entity world.
	 * <p>
	 * @return the entity {@link World.}
	 */
	public World getWorld() {
		return world;
	}
	
	public UpdatableEntity clone() {
		try {
			return (UpdatableEntity) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof UpdatableEntity)) {
			return false;
		}
		
		UpdatableEntity other = (UpdatableEntity) obj;
		if ((other.getUniqueId() != null) != (id != null)) {
			return false;
		}
		
		if (id != null && !id.equals(other.getUniqueId())) {
			return false;
		}
		return true;
	}
}
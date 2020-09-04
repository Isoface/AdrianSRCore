package com.hotmail.adriansr.core.util.math.target;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * An implementation of {@link TargetFilter} for filtering entities with an
 * {@link EntityType}.
 * <p>
 * @author AdrianSR / Friday 17 April, 2020 / 03:58 PM
 */
public class TargetFilterType implements TargetFilter {
	
	/** the entity type */
	protected final EntityType type;
	
	/**
	 * Constructs the target filter.
	 * <p>
	 * @param type the entity type to filter.
	 */
	public TargetFilterType ( EntityType type ) {
		this.type = type;
	}

	@Override
	public boolean accept ( Entity entity ) {
		return type.equals ( entity.getType ( ) );
	}
}
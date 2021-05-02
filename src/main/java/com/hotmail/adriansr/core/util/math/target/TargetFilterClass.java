package com.hotmail.adriansr.core.util.math.target;

import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;

/**
 * An implementation of {@link TargetFilter} for filtering entities with an
 * entity class.
 * <p>
 * @author AdrianSR / Friday 17 April, 2020 / 03:58 PM
 * @param <T> the entity class, for example: {@link Enderman}.
 */
public class TargetFilterClass < T extends Entity > implements TargetFilter {
	
	/** the class extending {@link Entity} */
	protected final Class < T > clazz;
	
	/**
	 * Construct the target filter.
	 * <p>
	 * @param clazz the entity class.
	 */
	public TargetFilterClass ( Class < T > clazz ) {
		this.clazz = clazz;
	}

	@Override
	public boolean accept ( Entity entity ) {
		return clazz.isAssignableFrom ( entity.getClass ( ) );
	}
}
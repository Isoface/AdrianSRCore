package com.hotmail.adriansr.core.util.math.target;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * An implementation of {@link TargetFilterType} for filtering entities of type
 * {@link Player}.
 * <p>
 * @author AdrianSR / Friday 17 April, 2020 / 03:59 PM
 */
public class TargetFilterPlayer extends TargetFilterType {
	
	/**
	 * Constructs the target filter.
	 */
	public TargetFilterPlayer ( ) {
		super ( EntityType.PLAYER );
	}
}
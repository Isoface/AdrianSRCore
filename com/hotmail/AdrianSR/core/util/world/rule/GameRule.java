package com.hotmail.AdrianSR.core.util.world.rule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.World;

import com.hotmail.AdrianSR.core.util.Validable;

public class GameRule implements Validable {
	
	private final GameRuleType     type;
	private final Object          value;
	private final Set<GameRule> parents;
	
	public GameRule(GameRuleType type, Object value, GameRule... parents) {
		Validate.notNull(type, "The type cannot be null!");
		Validate.notNull(value, "The value cannot be null!");
		Validate.isTrue(type.isSameDataType(value),
				"The value of the game rule type is '" + type.getDataType().getPrimitive() + "'!");
		this.type    = type;
		this.value   = value;
		this.parents = new HashSet<>();
		this.parents.addAll(Arrays.asList(parents)
				.stream()
				.filter(GameRule::isValid)
				.collect(Collectors.toSet()));
	}

	public GameRuleType getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}
	
	public Set<GameRule> getParents() {
		return parents;
	}
	
	/**
	 * Applies this rule the given world.
	 * <p>
	 * @param world the world to apply.
	 * @return the given world with this rule applied.
	 */
	public World apply(World world) {
		this.getType().apply(world, value);
		this.parents.forEach(parent -> parent.getType().apply(world, parent.getValue()));
		return world;
	}

	@Override
	public boolean isValid() {
		return getType() != null && getValue() != null && getType().isSameDataType(getValue());
	}

	@Override
	public boolean isInvalid() {
		return !isValid();
	}
}
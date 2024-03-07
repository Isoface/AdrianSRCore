package es.outlook.adriansrj.core.util.world;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.World;

import es.outlook.adriansrj.core.util.Validable;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Tuesday 28 July, 2020 / 04:16 PM
 */
public class GameRule implements Validable {
	
	protected final GameRuleType        type;
	protected final Object             value;
	protected final Set < GameRule > parents;
	
	/**
	 * Construct the {@link GameRule}.
	 * <p>
	 * @param type game rule type.
	 * @param value game rule value.
	 * @param parents required {@link GameRule GameRules} to work.
	 */
	public GameRule ( GameRuleType type , Object value , GameRule... parents ) {
		Validate.notNull ( type , "yype cannot be null!" );
		Validate.notNull ( value , "value cannot be null!" );
		Validate.isTrue ( type.isSameDataType ( value ) , "the specified type and value are incompatible!" );
		
		this.type    = type;
		this.value   = value;
		this.parents = new HashSet < > ( );
		this.parents.addAll ( Arrays.asList ( parents )
				.stream ( )
				.filter ( GameRule::isValid )
				.collect ( Collectors.toSet ( ) ) );
	}

	/**
	 * Gets the game rule type.
	 * <p>
	 * @return game rule type.
	 */
	public GameRuleType getType ( ) {
		return type;
	}

	/**
	 * Gets the game rule value.
	 * <p>
	 * @return game rule value.
	 */
	public Object getValue ( ) {
		return value;
	}
	
	/**
	 * Gets the {@link GameRule GameRules} this requires to work.
	 * <p>
	 * @return required game rules.
	 */
	public Set < GameRule > getParents ( ) {
		return parents;
	}
	
	/**
	 * Applies this rule the given world.
	 * <p>
	 * @param world the world to apply.
	 * @return the same world, useful for chaining.
	 */
	public World apply ( World world ) {
		this.getType ( ).apply ( world , value );
		this.parents.forEach ( parent -> parent.getType ( ).apply ( world , parent.getValue ( ) ) );
		return world;
	}

	@Override
	public boolean isValid ( ) {
		return getType ( ) != null && getValue ( ) != null && getType ( ).isSameDataType ( getValue ( ) );
	}

	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}
}
package es.outlook.adriansrj.core.util.configurable.enchantment;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.itemstack.ItemStackEnchantment;
import es.outlook.adriansrj.core.util.loadable.LoadableEntry;
import es.outlook.adriansrj.core.util.saveable.SavableEntry;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Wednesday 09 June, 2021 / 10:55 AM
 */
public class ConfigurableItemStackEnchantment implements Configurable {

	public static final String ENCHANT_KEY = "enchant";
	public static final String LEVEL_KEY   = "level";

	@LoadableEntry ( key = ENCHANT_KEY )
	@SavableEntry ( key = ENCHANT_KEY )
	public String name;

	@LoadableEntry ( key = LEVEL_KEY )
	@SavableEntry ( key = LEVEL_KEY )
	public int level;
	
	@SuppressWarnings ( "deprecation" )
	public ConfigurableItemStackEnchantment ( Enchantment enchantment , int level ) {
		this.name  = enchantment.getName ( );
		this.level = level;
	}
	
	public ConfigurableItemStackEnchantment ( ItemStackEnchantment enchantment ) {
		this ( enchantment.getEnchantment ( ) , enchantment.getLevel ( ) );
	}
	
	public ConfigurableItemStackEnchantment ( ) {
		// uninitialized
	}
	
	public String getEnchantmentName ( ) {
		return name;
	}

	public int getEnchantmentLevel ( ) {
		return level;
	}
	
	@SuppressWarnings ( "deprecation" )
	public ItemStackEnchantment toEnchantment ( ) {
		if ( isValid ( ) ) {
			return new ItemStackEnchantment ( Enchantment.getByName ( name ) , level );
		} else {
			return null;
		}
	}
	
	@Override
	public ConfigurableItemStackEnchantment load ( ConfigurationSection section ) {
		loadEntries ( section );
		return this;
	}

	@Override
	public int save ( ConfigurationSection section ) {
		return saveEntries ( section );
	}
	
	@SuppressWarnings ( "deprecation" )
	@Override
	public boolean isValid ( ) {
		return Enchantment.getByName ( name ) != null && level > -1;
	}

	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}
}
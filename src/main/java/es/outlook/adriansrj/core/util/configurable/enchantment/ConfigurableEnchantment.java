package es.outlook.adriansrj.core.util.configurable.enchantment;

import java.util.Arrays;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.loadable.Loadable;
import es.outlook.adriansrj.core.util.loadable.LoadableEntry;
import es.outlook.adriansrj.core.util.saveable.SavableEntry;

/**
 * @author AdrianSR / Wednesday 09 June, 2021 / 10:58 AM
 * @deprecated {@link ConfigurableItemStackEnchantment} should be used instead.
 * @see {@link ConfigurableItemStackEnchantment}
 */
@Deprecated
public class ConfigurableEnchantment implements Configurable {
	
	public static final String ENCHANT_KEY = "enchant";
	public static final String   LEVEL_KEY = "level";
	
	@LoadableEntry(key = ENCHANT_KEY)
	@SavableEntry (key = ENCHANT_KEY)
	public String name;
	
	@LoadableEntry(key = LEVEL_KEY)
	@SavableEntry (key = LEVEL_KEY)
	public Integer level;
	
	/**
	 * Construct a new uninitialized {@link ConfigurableEnchantment}.
	 */
	public ConfigurableEnchantment() {
		/* uninitialized */
	}
	
	public ConfigurableEnchantment(String enchantment_name, Integer level) {
		this.name  = enchantment_name;
		this.level = level;
	}
	
	@SuppressWarnings("deprecation")
	public ConfigurableEnchantment(Enchantment enchantment, Integer level) {
		this(enchantment.getName(), level);
	}
	
	@Override
	public Loadable load(ConfigurationSection section) {
		return this.loadEntries(section);
	}
	
	@Override
	public int save(ConfigurationSection section) {
		return this.saveEntries(section);
	}
	
	public String getEnchantmentName() {
		return name;
	}
	
	public Integer getEnchantmentLevel() {
		return level;
	}
	
    /**
     * Gets the enchantment bound to the
     * enchantment name.
     * <p>
     * @return Enchantment the {@link Enchantment} bound to the {@link ConfigurableEnchantment#name}.
     */
	@SuppressWarnings("deprecation")
	public Enchantment getEnchantment() {
		return Arrays.stream(Enchantment.values())
				.filter(enchantment -> enchantment.getName().equalsIgnoreCase(getEnchantmentName())).findAny()
				.orElse(null);
	}

	@Override
	public boolean isValid() {
		return getEnchantment() != null 
				&& getEnchantmentLevel() != null && getEnchantmentLevel().intValue() > -1;
	}

	@Override
	public boolean isInvalid() {
		return !isValid();
	}
}
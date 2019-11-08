package com.hotmail.AdrianSR.core.util.file;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

public final class YmlUtils {

	public static int setNotSet(ConfigurationSection section, String key, Object value) {
		if (!section.isSet(key)) {
			section.set(key, value);
			return 1;
		}
		return 0;
	}
	
	public static int setNotEqual(ConfigurationSection section, String key, Object update) {
		if (!section.isSet(key) || !update.equals(section.get(key))) {
			section.set(key, update);
			return 1;
		}
		return 0;
	}
	
	public static ConfigurationSection createNonExisting(ConfigurationSection parent, String name) {
		return parent.isConfigurationSection(name) ? parent.getConfigurationSection(name) : parent.createSection(name);
	}
	
	public static ConfigurationSection getConfigurationSection(CustomYamlConfiguration yaml, String node) {
		String fix_node = getFixPathSeparator(yaml, node);
		if (yaml.isConfigurationSection(fix_node)) {
			return yaml.getConfigurationSection(fix_node);
		}
		
		if (fix_node.lastIndexOf(yaml.options().pathSeparator()) != -1) {
			return yaml.getConfigurationSection(fix_node.substring(0, fix_node.lastIndexOf(yaml.options().pathSeparator())));
		}
		return yaml;
	}
	
	public static String getPathName(CustomYamlConfiguration yaml, String key) {
		String fixed_key = getFixPathSeparator(yaml, key);
		if (fixed_key.lastIndexOf(yaml.options().pathSeparator()) != -1
				&& fixed_key.length() > ( fixed_key.lastIndexOf(yaml.options().pathSeparator()) + 1 ) ) {
			return fixed_key.substring(fixed_key.lastIndexOf(yaml.options().pathSeparator()) + 1);
		}
		return fixed_key;
	}
	
	public static String getFixPathSeparator(Configuration section, String key) {
		return key.replace('.', section.options().pathSeparator());
	}
	
	/**
	 * Ignore '//' '\\' in the {@link World} names.
	 * 
	 * @param worldName the World name to check.
	 * @return the real World name in this Operating System.
	 */
	public static String getVerifyUniversalPCAddres(String worldName) {
		// get string to transform and check it.
		String world = worldName;
		if (world != null) {
			try {
				// get world and check it.
				World Try = Bukkit.getWorld(world);
				if (Try == null || Try.getName() == null) {
					if (world.contains("/")) {
						world = world.replace("/", "\\");
					} else if (world.contains("\\")) {
						world = world.replace("\\", "/");
					}
				}
			} catch (Throwable e) {
				// ignore.
			}
		}
		return world;
	}
}

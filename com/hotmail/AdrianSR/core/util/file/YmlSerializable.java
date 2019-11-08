package com.hotmail.AdrianSR.core.util.file;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents all objects
 * that can saved to
 * a {@link ConfigurationSection}.
 * <p>
 * @author AdrianSR
 */
public interface YmlSerializable {

	/**
	 * Save to a {@link ConfigurationSection}.
	 * <p>
	 * @param section the {@link ConfigurationSection} to save in.
	 */
	public void save(ConfigurationSection section);
}

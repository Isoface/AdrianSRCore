package com.hotmail.AdrianSR.core.util.dependence;

import org.apache.commons.lang.Validate;

import com.hotmail.AdrianSR.core.main.CustomPlugin;

/**
 * Represents the possible plugins on which a {@link CustomPlugin} may depend.
 * <p>
 * @author AdrianSR / Date: 13 oct. 2019 / Time: 12:11:06 p. m.
 */
public final class PluginDependence {

	private final String           depended_plugin;
	private final String   depended_plugin_version;
	private final String         not_found_message;
	private final String min_version_error_message;

	/**
	 * Constructs a new Plugin Dependence.
	 * <p>
	 * @param depended_plugin           the name of the dependen plugin.
	 * @param depended_plugin_version   the required minimum version of the dependen
	 *                                  plugin, or null if no required.
	 * @param not_found_message         the message to be printed when the depended
	 *                                  plugin is missing.
	 * @param min_version_error_message the error message to print if the version of
	 *                                  the depended plugin is older than the
	 *                                  required version, or null if no minimum
	 *                                  version is required.
	 */
	public PluginDependence(String depended_plugin, String depended_plugin_version, String not_found_message,
			String min_version_error_message) {
		Validate.notNull(depended_plugin, "The depended plugin cannot be null!");
		Validate.notNull(depended_plugin, "The not found message cannot be null!");
		this.depended_plugin           = depended_plugin;
		this.depended_plugin_version   = depended_plugin_version;
		this.not_found_message         = not_found_message;
		this.min_version_error_message = min_version_error_message;
	}

	/**
	 * Gets the name of the depended plugin.
	 * <p>
	 * @return Depended plugin's name.
	 */
	public String getDependedPlugin() {
		return depended_plugin;
	}

	/**
	 * Gets the required minimum version of the depended plugin, or null if no
	 * minimum version required.
	 * <p>
	 * PRECISION IS NOT GUARANTEED WHEN DETECTING THE MINIMUM VERSION REQUIRED.
	 * <p>
	 * @return the required minimum version of the depended plugin, or null if no
	 *         minimum version required.
	 */
	public String getRequiredMinimumVersion() {
		return depended_plugin_version;
	}

	/**
	 * Gets the message to be printed when the depended plugin is missing.
	 * <p>
	 * @return the message to be printed when the depended plugin is missing.
	 */
	public String getNotFoundErrorMessage() {
		return not_found_message;
	}

	/**
	 * Returns the error message to print if the version of the depended plugin is
	 * older than the required version, or null if no minimum version is required.
	 * <p>
	 * If this message is null, a default message will be sent.
	 * <p>
	 * @return required minimum version of the depended plugin error message, or
	 *         null if no minimum version is required.
	 */
	public String getRequiredMinimumVersionErrorMessage() {
		return min_version_error_message;
	}
}
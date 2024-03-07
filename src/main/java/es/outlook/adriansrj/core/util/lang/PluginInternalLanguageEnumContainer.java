package es.outlook.adriansrj.core.util.lang;

import java.util.Locale;

/**
 * Like the name says, this is a container for the different internal languages
 * that a {@link Plugin} may support.
 * The {@link Plugin} loads the configuration of a specific language and saves
 * it on a {@link PluginInternalLanguageEnumContainer}, then the plugin can use that language
 * for internal process.
 * Note that the {@link PluginInternalLanguageEnumContainer}s must have only the internal language
 * of the plugin, the containers are not intended to be configured externally in any way!
 * <p>
 * @author AdrianSR / Date: 14 oct. 2019 / Time: 10:04:34 p.�m.
 */
public interface PluginInternalLanguageEnumContainer {
	
	/**
	 * Returns the key (or path) where the
	 * value of this container item can be found.
	 * <p>
	 * @return the key. (or path).
	 */
	String getKey ( );
	
	/**
	 * Gets the value of the specific given {@link Locale},
	 * or null if the given locale is not supported.
	 * <p>
	 * @param locale the language locale.
	 * @return the value of the locale or null if not supported.
	 */
	String getValue ( Locale locale );
	
	/**
	 * Sets the value for the specified {@link Locale}.
	 * <p>
	 * @param value the value of the given locale.
	 * @param locale the language identifier of this item.
	 */
	void setValue ( String value , Locale locale );
}
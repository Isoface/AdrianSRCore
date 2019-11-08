package com.hotmail.AdrianSR.core.util.lang;

import java.util.Locale;

/**
 * The language enum container, like the name says, it's a container for the different languages
 * that a {@link CustomPlugin} may support.
 * The {@link CustomPlugin} loads the configuration of a specific language and saves
 * it on a {@link CustomPluginLanguageContainer}, then the plugin can use that language
 * for internal process.
 * Note that the {@link CustomPluginLanguageContainer}s must have only the internal language
 * of the plugin, the containers can not be configured by server owners in any way!
 * <p>
 * @author AdrianSR / Date: 14 oct. 2019 / Time: 10:04:34 p. m.
 */
public interface CustomPluginLanguageEnumContainer {
	
	/**
	 * Returns the key (or path) where the
	 * value of this container item can be found.
	 * <p>
	 * @return the key. (or path).
	 */
	public String getKey();
	
	/**
	 * Gets the value of the specific given {@link Locale},
	 * or null if the given locale is not supported.
	 * <p>
	 * @param locale the language locale.
	 * @return the value of the locale or null if not supported.
	 */
	public String getValue(Locale locale);
	
	/**
	 * Sets the value of the specific given {@link Locale}.
	 * <p>
	 * @param value the value of the given locale.
	 * @param locale the language identifier of this item.
	 */
	public void setValue(String value, Locale locale);
}
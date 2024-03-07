package es.outlook.adriansrj.core.util.yaml;

import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationOptionsComments;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Useful class for dealing with {@link YamlConfiguration}s.
 * <p>
 *
 * @author AdrianSR / Thursday 16 April, 2020 / 12:13 PM
 */
public class YamlUtil {
	
	/**
	 * Sets the specified path to the given value if and only if it is not already set.
	 * <p>
	 * If value is null, the entry will be removed. Any existing entry will be replaced, regardless of what the new
	 * value is.
	 * <p>
	 * Some implementations may have limitations on what you may store. See their individual javadocs for details. No
	 * implementations should allow you to store {@link Configuration}s or {@link ConfigurationSection}s, please use
	 * {@link ConfigurationSection#createSection(java.lang.String)} for that.
	 * <p>
	 *
	 * @param section the configuration section to set.
	 * @param path    the path of the object to set.
	 * @param value   new value to set the path to.
	 */
	public static boolean setNotSet ( ConfigurationSection section , String path , Object value ) {
		if ( section.isSet ( path ) ) {
			return false;
		}
		
		section.set ( path , value );
		return true;
	}
	
	/**
	 * Sets the specified path to the given value if and only if it the value at the provided path is not the same as
	 * {@code value}.
	 * <p>
	 * If value is null, the entry will be removed. Any existing entry will be replaced, regardless of what the new
	 * value is.
	 * <p>
	 * Some implementations may have limitations on what you may store. See their individual javadocs for details. No
	 * implementations should allow you to store {@link Configuration}s or {@link ConfigurationSection}s, please use
	 * {@link ConfigurationSection#createSection(java.lang.String)} for that.
	 * <p>
	 *
	 * @param section the configuration section to set.
	 * @param path    the path of the object to set.
	 * @param value   new value to set the path to.
	 */
	public static boolean setNotEqual ( ConfigurationSection section , String path , Object value ) {
		if ( setNotSet ( section , path , value ) ) {
			return true;
		} else if ( Objects.equals ( value , section.get ( path ) ) ) {
			return false;
		}
		
		section.set ( path , value );
		return true;
	}
	
	/**
	 * Creates an empty {@link ConfigurationSection} within the specified {@link ConfigurationSection} if and only if
	 * there is no another section with the same name as the provided.
	 * <p>
	 *
	 * @param section the section at which the new section will be created.
	 * @param name    the name for the section.
	 *
	 * @return the newly created section, or the already existing section.
	 */
	public static ConfigurationSection createNotExisting ( ConfigurationSection section , String name ) {
		return section.isConfigurationSection ( name ) ? section.getConfigurationSection ( name )
				: section.createSection ( name );
	}
	
	/**
	 * Gets the {@link ConfigurationSection}s within the desired {@link ConfigurationSection} ( sub-sections of the
	 * desired section ).
	 * <p>
	 *
	 * @param section the {@link ConfigurationSection} where the sub-sections are stored.
	 *
	 * @return the sub-sections of the desired section.
	 */
	public static Set < ConfigurationSection > getSubSections ( ConfigurationSection section ) {
		Set < ConfigurationSection > sections = new HashSet <> ( );
		
		section.getKeys ( false ).stream ( ).filter ( key -> section.isConfigurationSection ( key ) )
				.forEach ( key -> sections.add ( section.getConfigurationSection ( key ) ) );
		
		return sections;
	}
	
	/**
	 * Replaces the desired character that is used as path separator by the specified {@code alt_char}.
	 * <p>
	 *
	 * @param key            the key that contains the separator to replace.
	 * @param path_separator the path separator to replace.
	 * @param alt_char       the path separator to use instead the current one.
	 *
	 * @return key containing the new path separator.
	 */
	public static String alternatePathSeparator ( String key , char path_separator , char alt_char ) {
		return key.replace ( path_separator , alt_char );
	}
	
	/**
	 * Replaces the common path separator {@literal '.'} by the specified {@code alt_char}.
	 * <p>
	 *
	 * @param key      the key that contains the common separator to replace.
	 * @param alt_char the path separator to use instead the common one.
	 *
	 * @return key containing the new path separator.
	 */
	public static String alternatePathSeparator ( String key , char alt_char ) {
		return alternatePathSeparator ( key , '.' , alt_char );
	}
	
	/**
	 * Comments the specified path.
	 * <p><p/>
	 * This method will check if the root of the provided {@link ConfigurationSection} is a valid instanceof {@link
	 * es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments}, and, if it is, then the path will be
	 * commented, otherwise, this method will not have any effect.
	 * <p><p/>
	 *
	 * @param section the section within the specified path is.
	 * @param path    the path to comment.
	 * @param comment the comment.
	 */
	public static void comment ( ConfigurationSection section , String path , String comment ) {
		if ( section.getRoot ( ) instanceof YamlConfigurationComments ) {
			YamlConfigurationOptionsComments options =
					( ( YamlConfigurationComments ) section.getRoot ( ) ).options ( );
			String current_path = section.getCurrentPath ( );
			
			if ( StringUtil.isNotBlank ( current_path ) ) {
				options.comment ( current_path + options.pathSeparator ( ) + path , comment );
			} else {
				options.comment ( path , comment );
			}
		}
	}
}
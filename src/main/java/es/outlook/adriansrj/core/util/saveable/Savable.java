package es.outlook.adriansrj.core.util.saveable;

import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.ConfigurableCollectionEntry;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents an Object that can be saved on a {@link ConfigurationSection}.
 * <p>
 * The method {@link #saveEntries(ConfigurationSection)} saves the value of any field in this instance that has the
 * annotation {@link SavableEntry}.
 * <p>
 *
 * @author AdrianSR / Sunday 03 November, 2019 / 11:56 AM
 */
public interface Savable {
	
	/**
	 * Save this Object on the provided {@link ConfigurationSection}.
	 * <p>
	 *
	 * @param section the section to save on.
	 *
	 * @return total done changes in the provided section after saving.
	 */
	int save ( ConfigurationSection section );
	
	/**
	 * Save on the given {@link ConfigurationSection}.
	 * <p>
	 * This method saves all the fields that has the annotation {@link SavableEntry}.
	 * <p>
	 *
	 * @param section the section in which this will be saved.
	 * @return total done changes in the provided section after saving.
	 */
	@SuppressWarnings ( "unchecked" )
	default int saveEntries ( ConfigurationSection section ) {
		return saveEntries ( section , true );
	}
	
	/**
	 * Save on the given {@link ConfigurationSection}.
	 * <p>
	 * This method saves all the fields that has the annotation {@link SavableEntry}.
	 * <p>
	 *
	 * @param section the section in which this will be saved.
	 * @param inherited whether to save inherited entries (entries in the super classes).
	 *
	 * @return total done changes in the provided section after saving.
	 */
	@SuppressWarnings ( "unchecked" )
	default int saveEntries ( ConfigurationSection section , boolean inherited ) {
		Validate.notNull ( section , "The section cannot be null!" );
		
		Set < Field > entries = new LinkedHashSet <> ( );
		int           count   = 0;
		Class < ? >   clazz   = getClass ( );
		
		// finding out entries
		if ( inherited ) {
			Class < ? extends Savable > current = clazz.asSubclass ( Savable.class );
			
			while ( current != null ) {
				// extracting fields
				entries.addAll ( Arrays.asList ( current.getDeclaredFields ( ) ) );
				
				// finding out next savable class from super classes
				Class < ? > super_clazz = current.getSuperclass ( );
				
				if ( Savable.class.isAssignableFrom ( super_clazz ) ) {
					current = super_clazz.asSubclass ( Savable.class );
				} else {
					current = null;
				}
			}
		} else {
			entries.addAll ( Arrays.asList ( getClass ( ).getDeclaredFields ( ) ) );
		}
		
		// then saving
		for ( final Field entry : entries ) {
			if ( Modifier.isStatic ( entry.getModifiers ( ) ) ) {
				continue;
			}
			
			// FieldReflection.setAccessible ( entry , true );
			entry.setAccessible ( true );
			
			Object value = null;
			
			try {
				// value = FieldReflection.getValue ( this , entry.getName ( )
				// );
				value = entry.get ( this );
			} catch ( SecurityException | IllegalArgumentException | IllegalAccessException ex ) {
				throw new IllegalStateException ( "cannot get the value of the field '" + entry.getName ( ) + "'" ,
												  ex );
			}
			
			if ( value == null ) {
				continue;
			}
			
			ConfigurationSection sub_section = null;
			
			if ( entry.isAnnotationPresent ( SavableEntry.class )
					|| entry.isAnnotationPresent ( ConfigurableEntry.class ) ) {
				// reading configuration
				String         key        = null;
				String         comment    = null;
				String         subsection = null;
				SaveActionType action     = null;
				
				if ( entry.isAnnotationPresent ( SavableEntry.class ) ) {
					SavableEntry options = entry.getAnnotation ( SavableEntry.class );
					
					key        = options.key ( );
					subsection = options.subsection ( );
					action     = options.action ( );
					comment    = options.comment ( );
				} else {
					ConfigurableEntry options = entry.getAnnotation ( ConfigurableEntry.class );
					
					key        = options.key ( );
					subsection = options.subsection ( );
					action     = options.saveAction ( );
					comment    = options.comment ( );
				}
				
				boolean saveable  = Savable.class.isAssignableFrom ( entry.getType ( ) );
				boolean blank_key = StringUtils.isBlank ( key );
				ConfigurationSection destiny_section = ( StringUtils.isNotBlank ( subsection )
						? YamlUtil.createNotExisting ( section , subsection ) : section );
				
				if ( blank_key && !saveable ) {
					throw new UnsupportedOperationException ( "Only the entries of type '" + Savable.class.getName ( )
																	  + "' can be saved on a ConfigurationSection " +
																	  "without a key!" );
				}
				
				if ( destiny_section != null ) {
					if ( blank_key ) {
						count += ( ( Savable ) value ).save ( destiny_section );
					} else {
						if ( saveable ) {
							count += ( ( Savable ) value ).save (
									destiny_section = YamlUtil.createNotExisting ( destiny_section , key ) );
						} else {
							// enums are saved in a slightly different way:
							// the name of the enumeration is saved.
							if ( entry.getType ( ).isEnum ( ) ) {
								value = ( ( Enum < ? > ) value ).name ( );
							}
							
							switch ( action ) {
								case NOT_EQUAL:
									count += YamlUtil.setNotEqual ( destiny_section , key , value ) ? 1 : 0;
									break;
								
								case NOT_SET:
									count += YamlUtil.setNotSet ( destiny_section , key , value ) ? 1 : 0;
									break;
								
								case NORMAL:
								default:
									destiny_section.set ( key , value );
									count++;
									break;
							}
						}
					}
					
					// commenting
					if ( StringUtil.isNotBlank ( comment ) ) {
						YamlUtil.comment ( destiny_section , key , comment );
					}
				}
			} else if ( entry.isAnnotationPresent ( SavableCollectionEntry.class )
					|| entry.isAnnotationPresent ( ConfigurableCollectionEntry.class ) ) {
				// reading configuration
				String subsection       = null;
				String subsectionprefix = null;
				
				if ( entry.isAnnotationPresent ( SavableCollectionEntry.class ) ) {
					SavableCollectionEntry options = entry.getAnnotation ( SavableCollectionEntry.class );
					
					subsection       = options.subsection ( );
					subsectionprefix = options.subsectionprefix ( );
				} else {
					ConfigurableCollectionEntry options = entry.getAnnotation ( ConfigurableCollectionEntry.class );
					
					subsection       = options.subsection ( );
					subsectionprefix = options.subsectionprefix ( );
				}
				
				sub_section = StringUtils.isNotBlank ( subsection )
						? YamlUtil.createNotExisting ( section , subsection ) : null;
				
				if ( sub_section == null && StringUtils.isNotBlank ( subsection ) ) {
					throw new UnsupportedOperationException (
							"The savable collection entry '" + entry.getName ( )
									+ "' specified an invalid sub-section: '" + subsection + "'" );
				}
				
				if ( !Collection.class.isAssignableFrom ( entry.getType ( ) ) ) {
					throw new UnsupportedOperationException (
							"The savable collection entry '" + entry.getName ( )
									+ "' is not a valid instance of '" + Collection.class.getName ( ) + "'" );
				}
				
				if ( !Savable.class
						.isAssignableFrom ( FieldReflection.getParameterizedTypesClasses ( entry )[ 0 ] ) ) {
					throw new UnsupportedOperationException (
							"The elements of the collection of the savable collection entry " + entry.getName ( )
									+ " must be of type '" + Savable.class.getName ( ) + "'!" );
				}
				
				int item_count = 0;
				
				for ( Savable item : ( Collection < Savable > ) value ) {
					count += item.save (
							( sub_section != null ? sub_section : section ).createSection ( StringUtil.defaultIfBlank (
									subsectionprefix , "" ) + item_count++ ) );
				}
			}
		}
		return count;
	}
}
package com.hotmail.adriansr.core.util.saveable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.reflection.general.FieldReflection;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

/**
 * Represents an Object that can be saved on a {@link ConfigurationSection}.
 * <p>
 * The method {@link #saveEntries(ConfigurationSection)} saves the value of any
 * field in this instance that has the annotation {@link SaveableEntry}.
 * <p>
 * @author AdrianSR / Sunday 03 November, 2019 / 11:56 AM
 */
public interface Saveable {
	
	/**
	 * Save this Object on the provided {@link ConfigurationSection}.
	 * <p>
	 * @param section the section to save on.
	 * @return total done changes in the provided section after saving.
	 */
	public int save(ConfigurationSection section);
	
	/**
	 * Save on the given {@link ConfigurationSection}.
	 * <p>
	 * This method saves all the fields that has the annotation
	 * {@link SaveableEntry}.
	 * <p>
	 * @param section the section in which this will be saved.
	 * @return total done changes in the provided section after saving.
	 */
	@SuppressWarnings("unchecked")
	public default int saveEntries ( ConfigurationSection section ) {
		Validate.notNull ( section , "The section cannot be null!" );
		
		List < Field > entries = new ArrayList < > ( );
		int              count = 0;
		Class < ? >      clazz = getClass ( );
		
		// finding out entries
		Class < ? extends Saveable > current = clazz.asSubclass ( Saveable.class );
		
		while ( current != null ) {
			// extracting fields
			for ( Field field : current.getDeclaredFields ( ) ) {
				if ( entries.contains ( field ) ) {
					continue;
				} else {
					entries.add ( field );
				}
			}
			
			// finding out next saveable class from super classes
			Class < ? > super_clazz = current.getSuperclass ( );
			
			if ( Saveable.class.isAssignableFrom ( super_clazz ) ) {
				current = super_clazz.asSubclass ( Saveable.class );
			} else {
				current = null;
			}
		}
		
		// then saving
		for ( Field entry : entries ) {
			Object value = null;
			
			try {
				value = FieldReflection.getValue ( this , entry.getName ( ) );
			} catch ( SecurityException | NoSuchFieldException 
					| IllegalArgumentException | IllegalAccessException ex ) {
				throw new IllegalStateException ( "cannot get the value of the field '" + entry.getName ( ) + "'" );
			}
			
			if ( value == null ) {
				continue;
			}
			
			ConfigurationSection sub_section = null;
			
			if ( entry.isAnnotationPresent ( SaveableEntry.class ) ) {
				SaveableEntry                options = entry.getAnnotation ( SaveableEntry.class );
				boolean                     saveable = Saveable.class.isAssignableFrom ( entry.getType ( ) );
				boolean                    blank_key = StringUtils.isBlank ( options.key ( ) );
				ConfigurationSection destiny_section = ( !StringUtils.isBlank(options.subsection())
														? YamlUtil.createNotExisting(section, options.subsection())
														: section );
				
				if ( blank_key && !saveable ) {
					throw new UnsupportedOperationException("Only the entries of type '" + Saveable.class.getName()
							+ "' can be saved on a ConfigurationSection without a key!");
				}
				
				if ( destiny_section != null ) {
					if ( blank_key ) {
						count += ((Saveable) value).save(destiny_section);
					} else {
						switch (options.action()) {
						case NOT_EQUAL:
							count += YamlUtil.setNotEqual ( destiny_section, options.key(), value ) ? 1 : 0;
							break;
						case NOT_SET:
							count += YamlUtil.setNotSet ( destiny_section, options.key(), value ) ? 1 : 0;
							break;
						case NORMAL:
						default:
							destiny_section.set(options.key(), value);
							count ++;
							break;
						}
					}
				}
			} else if (entry.isAnnotationPresent(SaveableCollectionEntry.class)) {
				SaveableCollectionEntry options = entry.getAnnotation(SaveableCollectionEntry.class);
				sub_section                     = !StringUtils.isBlank(options.subsection())
						? YamlUtil.createNotExisting(section, options.subsection())
						: null;
				if (sub_section == null) {
					throw new UnsupportedOperationException(
							"The saveable collection entry '" + entry.getName() + "' must have a valid sub-section!");
				}
				
				if (!Collection.class.isAssignableFrom(entry.getType())) {
					throw new UnsupportedOperationException("The saveable collection entry '" + entry.getName()
							+ "' is not a valid instance of '" + Collection.class.getName() + "'!");
				}
				
				if (!Saveable.class.isAssignableFrom(FieldReflection.getParameterizedTypesClasses(entry)[0])) {
					throw new UnsupportedOperationException(
							"The elements of the collection of the saveable collection entry " + entry.getName()
									+ " must be of type '" + Saveable.class.getName() + "'!");
				}
				
				int item_count = 0;
				for (Saveable item : (Collection<Saveable>) value) {
					item.save(sub_section.createSection(
							StringUtil.defaultIfBlank(options.subsectionprefix(), "") + String.valueOf(item_count ++)));
				}
			}
		}
		return count;
	}
}
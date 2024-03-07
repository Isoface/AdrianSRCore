package es.outlook.adriansrj.core.util.loadable;

import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.Validable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableCollectionEntry;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.reflection.DataType;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents the classes that can be loaded from a
 * {@link ConfigurationSection}.
 * <p>
 * @apiNote Every single class that implements this interface must to have an
 *          empty constructor whose will act as 'uninitialized constructor',
 *          then the method {@link Loadable#loadEntries(ConfigurationSection)}
 *          can create uninitialized instances and initialize it by using the
 *          method {@link Loadable#load(ConfigurationSection)}, that may
 *          initialize that instance correctly.
 *          <p>
 * @author AdrianSR / Sunday 03 November, 2019 / 11:57 AM
 */
public interface Loadable extends Validable {
	
	/**
	 * Load the configuration of this from the given
	 * {@link ConfigurationSection}.
	 * <p>
	 *
	 * @param section the section where the configuration is located.
	 * @return This Object, for chaining.
	 */
	Loadable load ( ConfigurationSection section );
	
	/**
	 * Load the configuration of every single field in this class from the given
	 * {@link ConfigurationSection} that has the annotation
	 * {@link @LoadableEntry}/{@link @LoadableCollectionEntry} present.
	 * <p>
	 *
	 * @param section the section where the configuration is located.
	 * @return This Object, for chaining.
	 */
	@SuppressWarnings ( "unchecked" )
	default Loadable loadEntries ( ConfigurationSection section ) {
		return loadEntries ( section , true );
	}
	
	/**
	 * Load the configuration of every single field in this class from the given
	 * {@link ConfigurationSection} that has the annotation
	 * {@link @LoadableEntry}/{@link @LoadableCollectionEntry} present.
	 * <p>
	 *
	 * @param section the section where the configuration is located.
	 * @param inherited whether to load inherited entries (entries in the super classes).
	 * @return This Object, for chaining.
	 */
	@SuppressWarnings ( "unchecked" )
	default Loadable loadEntries ( ConfigurationSection section , boolean inherited ) {
		Validate.notNull ( section , "The section cannot be null!" );
		
		Set < Field > entries = new LinkedHashSet <> ( );
		
		// finding out entries
		if ( inherited ) {
			Class < ? extends Loadable > current = getClass ( ).asSubclass ( Loadable.class );
			
			while ( current != null ) {
				// extracting fields
				entries.addAll ( Arrays.asList ( current.getDeclaredFields ( ) ) );
				
				// finding out next loadable class from super classes
				Class < ? > super_clazz = current.getSuperclass ( );
				
				if ( Loadable.class.isAssignableFrom ( super_clazz ) ) {
					current = super_clazz.asSubclass ( Loadable.class );
				} else {
					current = null;
				}
			}
		} else {
			entries.addAll ( Arrays.asList ( getClass ( ).getDeclaredFields ( ) ) );
		}
		
		// then loading
		for ( Field entry : entries ) {
			if ( Modifier.isStatic ( entry.getModifiers ( ) ) ) {
				continue;
			}
			
			ConfigurationSection sub_section = null;
			
			if ( entry.isAnnotationPresent ( LoadableEntry.class )
					|| entry.isAnnotationPresent ( ConfigurableEntry.class ) ) {
				if ( Modifier.isFinal ( entry.getModifiers ( ) ) ) {
					throw new UnsupportedOperationException (
							"The loadable entry '" + entry.getName ( ) + "' cannot be final!" );
				}
				
				entry.setAccessible ( true );
				
				// reading configuration
				String key        = null;
				String subsection = null;
				
				if ( entry.isAnnotationPresent ( LoadableEntry.class ) ) {
					LoadableEntry options = entry.getAnnotation ( LoadableEntry.class );
					
					key        = options.key ( );
					subsection = options.subsection ( );
				} else {
					ConfigurableEntry options = entry.getAnnotation ( ConfigurableEntry.class );
					
					key        = options.key ( );
					subsection = options.subsection ( );
				}
				
				boolean loadable  = Loadable.class.isAssignableFrom ( entry.getType ( ) );
				boolean blank_key = StringUtils.isBlank ( key );
				ConfigurationSection destiny_section = ( StringUtils.isNotBlank ( subsection )
						? section.getConfigurationSection ( subsection ) : section );
				
				if ( blank_key && !loadable ) {
					throw new UnsupportedOperationException (
							"Only the entries of type '" + Loadable.class.getName ( )
									+ "' can be loadaed from a ConfigurationSection!" );
				}
				
				Object getted = null;
				
				if ( destiny_section != null ) {
					if ( loadable ) {
						try {
							Constructor < ? > noargs_constructor = entry.getType ( ).getConstructor ( );
							
							if ( noargs_constructor == null ) {
								throw new UnsupportedOperationException (
										"A new instance of '" + entry.getType ( ).getSimpleName ( )
												+ "' couldn't be created, as there is not a public constructor " +
												"with no arguments within that class!" );
							}
							
							noargs_constructor.setAccessible ( true );
							
							try {
								// creates and loads the instance
								getted = noargs_constructor.newInstance ( );
								
								if ( blank_key ) {
									getted = ( ( Loadable ) getted ).load ( destiny_section );
								} else {
									ConfigurationSection final_section = destiny_section.getConfigurationSection (
											key );
									
									if ( final_section != null ) {
										getted = ( ( Loadable ) getted ).load ( final_section );
									}
								}
							} catch ( InstantiationException | IllegalAccessException | IllegalArgumentException
									| InvocationTargetException e ) {
								throw new UnsupportedOperationException ( "A new instance of '"
																				  + entry.getType ( )
										.getSimpleName ( ) + "' couldn't be created: " , e );
							}
						} catch ( NoSuchMethodException | SecurityException e ) {
							/* ignore */
						}
					} else {
						// enums are to be loaded in a slightly different way:
						// the name of the enum is loaded, and from that name
						// we get the instance.
						if ( entry.getType ( ).isEnum ( ) ) {
							Object raw = destiny_section.get ( key );
							
							if ( raw != null ) {
								getted = EnumReflection.getEnumConstant (
										entry.getType ( ).asSubclass ( Enum.class ) , raw.toString ( ) );
							}
						} else {
							getted = destiny_section.get ( key , null );
						}
					}
				}
				
				if ( getted != null ) {
					DataType getted_type = DataType.fromClass ( getted.getClass ( ) );
					DataType entry_type  = DataType.fromClass ( entry.getType ( ) );
					
					if ( entry.getType ( ).isAssignableFrom ( getted.getClass ( ) ) || entry_type == getted_type
							|| ( getted_type.isNumber ( ) && entry_type.isNumber ( ) ) ) {
						try {
							if ( entry_type != null && entry_type.isNumber ( ) ) {
								Number wrapper = ( Number ) getted;
								Object number  = null;
								
								switch ( entry_type ) {
									case BYTE: { number = wrapper.byteValue ( ); break; }
									case SHORT: { number = wrapper.shortValue ( ); break; }
									case INTEGER: { number = wrapper.intValue ( ); break; }
									case LONG: { number = wrapper.longValue ( ); break; }
									case FLOAT: { number = wrapper.floatValue ( ); break; }
									case DOUBLE: { number = wrapper.doubleValue ( ); break; }
									
									default:
										number = wrapper;
								}
								
								entry.set ( this , number );
							} else {
								entry.set ( this , getted );
							}
						} catch ( SecurityException | IllegalAccessException e ) {
							throw new IllegalStateException (
									"cannot load the value to the field '" + entry.getName ( ) + "'" , e );
						}
					}
				}
			} else if ( entry.isAnnotationPresent ( LoadableCollectionEntry.class )
					|| entry.isAnnotationPresent ( ConfigurableCollectionEntry.class ) ) {
				if ( !Collection.class.isAssignableFrom ( entry.getType ( ) ) ) {
					throw new UnsupportedOperationException (
							"The loadable collection entry '" + entry.getName ( )
									+ "' is not a valid instance of '" + Collection.class.getName ( ) + "'!" );
				}
				
				Object value = null;
				
				try {
					value = FieldReflection.getValue ( this , entry.getName ( ) );
				} catch ( SecurityException | NoSuchFieldException | IllegalArgumentException
						| IllegalAccessException e1 ) {
					throw new IllegalStateException ( "cannot get the value of the field '" + entry.getName ( ) +
															  "'" );
				}
				
				if ( value == null ) {
					throw new UnsupportedOperationException (
							"The loadable collection entry '" + entry.getName ( ) + "' must be already initialized!" );
				}
				
				// reading subsection
				String subsection = null;
				
				if ( entry.isAnnotationPresent ( LoadableCollectionEntry.class ) ) {
					subsection = entry.getAnnotation ( LoadableCollectionEntry.class ).subsection ( );
				} else {
					subsection = entry.getAnnotation ( ConfigurableCollectionEntry.class ).subsection ( );
				}
				
				Collection < Loadable > collection = ( ( Collection < Loadable > ) value );
				
				if ( !Loadable.class
						.isAssignableFrom ( FieldReflection.getParameterizedTypesClasses ( entry )[ 0 ] ) ) {
					throw new UnsupportedOperationException ( "The elements type of the loadable collection entry '"
																	  + entry.getName ( ) + "' must be of the type '"
																	  + Loadable.class.getName ( ) + "'!" );
				}
				
				Class < ? extends Loadable > element_type = ( Class < ? extends Loadable > ) FieldReflection
						.getParameterizedTypesClasses ( entry )[ 0 ];
				Constructor < ? > no_params_constructor = null;
				
				try {
					no_params_constructor = element_type.getConstructor ( );
					no_params_constructor.setAccessible ( true );
				} catch ( NoSuchMethodException ex ) {
					throw new UnsupportedOperationException (
							"A new instance of '" + element_type.getSimpleName ( )
									+ "' couldn't be created, because there is" +
									" " +
									"not a constructor with no arguments " +
									"within" +
									" " +
									"that class!" );
				} catch ( SecurityException e ) {
					// ignored
				}
				
				sub_section = !StringUtils.isBlank ( subsection )
						? section.getConfigurationSection ( subsection ) : null;
				
				if ( sub_section != null || StringUtil.isBlank ( subsection ) ) {
					for ( String key : ( sub_section != null ? sub_section : section ).getKeys ( false ) ) {
						ConfigurationSection element_section = ( sub_section != null ? sub_section : section )
								.getConfigurationSection ( key );
						
						if ( element_section == null ) {
							continue;
						}
						
						try {
							// don't skip invalids
							collection.add (
									( ( Loadable ) no_params_constructor.newInstance ( ) ).load ( element_section ) );
						} catch ( Throwable t ) {
							t.printStackTrace ( );
							/* ignore */
						}
					}
				}
			}
		}
		return this;
	}
}
package com.hotmail.adriansr.core.util.loadable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.adriansr.core.util.Validable;
import com.hotmail.adriansr.core.util.reflection.DataType;
import com.hotmail.adriansr.core.util.reflection.general.FieldReflection;

/**
 * Represents the classes that can be loaded from a {@link ConfigurationSection}.
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
	 * Load the configuration of this from the given {@link ConfigurationSection}.
	 * <p>
	 * @param section the section where the configuration is located.
	 * @return This Object, for chaining.
	 */
	public Loadable load(ConfigurationSection section);

	/**
	 * Load the configuration of every single field in this class from the
	 * given {@link ConfigurationSection} that has the annotation
	 * {@link @LoadableEntry}/{@link @LoadableCollectionEntry} present.
	 * <p>
	 * @param section the section where the configuration is located.
	 * @return This Object, for chaining.
	 */
	@SuppressWarnings("unchecked")
	public default Loadable loadEntries(ConfigurationSection section) {
		Validate.notNull(section, "The section cannot be null!");
		for (Field entry : getClass().getDeclaredFields()) {
			ConfigurationSection sub_section = null;
			if (entry.isAnnotationPresent(LoadableEntry.class)) {
				if (Modifier.isFinal(entry.getModifiers())) {
					throw new UnsupportedOperationException(
							"The loadable entry '" + entry.getName() + "' cannot be final!");
				}

				LoadableEntry                options = entry.getAnnotation(LoadableEntry.class);
				boolean                     loadable = Loadable.class.isAssignableFrom(entry.getType());
				boolean                    blank_key = StringUtils.isBlank(options.key());
				ConfigurationSection destiny_section = ( !StringUtils.isBlank(options.subsection())
														? section.getConfigurationSection(options.subsection())
														: null );
				
				if ( blank_key && !loadable ) {
					throw new UnsupportedOperationException("Only the entries of type '" + Loadable.class.getName()
							+ "' can be loadaed from a ConfigurationSection!");
				}
				
				Object getted = null;
				if (blank_key) {
					try {
						Constructor<?> uninitialized_constructor = entry.getType().getConstructor();
						if (uninitialized_constructor == null) {
							throw new UnsupportedOperationException("A new instance of '"
									+ entry.getType().getSimpleName()
									+ "' couldn't be created, because there is not an empty constructor within that class!");
						}
						
						uninitialized_constructor.setAccessible(true);
						try {
							getted = uninitialized_constructor.newInstance(); // create uninitialized instance 
							getted = ((Loadable) getted).load(destiny_section);
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException e) {
							throw new UnsupportedOperationException("A new instance of '"
									+ entry.getType().getSimpleName()
									+ "' couldn't be created: ", e);
						}
					} catch (NoSuchMethodException | SecurityException e) {
						/* ignore */
					}
				} else {
					getted = destiny_section.get(options.key(), null);
				}
				
				if (getted != null) {
					if (entry.getType().isAssignableFrom(getted.getClass())
							|| DataType.fromClass(entry.getType()) == DataType.fromClass(getted.getClass())) {
						try {
							FieldReflection.setValue(this, entry.getName(), getted);
						} catch (SecurityException | NoSuchFieldException | IllegalAccessException e) {
							throw new IllegalStateException ( "cannot load the value to the field '" + entry.getName ( ) + "'" );
						}
					}
				}
			} else if (entry.isAnnotationPresent(LoadableCollectionEntry.class)) {
				if (!Collection.class.isAssignableFrom(entry.getType())) {
					throw new UnsupportedOperationException("The loadable collection entry '" + entry.getName()
							+ "' is not a valid instance of '" + Collection.class.getName() + "'!");
				}
				
				Object value = null;
				try {
					value = FieldReflection.getValue(this, entry.getName());
				} catch (SecurityException | NoSuchFieldException | IllegalArgumentException
						| IllegalAccessException e1) {
					throw new IllegalStateException ( "cannot get the value of the field '" + entry.getName ( ) + "'" );
				}
				
				if (value == null) {
					throw new UnsupportedOperationException(
							"The loadable collection entry '" + entry.getName() + "' must be already initialized!");
				}

				Collection<Loadable> collection = ((Collection<Loadable>) value);
				LoadableCollectionEntry options = entry.getAnnotation(LoadableCollectionEntry.class);
				if (!Loadable.class.isAssignableFrom(FieldReflection.getParameterizedTypesClasses(entry)[0])) {
					throw new UnsupportedOperationException("The elements type of the loadable collection entry '"
							+ entry.getName() + "' must be of the type '" + Loadable.class.getName() + "'!");
				}

				Class<? extends Loadable> element_type = (Class<? extends Loadable>) FieldReflection
						.getParameterizedTypesClasses(entry)[0];
				Constructor<?> uninitialized_constructor = null;
				try {
					uninitialized_constructor = element_type.getConstructor();
					if (uninitialized_constructor == null) {
						throw new UnsupportedOperationException("A new instance of '" + element_type.getSimpleName()
								+ "' couldn't be created, because there is not an empty constructor within that class!");
					}
					uninitialized_constructor.setAccessible(true);
				} catch (NoSuchMethodException | SecurityException e) {
					/* ignore */
				}

				sub_section = !StringUtils.isBlank(options.subsection())
						? section.getConfigurationSection(options.subsection())
						: null;
				for (String key : (sub_section != null ? sub_section : section).getKeys(false)) {
					ConfigurationSection element_section = (sub_section != null ? sub_section : section)
							.getConfigurationSection(key);
					if (element_section == null) {
						continue;
					}

					try {
						collection.add(((Loadable) uninitialized_constructor.newInstance()).load(element_section)); // don't skip invalids
//						Loadable element = ((Loadable) uninitialized_constructor.newInstance()).load(element_section);
//						if (element.isValid()) { // skip invalids
//							collection.add(element);
//						}
					} catch (Throwable t) {
						/* ignore */
					}
				}
			}
		}
		return this;
	}
}
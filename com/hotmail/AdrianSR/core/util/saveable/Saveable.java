package com.hotmail.AdrianSR.core.util.saveable;

import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSR.core.util.TextUtils;
import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;
import com.hotmail.AdrianSR.core.util.file.YmlUtils;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Sunday 03 November, 2019 / 11:56 AM
 */
public interface Saveable {
	
	public int save(ConfigurationSection section);
	
	/**
	 * Save on the given {@link ConfigurationSection}.
	 * <p>
	 * This method saves all the fields that has the annotation
	 * {@link SaveableEntry}.
	 * <p>
	 * @param section the section in which this will be saved.
	 * @return the number of made changes in the given section after saving.
	 */
	@SuppressWarnings("unchecked")
	public default int saveEntries(ConfigurationSection section) {
		Validate.notNull(section, "The section cannot be null!");
		int      count = 0;
		Class<?> clazz = getClass();
		for (Field entry : clazz.getDeclaredFields()) {
			Object value = ReflectionUtils.getField(this, entry.getName());
			if (value == null) {
				continue;
			}
			
			ConfigurationSection sub_section = null;
			if (entry.isAnnotationPresent(SaveableEntry.class)) {
				SaveableEntry                options = entry.getAnnotation(SaveableEntry.class);
				boolean                     saveable = Saveable.class.isAssignableFrom(entry.getType());
				boolean                    blank_key = StringUtils.isBlank(options.key());
				ConfigurationSection destiny_section = ( !StringUtils.isBlank(options.subsection())
														? YmlUtils.createNonExisting(section, options.subsection())
														: section );
				
				if ( blank_key && !saveable ) {
					throw new UnsupportedOperationException("Only the entries of type '" + Saveable.class.getName()
							+ "' can be saved on a ConfigurationSection without a key!");
				}
				
				if (blank_key) {
					count += ((Saveable) value).save(destiny_section);
				} else {
					switch (options.action()) {
					case NOT_EQUAL:
						count += YmlUtils.setNotEqual( destiny_section, options.key(), value );
						break;
					case NOT_SET:
						count += YmlUtils.setNotSet( destiny_section, options.key(), value );
						break;
					case NORMAL:
					default:
						destiny_section.set(options.key(), value);
						count ++;
						break;
					}
				}
			} else if (entry.isAnnotationPresent(SaveableCollectionEntry.class)) {
				SaveableCollectionEntry options = entry.getAnnotation(SaveableCollectionEntry.class);
				sub_section                     = !StringUtils.isBlank(options.subsection())
						? YmlUtils.createNonExisting(section, options.subsection())
						: null;
				if (sub_section == null) {
					throw new UnsupportedOperationException(
							"The saveable collection entry '" + entry.getName() + "' must have a valid sub-section!");
				}
				
				if (!Collection.class.isAssignableFrom(entry.getType())) {
					throw new UnsupportedOperationException("The saveable collection entry '" + entry.getName()
							+ "' is not a valid instance of '" + Collection.class.getName() + "'!");
				}
				
				if (!Saveable.class.isAssignableFrom(ReflectionUtils.getParameterizedTypesClasses(entry)[0])) {
					throw new UnsupportedOperationException(
							"The elements of the collection of the saveable collection entry " + entry.getName()
									+ " must be of type '" + Saveable.class.getName() + "'!");
				}
				
				int item_count = 0;
				for (Saveable item : (Collection<Saveable>) value) {
					item.save(sub_section.createSection(
							TextUtils.getNotNull(options.subsectionprefix(), "") + String.valueOf(item_count ++)));
				}
			}
		}
		return count;
	}
//	public default int saveEntries(ConfigurationSection section) {
//		int      count = 0;
//		Class<?> clazz = getClass();
////		if (clazz.isInterface()) {
////			throw new UnsupportedOperationException("The Saveable annotations doesn't work on interfaces!");
////		}
//		for (Field entry : clazz.getDeclaredFields()) {
//			Object value = ReflectionUtils.getField(this, entry.getName());
//			if (!entry.isAnnotationPresent(SaveableEntry.class) || value == null) {
//				continue;
//			}
//			
//			boolean               collection = Collection.class.isAssignableFrom(entry.getType());
//			SaveableEntry            options = entry.getAnnotation(SaveableEntry.class);
//			ConfigurationSection sub_section = !StringUtils.isBlank(options.subsection())
//					? YmlUtils.createNonExisting(section, options.subsection())
//					: null;
//			if (collection) {
//				if (sub_section == null) {
//					throw new UnsupportedOperationException(
//							"The saveable collection entry '" + entry.getName() + "' must have a valid sub-section!");
//				}
//				
//				Collection<?> collection_instance = (Collection<?>) value;
//				if (collection_instance.isEmpty()) {
//					continue;
//				}
//
//				int item_count = 0;
//				for (Object item : collection_instance) {
//					/* determines if the entry is a collection of Saveable entries */ // item != null && Saveable.class.isAssignableFrom(item.getClass())
//					if (item != null && Saveable.class.isAssignableFrom(ReflectionUtils.getParameterizedTypesClasses(entry)[0])) { 
//						((Saveable) item).save(sub_section.createSection(String.valueOf(item_count ++)));
//						continue;
//					}
//					break;
//				}
//			} else if (!StringUtils.isBlank(options.key())) {
//				switch (options.action()) {
//				case NOT_EQUAL:
//					count += YmlUtils.setNotEqual( ( sub_section != null ? sub_section : section ) , options.key(), value);
//					break;
//				case NOT_SET:
//					count += YmlUtils.setNotSet( ( sub_section != null ? sub_section : section ), options.key(), value);
//					break;
//				case NORMAL:
//				default:
//					( sub_section != null ? sub_section : section ).set(options.key(), value);
//					count ++;
//					break;
//				}
//			}
//		}
//		return count;
//	}
}
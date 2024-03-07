package es.outlook.adriansrj.core.util.loadable;

import es.outlook.adriansrj.core.util.StringUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.FIELD )
public @interface LoadableCollectionEntry {
	
	/**
	 * Gets the name of the sub-section to load from.
	 * <br>
	 * Note that if a <b>blank</b> string if returned,
	 * the collection will be loaded from the current section.
	 * <br>
	 * @return the name of the sub-section to load from.
	 */
	String subsection ( ) default StringUtil.EMPTY;
}
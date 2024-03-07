package es.outlook.adriansrj.core.util.loadable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang.StringUtils;

@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.FIELD )
public @interface LoadableEntry {
	
	/**
	 * The key/path of this entry.
	 * <p>
	 * @return key/path of this entry.
	 */
	String key ( ) default StringUtils.EMPTY;
	
	/**
	 * Gets the name of the sub-section where this is located.
	 * <p>
	 *
	 * @return the name of the sub-section where this is located.
	 */
	String subsection ( ) default StringUtils.EMPTY;
}

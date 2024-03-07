package es.outlook.adriansrj.core.util.configurable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import es.outlook.adriansrj.core.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import es.outlook.adriansrj.core.util.saveable.SaveActionType;

@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.FIELD )
public @interface ConfigurableEntry {
	
	/**
	 * The key/path of this entry.
	 * <p>
	 * @return key/path of this entry.
	 */
	String key ( ) default StringUtils.EMPTY;
	
	/**
	 * The comment of this entry.
	 * <p/>
	 * @return the comment of this entry, or a blank string if no comment.
	 */
	String comment ( ) default StringUtil.EMPTY;
	
	/**
	 * Defines how this entry should be saved.
	 * <p>
	 * @return how this entry should be saved.
	 */
	SaveActionType saveAction ( ) default SaveActionType.NORMAL;

	/**
	 * Gets the name of the sub-section where this is located.
	 * <p>
	 * @return the name of the sub-section where this is located.
	 */
	String subsection ( ) default StringUtils.EMPTY;
}
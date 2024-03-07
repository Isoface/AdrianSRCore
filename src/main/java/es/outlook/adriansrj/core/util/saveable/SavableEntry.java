package es.outlook.adriansrj.core.util.saveable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import es.outlook.adriansrj.core.util.StringUtil;
import org.apache.commons.lang.StringUtils;

@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.FIELD )
public @interface SavableEntry {
	
	/**
	 * The key/path of this entry.
	 * <p>
	 * @return key/path of this entry.
	 */
	String key ( ) default StringUtils.EMPTY;
	
	/**
	 * The comment of this entry.
	 * <p/>
	 *
	 * @return the comment of this entry, or a blank string if no comment.
	 */
	String comment ( ) default StringUtil.EMPTY;
	
	/**
	 * The save action that determines how this entry is going to be saved.
	 * <p/>
	 *
	 * @return action that determines how this entry is going to be saved.
	 */
	SaveActionType action ( ) default SaveActionType.NORMAL;
	
	/**
	 * Gets the name of the sub-section where this will be saved on.
	 * <p>
	 *
	 * @return the name of the sub-section where this will be saved.
	 */
	String subsection ( ) default StringUtils.EMPTY;
}

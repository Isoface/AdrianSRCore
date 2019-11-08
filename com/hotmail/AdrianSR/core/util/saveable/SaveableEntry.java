package com.hotmail.AdrianSR.core.util.saveable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang.StringUtils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SaveableEntry {

	String key();
	
	SaveActionType action() default SaveActionType.NORMAL;

	/**
	 * Gets the name of the sub-section where this
	 * will be saved on.
	 * <p>
	 * @return the name of the sub-section where this will be saved.
	 */
	String subsection() default StringUtils.EMPTY;
}

package com.hotmail.adriansr.core.util.loadable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang.StringUtils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LoadableEntry {

	String key();
	
	/**
	 * Gets the name of the sub-section where this
	 * is located.
	 * <p>
	 * @return the name of the sub-section where this is located.
	 */
	String subsection() default StringUtils.EMPTY;
}

package es.outlook.adriansrj.core.util.saveable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang.StringUtils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SavableCollectionEntry {

	/**
	 * Gets the name of the sub-section where this
	 * will be saved.
	 * <p>
	 * @return the name of the sub-section where this will be saved.
	 */
	String subsection();
	
	/**
	 * Gets the prefix for the name of sub-section where this will be saved.
	 * <p>
	 * @return the prefix for the name of sub-section where this will be saved.
	 */
	String subsectionprefix() default StringUtils.EMPTY;
}

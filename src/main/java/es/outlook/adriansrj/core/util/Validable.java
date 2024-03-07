package es.outlook.adriansrj.core.util;

/**
 * Simple interface for validating Objects.
 * <p>
 * @author AdrianSR / Sunday 22 March, 2020 / 08:16 AM
 */
public interface Validable {
	
	/**
	 * Gets whether this Object represents a valid instance.
	 * <p>
	 * @return true if valid.
	 */
	boolean isValid ( );
	
	/**
	 * Gets whether this Object represents an invalid instance.
	 * <p>
	 * @return true if invalid.
	 */
	default boolean isInvalid ( ) {
		return !isValid ( );
	}
}
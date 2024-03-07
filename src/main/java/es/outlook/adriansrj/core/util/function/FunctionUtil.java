package es.outlook.adriansrj.core.util.function;

import java.util.function.Predicate;

/**
 * Useful class for dealing with functions.
 * <p>
 * @author AdrianSR / Thursday 16 April, 2020 / 12:05 PM
 */
public class FunctionUtil {
	
	/**
	 * Returns a predicate that represents the logical negation of the provided
	 * predicate.
	 * <p>
	 * @return a predicate that represents the logical negation of the provided
	 *         predicate
	 */
	public static < T > Predicate < T > negate ( Predicate < T > predicate ) {
		return predicate.negate ( );
	}
}
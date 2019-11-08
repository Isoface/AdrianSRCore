package com.hotmail.AdrianSR.core.util.classes;

import java.util.function.Predicate;

public class PredicateUtils {

	/**
	 * Returns a predicate that represents the logical negation of the
     * given predicate.
	 * <p>
	 * @param predicate the predicate to negate.
	 * @return logical negation of the given predicate.
	 */
	public static <T> Predicate<T> negate(Predicate<T> predicate) {
		return predicate.negate();
	}
}

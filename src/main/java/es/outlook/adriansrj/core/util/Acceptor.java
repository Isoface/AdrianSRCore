package es.outlook.adriansrj.core.util;

/**
 * A value tester for accepting/reject values.
 * <p>
 * @author AdrianSR / Sunday 25 April, 2021 / 03:00 PM
 */
public interface Acceptor < T > {

	/**
	 * Tests whether the provided value should be accepted or not.
	 * <p>
	 * @param value the value to test.
	 * @return whether the provided value is accepted or not.
	 */
	boolean accept ( T value );
}

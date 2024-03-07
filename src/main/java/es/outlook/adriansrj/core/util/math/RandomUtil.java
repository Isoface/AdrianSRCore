package es.outlook.adriansrj.core.util.math;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Useful class for dealing with randomness.
 *
 * @author AdrianSR / 05/09/2021 / 09:26 a. m.
 */
public class RandomUtil {
	
	public static final Random RANDOM = new Random ( );
	
	/**
	 * Gets a random non-null element from the provided {@link Collection}.
	 *
	 * @param collection the collection to get from.
	 * @param <T> the type of element.
	 * @return the randomly selected element, or <b>null</b>
	 * if there are no non-null element in the collection.
	 */
	public static < T > T getRandomElement ( Collection < T > collection ) {
		List < T > list = collection.stream ( ).filter ( Objects :: nonNull )
				.collect ( Collectors.toList ( ) );
		
		if ( list.size ( ) > 0 ) {
			T random = null;
			
			do {
				if ( ( random = list.get (
						RandomUtil.nextInt ( list.size ( ) ) ) ) != null ) {
					return random;
				}
			} while ( true );
		}
		return null;
	}
	
	/**
	 * Gets a random non-null element from the provided array.
	 *
	 * @param array the array to get from.
	 * @param <T> the type of element.
	 * @return the randomly selected element, or <b>null</b>
	 * if there are no non-null elements in the array.
	 */
	public static < T > T getRandomElement ( T[] array ) {
		if ( Arrays.stream ( array ).anyMatch ( Objects :: nonNull ) ) {
			T random = null;
			
			do {
				if ( ( random = array[ RandomUtil.nextInt ( array.length ) ] ) != null ) {
					return random;
				}
			} while ( true );
		} else {
			// cannot get random element as
			// there are no non-null elements
			// in the provided array.
			return null;
		}
	}
	
	/**
	 * <p>Returns the next pseudorandom, uniformly distributed int value
	 * from the Math.random() sequence.</p>
	 * <b>N.B. All values are >= 0.<b>
	 * @return the random int
	 */
	public static int nextInt ( ) {
		return nextInt ( RANDOM );
	}
	
	/**
	 * <p>Returns the next pseudorandom, uniformly distributed int value
	 * from the given <code>random</code> sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random int
	 */
	public static int nextInt ( Random random ) {
		return random.nextInt ( );
	}
	
	/**
	 * <p>Returns a pseudorandom, uniformly distributed int value
	 * between <code>0</code> (inclusive) and the specified value
	 * (exclusive), from the Math.random() sequence.</p>
	 *
	 * @param n  the specified exclusive max-value
	 * @return the random int
	 */
	public static int nextInt ( int n ) {
		return nextInt ( RANDOM , n );
	}
	
	/**
	 * <p>Returns a pseudorandom, uniformly distributed int value
	 * between <code>0</code> (inclusive) and the specified value
	 * (exclusive), from the given Random sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @param n  the specified exclusive max-value
	 * @return the random int
	 */
	public static int nextInt ( Random random , int n ) {
		// check this cannot return 'n'
		return random.nextInt ( n );
	}
	
	/**
	 * <p>Returns the next pseudorandom, uniformly distributed long value
	 * from the Math.random() sequence.</p>
	 * <b>N.B. All values are >= 0.<b>
	 * @return the random long
	 */
	public static long nextLong ( ) {
		return nextLong ( RANDOM );
	}
	
	/**
	 * <p>Returns the next pseudorandom, uniformly distributed long value
	 * from the given Random sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random long
	 */
	public static long nextLong ( Random random ) {
		return random.nextLong ( );
	}
	
	/**
	 * <p>Returns the next pseudorandom, uniformly distributed boolean value
	 * from the Math.random() sequence.</p>
	 *
	 * @return the random boolean
	 */
	public static boolean nextBoolean ( ) {
		return nextBoolean ( RANDOM );
	}
	
	/**
	 * <p>Returns the next pseudorandom, uniformly distributed boolean value
	 * from the given random sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random boolean
	 */
	public static boolean nextBoolean ( Random random ) {
		return random.nextBoolean ( );
	}
	
	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the Math.random()
	 * sequence.</p>
	 *
	 * @return the random float
	 */
	public static float nextFloat ( ) {
		return nextFloat ( RANDOM );
	}
	
	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the given Random
	 * sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random float
	 */
	public static float nextFloat ( Random random ) {
		return random.nextFloat ( );
	}
	
	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the Math.random()
	 * sequence.</p>
	 *
	 * @return the random double
	 */
	public static double nextDouble ( ) {
		return nextDouble ( RANDOM );
	}
	
	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the given Random
	 * sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random double
	 */
	public static double nextDouble ( Random random ) {
		return random.nextDouble ( );
	}
}
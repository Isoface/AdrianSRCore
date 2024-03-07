package es.outlook.adriansrj.core.util;

import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;

import java.util.concurrent.TimeUnit;

/**
 * An immutable duration that is capable to return its value on milliseconds,
 * seconds, minutes, hours or days.
 * <p>
 * @author AdrianSR / Sunday 04 July, 2021 / 12:08 PM
 */
public class Duration {
	
	/**
	 * Represents the zero in the {@link Duration}s.
	 */
	public static final Duration ZERO = new Duration ( 0L , TimeUnit.NANOSECONDS );
	
	/**
	 * Represents an infinite duration.
	 */
	public static final Duration INFINITE = new Duration ( );
	
	protected static final String DURATION_FORMAT_SEPARATOR = " ";
	protected static final String DURATION_FORMAT           = "%d" + DURATION_FORMAT_SEPARATOR + "%s";
	
	/**
	 * Returns a new {@link Duration} that has been created using the given time
	 * unit.
	 * <p>
	 * @param unit     the time unit of the duration
	 * @param duration the time
	 * @return {@link Duration} in the given unit.
	 */
	public static Duration of ( TimeUnit unit , long duration ) {
		switch ( unit ) {
			case DAYS:
				return ofDays ( duration );
			
			case HOURS:
				return ofHours ( duration );
			
			case MICROSECONDS:
				return ofMicroseconds ( duration );
			
			case MILLISECONDS:
				return ofMilliseconds ( duration );
			
			case MINUTES:
				return ofMinutes ( duration );
			
			case NANOSECONDS:
				return ofNanos ( duration );
			
			case SECONDS:
				return ofSeconds ( duration );
		}
		return of ( TimeUnit.NANOSECONDS , 0L );
	}
	
	/**
	 * Parses a duration from the provided string.
	 *
	 * @param regex the string to parse.
	 * @return the resulting duration.
	 */
	public static Duration parse ( String regex ) {
		long     duration = 0L;
		TimeUnit unit     = null;
		
		if ( regex.contains ( DURATION_FORMAT_SEPARATOR ) ) {
			String [ ] args = regex.split ( DURATION_FORMAT_SEPARATOR );
			
			if ( args.length > 1 ) {
				String uncast_duration = args [ 0 ];
				String     uncast_unit = args [ 1 ];
				
				try {
					duration = Long.valueOf ( uncast_duration );
					unit     = EnumReflection.getEnumConstant ( TimeUnit.class , uncast_unit );
				} catch ( NumberFormatException ex ) {
					// ignored exception
				}
			}
		}
		
		return new Duration ( duration , unit );
	}
	/**
	 * Returns a new {@link Duration} that has been created using nanoseconds.
	 * <p>
	 * @param nanos the nanoseconds.
	 * @return {@link Duration} in nanoseconds.
	 */
	public static Duration ofNanos ( long nanos ) {
		return new Duration ( nanos , TimeUnit.NANOSECONDS );
	}
	
	/**
	 * Returns a new {@link Duration} that has been created using microseconds.
	 * <p>
	 * @param micros the microseconds.
	 * @return {@link Duration} in microseconds.
	 */
	public static Duration ofMicroseconds ( long micros ) {
		return new Duration ( micros , TimeUnit.MICROSECONDS );
	}
	
	/**
	 * Returns a new {@link Duration} that has been created using milliseconds.
	 * <p>
	 * @param millis the milliseconds.
	 * @return {@link Duration} in milliseconds.
	 */
	public static Duration ofMilliseconds ( long millis ) {
		return new Duration ( millis , TimeUnit.MILLISECONDS );
	}
	
	/**
	 * Returns a new {@link Duration} that has been created using milliseconds.
	 * <p>
	 * @param seconds the time in seconds.
	 * @return {@link Duration} in milliseconds.
	 */
	public static Duration ofSeconds ( long seconds ) {
		return new Duration ( seconds , TimeUnit.SECONDS );
	}
	
	/**
	 * Returns a new {@link Duration} that has been created using minutes.
	 * <p>
	 * @param minutes the minutes.
	 * @return {@link Duration} in minutes.
	 */
	public static Duration ofMinutes ( long minutes ) {
		return new Duration ( minutes , TimeUnit.MINUTES );
	}
	
	/**
	 * Returns a new {@link Duration} that has been created using hours.
	 * <p>
	 * @param hours the hours.
	 * @return {@link Duration} in hours.
	 */
	public static Duration ofHours ( long hours ) {
		return new Duration ( hours , TimeUnit.HOURS );
	}
	
	/**
	 * Returns a new {@link Duration} that has been created using days.
	 * <p>
	 * @param days the days.
	 * @return {@link Duration} in days.
	 */
	public static Duration ofDays ( long days ) {
		return new Duration ( days , TimeUnit.DAYS );
	}
	
	protected long     duration;
	protected TimeUnit unit;
	protected boolean  infinite;
	
	/**
	 * Construct duration.
	 * <p>
	 * @param duration the duration.
	 * @param unit     the time unit.
	 */
	public Duration ( long duration , TimeUnit unit ) {
		if ( duration <= 0L || unit == null ) {
			this.duration = 0L;
			this.unit     = TimeUnit.NANOSECONDS;
		} else {
			this.duration = duration;
			this.unit     = unit;
		}
		
		this.infinite = false;
	}
	
	/**
	 * Constructs an inifinite duration.
	 */
	protected Duration ( ) {
		this.duration = 0;
		this.unit     = TimeUnit.DAYS;
		this.infinite = true;
	}
	
	/**
	 * Construct duration from milliseconds.
	 * <p>
	 * @param millis the duration in milliseconds.
	 */
	public Duration ( long millis ) {
		this ( millis , TimeUnit.MILLISECONDS );
	}
	
	/**
	 * Returns the original duration.
	 */
	public long getDuration ( ) {
		return duration;
	}
	
	/**
	 * Returns the time unit.
	 */
	public TimeUnit getUnit ( ) {
		return unit;
	}
	
	/**
	 * Returns the duration converted to nanoseconds.
	 */
	public long toNanos ( ) {
		return isInfinite ( ) ? Long.MAX_VALUE : unit.toNanos ( duration );
	}
	
	/**
	 * Returns the duration converted to microseconds.
	 */
	public long toMicros ( ) {
		return isInfinite ( ) ? Long.MAX_VALUE : unit.toMicros ( duration );
	}
	
	/**
	 * Returns the duration converted to milliseconds.
	 */
	public long toMillis ( ) {
		return isInfinite ( ) ? Long.MAX_VALUE : unit.toMillis ( duration );
	}
	
	/**
	 * Returns the duration converted to seconds.
	 */
	public long toSeconds ( ) {
		return isInfinite ( ) ? Long.MAX_VALUE : unit.toSeconds ( duration );
	}
	
	/**
	 * Returns the duration converted to minutes.
	 */
	public long toMinutes ( ) {
		return isInfinite ( ) ? Long.MAX_VALUE : unit.toMinutes ( duration );
	}
	
	/**
	 * Returns the duration converted to hours.
	 */
	public long toHours ( ) {
		return isInfinite ( ) ? Long.MAX_VALUE : unit.toHours ( duration );
	}
	
	/**
	 * Returns the duration converted to days.
	 */
	public long toDays ( ) {
		return isInfinite ( ) ? Long.MAX_VALUE : unit.toDays ( duration );
	}
	
	/**
	 * Returns true if this is zero.
	 */
	public boolean isZero ( ) {
		return ZERO.equals ( this );
	}
	
	/**
	 * Gets whether this duration is infinite or not.
	 * <p>
	 * @return whether this duration is infinite or not.
	 */
	public boolean isInfinite ( ) {
		return infinite;
	}
	
	@Override
	public int hashCode ( ) {
		final int prime  = 31;
		int       result = 1;
		result = prime * result + ( int ) ( duration ^ ( duration >>> 32 ) );
		result = prime * result + ( infinite ? 1231 : 1237 );
		result = prime * result + ( ( unit == null ) ? 0 : unit.hashCode ( ) );
		return result;
	}
	
	@Override
	public boolean equals ( Object obj ) {
		if ( obj == this ) {
			return true;
		} else {
			if ( obj instanceof Duration ) {
				Duration other = ( Duration ) obj;
				
				if ( other.infinite ) {
					return this.infinite;
				} else {
					return other.duration == this.duration && other.unit == this.unit;
				}
			}
			return false;
		}
	}
}
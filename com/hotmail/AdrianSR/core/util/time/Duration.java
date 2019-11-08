package com.hotmail.AdrianSR.core.util.time;

import java.util.concurrent.TimeUnit;

/**
 * A duration that can be converted to milliseconds, 
 * seconds, minutes, hours or days.
 * <p>
 * @author AdrianSR
 */
public class Duration {
	
	/**
	 * Represents the zero in the {@link Duration}s.
	 */
	public static final Duration ZERO = new Duration(0L, TimeUnit.NANOSECONDS);
	
	/**
	 * Returns a new {@link Duration}
	 * that has been created using the given
	 * time unit.
	 * <p>
	 * @param unit the time unit of the duration
	 * @param duration the time
	 * @return {@link Duration} in the given unit.
	 */
	public static Duration of(TimeUnit unit, long duration) {
		switch(unit) {
		case DAYS:
			return ofDays(duration);
		case HOURS:
			return ofHours(duration);
		case MICROSECONDS:
			return ofMicroseconds(duration);
		case MILLISECONDS:
			return ofMilliseconds(duration);
		case MINUTES:
			return ofMinutes(duration);
		case NANOSECONDS:
			return ofNanos(duration);
		case SECONDS:
			return ofSeconds(duration);
		}
		return of(TimeUnit.NANOSECONDS, 0L);
	}

	/**
	 * Returns a new {@link Duration}
	 * that has been created using nanoseconds.
	 * <p>
	 * @param nanos the nanoseconds.
	 * @return {@link Duration} in nanoseconds.
	 */
	public static Duration ofNanos(long nanos) {
		return new Duration(nanos, TimeUnit.NANOSECONDS);
	}

	/**
	 * Returns a new {@link Duration}
	 * that has been created using microseconds.
	 * <p>
	 * @param micros the microseconds.
	 * @return {@link Duration} in microseconds.
	 */
	public static Duration ofMicroseconds(long micros) {
		return new Duration(micros, TimeUnit.MICROSECONDS);
	}

	/**
	 * Returns a new {@link Duration}
	 * that has been created using milliseconds.
	 * <p>
	 * @param millis the milliseconds.
	 * @return {@link Duration} in milliseconds.
	 */
	public static Duration ofMilliseconds(long millis) {
		return new Duration(millis, TimeUnit.MILLISECONDS);
	}

	/**
	 * Returns a new {@link Duration}
	 * that has been created using milliseconds.
	 * <p>
	 * @param millis the milliseconds.
	 * @return {@link Duration} in milliseconds.
	 */
	public static Duration ofSeconds(long seconds) {
		return new Duration(seconds, TimeUnit.SECONDS);
	}

	/**
	 * Returns a new {@link Duration}
	 * that has been created using minutes.
	 * <p>
	 * @param minutes the minutes.
	 * @return {@link Duration} in minutes.
	 */
	public static Duration ofMinutes(long minutes) {
		return new Duration(minutes, TimeUnit.MINUTES);
	}

	/**
	 * Returns a new {@link Duration}
	 * that has been created using hours.
	 * <p>
	 * @param hours the hours.
	 * @return {@link Duration} in hours.
	 */
	public static Duration ofHours(long hours) {
		return new Duration(hours, TimeUnit.HOURS);
	}

	/**
	 * Returns a new {@link Duration}
	 * that has been created using days.
	 * <p>
	 * @param days the days.
	 * @return {@link Duration} in days.
	 */
	public static Duration ofDays(long days) {
		return new Duration(days, TimeUnit.DAYS);
	}

	protected long duration;
	protected TimeUnit unit;
	
	/**
	 * Construct duration.
	 * <p>
	 * @param duration the duration.
	 * @param unit the time unit.
	 */
	public Duration(long duration, TimeUnit unit) {
		if (duration <= 0L || unit == null) {
			this.duration = 0L;
			this.unit     = TimeUnit.NANOSECONDS;
		} else {
			this.duration = duration;
			this.unit     = unit;
		}
	}

	/**
	 * Construct duration from milliseconds.
	 * <p>
	 * @param millis the duration in milliseconds.
	 */
	public Duration(long millis) {
		this(millis, TimeUnit.MILLISECONDS);
	}

	/**
	 * Returns the original duration.
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Returns the time unit.
	 */
	public TimeUnit getUnit() {
		return unit;
	}

	/**
	 * Returns the duration
	 * converted to nanoseconds.
	 */
	public long toNanos() {
		return unit.toNanos(duration);
	}

	/**
	 * Returns the duration
	 * converted to microseconds.
	 */
	public long toMicros() {
		return unit.toMicros(duration);
	}

	/**
	 * Returns the duration
	 * converted to milliseconds.
	 */
	public long toMillis() {
		return unit.toMillis(duration);
	}

	/**
	 * Returns the duration
	 * converted to seconds.
	 */
	public long toSeconds() {
		return unit.toSeconds(duration);
	}

	/**
	 * Returns the duration
	 * converted to minutes.
	 */
	public long toMinutes() {
		return unit.toMinutes(duration);
	}

	/**
	 * Returns the duration
	 * converted to hours.
	 */
	public long toHours() {
		return unit.toHours(duration);
	}

	/**
	 * Returns the duration
	 * converted to days.
	 */
	public long toDays() {
		return unit.toDays(duration);
	}
	
	/**
	 * Returns true if this is zero.
	 */
	public boolean isZero() {
		return ZERO.equals(this);
	}
}
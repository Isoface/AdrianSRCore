package com.hotmail.AdrianSR.core.util.time;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

	/**
	 * Get a {@link TimeUnit} from name.
	 * @param input the name.
	 * @return a TimeUnit.
	 */
	public static TimeUnit getUnitFromName(String input) {
		switch (input.toLowerCase()) {
			case "error":
	
			case "s":
			case "sec":
			case "secs":
			case "second":
			case "seconds":
				return TimeUnit.SECONDS;
	
			case "m":
			case "min":
			case "mins":
			case "minute":
			case "minutes":
				return TimeUnit.MINUTES;
	
			case "h":
			case "hr":
			case "hrs":
			case "hour":
			case "hours":
				return TimeUnit.HOURS;
	
			case "d":
			case "day":
			case "days":
				return TimeUnit.DAYS;
			default:
				return null;
		}
	}
}

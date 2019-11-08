package com.hotmail.AdrianSR.core.util;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

	/**
	 * Get a {@link TimeUnit} from name.
	 * @param input the name.
	 * @return a TimeUnit.
	 */
	public static TimeUnit getUnitFromName(String input) {
		TimeUnit u;
		switch (input.toLowerCase()) {
		case "error":
		default:
			return null;

		case "s":
		case "sec":
		case "secs":
		case "second":
		case "seconds":
			u = TimeUnit.SECONDS;
			break;

		case "m":
		case "min":
		case "mins":
		case "minute":
		case "minutes":
			u = TimeUnit.MINUTES;
			break;

		case "h":
		case "hr":
		case "hrs":
		case "hour":
		case "hours":
			u = TimeUnit.HOURS;
			break;

		case "d":
		case "day":
		case "days":
			u = TimeUnit.DAYS;
			break;
		}
		return u;
	}
}

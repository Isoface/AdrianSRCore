package com.hotmail.AdrianSR.core.util.localization;

import java.util.Arrays;

public enum DegreesDirection {
	
	/**
	 * The representation of any degree between south and south-west.
	 */
	S(0F, 45F),
	
	/**
	 * The representation of any degree between south-west and west.
	 */
	SW(45F, 90F),
	
	/**
	 * The representation of any degree between west and north-west.
	 */
	W(90F, 135F),
	
	/**
	 * The representation of any degree between north-west and north.
	 */
	NW(135F, 180F),
	
	/**
	 * The representation of any degree between north and north-east.
	 */
	N(180F, 225F),
	
	/**
	 * The representation of any degree between north-east and east.
	 */
	NE(225F, 270F),
	
	/**
	 * The representation of any degree between east and south-east.
	 */
	E(270F, 315F),
	
	/**
	 * The representation of any degree between south-east and south.
	 */
	SE(315F, 359.5F);
	
	private static final float MAX_DEGREES = 359.5F;
	private static final float MIN_DEGREES =     0F;
	
	public static DegreesDirection of(float degrees) {
		return Arrays.stream(DegreesDirection.values())
				.filter(sfdr -> sfdr.isBetween(degrees))
				.findAny().orElse(S);
	}
	
	private final float min;
	private final float max;
	
	DegreesDirection(float min, float max) {
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Returns true if the given degrees is between this {@link DegreesDirection}.
	 * <p>
	 * @param degrees the degrees.
	 * @return true if the given degrees is between this {@link DegreesDirection}.
	 */
	public boolean isBetween(float degrees) {
		if (degrees > MAX_DEGREES) { // check limits
			degrees = MAX_DEGREES;
		} else if (degrees < MIN_DEGREES) {
			degrees = MIN_DEGREES;
		}
		return ( degrees >= min && degrees <= max );
	}
}
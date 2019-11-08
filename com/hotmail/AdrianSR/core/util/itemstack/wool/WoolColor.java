package com.hotmail.AdrianSR.core.util.itemstack.wool;

/**
 * The wool colors.
 */
public enum WoolColor {
	
	/**
     * Represents white
     */
    WHITE(0),
    
    ORANGE(1),
    
    MAGENTA(2),
    
    /**
     * Represents light blue.
     */
    LIGHT_BLUE(3),
    
    /**
     * Represents yellow
     */
    YELLOW(4),
    
    /**
     * Represents lime.
     */
    LIME(5),
    
    PINK(6),
    
    /**
     * Represents gray.
     */
    GRAY(7),
    
    /**
     * Represents light gray.
     */
    LIGHT_GRAY(8),
    
    CYAN(9),
    
    PURPLE(10),
    
    /**
     * Represents blue.
     */
    BLUE(11),
    
    BROWN(12),
    
    /**
     * Represents green.
     */
    GREEN(13),
    
    /**
     * Represents red
     */
    RED(14),
	
    /**
     * Represents black.
     */
    BLACK(15),
    ;
    
	private final short value;
	
	WoolColor(final int value) {
		this.value = (short) value;
	}
	
	public short getShortValue() {
		return value;
	}
	
	public WoolItemStack toItemStack() {
		return toItemStack(1);
	}
	
	public WoolItemStack toItemStack(int amount) {
		return new WoolItemStack(this, amount);
	}
	
	public static WoolColor getFromShort(final short value) {
		for (WoolColor color : values()) {
			if (color.getShortValue() == value) {
				return color;
			}
		}
		return null;
	}
}

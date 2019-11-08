package com.hotmail.AdrianSR.core.util.itemstack.stainedglass;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The glass colors.
 */
public enum StainedGlassColor {
	
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
	
	StainedGlassColor(final int value) {
		this.value = (short) value;
	}
	
	public short getShortValue() {
		return value;
	}
	
	public ItemStack getColoredGlass() {
		return new ItemStack(Material.valueOf("STAINED_GLASS"), 1, getShortValue());
	}
	
	public ItemStack getColoredPaneGlass() {
		return new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, getShortValue());
	}
	
	public static StainedGlassColor getFromShort(final short value) {
		for (StainedGlassColor color : values()) {
			if (color.getShortValue() == value) {
				return color;
			}
		}
		return null;
	}
}

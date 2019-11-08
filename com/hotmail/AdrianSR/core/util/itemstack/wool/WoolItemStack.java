package com.hotmail.AdrianSR.core.util.itemstack.wool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.Utility;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.hotmail.AdrianSR.core.util.UniversalMaterial;
import com.hotmail.AdrianSR.core.util.itemstack.ItemStackUtils;
import com.hotmail.AdrianSR.core.util.itemstack.custom.CustomItemStack;

/**
 * Represents the item stacks whose type
 * is wool, and allows the developers to change
 * its color easily.
 * <p>
 * @author AdrianSR
 */
@SuppressWarnings("deprecation") public final class WoolItemStack extends CustomItemStack {
	
	public static final WoolColor         DEFAULT_COLOR = WoolColor.WHITE;
	public static final Material   WOOL_ITEM_STACK_TYPE = Material.valueOf("WOOL"); // UniversalMaterial.WHITE_WOOL.getMaterial();
	public static final boolean WOOL_COLOR_SET_BY_SHORT = ( Material.matchMaterial("WHITE_WOOL") == null );
	
    @Utility
    public WoolItemStack() {
    	this(DEFAULT_COLOR);
    }
    
    /**
     * A wool item stack with the specific color.
     * <p>
     * @param color the color that this wool item stack will have.
     */
	public WoolItemStack(WoolColor color) {
		this(color, 1);
	}

    /**
     * A wool item stack with the specific color.
     * <p>
     * @param color the color that this wool item stack will have.
     * @param amount stack size.
     */
    public WoolItemStack(WoolColor color, int amount) {
    	Validate.notNull(color, "Color cannot be null");
    	
    	/* initialize */
    	super.setType(WOOL_ITEM_STACK_TYPE);
    	this.setAmount(amount);
    	this.setColor(color);
    }
    
    /**
     * Creates a new wool item stack derived from the specified stack.
     * <p>
     * @param stack the stack to copy
     * @throws IllegalArgumentException if the specified stack is null or
     *     returns an item meta not created by the item factory
     */
    public WoolItemStack(final WoolItemStack stack) throws IllegalArgumentException {
        Validate.notNull(stack, "Cannot copy null stack");
        this.setAmount(stack.getAmount());
        if (ItemStackUtils.DURABILITY_FIELD_AVAILABLE) {
        	super.setDurability(stack.getDurability());
        }
        
        super.setData(stack.getData());
        if (stack.hasItemMeta()) {
            setItemMeta0(stack.getItemMeta(), WOOL_ITEM_STACK_TYPE);
        }
    }
    
    @Override
    public void setType(Material material) {
    	/* ignore */
    }
    
    @Override
    public void setData(MaterialData data) {
    	/* ignore */
    }
    
    @Override
    public void setDurability(short durability) {
    	/* ignore */
    }
    
    /**
     * Sets the color of this wool item stack.
     * <p>
     * @param color the new color.
     */
    public void setColor(WoolColor color) {
		Validate.notNull(color, "Color cannot be null");
		if (WOOL_COLOR_SET_BY_SHORT) { /* setting color by changing the durability */
			rawSetDurability(color.getShortValue());
		} else { /* setting color by changing the item stack type */
			super.setType(UniversalMaterial.valueOf( (color.name() + "_WOOL") ).getMaterial());
		}
	}
    
    /**
     * The durability of the wool item stack 
     * cannot be changed manually.
     */
	private void rawSetDurability(short durability) {
		if (!ItemStackUtils.DURABILITY_FIELD_AVAILABLE && durability == 0) {
			return;
		}
		super.setDurability(durability);
	}
	
	/**
	 * Cannot be overridden, so it's safe for constructor call
	 */
	private void setItemMeta0(ItemMeta itemMeta, Material material) {
		try {
			Method method = this.getClass().getDeclaredMethod("setItemMeta0", ItemMeta.class, Material.class);
			method.setAccessible(true); method.invoke(this, itemMeta, material);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
//	private void createData(final byte data) {
//	try {
//		Method method = this.getClass().getDeclaredMethod("createData", byte.class);
//		method.setAccessible(true); method.invoke(this, data);
//	} catch (NoSuchMethodException | SecurityException | IllegalAccessException 
//			| IllegalArgumentException | InvocationTargetException e) {
//		e.printStackTrace();
//	}
//}
}
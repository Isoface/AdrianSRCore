package es.outlook.adriansrj.core.util.itemstack.wool;

import es.outlook.adriansrj.core.util.itemstack.ItemStackUtil;
import es.outlook.adriansrj.core.util.itemstack.custom.CustomItemStack;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import es.outlook.adriansrj.core.util.server.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.Utility;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Represents the item stacks whose type
 * is wool, and allows the developers to change
 * its color easily.
 * <p>
 * @author AdrianSR
 */
@SuppressWarnings ( "deprecation" ) public final class WoolItemStack extends CustomItemStack {
	
	public static final WoolColor DEFAULT_COLOR = WoolColor.WHITE;
	
	private WoolColor color;
	
	@Utility
	public WoolItemStack ( ) {
		this ( DEFAULT_COLOR );
	}
	
	/**
	 * A wool item stack with the specific color.
	 * <p>
	 * @param color the color that this wool item stack will have.
	 */
	public WoolItemStack ( WoolColor color ) {
		this ( color , 1 );
	}
	
	/**
	 * A wool item stack with the specific color.
	 * <p>
	 * @param color the color that this wool item stack will have.
	 * @param amount stack size.
	 */
	public WoolItemStack ( WoolColor color , int amount ) {
		Validate.notNull ( color , "color cannot be null" );
		
		/* initialize */
		this.setAmount ( amount );
		this.setColor ( color );
	}
	
	/**
	 * Creates a new wool item stack derived from the specified stack.
	 * <p>
	 * @param stack the stack to copy
	 * @throws IllegalArgumentException if the specified stack is null or
	 *     returns an item meta not created by the item factory
	 */
	public WoolItemStack ( final WoolItemStack stack ) throws IllegalArgumentException {
		Validate.notNull ( stack , "cannot copy null stack" );
		
		this.setAmount ( stack.getAmount ( ) );
		
		if ( ItemStackUtil.AVAILABLE_DURABILITY_FIELD ) {
			super.setDurability ( stack.getDurability ( ) );
		}
		
		super.setData ( stack.getData ( ) );
		
		// finding out color
		WoolColor color = DEFAULT_COLOR;
		
		for ( WoolColor other : WoolColor.values ( ) ) {
			if ( Version.getServerVersion ( ).isOlder ( Version.v1_13_R1 )
					? other.getShortValue ( ) == super.getDurability ( )
					: stack.getType ( ) == getMaterialFromColor ( other ) ) {
				color = other;
				break;
			}
		}
		
		// item meta
		if ( stack.hasItemMeta ( ) ) {
			setItemMeta0 ( stack.getItemMeta ( ) , getMaterialFromColor ( color ) );
		}
		
		setColor ( color );
	}
	
	public WoolColor getColor ( ) {
		return color;
	}
	
	@Override
	public void setType ( Material material ) {
		/* ignore */
	}
	
	@Override
	public void setData ( MaterialData data ) {
		/* ignore */
	}
	
	@Override
	public void setDurability ( short durability ) {
		/* ignore */
	}
	
	/**
	 * Sets the color of this wool item stack.
	 * <p>
	 * @param color the new color.
	 */
	public void setColor ( WoolColor color ) {
		Validate.notNull ( color , "Color cannot be null" );
		
		if ( Version.getServerVersion ( ).isOlder ( Version.v1_13_R1 ) ) {
			// WHITE_WOOL will return WOOL in legacy versions
			super.setType ( UniversalMaterial.WHITE_WOOL.getMaterial ( ) );
			// we are setting color by changing the durability.
			rawSetDurability ( color.getShortValue ( ) );
		} else {
			// we are setting the color by changing the type.
			super.setType ( getMaterialFromColor ( color ) );
		}
		
		this.color = color;
	}
	
	private Material getMaterialFromColor ( WoolColor color ) {
		Material wool_material = EnumReflection.getEnumConstant (
				Material.class , color.name ( ) + "_WOOL" );
		
		// WHITE_WOOL will return WOOL in legacy versions
		return wool_material != null ? wool_material : UniversalMaterial.WHITE_WOOL.getMaterial ( );
	}
	
	/**
	 * The durability of the wool item stack
	 * cannot be changed manually.
	 */
	private void rawSetDurability ( short durability ) {
		if ( ItemStackUtil.AVAILABLE_DURABILITY_FIELD ) {
			super.setDurability ( durability );
		}
	}
	
	/**
	 * Cannot be overridden, so it's safe for constructor call
	 */
	private void setItemMeta0 ( ItemMeta itemMeta , Material material ) {
		try {
			Method method = this.getClass ( ).getDeclaredMethod (
					"setItemMeta0" , ItemMeta.class , Material.class );
			
			method.setAccessible ( true );
			method.invoke ( this , itemMeta , material );
		} catch ( NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e ) {
			e.printStackTrace ( );
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
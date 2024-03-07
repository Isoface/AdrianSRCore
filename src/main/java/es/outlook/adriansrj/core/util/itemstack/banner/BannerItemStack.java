package es.outlook.adriansrj.core.util.itemstack.banner;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import es.outlook.adriansrj.core.util.itemstack.custom.CustomItemStack;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import es.outlook.adriansrj.core.util.server.Version;

/**
 * Colorable banner item stack.
 * <p>
 * @author AdrianSR / Wednesday 16 June, 2021 / 10:45 PM
 */
@SuppressWarnings ( "deprecation" )
public final class BannerItemStack extends CustomItemStack implements Cloneable {
	
	private static final String COLORED_BANNER_MATERIAL_NAME_FORMAT = "%s_BANNER";
	
	/**
	 * Constructs a banner item stack with the default color: {@link BannerColor#WHITE}.
	 */
	public BannerItemStack ( ) {
		this ( BannerColor.WHITE );
	}
	
	/**
	 * Constructs a banner item stack with the specified color.
	 * <p>
	 * @param color the color of this banner.
	 */
	public BannerItemStack ( BannerColor color ) {
		this ( color , 1 );
	}
	
	/**
	 * Constructs a banner item stack with the specified color and amount.
	 * <p>
	 * @param color the color of this banner.
	 * @param amount the amount of this item.
	 */
	public BannerItemStack ( BannerColor color , int amount ) {
		if ( Version.getServerVersion ( ).isOlder ( Version.v1_13_R1 ) ) {
			super.setType ( EnumReflection.getEnumConstant ( Material.class , "BANNER" ) );
		}
		
		setColor ( color );
		setAmount ( amount );
	}
	
	/**
	 * Sets the color of this banner.
	 * <p>
	 * @param color the new color of this banner.
	 */
	public void setColor ( BannerColor color ) {
		Material wool_material = EnumReflection.getEnumConstant ( Material.class ,
				String.format ( COLORED_BANNER_MATERIAL_NAME_FORMAT , color.name ( ) ) );
		
		// 1.13 +
		if ( wool_material != null ) {
			// color is determined by type (1.13 +)
			super.setType ( wool_material );
		}
		// 1.8 - 1.12
		else {
			// color is determined by durability (1.8 - 1.12)
			super.setDurability ( color.getShortValue ( ) );
		}
	}
	
	@Override
	@Deprecated
	public void setType ( Material type ) {}

	@Override
	@Deprecated
	public void setData ( MaterialData data ) {}

	@Override
	@Deprecated
	public void setDurability ( short durability ) {}
}
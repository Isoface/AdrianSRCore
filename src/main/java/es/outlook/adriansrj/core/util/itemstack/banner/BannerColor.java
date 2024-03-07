package es.outlook.adriansrj.core.util.itemstack.banner;

/**
 * An enumeration for the possible colors of a banner.
 * <p>
 *
 * @author AdrianSR / Wednesday 16 June, 2021 / 11:07 PM
 */
public enum BannerColor {
	
	WHITE ( 15 ),
	ORANGE ( 14 ),
	MAGENTA ( 13 ),
	LIGHT_BLUE ( 12 ),
	YELLOW ( 11 ),
	LIME ( 10 ),
	PINK ( 9 ),
	GRAY ( 8 ),
	LIGHT_GRAY ( 7 ),
	CYAN ( 6 ),
	PURPLE ( 5 ),
	BLUE ( 4 ),
	BROWN ( 3 ),
	GREEN ( 2 ),
	RED ( 1 ),
	BLACK ( 0 ),
	
	;
	
	private final short value;
	
	BannerColor ( final int value ) {
		this.value = ( short ) value;
	}
	
	public short getShortValue ( ) {
		return value;
	}
	
	public BannerItemStack toItemStack ( ) {
		return toItemStack ( 1 );
	}
	
	public BannerItemStack toItemStack ( int amount ) {
		return new BannerItemStack ( this , amount );
	}
	
	public static BannerColor getFromShort ( final short value ) {
		for ( BannerColor color : values ( ) ) {
			if ( color.getShortValue ( ) == value ) {
				return color;
			}
		}
		return null;
	}
}
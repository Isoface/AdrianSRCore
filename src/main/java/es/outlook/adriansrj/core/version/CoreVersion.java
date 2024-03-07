package es.outlook.adriansrj.core.version;

import es.outlook.adriansrj.core.main.AdrianSRCore;

/**
 * An enumeration for most core versions, that implements some useful methods
 * for comparing versions.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 02:07 PM
 */
public enum CoreVersion {
	
	v1_2_0 ( 120 ),
	v1_2_1 ( 121 ),
	v1_2_2 ( 122 ),
	v1_2_3 ( 123 ),
	v1_2_4 ( 124 ),
	v1_2_5 ( 125 ),
	v1_2_6 ( 126 ),
	v1_2_7 ( 127 ),
	v1_2_8 ( 128 ),
	v1_2_9 ( 129 ),
	
	v2_0_0 ( 200 ),
	v2_0_1 ( 201 ),
	v2_0_2 ( 202 ) ,
	v2_0_3 ( 203 ) ,
	v2_0_4 ( 204 ) ,
	;
	
	private final int id;
	
	CoreVersion ( int id ) {
		this.id = id;
	}
	
	/**
	 * Gets the version of the current running core.
	 * <p>
	 * @return the version of this core.
	 */
	public static CoreVersion getCoreVersion ( ) {
		return valueOf ( format ( AdrianSRCore.getInstance ( ).getDescription ( ).getVersion ( ) ) );
	}
	
	private static String format ( String suppose_version ) {
		return ( "v" + suppose_version.trim ( ).toLowerCase ( ).replace ( "." , "_" )
				.replace ( "v" , "" ) );
	}
	
	/**
	 * Gets the version's id.
	 * <p>
	 * @return version's id.
	 */
	public int getID ( ) {
		return id;
	}
	
	/**
	 * Checks whether this version is older than the provided version.
	 * <p>
	 * @param other the other version.
	 * @return true if older.
	 */
	public boolean isOlder ( CoreVersion other ) {
		return getID ( ) < other.getID ( );
	}
	
	/**
	 * Checks whether this version is older than or equals to the provided version.
	 * <p>
	 * @param other the other version.
	 * @return true if older or equals.
	 */
	public boolean isOlderEquals ( CoreVersion other ) {
		return getID ( ) <= other.getID ( );
	}
	
	/**
	 * Checks whether this version is newer than the provided version.
	 * <p>
	 * @param other the other version.
	 * @return true if newer.
	 */
	public boolean isNewer ( CoreVersion other ) {
		return getID ( ) > other.getID ( );
	}
	
	/**
	 * Checks whether this version is newer than or equals to the provided version.
	 * <p>
	 * @param other the other version.
	 * @return true if newer or equals.
	 */
	public boolean isNewerEquals ( CoreVersion other ) {
		return getID ( ) >= other.getID ( );
	}
}
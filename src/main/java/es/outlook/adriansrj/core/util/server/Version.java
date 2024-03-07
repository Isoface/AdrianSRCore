package es.outlook.adriansrj.core.util.server;

import org.bukkit.Bukkit;

/**
 * An enumeration for most server versions, that implements some useful methods
 * for comparing versions.
 * <p>
 * @author AdrianSR / Sunday 12 April, 2020 / 05:19 PM
 */
public enum Version {

	/* legacy versions */
	v1_8_R1 ( 181 ),
	v1_8_R2 ( 182 ),
	v1_8_R3 ( 183 ),
	v1_9_R1 ( 191 ),
	v1_9_R2 ( 192 ),
	v1_10_R1 ( 1101 ),
	v1_11_R1 ( 1111 ),
	v1_12_R1 ( 1121 ),

	/* latest versions */
	v1_13_R1 ( 1131 ),
	v1_13_R2 ( 1132 ),
	v1_14_R1 ( 1141 ),
	v1_15_R1 ( 1151 ),
	v1_16_R1 ( 1161 ),
	v1_16_R2 ( 1162 ),
	v1_16_R3 ( 1163 ),
	v1_17_R1 ( 1171 ),
	v1_18_R1 ( 1181 ),

	;

	private final int id;

	Version ( int id ) {
		this.id = id;
	}

	/**
	 * Gets the version of the current running server.
	 * <p>
	 * Note that server versions older than {@link Version#v1_8_R1} are NOT
	 * supported.
	 * <p>
	 * @return the version of this server.
	 */
	public static Version getServerVersion ( ) {
		final String packaje = Bukkit.getServer ( ).getClass ( ).getPackage ( ).getName ( );
		final String version = packaje.substring ( packaje.lastIndexOf ( "." ) + 1 );

		return valueOf ( version );
	}

	/**
	 * Gets the version's id.
	 * <p>
	 * @return version's id.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Checks whether this version is older than the provided version.
	 * <p>
	 * @param other the other version.
	 * @return true if older.
	 */
	public boolean isOlder ( Version other ) {
		return getID ( ) < other.getID ( );
	}

	/**
	 * Checks whether this version is older than or equals to the provided version.
	 * <p>
	 * @param other the other version.
	 * @return true if older or equals.
	 */
	public boolean isOlderEquals ( Version other ) {
		return getID() <= other.getID();
	}

	/**
	 * Checks whether this version is newer than the provided version.
	 * <p>
	 * @param other the other version.
	 * @return true if newer.
	 */
	public boolean isNewer ( Version other ) {
		return getID() > other.getID();
	}

	/**
	 * Checks whether this version is newer than or equals to the provided version.
	 * <p>
	 * @param other the other version.
	 * @return true if newer or equals.
	 */
	public boolean isNewerEquals ( Version other ) {
		return getID() >= other.getID();
	}

	/**
	 * Checks whether this has the same version as the provided version.
	 * <p>
	 * <pre><code>
	 * Version.1_8_R1.equalsVersion ( 1_8_R3 ) = true
	 * Version.1_9_R1.equalsVersion ( 1_8_R1 ) = false
	 * </pre></code>
	 * <p>
	 * @param other the other version.
	 * @return true if has the same version.
	 */
	public boolean equalsVersion ( Version other ) {
		final String s0 = name().substring       ( 0 , name().indexOf ( "_R" ) );
		final String s1 = other.name().substring ( 0 , other.name().indexOf ( "_R" ) );

		return s0.equals ( s1 );
	}

	/**
	 * Checks whether this has the same revision as the provided version.
	 * <p>
	 * <pre><code>
	 * Version.1_8_R3.equalsRevision ( 1_9_R3 ) = true
	 * Version.1_8_R1.equalsRevision ( 1_8_R3 ) = false
	 * </pre></code>
	 * <p>
	 * @param other the other version.
	 * @return true if has the same revision.
	 */
	public boolean equalsRevision ( Version other ) {
		final String s0 = name().substring       ( name().indexOf ( "R" ) + 1 );
		final String s1 = other.name().substring ( other.name().indexOf ( "R" ) + 1 );

		return s0.equals ( s1 );
	}
}
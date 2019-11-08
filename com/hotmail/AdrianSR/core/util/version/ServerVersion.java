package com.hotmail.AdrianSR.core.util.version;

import org.bukkit.Bukkit;

/**
 * Represents the verius 
 * server versions.
 * <p>
 * @author AdrianSR
 */
public enum ServerVersion {

	/**
	 * 1.7 versions.
	 */
	v1_7_R1(1_07_01),
	v1_7_R2(1_07_02), 
	v1_7_R3(1_07_03), 
	v1_7_R4(1_07_04), 
	
	/**
	 * 1.8 versions.
	 */
	v1_8_R1(1_08_01),
	v1_8_R2(1_08_02),
	v1_8_R3(1_08_03),
	
	/**
	 * 1.9 versions.
	 */
	v1_9_R1(1_09_01),
	v1_9_R2(1_09_02),
	
	/**
	 * latest versions.
	 */
	v1_10_R1(1_10_01),
	v1_11_R1(1_11_01),
	v1_12_R1(1_12_01),
	v1_13_R1(1_13_01),
	v1_13_R2(1_13_02),
	v1_14_R1(1_14_01);
	
	/**
	 * Version id.
	 */
	private int id;
	
	/**
	 * Construct new server version.
	 * <p>
	 * @param id version id.
	 */
	ServerVersion(int id) {
		this.id = id;
	}
	
	/**
	 * Get server version id.
	 * <p>
	 * @return version id.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Check this version is 
	 * newer than other.
	 * <p>
	 * @param version the version to compare.
	 * @return true if is newer.
	 */
	public boolean isNewerThan(ServerVersion version) {
		return version != null && getID() > version.getID();
	}
	
	/**
	 * Check this version is 
	 * newer or equals than other.
	 * <p>
	 * @param version the version to compare.
	 * @return true if is newer or equals.
	 */
	public boolean isNewerEqualsThan(ServerVersion version) {
		return version != null && getID() >= version.getID();
	}
	
	/**
	 * Check this version is 
	 * older than other.
	 * <p>
	 * @param version the version to compare.
	 * @return true if is older.
	 */
	public boolean isOlderThan(ServerVersion version) {
		return version != null && getID() < version.getID();
	}
	
	/**
	 * Check this version is 
	 * older or equals than other.
	 * <p>
	 * @param version the version to compare.
	 * @return true if is older or equals.
	 */
	public boolean isOlderEqualsThan(ServerVersion version) {
		return version != null && getID() <= version.getID();
	}
	
	/**
	 * Example:
	 * ( 1_7_R1 == 1_7_R3 ) = true
	 * ( 1_8_R1 == 1_7_R1 ) = false
	 */
	public boolean sameVersion(ServerVersion other) {
		return name().substring( 0, name().indexOf("_R") )
				.equals(other.name().substring( 0, other.name().indexOf("_R") ));
	}
	
	/**
	 * Example:
	 * ( 1_8_R3 == 1_7_R3 ) = true
	 * ( 1_8_R1 == 1_8_R3 ) = false
	 */
	public boolean sameSubVersion(ServerVersion other) {
		return name().substring((name().indexOf("R") + 1))
				.equals(other.name().substring((other.name().indexOf("R") + 1)));
	}
	
	/**
	 * Check server version
	 * is newer than other version.
	 * <p>
	 * @param version the version to compare.
	 * @return true if is.
	 */
	public static boolean serverNewerThan(ServerVersion version) {
		return getVersion().isNewerThan(version);
	}
	
	/**
	 * Check server version
	 * is newer or equals than 
	 * other version.
	 * <p>
	 * @param version the version to compare.
	 * @return true if is.
	 */
	public static boolean serverNewerEqualsThan(ServerVersion version) {
		return getVersion().isNewerEqualsThan(version);
	}
	
	/**
	 * Check server version
	 * is older than 
	 * other version.
	 * <p>
	 * @param version the version to compare.
	 * @return true if is.
	 */
	public static boolean serverOlderThan(ServerVersion version) {
		return getVersion().isOlderThan(version);
	}
	
	/**
	 * Check server version
	 * is older or equals than 
	 * other version.
	 * <p>
	 * @param version the version to compare.
	 * @return true if is.
	 */
	public static boolean serverOlderEqualsThan(ServerVersion version) {
		return getVersion().isOlderEqualsThan(version);
	}
	
	/**
	 * Example:
	 * ( 1_7_R1 == 1_7_R3 ) = true
	 * ( 1_8_R1 == 1_7_R1 ) = false
	 */
	public static boolean serverSameVersion(ServerVersion version) {
		return getVersion().sameVersion(version);
	}
	
	/**
	 * Example:
	 * ( 1_8_R3 == 1_7_R3 ) = true
	 * ( 1_8_R1 == 1_8_R3 ) = false
	 */
	public static boolean serverSameSubVersion(ServerVersion version) {
		return getVersion().sameSubVersion(version);
	}
	
	/**
	 * Get this server version.
	 * <p>
	 * @return this server version.
	 */
	public static ServerVersion getVersion() {
		// get version string.
		final String packageName = Bukkit.getServer().getClass().getPackage().getName();
		final String version     = packageName.substring(packageName.lastIndexOf(".") + 1);
		
		// return version enum.
		return valueOf(version);
	}
}
package com.hotmail.AdrianSR.core.version;

import java.util.Arrays;

import com.hotmail.AdrianSR.core.main.core.AdrianSRCore;

public enum CoreVersion {

	v1_2_0,
	v1_2_1,
	v1_2_2,
	v1_2_3,
	v1_2_4,
	v1_2_5,
	v1_2_6,
	v1_2_7,
	v1_2_8,
	v1_2_9,
	v1_3_0,
	;
	
	private int id;
	
	CoreVersion() {
		this.id = Integer.valueOf( name().replace("v", "").replace("_", "") );
	}
	
	public int getID() {
		return id;
	}
	
	/**
	 * Formats the given string.
	 * Examples:
	 *  - formatVersionName( "1.3.0" ) = v1_3_0
	 *  - formatVersionName( "V1.3_0" ) = v1_3_0
	 * <p>
	 * @param suppose_version the suppose version name.
	 * @return formatted version name.
	 */
	public static String formatVersionName(String suppose_version) {
		return ( "v" + suppose_version.trim().toLowerCase().replace(".", "_").replace("v", "") );
	}
	
	public static CoreVersion of(String name) {
		return Arrays.stream(values()).filter(v -> v.name().equalsIgnoreCase(name)).findAny().orElse(null);
	}
	
	public static CoreVersion getVersion() {
		return of(CoreVersion.formatVersionName(AdrianSRCore.getInstance().getDescription().getVersion()));
	}
	
	/**
	 * Check this version is 
	 * newer than other.
	 * <p>
	 * @param version the version to compare.
	 * @return true if is newer.
	 */
	public boolean isNewerThan(CoreVersion version) {
		return version != null && getID() > version.getID();
	}
	
	/**
	 * Check this version is 
	 * newer or equals than other.
	 * <p>
	 * @param version the version to compare.
	 * @return true if is newer or equals.
	 */
	public boolean isNewerEqualsThan(CoreVersion version) {
		return version != null && getID() >= version.getID();
	}
	
	/**
	 * Check this version is 
	 * older than other.
	 * <p>
	 * @param version the version to compare.
	 * @return true if is older.
	 */
	public boolean isOlderThan(CoreVersion version) {
		return version != null && getID() < version.getID();
	}
	
	/**
	 * Check this version is 
	 * older or equals than other.
	 * <p>
	 * @param version the version to compare.
	 * @return true if is older or equals.
	 */
	public boolean isOlderEqualsThan(CoreVersion version) {
		return version != null && getID() <= version.getID();
	}
}
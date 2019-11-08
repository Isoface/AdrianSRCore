package com.hotmail.AdrianSR.core.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;

import us.myles.ViaVersion.api.ViaVersion;

/**
 * Represents a class to
 * get Players client 
 * version number.
 * <p>
 * @author AdrianSR
 */
@SuppressWarnings("deprecation") public final class ClientVersion {
	
	/**
	 * Player client version number.
	 */
	private final int version;
	
	/**
	 * Construct a new {@link Player}
	 * client version number getter.
	 * <p>
	 * @param player Player to get.
	 */
	public ClientVersion(final Player player) {
		if (player == null || !player.isOnline()) {
			version = -1;
		} else if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
			version = ViaVersion.getInstance().getPlayerVersion(player);
		}  else if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			version = ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
		} else { // if there is no any plugin for allowing multiple versions on the same server
			switch (ServerVersion.getVersion()) {
				case v1_8_R1:
				case v1_8_R2:
				case v1_8_R3:
					version = 47;
					break;
					
				case v1_9_R1:
					version = 107;
					break;
					
				case v1_9_R2:
					version = 109;
					break;
					
				case v1_10_R1:
					version = 210;
					break;
					
				case v1_11_R1:
					version = 315;
					break;
				
				case v1_12_R1:
					version = 335;
					break;
					
				case v1_13_R1:
					version = 393;
					break;
					
				default:
					version = -1;
					break;
			}
		}
	}
	
	/**
	 * Get the Player client
	 * version.
	 * <p>
	 * @return Player client version number.
	 */
	public int get() {
		return version;
	}
}
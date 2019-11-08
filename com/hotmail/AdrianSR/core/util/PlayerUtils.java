package com.hotmail.AdrianSR.core.util;

import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

/**
 * An useful class for handling any {@link Player}.
 * <p>
 * @author AdrianSR / Date: 9 oct. 2019 / Time: 7:20:21 p. m.
 */
public class PlayerUtils {

	/**
	 * Checks the state of the given player.
	 * <p>
	 * Returns true if the given player is online.
	 * <p>
	 * @param player the player to check.
	 * @return true if the given player is online.
	 */
	public static boolean isValid(Player player) {
		return player != null && player.isOnline();
	}
	
	public static int getClientVersion(Player player) {
		return new ClientVersion(player).get();
	}
	
	/**
	 * Gets the given player prefix.
	 * <p>
	 * @param player the player to get prefix.
	 * @return the player prefix on the first found enabled permission plugin or null if all the compatible
	 * permission plugins are disabled or if the player doesn't have any prefix.
	 */
	public static String getPlayerPrefix(Player player) {
		PlayerPrefix prefix = new PlayerPrefix(player);
		return prefix.getPermissionsExPrefix() != null ? prefix.getPermissionsExPrefix()
				: prefix.getPowerfulPermsPrefix() != null ? prefix.getPowerfulPermsPrefix()
						: prefix.getUltraPermissionsPrefix() != null ? prefix.getUltraPermissionsPrefix() : null;
	}
	
	// TODO: TEST
    /**
     * Sets whether the entity is invulnerable or not.
     * <p>
     * When an entity is invulnerable it can only be damaged by players in
     * creative mode.
     * <p>
     * @param ticks the invulnerable time in server ticks.
     */
	public static void setInvulnerable(final Player player, long ticks) {
		if (isValid(player)) {
			player.setInvulnerable(true);
			new Thread(() -> {
				try {
					long seconds = ( ticks / 20 ); // from server ticks to seconds
					Thread.sleep( TimeUnit.SECONDS.toMillis( seconds ) );
				} catch (Exception e) {
					/* ignore */
				}
				player.setInvulnerable(false);
			}).start();
		}
	}
}
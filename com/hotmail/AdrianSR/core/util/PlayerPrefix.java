package com.hotmail.AdrianSR.core.util;

import org.bukkit.entity.Player;

import com.github.gustav9797.PowerfulPerms.PowerfulPerms;
import com.github.gustav9797.PowerfulPermsAPI.PermissionPlayer;

import me.TechXcrafter.UltraPermissions.UltraPermissions;
import me.TechXcrafter.UltraPermissions.storage.User;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * An useful class for getting the player prefix
 * of a permission plugin.
 * <p>
 * @author AdrianSR / Date: 9 oct. 2019 / Time: 7:43:58 p. m.
 */
public final class PlayerPrefix {

	/**
	 * player prefixes.
	 */
	private String premissions_ex_prefix    = null;
	private String ultra_permissions_prefix = null;
	private String powerful_perms_prefix    = null;

	/**
	 * Construct a new player prefix getter.
	 * <blockquote>
	 * <b> Compatible plugins:
	 * <p>
	 * - PermissionsEx 
	 * <p>
	 * - UltraPermissions 
	 * <p>
	 * - PowerfulPerms
	 * </em>
	 * </blockquote>
	 * <p>
	 * @param player the player to get prefix.
	 */
	public PlayerPrefix(final Player player) {
		/* PermissionEx prefix */
		try {
			Class.forName("ru.tehkode.permissions.bukkit.PermissionsEx");
			if (PermissionsEx.getUser(player) != null) {
				premissions_ex_prefix = PermissionsEx.getUser(player).getPrefix();
			}
		} catch (Throwable t) {
			/* ignore */
		}

		/* UltraPermissions prefix */
		try {
			Class.forName("me.TechXcrafter.UltraPermissions.UltraPermissions");
			final User user = UltraPermissions.getUserFromUUID(player.getUniqueId());
			if (user != null) {
				ultra_permissions_prefix = user.getPrefix();
			}
		} catch (Throwable t) {
			/* ignore */
		}

		/* PowerfulPerms prefix */
		try {
			Class.forName("com.github.gustav9797.PowerfulPerms");
			final PermissionPlayer user = PowerfulPerms.getPlugin().getPermissionManager()
					.getPermissionPlayer(player.getUniqueId());
			if (user != null) {
				powerful_perms_prefix = user.getPrefix();
			}
		} catch (Throwable t) {
			/* ignore */
		}
	}
	
	/**
	 * Gets player PermissionsEx prefix.
	 * <p>
	 * @return player PermissionsEx prefix, or null if the PermissionEx plugin is not
	 *         enabled or if the player doesn't have any prefix.
	 */
	public String getPermissionsExPrefix() {
		return premissions_ex_prefix;
	}
	
	/**
	 * Gets player UltraPermissions prefix.
	 * <p>
	 * @return player UltraPermissions prefix, or null if the UltraPermissions plugin is not
	 *         enabled or if the player doesn't have any prefix.
	 */
	public String getUltraPermissionsPrefix() {
		return ultra_permissions_prefix;
	}
	
	/**
	 * Gets player PowerfulPerms prefix.
	 * <p>
	 * @return player PowerfulPerms prefix, or null if the PowerfulPerms plugin is
	 *         not enabled or if the player doesn't have any prefix.
	 */
	public String getPowerfulPermsPrefix() {
		return powerful_perms_prefix;
	}
}
package es.outlook.adriansrj.core.util.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.plugin.Plugin;

/**
 * Useful class for dealing with {@link Player} related things.
 * <p>
 * @author AdrianSR / Monday 14 June, 2021 / 12:02 PM
 */
public class PlayerUtil {
	
	@SuppressWarnings ( "deprecation" )
	public static ItemStack getItemInMainHand ( Player player ) {
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_9_R1 ) ) {
			return player.getInventory ( ).getItemInMainHand ( );
		} else {
			// we gotta implement this annoying thing due to the people that
			// still wants to play on 1.8 since they don't accept old pvp
			// mechanics will never come back!!!!!!!!
			return player.getItemInHand ( );
		}
	}
	
	@SuppressWarnings ( "deprecation" )
	public static void setItemInMainHand ( Player player , ItemStack item ) {
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_9_R1 ) ) {
			player.getInventory ( ).setItemInMainHand ( item );
		} else {
			// we gotta implement this annoying thing due to the people that
			// still wants to play on 1.8 since they don't accept old pvp
			// mechanics will never come back!!!!!!!!
			player.setItemInHand ( item );
		}
	}
	
	public static void hidePlayer ( Player player , Player target , Plugin plugin ) {
		try {
			player.hidePlayer ( plugin , target );
		} catch ( NoSuchMethodError ex ) {
			player.hidePlayer ( target );
		}
	}
	
	public static void showPlayer ( Player player , Player target , Plugin plugin ) {
		try {
			player.showPlayer ( plugin , target );
		} catch ( NoSuchMethodError ex ) {
			player.showPlayer ( target );
		}
	}
}
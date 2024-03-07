package es.outlook.adriansrj.core.util.permission;

import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Useful class for getting the prefix of a player. Supported permission
 * plugins:
 * <li> PermissionsEx
 * <li> UltraPermissions
 * <li> PowerfulPerms
 * <li> LuckPerms
 * <p>
 * @author AdrianSR / Monday 27 July, 2020 / 04:56 PM
 */
public class PrefixUtil {
	
	/**
	 * @param player
	 * @return
	 */
	public static String getNextAvailablePrefix ( Player player ) {
		List < String > list = getAvailablePrefixes ( player );
		return list.isEmpty ( ) ? null : list.get ( 0 );
	}
	
	/**
	 * @param player
	 * @return
	 */
	public static List < String > getAvailablePrefixes ( Player player ) {
		List < String > prefixes = new ArrayList <> ( );
		for ( String prefix : new String[]
				{
						getPermissionsExPrefix ( player ) ,
						getUltraPermissionsPrefix ( player ) ,
						getPowerfulPermsPrefix ( player ) ,
						getLuckPermsPrefix ( player ) ,
				} ) {
			if ( prefix != null ) {
				prefixes.add ( prefix );
			}
		}
		return prefixes;
	}
	
	/**
	 * @param player
	 * @return
	 */
	public static String getPermissionsExPrefix ( Player player ) {
//		try {
//			return ru.tehkode.permissions.bukkit.PermissionsEx.getUser ( player ).getPrefix ( );
//		} catch ( Throwable ex ) {
//			return null;
//		}
		
		return null;
	}
	
	/**
	 * @param player
	 * @return
	 */
	public static String getUltraPermissionsPrefix ( Player player ) {
//		try {
//			return me.TechXcrafter.UltraPermissions.UltraPermissions.getUserFromUUID ( player.getUniqueId ( ) )
//					.getPrefix ( );
//		} catch ( Throwable ex ) {
//			return null;
//		}
		
		return null;
	}
	
	/**
	 * @param player
	 * @return
	 */
	public static String getPowerfulPermsPrefix ( Player player ) {
//		try {
//			return com.github.gustav9797.PowerfulPerms.PowerfulPerms.getPlugin ( ).getPermissionManager ( )
//					.getPermissionPlayer ( player.getUniqueId ( ) ).getPrefix ( );
//		} catch ( Throwable ex ) {
//			return null;
//		}
		
		return null;
	}
	
	/**
	 * @param player
	 * @return
	 */
	public static String getLuckPermsPrefix ( Player player ) {
//		try {
//			return net.luckperms.api.LuckPermsProvider.get ( ).getUserManager ( ).getUser ( player.getUniqueId ( ) )
//					.getCachedData ( ).getMetaData ( ).getPrefix ( );
//		} catch ( Throwable ex ) {
//			return null;
//		}
		return null;
	}
}
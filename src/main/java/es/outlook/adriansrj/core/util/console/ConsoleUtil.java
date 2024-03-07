package es.outlook.adriansrj.core.util.console;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 * Useful methods for dealing with the console of servers based on Bukkit.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 04:25 PM
 */
public class ConsoleUtil {
	
	/**
	 * Writes to console the desired message.
	 * <p>
	 * @param message the message to send.
	 */
	public static void sendMessage ( String message ) {
		Bukkit.getConsoleSender().sendMessage ( message );
	}
	
	/**
	 * Writes to console the desired formatted message.
	 * <p>
	 * @param color the color of the format.
	 * @param message the content of the message.
	 * @param plugin the plugin sender.
	 */
	public static void sendPluginMessage ( ChatColor color , String message , Plugin plugin ) {
		sendMessage ( color + "[" + plugin.getName() + "] " + message  );
	}
	
	/**
	 * Writes to console the desired formatted message.
	 * <p>
	 * @param message the content of the message.
	 * @param plugin the plugin sender.
	 */
	public static void sendPluginMessage ( String message , Plugin plugin ) {
		sendMessage ( "[" + plugin.getName() + "] " + message  );
	}
	
	/**
	 * Writes to console the desired formatted message.
	 * <p>
	 * @param color the color of the format.
	 * @param message the content of the message.
	 * @param prefix the plugin sender alias.
	 */
	public static void sendPluginMessage ( ChatColor color , String message , String alias ) {
		sendMessage ( color + "[" + alias + "] " + message  );
	}
	
	/**
	 * Writes to console the desired formatted message.
	 * <p>
	 * @param message the content of the message.
	 * @param prefix the plugin sender alias.
	 */
	public static void sendPluginMessage ( String message , String alias ) {
		sendMessage ( "[" + alias + "] " + message  );
	}
}
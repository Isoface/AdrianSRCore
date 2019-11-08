package com.hotmail.AdrianSR.core.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.hotmail.AdrianSR.core.main.CustomPlugin;

/**
 * Represents a Console message utils class.
 * 
 * @author AdrianSR
 */
public final class PrintUtils {
	
	/**
	 * Print plugin message.
	 * <p>
	 * @param mess the message to print.
	 */
	public static void print(String mess, final CustomPlugin plugin) {
		print(ChatColor.GREEN, mess, plugin);
	}
	
	/**
	 * Print plugin message.
	 * 
	 * @param color the message color.
	 * @param mess the message to print.
	 */
	public static void print(ChatColor color, String mess, final CustomPlugin plugin) {
		Bukkit.getConsoleSender().sendMessage(color + getPluginName(plugin) + " " + mess);
	}
	
	/**
	 * @return the plugin console name.
	 */
	private static String getPluginName(final CustomPlugin plugin) {
		return "[" + plugin.getName() + "]";
	}
}
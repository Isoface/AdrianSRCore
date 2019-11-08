package com.hotmail.AdrianSR.core.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents a {@link org.bukkit.command.Command} Utils class.
 * 
 * @author AdrianSR
 */
public class CommandUtils {
	
	public static final String MUST_BE_PLAYER_MESSAGE = ChatColor.RED + "You must be a player to use this command!";
	
	/**
	 * Check if a command sender instanceof player.
	 * 
	 * @param sender the sender.
	 * @return true if is a player.
	 */
	public static boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}
	
	/**
	 * Get a player from command sender.
	 * 
	 * @param sender the player sender.
	 * @return player if the CommandSender instanceof Player, or null if is not.
	 */
	public static Player getPlayer(CommandSender sender) {
		return isPlayer(sender) ? ((Player) sender) : null;
	}
	
	/**
	 * Check arguments length.
	 * 
	 * @param args the arguments.
	 * @param min the min number of arguments.
	 * @return true if contains the min.
	 */
	public static boolean containsMinArgs(final String[] args, final int min) {
		return args.length > min;
	}
}

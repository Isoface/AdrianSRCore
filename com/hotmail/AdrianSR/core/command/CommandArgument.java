package com.hotmail.adriansr.core.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Represents an argument of a command handled by a {@link CommandHandler}.
 * <p>
 * @author AdrianSR / Wednesday 15 April, 2020 / 10:51 AM
 */
public interface CommandArgument  {
	
	/**
	 * Gets the name which identifies this argument.
	 * <p>
	 * @return argument's name.
	 */
	public String getName ( );
	
	/**
	 * Executes this argument. Note that the {@code subargs} array might exclude the
	 * name of this argument.
	 * <p>
	 * @param sender  the source of the command.
	 * @param command the command which was executed.
	 * @param label   the alias of the command which was used.
	 * @param subargs the passed command arguments, excluding this.
	 */
	public void execute ( CommandSender sender , Command command , String label , String[] subargs );
	
	/**
	 * Executes this argument. Note that the {@code subargs} array might exclude the
	 * name of this argument.
	 * <p>
	 * @param sender  the source of the command.
	 * @param command the command which was executed.
	 * @param alias   the alias of the command which was used.
	 * @param subargs the passed command arguments, excluding this, including final
	 *                partial argument to be completed and command label.
	 * @return a List of possible completions for the final argument, or null to
	 *         default to the command executor.
	 */
	public List < String > tab ( CommandSender sender , Command command , String alias , String [] subargs );
}
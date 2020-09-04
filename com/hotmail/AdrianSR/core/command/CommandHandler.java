package com.hotmail.adriansr.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.adriansr.core.handler.PluginHandler;

/**
 * Convenience implementation of {@link CommandExecutor} and
 * {@link TabCompleter}, for handling {@link PluginCommand}s.
 * <p>
 * @author AdrianSR / Wednesday 15 April, 2020 / 10:42 AM
 */
public class CommandHandler extends PluginHandler implements CommandExecutor , TabCompleter  {
	
	/** the handling command */
	protected final PluginCommand command;
	/** the handling arguments */
	protected final Set < CommandArgument > arguments;
	/** the help argument */
	protected CommandHelpArgument help;
	
	/**
	 * Constructs the command handler for handling the provided
	 * {@link PluginCommand}, with the desired {@link CommandArgument}s.
	 * <p>
	 * @param command   the command to handle.
	 * @param arguments the arguments to handle.
	 */
	public CommandHandler ( final PluginCommand command , final CommandArgument... arguments ) {
		super ( command.getPlugin ( ) );
		
		Validate.notNull ( command , "the command cannot be null!" );
		
		command.setExecutor ( this );
		command.setTabCompleter ( this );
		
		this.command   = command;
		this.arguments = new HashSet<>();
		
		if ( arguments != null ) {
			for ( CommandArgument argument : arguments ) {
				this.arguments.add ( argument );
			}
		}
	}
	
	/**
	 * Constructs the command handler for handling a {@link PluginCommand} of the
	 * provided {@link JavaPlugin}, with the desired {@link CommandArgument}s.
	 * <p>
	 * @param plugin    the plugin owner of the command.
	 * @param name      the name of the commmand described in the
	 *                  <strong>{@code plugin.yml}</strong> file.
	 * @param arguments the arguments to handle.
	 */
	public CommandHandler ( final JavaPlugin plugin , final String name , final CommandArgument... arguments ) {
		this ( plugin.getCommand ( name ) , arguments );
	}
	
	/**
	 * Gets the {@link PluginCommand} this handler handles.
	 * <p>
	 * @return the handling command.
	 */
	public PluginCommand getCommand() {
		return command;
	}
	
	/**
	 * Gets the arguments this handler handles.
	 * <p>
	 * @return the handling arguments.
	 */
	public Set < CommandArgument > getArguments() {
		return Collections.unmodifiableSet ( arguments );
	}
	
	/**
	 * Gets the argument used for providing help to the command sender.
	 * <p>
	 * @return the help argument.
	 */
	public CommandHelpArgument getHelpArgument() {
		return help;
	}
	
	/**
	 * Sets the argument used for providing help to the command sender.
	 * <p>
	 * @param help the help argument, or null if no required.
	 * @return this Object, for chaining.
	 */
	public CommandHandler setHelpArgument ( CommandHelpArgument help ) {
		this.help = help;
		return this;
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}

	@Override
	public boolean onCommand ( CommandSender sender , Command command , String label , String [] args ) {
		if ( args.length == 0 ) {
			if ( getHelpArgument() != null ) {
				getHelpArgument().execute ( sender , command , label , args );
			}
			return true;
		}
		
		/* extracting the sub-arguments */
		final String[] subargs = args.length > 1 ? Arrays.copyOfRange ( args , 1 , args.length ) : new String [ 0 ];
		
		/* looking for the corresponding argument */
		final CommandArgument argument = getArguments().stream()
				.filter ( other -> args [ 0 ].equalsIgnoreCase ( other.getName ( ) ) )
				.findAny().orElse ( null );
		
		/* execute the argument if handled, and the help argument if not */
		if ( argument != null ) {
			argument.execute ( sender , command , label , subargs ); // handled
		} else if ( getHelpArgument() != null ) {
			getHelpArgument().execute ( sender , command , label , subargs ); // not handled
		}
		return true;
	}
	
	@Override
	public List < String > onTabComplete ( CommandSender sender , Command command , String alias , String [] args ) {
		/* passing sub-arguments of the matching argument if any */
		final CommandArgument argument = getArguments().stream()
				.filter ( other -> args [ 0 ].equalsIgnoreCase ( other.getName ( ) ) )
				.findAny().orElse ( null );
		if ( argument != null ) {
			return argument.tab ( sender , command , alias,
					( args.length > 1 ? Arrays.copyOfRange ( args , 1 , args.length ) : new String [ 0 ] ) );
		}
		
		/* passing available arguments of this command */
		final List < String > names = new ArrayList<>(); 
		getArguments().stream().forEach ( arg -> names.add ( arg.getName ( ) ) );
		return names.isEmpty() ? null : names;
	}
}
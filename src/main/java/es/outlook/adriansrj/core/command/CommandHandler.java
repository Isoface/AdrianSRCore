package es.outlook.adriansrj.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import es.outlook.adriansrj.core.plugin.custom.PluginCustom;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import es.outlook.adriansrj.core.handler.PluginHandler;

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
		
		this.command = command;
		this.command.setExecutor ( this );
		this.command.setTabCompleter ( this );
		
		this.arguments = new HashSet < > ( );
		
		if ( arguments != null ) {
			for ( CommandArgument argument : arguments ) {
				this.arguments.add ( argument );
			}
		}
		
		// here we set a default use message in case none was specified in plugin.yml
		setDefaultUsageMessage ( );
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
	 * Constructs the command handler for handling a {@link PluginCommand} of the
	 * provided {@link PluginCustom}, with the desired {@link CommandArgument}s.
	 * <p>
	 * @param plugin    the plugin owner of the command.
	 * @param name      the name of the commmand described in the
	 *                  <strong>{@code plugin.yml}</strong> file.
	 * @param arguments the arguments to handle.
	 */
	public CommandHandler ( final PluginCustom plugin , final String name , final CommandArgument... arguments ) {
		this ( plugin.getCommand ( name ) , arguments );
	}
	
	/**
	 * Gets the {@link PluginCommand} this handler handles.
	 * <p>
	 * @return the handling command.
	 */
	public PluginCommand getCommand ( ) {
		return command;
	}
	
	/**
	 * Gets the arguments this handler handles.
	 * <p>
	 * @return the handling arguments.
	 */
	public Set < CommandArgument > getArguments ( ) {
		return Collections.unmodifiableSet ( arguments );
	}
	
	/**
	 * Registers the provided {@link CommandArgument}.
	 * <p>
	 * @param argument the argument to register.
	 * @return this Object, for chaining.
	 */
	public CommandHandler registerArgument ( CommandArgument argument ) {
		Validate.notNull ( argument , "argument cannot be null!" );
		
		arguments.add ( argument );
		return this;
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}

	@Override
	public boolean onCommand ( CommandSender sender , Command command , String label , String [ ] args ) {
		if ( args.length > 0 ) {
			// here we're extracting the sub-arguments
			String [ ] subargs = args.length > 1 ? Arrays.copyOfRange ( args , 1 , args.length ) : new String [ 0 ];
			
			// here we're looking for the corresponding argument
			CommandArgument argument = getArguments ( ).stream ( )
					.filter ( other -> args [ 0 ].equalsIgnoreCase ( other.getName ( ) ) )
					.findAny ( ).orElse ( null );
			
			if ( argument != null ) {
				if ( !argument.execute ( sender , command , label , subargs ) ) {
					sender.sendMessage ( ChatColor.RED + argument.getUsage ( ) );
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
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
	
	/**
	 * Creates and set a default usage message to be used.
	 */
	protected void setDefaultUsageMessage ( ) {
		StringBuilder builder = new StringBuilder ( );
		builder.append ( '/' );
		builder.append ( "<command>" );
		
		Iterator < CommandArgument > argument_iterator = this.arguments.iterator ( );
		
		if ( argument_iterator.hasNext ( ) ) {
			builder.append ( ' ' );
			builder.append ( '<' );
			
			while ( argument_iterator.hasNext ( ) ) {
				CommandArgument argument = argument_iterator.next ( );
				
				builder.append ( argument.getName ( ) );
				builder.append ( argument_iterator.hasNext ( ) ? '|' : '>' );
			}
		}
		
		command.setUsage ( ChatColor.RED + builder.toString ( ) );
	}
}
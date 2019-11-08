package com.hotmail.AdrianSR.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.manager.CustomPluginManager;
import com.hotmail.AdrianSR.core.util.CommandUtils;
import com.hotmail.AdrianSR.core.util.TextUtils;

public class CustomCommandManager extends CustomPluginManager {
	
	private   final Set<CustomCommandArgument> arguments;
	protected final Command                      command;
	protected       boolean                allow_console;
	protected       String                   console_msg;
	protected       CustomCommandArgument  help_argument;

	public CustomCommandManager(CustomPlugin plugin, String command, boolean allow_console) {
		super(plugin); 
		Validate.notNull(plugin.getCommand(command), "The given command couln't be found in the commands of the given plugin!");
		this.setExecutorOf(command);
		this.setTabCompleterOf(command);

		this.arguments     = new HashSet<>();
		this.command       = plugin.getCommand(command);
		this.allow_console = allow_console;
		this.console_msg   = ChatColor.RED + "You cannot use this command from the console!";
		this.help_argument = new ShowAllHelpCommandArgument("Information about the command '" + command + "'", "/" + command + " help", this);
	}
	
	public Command getCommand() {
		return command;
	}
	
	public boolean isAllowConsole() {
		return allow_console;
	}
	
	public CustomCommandManager setAllowConsole(boolean allow) {
		this.allow_console = allow;
		return this;
	}
	
	public String getExecutedFromConsoleMessage() {
		return console_msg;
	}
	
	public CustomCommandManager setExecutedFromConsoleMessage(String message) {
		this.console_msg = message;
		return this;
	}
	
	public CustomCommandManager registerArgument(CustomCommandArgument arg) {
		this.arguments.add(arg);
		return this;
	}
	
	public CustomCommandManager unregisterArgument(CustomCommandArgument arg) {
		this.arguments.remove(arg);
		return this;
	}
	
	public Set<CustomCommandArgument> getArguments() {
		return Collections.unmodifiableSet(arguments);
	}
	
	public CustomCommandManager setHelpArgument(CustomCommandArgument help_argument) {
		this.help_argument = help_argument;
		return this;
	}
	
	public CustomCommandArgument getHelpArgument() {
		return help_argument;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ( !CommandUtils.isPlayer(sender) && !isAllowConsole() ) {
			sender.sendMessage(TextUtils.getNotNull(getExecutedFromConsoleMessage(), "null"));
			return false;
		} else if (args.length == 0) {
			return ( getHelpArgument() == null ? false : getHelpArgument().execute(sender, command, new String[0]));
		}

		CustomCommandArgument base_argument = getArguments().stream()
				.filter(arg -> args[0].equalsIgnoreCase(arg.getName()))
				.findAny().orElse(null);
		if (base_argument == null) {
			return ( getHelpArgument() == null 
					? false
					: getHelpArgument().execute(sender, command,
							( args.length > 1 
							? Arrays.copyOfRange(args, 1, args.length)
							: new String[0] )) );
		} else if (!CommandUtils.isPlayer(sender) && !base_argument.isAllowConsole()) {
			sender.sendMessage(TextUtils.getNotNull(getExecutedFromConsoleMessage(), "null"));
			return true;
		}
		
		base_argument.execute(sender, command, ( args.length > 1 
				? Arrays.copyOfRange(args, 1, args.length) 
				: new String[0] ) );
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String>    argument_names = new ArrayList<>();
		CustomCommandArgument argument = null;
		for (CustomCommandArgument other : getArguments()) {
			if (args.length > 0 && other.getName().equalsIgnoreCase(args[0])) {
				argument = other;
			}
			argument_names.add(other.getName());
		}
		
		if (argument != null) {
			return argument.tab(sender, command,
					( args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0] ));
		}
		return argument_names;
	}
}
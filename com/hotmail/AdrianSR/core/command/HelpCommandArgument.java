package com.hotmail.AdrianSR.core.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpCommandArgument extends CustomCommandArgument {
	
	private final String   description;
	private final String usage_message;
	private final String  help_message;
	
	public HelpCommandArgument(String description, String usage_message, String help_message) {
		this.description   = description;
		this.usage_message = usage_message;
		this.help_message  = help_message;
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public boolean isAllowConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getUsageMessage() {
		return usage_message;
	}
	
	public String getHelpMessage() {
		return help_message;
	}

	@Override
	public boolean execute(CommandSender sender, Command command, String... sub_args) {
		sender.sendMessage(getHelpMessage());
		return true;
	}

	@Override
	public List<String> tab(CommandSender sender, Command command, String... sub_args) {
		return null;
	}
}
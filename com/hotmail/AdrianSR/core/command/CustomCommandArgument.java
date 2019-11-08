package com.hotmail.AdrianSR.core.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class CustomCommandArgument {
	
	public abstract String getName();
	
	public abstract boolean isAllowConsole();
	
	public abstract String getDescription();
	
	public abstract String getUsageMessage();
	
	public abstract boolean execute(CommandSender sender, Command command, String... sub_args);
	
	public abstract List<String> tab(CommandSender sender, Command command, String... sub_args);
}
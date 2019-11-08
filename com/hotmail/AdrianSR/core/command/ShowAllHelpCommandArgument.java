package com.hotmail.AdrianSR.core.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

public class ShowAllHelpCommandArgument extends HelpCommandArgument {

	private final CustomCommandManager cmd_manager;
	
	public ShowAllHelpCommandArgument(String description, String usage_message, CustomCommandManager cmd_manager) {
		super(description, usage_message, null);
		this.cmd_manager = cmd_manager;
	}

	@Override
	public String getHelpMessage() {
		StringBuilder help_message = new StringBuilder(ChatColor.LIGHT_PURPLE + "The command '" + cmd_manager.getCommand().getName() + "': \n");
		cmd_manager.getArguments().forEach(arg -> {
			if (!StringUtils.isBlank(arg.getName())) {
				help_message.append(ChatColor.GRAY + "- /" + " Argument '" + arg.getName().toLowerCase() + "': ");
				help_message.append("\n");
				help_message.append(ChatColor.GRAY + "   - Usage: " + arg.getUsageMessage());      help_message.append("\n");
				help_message.append(ChatColor.GRAY + "   - Description: " + arg.getDescription()); help_message.append("\n");
				help_message.append(" \n ");
			}
		});
		return help_message.toString();
	}
}
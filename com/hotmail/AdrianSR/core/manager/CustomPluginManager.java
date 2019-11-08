package com.hotmail.AdrianSR.core.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;

import com.hotmail.AdrianSR.core.main.CustomPlugin;

public abstract class CustomPluginManager implements Listener, CommandExecutor, TabCompleter {

	protected static final Map<Class<? extends CustomPluginManager>, CustomPluginManager> MANAGER_INSTANCES = new HashMap<>();
	
	protected CustomPlugin         plugin;
	private   boolean listener_registered;
	private   boolean        executor_set;
	private   boolean   tab_completer_set;

	public CustomPluginManager(CustomPlugin plugin) {
		if (MANAGER_INSTANCES.containsKey(getClass())) { /* check instance */
			throw new UnsupportedOperationException("This manager is already initialized!");
		}
		MANAGER_INSTANCES.put(getClass(), this);
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}
	
	/**
	 * Register events on this class.
	 * <p>
	 * This class must implements the {@link Listener} interface.
	 */
	protected void registerEvents() {
		Validate.isTrue(!listener_registered, "This listener has already ben registered!");
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.listener_registered = true;
	}
	
	protected void setExecutorOf(String command) {
		Validate.isTrue(!executor_set, "This is already the executor of a command!");
		Validate.isTrue(!StringUtils.isBlank(command), "The given command is not valid!");
		plugin.getCommand(command).setExecutor(this);
		this.executor_set = true;
	}
	
	protected void setTabCompleterOf(String command) {
		Validate.isTrue(!tab_completer_set, "This is already the tab completer of a command!");
		Validate.isTrue(!StringUtils.isBlank(command), "The given command is not valid!");
		plugin.getCommand(command).setTabCompleter(this);
		this.tab_completer_set = true;
	}
}
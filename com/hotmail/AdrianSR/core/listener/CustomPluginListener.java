package com.hotmail.AdrianSR.core.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.hotmail.AdrianSR.core.main.CustomPlugin;

public abstract class CustomPluginListener implements Listener {
	
	protected CustomPlugin plugin;

	public CustomPluginListener(CustomPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	public CustomPlugin getPlugin() {
		return plugin;
	}
}
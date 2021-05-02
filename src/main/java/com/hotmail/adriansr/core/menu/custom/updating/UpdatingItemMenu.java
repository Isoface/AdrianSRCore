package com.hotmail.adriansr.core.menu.custom.updating;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.hotmail.adriansr.core.menu.Item;
import com.hotmail.adriansr.core.menu.ItemMenu;
import com.hotmail.adriansr.core.menu.custom.updating.handler.UpdatingItemMenuHandler;
import com.hotmail.adriansr.core.menu.size.ItemMenuSize;

public class UpdatingItemMenu extends ItemMenu {
	
	public UpdatingItemMenu(String title, ItemMenuSize size, ItemMenu parent, Item... contents) {
		super(title, size, parent, contents);
	}
	
	@Override
	public UpdatingItemMenuHandler getHandler() {
		return (UpdatingItemMenuHandler) this.handler;
	}
	
	@Override
	public boolean registerListener(Plugin plugin) {
		if (this.handler == null) {
			Bukkit.getPluginManager().registerEvents( ( this.handler = new UpdatingItemMenuHandler(this, plugin) ), plugin);
			return true;
		}
		return false;
	}
}
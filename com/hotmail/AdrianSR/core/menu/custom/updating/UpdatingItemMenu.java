package com.hotmail.AdrianSR.core.menu.custom.updating;

import org.bukkit.Bukkit;

import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.menu.ItemMenu;
import com.hotmail.AdrianSR.core.menu.custom.updating.handler.UpdatingItemMenuHandler;
import com.hotmail.AdrianSR.core.menu.item.Item;
import com.hotmail.AdrianSR.core.menu.size.ItemMenuSize;

public class UpdatingItemMenu extends ItemMenu {
	
	public UpdatingItemMenu(String title, ItemMenuSize size, ItemMenu parent, Item... contents) {
		super(title, size, parent, contents);
	}
	
	public UpdatingItemMenuHandler getHandler() {
		return (UpdatingItemMenuHandler) this.handler;
	}
	
	@Override
	public boolean registerListener(CustomPlugin plugin) {
		if (this.handler == null) {
			Bukkit.getPluginManager().registerEvents( ( this.handler = new UpdatingItemMenuHandler(this, plugin) ), plugin);
			return true;
		}
		return false;
	}
}
package com.hotmail.adriansr.core.menu.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import com.hotmail.adriansr.core.menu.ItemMenu;
import com.hotmail.adriansr.core.menu.action.ItemMenuClickAction;
import com.hotmail.adriansr.core.menu.holder.ItemMenuHolder;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

public class ItemMenuHandler implements Listener {
	
	protected final ItemMenu       menu;
	protected final Plugin plugin;

	public ItemMenuHandler(ItemMenu menu, Plugin plugin) {
		this.menu   = menu;
		this.plugin = plugin;
	}
	
	public void delayedOpen(Player player, int delay) {
		delayed(() -> { menu.open(player); }, delay);
	}
	
	public void delayedUpdate(Player player, int delay) {
		delayed(() -> { menu.update(player); }, delay);
	}
	
	public void delayedClose(Player player, int delay) {
		delayed(() -> { menu.close(player); }, delay);
	}
	
	public void unregisterListener() {
		HandlerList.unregisterAll(this);
	}
	
	protected void delayed(Runnable runnable, int delay) {
		SchedulerUtil.scheduleSyncDelayedTask ( runnable , delay , plugin );
	}
	
	@Deprecated
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory() == null
				|| !(event.getWhoClicked() instanceof Player) 
				|| !(event.getInventory().getHolder() instanceof ItemMenuHolder)) {
			return;
		}
		
		ItemMenuHolder holder = (ItemMenuHolder) event.getInventory().getHolder();
		if (!menu.equals(holder.getItemMenu())) {
			return;
		}
		
		final ItemMenuClickAction action = new ItemMenuClickAction(menu, event);
		((ItemMenuHolder) event.getInventory().getHolder()).getItemMenu().onClick(action);
		event.setCurrentItem(action.getCurrentItem());
		event.setCursor(action.getCursor());
		event.setCancelled(true);
	}
	
	@Deprecated
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPluginDisable(PluginDisableEvent event) {
		if (event.getPlugin().equals(plugin)) {
			this.menu.closeOnlinePlayers();
		}
	}
}
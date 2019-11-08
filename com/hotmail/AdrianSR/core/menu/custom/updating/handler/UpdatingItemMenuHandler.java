package com.hotmail.AdrianSR.core.menu.custom.updating.handler;

import org.bukkit.scheduler.BukkitTask;

import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.menu.ItemMenu;
import com.hotmail.AdrianSR.core.menu.handler.ItemMenuHandler;
import com.hotmail.AdrianSR.core.util.Schedulers;

public class UpdatingItemMenuHandler extends ItemMenuHandler {
	
	protected BukkitTask updater_task;

	public UpdatingItemMenuHandler(ItemMenu menu, CustomPlugin plugin) {
		super(menu, plugin);
	}
	
	@Override
	public void unregisterListener() {
		super.unregisterListener();
		stopUpdating();
	}

	public void startUpdating(int start_delay, int ticks) {
		stopUpdating();
		this.updater_task = Schedulers.syncRepeating(() -> {
			this.menu.updateOnlinePlayers();
		}, start_delay, ticks, plugin);
	}
	
	public void stopUpdating() {
		if (updater_task != null) {
			updater_task.cancel();
			updater_task = null;
		}
	}
}

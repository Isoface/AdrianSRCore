package es.outlook.adriansrj.core.menu.custom.updating.handler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.handler.ItemMenuHandler;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;

public class UpdatingItemMenuHandler extends ItemMenuHandler {
	
	protected BukkitTask updater_task;

	public UpdatingItemMenuHandler(ItemMenu menu, Plugin plugin) {
		super(menu, plugin);
	}
	
	public boolean isUpdating ( ) {
		return updater_task != null && Bukkit.getScheduler ( ).isCurrentlyRunning ( updater_task.getTaskId ( ) );
	}
	
	@Override
	public void unregisterListener() {
		super.unregisterListener();
		stopUpdating();
	}
	
	public void startUpdating(int start_delay, int ticks) {
		stopUpdating();
		this.updater_task = SchedulerUtil.runTaskTimer (() -> {
			this.menu.updateOnlinePlayers();
		}, start_delay , ticks , plugin );
	}
	
	public void stopUpdating() {
		if (updater_task != null) {
			updater_task.cancel();
			updater_task = null;
		}
	}
}

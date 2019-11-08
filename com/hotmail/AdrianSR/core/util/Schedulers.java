package com.hotmail.AdrianSR.core.util;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.hotmail.AdrianSR.core.main.CustomPlugin;

/**
 * Represents a Schedulers util class.
 * 
 * @author AdrianSR
 */
public final class Schedulers {
	
	/**
	 * Never run a Asynchronously Task in the Main Thread.
	 * 
	 * @param run the runnable.
	 */
	public static void doNeverInMainThread(Runnable run, final CustomPlugin plugin) {
		if (!Bukkit.isPrimaryThread()) {
			run.run();
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(plugin, run);
	}

	/**
	 * Never Run a Task in the Main Thread.
	 * 
	 * @param run the runnable.
	 */
	public static void doNeverOutOfMainThread(Runnable run, final CustomPlugin plugin) {
		if (Bukkit.isPrimaryThread()) {
			run.run();
			return;
		}
		Bukkit.getScheduler().runTask(plugin, run);
	}

	/**
	 * Run a Sync Delayed Task.
	 * 
	 * @param task the Runnable Interface.
	 * @param ticks the ticks amount.
	 * @return the Task id.
	 */
	public static int scheduleSync(Runnable task, int ticks, final CustomPlugin plugin) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, ticks);
	}

	/**
	 * Run a Asynchronously Task Later.
	 * 
	 * @param task the Runnable Interface.
	 * @param ticks the ticks amount.
	 * @return the Task id.
	 */
	public static void scheduleAsync(Runnable task, int ticks, final CustomPlugin plugin) {
		if (!plugin.isEnabled()) {
			task.run();
		} else {
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, ticks);
		}
	}

	/**
	 * Run a Repeating Asynchronously Task.
	 * 
	 * @param runnable the Runnable Interace.
	 * @param ticks the ticks amount.
	 */
	public static BukkitTask asyncRepeating(Runnable runnable, int ticks, final CustomPlugin plugin) {
		return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, ticks, ticks);
	}

	/**
	 * Run a Repeating Task.
	 * 
	 * @param runnable the Runnable Interface.
	 * @param ticks the ticks amount.
	 */
	public static BukkitTask syncRepeating(Runnable runnable, int ticks, final CustomPlugin plugin) {
		return Bukkit.getScheduler().runTaskTimer(plugin, runnable, ticks, ticks);
	}
	
	/**
	 * Run a Repeating Task.
	 * 
	 * @param runnable the Runnable Interface.
	 * @param ticks the ticks amount.
	 * @param start_delay ticks to wait before run
	 */
	public static BukkitTask syncRepeating(Runnable runnable, int start_delay, int ticks, final CustomPlugin plugin) {
		return Bukkit.getScheduler().runTaskTimer(plugin, runnable, start_delay, ticks);
	}
	
	/**
	 * Run a Cancellable Repeating task.
	 * 
	 * @param runnable the runnable.
	 * @param ticks the ticks aamount.
	 * @return the Bukkit Runnable Task.
	 */
	public static BukkitTask cancellableRepeatingTaks(Runnable runnable, int ticks, final CustomPlugin plugin) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				// run runnable.
				runnable.run();
			}
		}.runTaskTimer(plugin, ticks, ticks);
	}

	/**
	 * Run an Asynchronously Task.
	 * 
	 * @param runnable the Runnable Interface.
	 */
	public static void async(Runnable runnable, final CustomPlugin plugin) {
		if (!plugin.isEnabled()) {
			runnable.run();
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
		}
	}
	
	/**
	 * Run a Synchronously Task.
	 * 
	 * @param runnable the Runnable Interface.
	 */
	public static void sync(Runnable runnable, final CustomPlugin plugin) {
		if (!plugin.isEnabled()) {
			runnable.run();
		} else {
			Bukkit.getScheduler().runTask(plugin, runnable);
		}
	}

	/**
	 * Stop Task.
	 * 
	 * @param taskId the task id.
	 */
	public static void cancel(int taskId) {
		Bukkit.getScheduler().cancelTask(taskId);
	}
}

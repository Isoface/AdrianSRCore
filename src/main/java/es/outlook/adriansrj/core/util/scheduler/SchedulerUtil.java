package es.outlook.adriansrj.core.util.scheduler;

import es.outlook.adriansrj.core.main.AdrianSRCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Useful class for dealing with schedulers. This is just a direct-access to the
 * scheduler of Bukkit.
 * <p>
 * @author AdrianSR / Wednesday 15 April, 2020 / 01:23 PM
 * @see Bukkit#getScheduler()
 */
public class SchedulerUtil {
	
	/**
	 * Schedules a once off task to occur after a delay.
	 * <p>
	 * This task will be executed by the main server thread.
	 * <p>
	 * @param task   Task to be executed
	 * @param delay  Delay in server ticks before executing task
	 * @param plugin Plugin that owns the task
	 * @return Task id number (-1 if scheduling failed)
	 */
	public static int scheduleSyncDelayedTask ( Runnable task , long delay , Plugin plugin ) {
		return getScheduler ( ).scheduleSyncDelayedTask ( plugin , task , delay );
	}
	
	/**
	 * Schedules a once off task to occur after a delay.
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * This task will be executed by the main server thread.
	 * <p>
	 * @param task   Task to be executed
	 * @param delay  Delay in server ticks before executing task
	 * @return Task id number (-1 if scheduling failed)
	 */
	public static int scheduleSyncDelayedTask ( Runnable task , long delay ) {
		return scheduleSyncDelayedTask ( task , delay , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * Schedules a once off task to occur as soon as possible.
	 * <p>
	 * This task will be executed by the main server thread.
	 * <p>
	 * @param task   Task to be executed
	 * @param plugin Plugin that owns the task
	 * @return Task id number (-1 if scheduling failed)
	 */
	public static int scheduleSyncDelayedTask ( Runnable task , Plugin plugin ) {
		return getScheduler ( ).scheduleSyncDelayedTask ( plugin , task );
	}
	
	/**
	 * Schedules a once off task to occur as soon as possible.
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * This task will be executed by the main server thread.
	 * <p>
	 * @param task   Task to be executed
	 * @return Task id number (-1 if scheduling failed)
	 */
	public static int scheduleSyncDelayedTask ( Runnable task ) {
		return scheduleSyncDelayedTask ( task , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * Schedules a repeating task.
	 * <p>
	 * This task will be executed by the main server thread.
	 * <p>
	 * @param task   Task to be executed
	 * @param delay  Delay in server ticks before executing first repeat
	 * @param period Period in server ticks of the task
	 * @param plugin Plugin that owns the task
	 * @return Task id number (-1 if scheduling failed)
	 */
	public static int scheduleSyncRepeatingTask ( Runnable task , long delay , long period , Plugin plugin ) {
		return getScheduler ( ).scheduleSyncRepeatingTask ( plugin , task , delay , period );
	}
	
	/**
	 * Schedules a repeating task.
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * This task will be executed by the main server thread.
	 * <p>
	 * @param task   Task to be executed
	 * @param delay  Delay in server ticks before executing first repeat
	 * @param period Period in server ticks of the task
	 * @return Task id number (-1 if scheduling failed)
	 */
	public static int scheduleSyncRepeatingTask ( Runnable task , long delay , long period ) {
		return scheduleSyncRepeatingTask ( task , delay , period , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * Calls a method on the main thread and returns a Future object. This task will
	 * be executed by the main server thread.
	 * <ul>
	 * <li>Note: The Future.get() methods must NOT be called from the main thread.
	 * <li>Note2: There is at least an average of 10ms latency until the isDone()
	 * method returns true.
	 * </ul>
	 * @param <T>    The callable's return type
	 * @param task   Task to be executed
	 * @param plugin Plugin that owns the task
	 * @return Future Future object related to the task
	 */
	public static < T > Future < T > callSyncMethod ( Callable < T > task , Plugin plugin ) {
		return getScheduler ( ).callSyncMethod ( plugin , task );
	}
	
	/**
	 * Calls a method on the main thread and returns a Future object. This task will
	 * be executed by the main server thread.
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * <ul>
	 * <li>Note: The Future.get() methods must NOT be called from the main thread.
	 * <li>Note2: There is at least an average of 10ms latency until the isDone()
	 * method returns true.
	 * </ul>
	 * @param <T>    The callable's return type
	 * @param task   Task to be executed
	 * @return Future Future object related to the task
	 */
	public static < T > Future < T > callSyncMethod ( Callable < T > task ) {
		return callSyncMethod ( task , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * Removes task from scheduler.
	 * <p>
	 * @param id Id number of task to be removed
	 */
	public static void cancelTask ( int id ) {
		getScheduler ( ).cancelTask ( id );
	}
	
	/**
	 * Removes all tasks associated with a particular plugin from the scheduler.
	 * <p>
	 * @param plugin Owner of tasks to be removed
	 */
	public static void cancelTasks ( Plugin plugin ) {
		getScheduler ( ).cancelTasks ( plugin );
	}
	
	/**
	 * Check if the task is currently running.
	 * <p>
	 * A repeating task might not be running currently, but will be running in the
	 * future. A task that has finished, and does not repeat, will not be running
	 * ever again.
	 * <p>
	 * Explicitly, a task is running if there exists a thread for it, and that
	 * thread is alive.
	 * <p>
	 * @param id The task to check.
	 * @return If the task is currently running.
	 */
	public static boolean isCurrentlyRunning ( int id ) {
		return getScheduler ( ).isCurrentlyRunning ( id );
	}
	
	/**
	 * Check if the task queued to be run later.
	 * <p>
	 * If a repeating task is currently running, it might not be queued now but
	 * could be in the future. A task that is not queued, and not running, will not
	 * be queued again.
	 * <p>
	 * @param id The task to check.
	 * @return If the task is queued to be run.
	 */
	public static boolean isQueued ( int id ) {
		return getScheduler ( ).isQueued ( id );
	}
	
	/**
	 * Returns a list of all active workers.
	 * <p>
	 * This list contains asynch tasks that are being executed by separate threads.
	 * <p>
	 * @return Active workers
	 */
	public static List < BukkitWorker > getActiveWorkers ( ) {
		return getScheduler ( ).getActiveWorkers ( );
	}
	
	/**
	 * Returns a list of all pending tasks. The ordering of the tasks is not related
	 * to their order of execution.
	 * <p>
	 * @return Active workers
	 */
	public static List < BukkitTask > getPendingTasks ( ) {
		return getScheduler ( ).getPendingTasks ( );
	}
	
	/**
	 * Returns a task that will run on the next server tick.
	 * <p>
	 * @param task   the task to be run
	 * @param plugin the reference to the plugin scheduling task
	 * @return a BukkitTask that contains the id number
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static BukkitTask runTask ( Runnable task , Plugin plugin ) throws IllegalArgumentException {
		return getScheduler ( ).runTask ( plugin , task );
	}
	
	/**
	 * Returns a task that will run on the next server tick.
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * @param task   the task to be run
	 * @return a BukkitTask that contains the id number
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static BukkitTask runTask ( Runnable task ) throws IllegalArgumentException {
		return runTask ( task , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * Returns a task that will run on the next server tick.
	 * <p>
	 * @param task   the task to be run
	 * @param plugin the reference to the plugin scheduling task
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static void runTask ( Consumer < BukkitTask > task , Plugin plugin ) throws IllegalArgumentException {
		getScheduler ( ).runTask ( plugin , task );
	}
	
	/**
	 * Returns a task that will run on the next server tick.
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * @param task   the task to be run
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static void runTask ( Consumer < BukkitTask > task ) throws IllegalArgumentException {
		runTask ( task , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Returns a task that will run asynchronously.
	 * <p>
	 * @param task   the task to be run
	 * @param plugin the reference to the plugin scheduling task
	 * @return a BukkitTask that contains the id number
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static BukkitTask runTaskAsynchronously ( Runnable task , Plugin plugin ) throws IllegalArgumentException {
		return getScheduler ( ).runTaskAsynchronously ( plugin , task );
	}
	
	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * Returns a task that will run asynchronously.
	 * <p>
	 * @param task   the task to be run
	 * @return a BukkitTask that contains the id number
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static BukkitTask runTaskAsynchronously ( Runnable task ) throws IllegalArgumentException {
		return runTaskAsynchronously ( task , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Returns a task that will run asynchronously.
	 * <p>
	 * @param task   the task to be run
	 * @param plugin the reference to the plugin scheduling task
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static void runTaskAsynchronously ( Consumer < BukkitTask > task , Plugin plugin )
			throws IllegalArgumentException {
		getScheduler ( ).runTaskAsynchronously ( plugin , task );
	}
	
	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * Returns a task that will run asynchronously.
	 * <p>
	 * @param task   the task to be run
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static void runTaskAsynchronously ( Consumer < BukkitTask > task )
			throws IllegalArgumentException {
		runTaskAsynchronously ( task , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * Returns a task that will run after the specified number of server ticks.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task
	 * @param plugin the reference to the plugin scheduling task
	 * @return a BukkitTask that contains the id number
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static BukkitTask runTaskLater ( Runnable task , long delay , Plugin plugin )
			throws IllegalArgumentException {
		return getScheduler ( ).runTaskLater ( plugin , task , delay );
	}
	
	/**
	 * Returns a task that will run after the specified number of server ticks.
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task
	 * @return a BukkitTask that contains the id number
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static BukkitTask runTaskLater ( Runnable task , long delay ) throws IllegalArgumentException {
		return runTaskLater ( task , delay , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * Returns a task that will run after the specified number of server ticks.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task
	 * @param plugin the reference to the plugin scheduling task
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static void runTaskLater ( Consumer < BukkitTask > task , long delay , Plugin plugin )
			throws IllegalArgumentException {
		getScheduler ( ).runTaskLater ( plugin , task , delay );
	}
	
	/**
	 * Returns a task that will run after the specified number of server ticks.
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static void runTaskLater ( Consumer < BukkitTask > task , long delay ) throws IllegalArgumentException {
		runTaskLater ( task , delay , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Returns a task that will run asynchronously after the specified number of
	 * server ticks.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task
	 * @param plugin the reference to the plugin scheduling task
	 * @return a BukkitTask that contains the id number
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static BukkitTask runTaskLaterAsynchronously ( Runnable task , long delay , Plugin plugin )
			throws IllegalArgumentException {
		return getScheduler ( ).runTaskLaterAsynchronously ( plugin , task , delay );
	}
	
	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * Returns a task that will run asynchronously after the specified number of
	 * server ticks.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task
	 * @return a BukkitTask that contains the id number
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static BukkitTask runTaskLaterAsynchronously ( Runnable task , long delay ) throws IllegalArgumentException {
		return runTaskLaterAsynchronously ( task , delay , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Returns a task that will run asynchronously after the specified number of
	 * server ticks.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task
	 * @param plugin the reference to the plugin scheduling task
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static void runTaskLaterAsynchronously ( Consumer < BukkitTask > task , long delay , Plugin plugin )
			throws IllegalArgumentException {
		getScheduler ( ).runTaskLaterAsynchronously ( plugin , task , delay );
	}
	
	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * Returns a task that will run asynchronously after the specified number of
	 * server ticks.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static void runTaskLaterAsynchronously ( Consumer < BukkitTask > task , long delay )
			throws IllegalArgumentException {
		runTaskLaterAsynchronously ( task , delay , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * Returns a task that will repeatedly run until cancelled, starting after the
	 * specified number of server ticks.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task
	 * @param period the ticks to wait between runs
	 * @param plugin the reference to the plugin scheduling task
	 * @return a BukkitTask that contains the id number
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static BukkitTask runTaskTimer ( Runnable task , long delay , long period , Plugin plugin )
			throws IllegalArgumentException {
		return getScheduler ( ).runTaskTimer ( plugin , task , delay , period );
	}
	
	/**
	 * Returns a task that will repeatedly run until cancelled, starting after the
	 * specified number of server ticks.
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task
	 * @param period the ticks to wait between runs
	 * @return a BukkitTask that contains the id number
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static BukkitTask runTaskTimer ( Runnable task , long delay , long period )
			throws IllegalArgumentException {
		return runTaskTimer ( task , delay , period , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * Returns a task that will repeatedly run until cancelled, starting after the
	 * specified number of server ticks.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task
	 * @param period the ticks to wait between runs
	 * @param plugin the reference to the plugin scheduling task
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static void runTaskTimer ( Consumer < BukkitTask > task , long delay , long period , Plugin plugin )
			throws IllegalArgumentException {
		getScheduler ( ).runTaskTimer ( plugin , task , delay , period );
	}
	
	/**
	 * Returns a task that will repeatedly run until cancelled, starting after the
	 * specified number of server ticks.
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task
	 * @param period the ticks to wait between runs
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static void runTaskTimer ( Consumer < BukkitTask > task , long delay , long period )
			throws IllegalArgumentException {
		runTaskTimer ( task , delay , period , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Returns a task that will repeatedly run asynchronously until cancelled,
	 * starting after the specified number of server ticks.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task for the first time
	 * @param period the ticks to wait between runs
	 * @param plugin the reference to the plugin scheduling task
	 * @return a BukkitTask that contains the id number
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static BukkitTask runTaskTimerAsynchronously ( Runnable task , long delay , long period , Plugin plugin )
			throws IllegalArgumentException {
		return getScheduler ( ).runTaskTimerAsynchronously ( plugin , task , delay , period );
	}
	
	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * Returns a task that will repeatedly run asynchronously until cancelled,
	 * starting after the specified number of server ticks.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task for the first time
	 * @param period the ticks to wait between runs
	 * @return a BukkitTask that contains the id number
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static BukkitTask runTaskTimerAsynchronously ( Runnable task , long delay , long period )
			throws IllegalArgumentException {
		return runTaskTimerAsynchronously ( task , delay , period , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Returns a task that will repeatedly run asynchronously until cancelled,
	 * starting after the specified number of server ticks.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task for the first time
	 * @param period the ticks to wait between runs
	 * @param plugin the reference to the plugin scheduling task
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static void runTaskTimerAsynchronously ( Consumer < BukkitTask > task , long delay , long period ,
			Plugin plugin ) throws IllegalArgumentException {
		getScheduler ( ).runTaskTimerAsynchronously ( plugin , task , delay , period );
	}
	
	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Note that it will be scheduled from the {@link AdrianSRCore} plugin.
	 * <p>
	 * Returns a task that will repeatedly run asynchronously until cancelled,
	 * starting after the specified number of server ticks.
	 * <p>
	 * @param task   the task to be run
	 * @param delay  the ticks to wait before running the task for the first time
	 * @param period the ticks to wait between runs
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalArgumentException if task is null
	 */
	public static void runTaskTimerAsynchronously ( Consumer < BukkitTask > task , long delay ,
			long period ) throws IllegalArgumentException {
		runTaskTimerAsynchronously ( task , delay , period , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * Gets the scheduler for managing scheduled events.
	 * <p>
	 * @return a scheduling service for this server
	 */
	public static BukkitScheduler getScheduler ( ) {
		return Bukkit.getScheduler ( );
	}
}
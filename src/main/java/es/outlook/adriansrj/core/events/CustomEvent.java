package es.outlook.adriansrj.core.events;

import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

/**
 * Simple implementation of {@link Event} that can call itself.
 * <p>
 * @author AdrianSR / Monday 13 April, 2020 / 03:40 PM
 */
public abstract class CustomEvent extends Event {
	
	/**
	 * The default constructor is defined for cleaner code. This constructor assumes
	 * the event is synchronous.
	 */
	public CustomEvent ( ) {
		super ( );
	}
	
	/**
	 * This constructor is used to explicitly declare an event as synchronous or
	 * asynchronous.
	 * <p>
	 * @param async true indicates the event will fire asynchronously, false by
	 *              default from default constructor
	 */
	public CustomEvent ( boolean async ) {
		super ( async );
	}
	
	/**
	 * Fires this event.
	 * <p>
	 * This is the same as:
	 * <pre><code>
	 * Bukkit.getPluginManager ( ).callEvent ( this );
	 * </code></pre>
	 * <p>
	 * @return this Object, for chaining.
	 * @throws IllegalStateException thrown when an asynchronous event is fired from
	 *                               synchronous code.
	 *                               <p>
	 *                               <i>Note: This is best-effort basis, and should
	 *                               not be used to test synchronized state. This is
	 *                               an indicator for flawed flow logic.</i>
	 */
	public CustomEvent call ( ) {
		Bukkit.getPluginManager ( ).callEvent ( this );
		return this;
	}
	
	/**
	 * Fires this event from the expected thread.
	 * <p>
	 * It means that depending on the returned value from {@link #isAsynchronous()},
	 * the event will be fired from an asynchronous thread, or from the server thread.
	 *
	 * @return this Object, for chaining.
	 */
	public CustomEvent callSafe ( ) {
		if ( Bukkit.isPrimaryThread ( ) && isAsynchronous ( ) ) {
			SchedulerUtil.runTaskAsynchronously ( this :: callSafe );
			return this;
		} else if ( !Bukkit.isPrimaryThread ( ) && !isAsynchronous ( ) ) {
			SchedulerUtil.runTask ( this :: callSafe );
			return this;
		}
		
		Bukkit.getPluginManager ( ).callEvent ( this );
		return this;
	}
}
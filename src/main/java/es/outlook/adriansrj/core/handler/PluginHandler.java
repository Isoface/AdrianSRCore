package es.outlook.adriansrj.core.handler;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Class for handling {@link Plugin}s. Also this is an implementation of
 * {@link Listener}, so the events in this class can be registered by using
 * {@link #register()} or unregister by using {@link #unregister()}.
 * <p>
 * Also {@link #isAllowMultipleInstances()} allows the developer to protect its handler
 * from the creation of more than one instance of it. Also
 * {@link #isAllowMultipleInstances()} or the class extending {@link PluginHandler} must
 * be <strong>{@code final}</strong> to prevent overriding.
 * <p>
 * @author AdrianSR / Thursday 16 April, 2020 / 11:32 AM
 */
public abstract class PluginHandler implements Listener {
	
	/**
	 * Map for storing handler instances.
	 */
	protected static final Map < Class < ? extends PluginHandler > , PluginHandler > HANDLER_INSTANCES = new HashMap < > ( );
	
	/**
     * This method provides fast access to the plugin handler that has provided the
     * given class.
	 * <p>
	 * @param <T> a class that extends PluginHandler.
	 * @param clazz the class desired.
	 * @return the instance of the plugin handle that provided the class.
	 */
	public static < T extends PluginHandler > T getPluginHandler ( Class < T > clazz ) {
		return clazz.cast ( HANDLER_INSTANCES.get ( clazz ) );
	}
	
	/** the handling plugin */
	protected final Plugin plugin;

	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public PluginHandler ( Plugin plugin ) {
		if ( !isAllowMultipleInstances ( ) && HANDLER_INSTANCES.containsKey ( getClass ( ) ) ) {
			throw new IllegalStateException ( "cannot create more than one instance of this handler!" );
		}
		
		this.plugin = plugin;
		PluginHandler.HANDLER_INSTANCES.put ( getClass ( ) , this );
	}
	
	/**
	 * Gets the plugin this handler handles.
	 * <p>
	 * @return the handling plugin.
	 */
	public Plugin getPlugin ( ) {
		return plugin;
	}
	
	/**
	 * Gets whether this handler allows the creation of more than one instance of
	 * it.
	 * <p>
	 * This is useful to avoid users to create instances of this handler.
	 * <p>
	 * @return true to allow more than one instance.
	 */
	protected abstract boolean isAllowMultipleInstances ( );
	
	/**
	 * Registers events in this class.
	 */
	protected void register ( ) {
		HandlerList.unregisterAll ( this );
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	/**
	 * Unregisters events in this class.
	 */
	protected void unregister ( ) {
		HandlerList.unregisterAll ( this );
	}
}
package es.outlook.adriansrj.core.plugin.custom;

import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.main.AdrianSRCore;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.SimplePluginManager;

import java.io.File;
import java.util.Objects;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 18/08/2021 / Time: 07:26 p. m.
 */
@Deprecated
public final class PluginCustomManager extends PluginHandler {
	
	@Deprecated
	public static PluginCustomManager getInstance ( ) {
		return PluginHandler.getPluginHandler ( PluginCustomManager.class );
	}
	
	protected final SimplePluginManager handle;
	protected       boolean             postworld;
	
	/**
	 * Constructs the plugin handler.
	 * <p>
	 *
	 * @param plugin the plugin to handle.
	 */
	public PluginCustomManager ( AdrianSRCore plugin ) {
		super ( plugin );
		register ( );
		
		// extracting command map
		SimpleCommandMap command_map = null;
		
		try {
			command_map = ( SimpleCommandMap ) FieldReflection.getAccessible (
					SimplePluginManager.class , "commandMap" ).get ( Bukkit.getPluginManager ( ) );
		} catch ( IllegalAccessException e ) {
			e.printStackTrace ( );
		} catch ( NoSuchFieldException e ) {
			e.printStackTrace ( );
		}
		
		this.handle = new SimplePluginManager ( Bukkit.getServer ( ) , command_map );
		this.handle.registerInterface ( PluginCustomLoader.class );
		
		// loading plugins
		loadPlugins ( );
		// enabling plugins on startup
		enablePlugins ( PluginLoadOrder.STARTUP );
		// masking postworld as true, so it will
		// load the postworld plugins once world
		// is successfully loaded
		this.postworld = true;
	}
	
	protected void loadPlugins ( ) {
		File folder = new File ( Bukkit.getWorldContainer ( ) , "plugins" );
		
		if ( folder.exists ( ) || folder.mkdirs ( ) ) {
			this.handle.loadPlugins ( new File ( Bukkit.getWorldContainer ( ) , "plugins" ) );
		} else {
			throw new IllegalStateException ( "couldn't load custom plugins" );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onLoad ( WorldLoadEvent event ) {
		if ( postworld ) {
			enablePlugins ( PluginLoadOrder.POSTWORLD );
			postworld = false;
		}
	}
	
	protected void enablePlugins ( PluginLoadOrder order ) {
		for ( org.bukkit.plugin.Plugin plugin : this.handle.getPlugins ( ) ) {
			if ( !plugin.isEnabled ( ) && plugin.getDescription ( ).getLoad ( ) == order ) {
				this.handle.enablePlugin ( plugin );
			}
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onDisable ( PluginDisableEvent event ) {
		if ( Objects.equals ( event.getPlugin ( ) , AdrianSRCore.getInstance ( ) ) ) {
			this.handle.disablePlugins ( );
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}

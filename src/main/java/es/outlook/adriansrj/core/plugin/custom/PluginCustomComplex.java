package es.outlook.adriansrj.core.plugin.custom;

import es.outlook.adriansrj.core.plugin.Plugin;
import es.outlook.adriansrj.core.plugin.PluginDependency;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.lang.PluginInternalLanguageEnumContainer;
import es.outlook.adriansrj.core.version.CoreVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 18/08/2021 / Time: 11:47 p. m.
 */
@Deprecated
public abstract class PluginCustomComplex extends PluginCustom {
	
	@Override
	public final void onEnable ( ) {
		/* checking the required core version */
		if ( getRequiredCoreVersion ( ) != null && CoreVersion.getCoreVersion ( )
				.isOlder ( getRequiredCoreVersion ( ) ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED ,
											"obsolete core version! a core version newer than or equal to "
													+ getRequiredCoreVersion ( ).name ( ) + " is required!" ,
											this );
			Bukkit.getPluginManager ( ).disablePlugin ( this );
			return;
		}
		
		/* checking the plugin dependences */
		if ( getDependences ( ) != null && getDependences ( ).length > 0 ) {
			for ( PluginDependency dependence : getDependences ( ) ) {
				final org.bukkit.plugin.Plugin plugin = Bukkit.getPluginManager ( )
						.getPlugin ( dependence.getName ( ) );
				final Boolean result = dependence.apply ( plugin );
				
				if ( result == null || result == false ) {
					Bukkit.getPluginManager ( ).disablePlugin ( this );
					return;
				}
			}
		}
		
		/* plugin setup */
		if ( !setUp ( ) || !isEnabled ( ) ) {
			Bukkit.getPluginManager ( ).disablePlugin ( this );
			return;
		}
		
		/* finalizing plugin setup */
		try {
			if ( !( setUpConfig ( ) && setUpHandlers ( ) && setUpCommands ( ) && setUpListeners ( ) ) ) {
				Bukkit.getPluginManager ( ).disablePlugin ( this );
				return;
			}
		} catch ( Throwable ex ) {
			// any exception will disable the plugin
			ex.printStackTrace ( );
			Bukkit.getPluginManager ( ).disablePlugin ( this );
			return;
		}
	}
	
	/**
	 * Setups this plugin. These methods will be called for the initialization of this plugin after checking the
	 * required core version, and the dependencies.
	 * <p>
	 * Also these methods should return <strong>{@code true}</strong> if the initialization was successfully, and
	 * <strong>{@code false}</strong> if not. Returning <strong>{@code false}</strong> from this method means that the
	 * initialization was unsuccessfully, then the plugin will be disabled automatically.
	 * <p>
	 *
	 * @return true if the initialization was successfully.
	 */
	protected abstract boolean setUp ( );
	
	/**
	 * Gets the required core version by this plugin. If the current core version is older than the required, the
	 * plugin
	 * will be disabled.
	 * <p>
	 * Also this method might return <strong>{@code null}</strong> if no core version is required.
	 * <p>
	 *
	 * @return the required core version, or null if no required.
	 */
	public abstract CoreVersion getRequiredCoreVersion ( );
	
	/**
	 * Gets the plugins on which this plugin depends.
	 * <p>
	 * This methods might return an empty array or <strong>{@code null}</strong> if this plugin doesn't depend on
	 * another.
	 * <p>
	 *
	 * @return the dependences or null if this plugin doesn't depend on another.
	 *
	 * @see {@link PluginDependency}
	 */
	public abstract PluginDependency[] getDependences ( );
	
	/**
	 * Returns the internal language enum container of this {@link Plugin}, or null if not implemented.
	 * <p>
	 * The items of the given container will be loaded from the .lang resources that should be found at the given
	 * resources package. {@link #getInternalLanguageResourcesPackage()}.
	 * <p>
	 *
	 * @return the language container of this {@link Plugin}, or null if not implemented.
	 */
	public abstract Class < ? extends Enum < ? extends PluginInternalLanguageEnumContainer > > getInternalLanguageContainer ( );
	
	/**
	 * Returns the package that stores this plugin internal language files (.lang files), or <strong>null</strong> if
	 * not implemented.
	 * <p>
	 *
	 * @return the package that stores this plugin internal language files (.lang files), or <strong>null</strong> if
	 * not implemented.
	 */
	public abstract String getInternalLanguageResourcesPackage ( );
	
	/**
	 * This method should setups the configuration.
	 * <p>
	 * Also this should return <strong>{@code true}</strong> if the configuration was loaded successfully, and
	 * <strong>{@code false}</strong> if not.
	 * <p>
	 * Note that if this methods returns <strong>{@code false}</strong>, then the plugin will be disabled
	 * automatically.
	 * <p>
	 * Note that this methods will be called after:
	 * <ul>
	 * <li> {@link #setUp()}
	 * </ul>
	 * <p>
	 *
	 * @return whether the configuration was loaded successfully. if <strong>false<strong/> is returned, the plugin
	 * will
	 * automatically be disabled as result.
	 */
	protected abstract boolean setUpConfig ( );
	
	/**
	 * This method should setups the plugin handlers.
	 * <p>
	 * Also this should return <strong>{@code true}</strong> if the initialization of the handlers was successfully,
	 * and
	 * <strong>{@code false}</strong> if not.
	 * <p>
	 * Note that if this methods returns <strong>{@code false}</strong>, then the plugin will be disabled
	 * automatically.
	 * <p>
	 * Note that this methods will be called after:
	 * <ul>
	 * <li> {@link #setUp()}
	 * <li> {@link #setUpConfig()}
	 * </ul>
	 * <p>
	 *
	 * @return whether the initialization of the handlers was successfully. if <strong>false<strong/> is returned, the
	 * plugin * will automatically be disabled as result.
	 */
	protected abstract boolean setUpHandlers ( );
	
	/**
	 * This method should setups the commands of the plugin.
	 * <p>
	 * Also this should return <strong>{@code true}</strong> if the initialization of the commands was successfully,
	 * and
	 * <strong>{@code false}</strong> if not.
	 * <p>
	 * Note that if this methods returns <strong>{@code false}</strong>, then the plugin will be disabled
	 * automatically.
	 * <p>
	 * Note that this methods will be called after:
	 * <ul>
	 * <li> {@link #setUp()}
	 * <li> {@link #setUpConfig()}
	 * <li> {@link #setUpHandlers()}
	 * </ul>
	 * <p>
	 *
	 * @return whether the initialization of the commands was successfully. if <strong>false<strong/> is returned, the
	 * plugin * will automatically be disabled as result.
	 */
	protected abstract boolean setUpCommands ( );
	
	/**
	 * This method should setups the listeners of the plugin.
	 * <p>
	 * Also this should return <strong>{@code true}</strong> if the initialization of the listeners was successfully,
	 * and <strong>{@code false}</strong> if not.
	 * <p>
	 * Note that if this methods returns <strong>{@code false}</strong>, then the plugin will be disabled
	 * automatically.
	 * <p>
	 * Note that this methods will be called after:
	 * <ul>
	 * <li> {@link #setUp()}
	 * <li> {@link #setUpConfig()}
	 * <li> {@link #setUpHandlers()}
	 * <li> {@link #setUpCommands()}
	 * </ul>
	 *
	 * @return whether the initialization of the listeners was successfully. if <strong>false<strong/> is returned, the
	 * plugin * will automatically be disabled as result.
	 */
	protected abstract boolean setUpListeners ( );
}
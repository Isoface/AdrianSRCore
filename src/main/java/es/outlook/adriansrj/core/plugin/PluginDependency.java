package es.outlook.adriansrj.core.plugin;

import java.util.function.Function;

import org.apache.commons.lang.Validate;

/**
 * Represents a possible plugin on which a {@link Plugin} may depend.
 * <p>
 * This class implements {@link Function} for receiving the plugin on which a
 * {@link Plugin} depends, and producing a resultant {@link Boolean}, the
 * function might receive <strong>{@code null}</strong> that means the plugin
 * has never been loaded by Bukkit plugin manager, in that case, if the
 * developer return <strong>{@code null}</strong> from this function, the plugin
 * will be disabled automatically.
 * <p>
 * Also the developer can send messages to the console when checking the
 * received plugin.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 02:18 PM
 * <p>
 * <h1> Implementation example: </h1> <pre><code>
 * PluginDependence dependence = new PluginDependence ( "ProtocolLib" ) {
 *     <code>@Override</code> 
 *     public Boolean apply ( org.bukkit.plugin.Plugin plugin ) {
 *         if ( plugin == null ) {
 *             ConsoleUtil.sendPluginMessage ( ChatColor.RED , "ProtocolLib couldn't be found!" , MyPlugin.getInstance ( ) );
 *         } else {
 *             return true; // <- returning <strong>{@code true}</strong> will not disable the plugin.
 *         }
 *         return null; // <- returning <strong>{@code null}</strong> or <strong>{@code false}</strong> will disable the plugin automatically
 *     }
 * };
 * </code></pre> 
 */
public abstract class PluginDependency implements Function < org.bukkit.plugin.Plugin , Boolean > {

	/** the name of the depending plugin */
	protected final String name;
	
	/**
	 * Construct the plugin dependence. Note the the plugin {@code name} is
	 * case-sensitive.
	 * <p>
	 * @param name the name of the depending plugin.
	 */
	public PluginDependency ( final String name ) {
		Validate.notNull ( name , "the name cannot be null!" );
		this.name = name;
	}
	
	/**
	 * Gets the name of the depending plugin.
	 * <p>
	 * @return the name of the depending plugin.
	 */
	public String getName() {
		return name;
	}
}
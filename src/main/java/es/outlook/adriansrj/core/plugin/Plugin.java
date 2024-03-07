package es.outlook.adriansrj.core.plugin;

import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.file.FileUtil;
import es.outlook.adriansrj.core.util.lang.PluginInternalLanguageEnumContainer;
import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import es.outlook.adriansrj.core.version.CoreVersion;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * An implementation of {@link JavaPlugin} that adds some useful utilities.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 11:47 AM
 */
public abstract class Plugin extends JavaPlugin {
	
	// TODO: check the internal language system.
	
//	// the library system implemented here is meant to be
//	// compatible with the bukkit library system. so in the
//	// far future, we will not need to use it anymore, but
//	// the plugin will be still working since it works
//	// pretty much in the same way.
//
//	/** plugins required libraries resolver */
//	public static final MavenDependencyResolver LIBRARY_RESOLVER;
//
//	static {
//		LIBRARY_RESOLVER = new MavenDependencyResolver ( new File (
//				Bukkit.getWorldContainer ( ) , "libraries" ) );
//	}
	
	/** plugin compilation id */
	protected String compilation_id;
	
	@Override
	public final void onLoad ( ) {
//		// registering repositories
//		MavenDependencyRepository[] repositories = getLibraryRepositories ( );
//
//		if ( repositories != null ) {
//			for ( MavenDependencyRepository repository : repositories ) {
//				LIBRARY_RESOLVER.addRepository ( repository );
//			}
//		}
		
//		// downloading and injecting libraries.
//		MavenDependency[] libraries = getLibraries ( );
//
//		if ( libraries != null && libraries.length > 0 ) {
//			LIBRARY_RESOLVER.inject ( this , libraries );
//		}
	}
	
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
		
		/* checking the plugin dependencies */
		PluginDependency[] dependencies = getDependencies ( );
		
		if ( dependencies != null && dependencies.length > 0 ) {
			for ( PluginDependency dependence : getDependencies ( ) ) {
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
	 * @return the required core version, or null if no required.
	 */
	public abstract CoreVersion getRequiredCoreVersion ( );
	
	/**
	 * Gets the plugins on which this plugin depends.
	 * <p>
	 * This methods might return an empty array or <strong>{@code null}</strong> if this plugin doesn't depend on
	 * another.
	 * <p>
	 * @return the dependencies or null if this plugin doesn't depend on another.
	 * @see PluginDependency
	 */
	public abstract PluginDependency[] getDependencies ( );
	
//	/**
//	 * <p>Gets the list of repositories to download libraries this plugin needs.</p>
//	 * @return the list of repositories to download libraries this plugin needs.
//	 */
//	public abstract MavenDependencyRepository[] getLibraryRepositories ( );
//
//	/**
//	 * <p>Gets the list of libraries this plugin needs.</p>
//	 * <p><b>Helps reduce plugin size and eliminates the need for relocation.</b><p/>
//	 * @return the list of libraries this plugin needs.
//	 */
//	public abstract MavenDependency[] getLibraries ( );
	
	/**
	 * Returns the internal language enum container of this {@link Plugin}, or null if not implemented.
	 * <p>
	 * The items of the given container will be loaded from the .lang resources that should be found at the given
	 * resources package. {@link #getInternalLanguageResourcesPackage()}.
	 * <p>
	 * @return the language container of this {@link Plugin}, or null if not implemented.
	 */
	public abstract Class < ? extends Enum < ? extends PluginInternalLanguageEnumContainer > > getInternalLanguageContainer ( );
	
	/**
	 * Returns the package that stores this plugin internal language files (.lang files), or <strong>null</strong> if
	 * not implemented.
	 * <p>
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
	 * @return whether the initialization of the listeners was successfully. if <strong>false<strong/> is returned, the
	 * plugin * will automatically be disabled as result.
	 */
	protected abstract boolean setUpListeners ( );
	
	public File getFile ( ) {
		return super.getFile ( );
	}
	
	/**
	 * Loads all the listeners within the provided package.
	 * <p>
	 * A listener within the provided package will be registered only when the following conditions are met:
	 * <ul>
	 * <li>The class implements the interface {@link Listener}.
	 * <li>The class is not <strong>{@code abstract}</strong>.
	 * <li>The class has a public constructor with the class of this plugin as parameter.
	 * </ul>
	 * In other case, the listener will be ignored.
	 * <p>
	 * Implementation example:
	 * <pre>
	 *  <code> <h1><strong>Plugin Class:</strong></h1>
	 * public class MyPlugin extends Plugin {
	 *     .....
	 * }
	 * <h1><strong>Listener Class:</strong></h1>
	 * public class MyListener implements Listener {
	 *     public MyListener ( MyPlugin plugin ) {
	 *         Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	 *     }
	 * }
	 * </code>
	 * </pre>
	 * <p>
	 * @param listener_package the listeners package.
	 * @throws InvocationTargetException exception to handle.
	 * @throws IllegalArgumentException  exception to handle.
	 * @throws IllegalAccessException    exception to handle.
	 * @throws InstantiationException    exception to handle.
	 */
	protected void setUpListenersPackage ( String listener_package )
			throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		for ( Class < ? > clazz : ClassReflection.getClasses ( getFile ( ) , listener_package ) ) {
			if ( Modifier.isAbstract ( clazz.getModifiers ( ) ) || !Listener.class.isAssignableFrom ( clazz ) ) {
				continue;
			}
			
			Constructor < ? > constructor;
			
			try {
				constructor = clazz.getConstructor ( getClass ( ) );
			} catch ( NoSuchMethodException | SecurityException ex ) {
				continue;
			}
			
			constructor.newInstance ( this );
		}
	}
	
	/**
	 * Saves the raw contents of any resource embedded with a plugin's .jar file assuming it can be found using {@link
	 * #getResource(String)}.
	 * <p>The resource is saved into the provided directory.</p>
	 * @param resource_path the embedded resource path to look for within the plugin's .jar file. (No preceding slash).
	 * @param replace       if true, the embedded resource will overwrite the contents of an existing file.
	 * @throws IllegalArgumentException if the resource path is null, empty, or points to a nonexistent resource.
	 */
	public void saveResource ( String resource_path , File directory , boolean replace ) {
		Validate.notNull ( resource_path , "resource path cannot be null" );
		Validate.notNull ( directory , "directory cannot be null" );
		Validate.isTrue ( StringUtil.isNotBlank ( resource_path ) , "resource path cannot be blank" );
		
		InputStream input = getResource ( resource_path = resource_path.replace ( '\\' , '/' ) );
		
		if ( input != null ) {
			int last_index = resource_path.lastIndexOf ( '/' );
			File out_file = new File ( directory , resource_path.length ( ) > 1 && last_index != -1
					? resource_path.substring ( last_index + 1 )
					: resource_path );
			
			if ( !directory.exists ( ) ) {
				directory.mkdirs ( );
			}
			
			if ( !out_file.exists ( ) || replace ) {
				try {
					FileUtil.copyInputStreamToFile ( input , out_file );
				} catch ( IOException e ) {
					e.printStackTrace ( );
				}
			}
		} else {
			throw new IllegalArgumentException (
					"The embedded resource '" + resource_path + "' couldn't be found in " + getFile ( ) );
		}
	}
	
	@Override
	public void saveResource ( String resource_path , boolean replace ) {
		Validate.notNull ( resource_path , "resource path cannot be null" );
		Validate.isTrue ( StringUtil.isNotBlank ( resource_path ) , "resource path cannot be blank" );
		
		// calculating out directory
		File out_directory = getDataFolder ( );
		int  last_index    = ( resource_path = resource_path.replace ( '\\' , '/' ) ).lastIndexOf ( '/' );
		
		if ( last_index != -1 ) {
			out_directory = new File ( getDataFolder ( ) , resource_path.substring ( 0 , last_index ) );
		}
		
		// then saving
		saveResource ( resource_path , out_directory , replace );
	}
	
	protected String getCompilationId ( ) {
		if ( compilation_id == null ) {
			YamlConfiguration plugin = YamlConfiguration.loadConfiguration (
					new InputStreamReader ( getResource ( "plugin.yml" ) ) );
			
			compilation_id = plugin.getString ( "compid" , String.valueOf ( 0 ) );
		}
		
		return compilation_id;
	}
}
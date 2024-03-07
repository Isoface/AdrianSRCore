package es.outlook.adriansrj.core.plugin.custom;

import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.file.FileUtil;
import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Logger;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 18/08/2021 / Time: 08:42 p. m.
 */
@Deprecated
public abstract class PluginCustom extends PluginBase {
	
	/**
	 * This method provides fast access to the plugin that has {@link #getProvidingPlugin(Class) provided} the given
	 * plugin class, which is usually the plugin that implemented it.
	 * <p>
	 * An exception to this would be if plugin's jar that contained the class does not extend the class, where the
	 * intended plugin would have resided in a different jar / classloader.
	 *
	 * @param <T>   a class that extends PluginCustom
	 * @param clazz the class desired
	 *
	 * @return the plugin that provides and implements said class
	 *
	 * @throws IllegalArgumentException if clazz is null
	 * @throws IllegalArgumentException if clazz does not extend {@link PluginCustom}
	 * @throws IllegalStateException    if clazz was not provided by a plugin, for example, if called with
	 *                                  <code>PluginCustom.getPlugin(PluginCustom.class)</code>
	 * @throws IllegalStateException    if called from the static initializer for given PluginCustom
	 * @throws ClassCastException       if plugin that provided the class does not extend the class
	 */
	public static < T extends PluginCustom > T getPlugin ( Class < T > clazz ) {
		Validate.notNull ( clazz , "class cannot be null" );
		
		if ( PluginCustom.class.isAssignableFrom ( clazz ) ) {
			ClassLoader class_loader = clazz.getClassLoader ( );
			
			if ( class_loader instanceof PluginCustomClassLoader ) {
				PluginCustom plugin = ( ( PluginCustomClassLoader ) class_loader ).result;
				
				if ( plugin != null ) {
					return clazz.cast ( plugin );
				} else {
					throw new IllegalStateException ( "Cannot get plugin for " + clazz + " from a static " +
															  "initializer" );
				}
			} else {
				throw new IllegalArgumentException (
						clazz + " is not initialized by " + PluginCustomClassLoader.class );
			}
		} else {
			throw new IllegalArgumentException ( clazz + " does not extend " + PluginCustom.class );
		}
	}
	
	/**
	 * This method provides fast access to the plugin that has provided the given class.
	 *
	 * @param clazz a class belonging to a plugin
	 *
	 * @return the plugin that provided the class
	 *
	 * @throws IllegalArgumentException if the class is not provided by a CustomPlugin
	 * @throws IllegalArgumentException if class is null
	 * @throws IllegalStateException    if called from the static initializer for given CustomPlugin
	 */
	public static PluginCustom getProvidingPlugin ( Class < ? > clazz ) {
		Validate.notNull ( clazz , "class cannot be null" );
		
		ClassLoader class_loader = clazz.getClassLoader ( );
		
		if ( class_loader instanceof PluginCustomClassLoader ) {
			PluginCustom plugin = ( ( PluginCustomClassLoader ) class_loader ).result;
			
			if ( plugin != null ) {
				return plugin;
			} else {
				throw new IllegalStateException ( "Cannot get plugin for " + clazz + " from a static " +
														  "initializer" );
			}
		} else {
			throw new IllegalArgumentException ( clazz + " is not provided by " + PluginCustomClassLoader.class );
		}
	}
	
	protected File                    file;
	protected File                    data_folder;
	protected PluginCustomLoader      loader;
	protected PluginCustomDescription description;
	protected PluginCustomClassLoader class_loader;
	protected PluginLogger            logger;
	
	protected boolean enabled;
	protected boolean naggable;
	protected String  compilation_id;
	
	public PluginCustom ( ) {
		ClassLoader class_loader = getClass ( ).getClassLoader ( );
		
		if ( class_loader instanceof PluginCustomClassLoader ) {
			( ( PluginCustomClassLoader ) class_loader ).initialize ( this );
		} else {
			throw new IllegalStateException ( "required " + PluginCustomClassLoader.class.getName ( ) + " loader" );
		}
	}
	
	public File getFile ( ) {
		return file;
	}
	
	@Override
	public File getDataFolder ( ) {
		return data_folder;
	}
	
	@Override
	public PluginDescriptionFile getDescription ( ) {
		return description.getHandle ( );
	}
	
	@Override
	public InputStream getResource ( String filename ) {
		Validate.notNull ( filename , "filename cannot be null" );
		
		try {
			URL url = getClassLoader ( ).getResource ( filename );
			
			if ( url != null ) {
				URLConnection connection = url.openConnection ( );
				connection.setUseCaches ( false );
				
				return connection.getInputStream ( );
			} else {
				return null;
			}
		} catch ( IOException ex ) {
			return null;
		}
	}
	
	/**
	 * Returns the ClassLoader which holds this plugin
	 *
	 * @return ClassLoader holding this plugin
	 */
	public PluginCustomClassLoader getClassLoader ( ) {
		return class_loader;
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
	 *
	 * @param listener_package the listeners package.
	 *
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
	 *
	 * @param resource_path the embedded resource path to look for within the plugin's .jar file. (No preceding slash).
	 * @param replace       if true, the embedded resource will overwrite the contents of an existing file.
	 *
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
					throw new RuntimeException ( e );
				}
			}
		} else {
			throw new IllegalArgumentException (
					"The embedded resource '" + resource_path + "' couldn't be found in " + file );
		}
	}
	
	@Override
	public void saveResource ( String resource_path , boolean replace ) {
		Validate.notNull ( resource_path , "resource path cannot be null" );
		Validate.isTrue ( StringUtil.isNotBlank ( resource_path ) , "resource path cannot be blank" );
		
		// calculating out directory
		File out_directory = data_folder;
		int  last_index    = ( resource_path = resource_path.replace ( '\\' , '/' ) ).lastIndexOf ( '/' );
		
		if ( last_index != -1 ) {
			out_directory = new File ( data_folder , resource_path.substring ( 0 , last_index ) );
		}
		
		// then saving
		saveResource ( resource_path , out_directory , replace );
	}
	
	@Override
	public PluginLoader getPluginLoader ( ) {
		return loader;
	}
	
	@Override
	public Server getServer ( ) {
		return loader.getServer ( );
	}
	
	@Override
	public boolean isEnabled ( ) {
		return enabled;
	}
	
	protected void setEnabled ( boolean enabled ) {
		if ( this.enabled != enabled ) {
			this.enabled = enabled;
			
			if ( enabled ) {
				onEnable ( );
			} else {
				onDisable ( );
			}
		}
	}
	
	@Override
	public boolean isNaggable ( ) {
		return naggable;
	}
	
	@Override
	public void setNaggable ( boolean flag ) {
		this.naggable = flag;
	}
	
	@Override
	public Logger getLogger ( ) {
		return logger;
	}
	
	/**
	 * Gets the command with the given name, specific to this plugin. Commands need to be registered in the {@link
	 * PluginDescriptionFile#getCommands() PluginDescriptionFile} to exist at runtime.
	 *
	 * @param name name or alias of the command
	 *
	 * @return the plugin command if found, otherwise null
	 */
	public PluginCommand getCommand ( String name ) {
		String        alias   = name.toLowerCase ( java.util.Locale.ENGLISH );
		PluginCommand command = getServer ( ).getPluginCommand ( alias );
		
		if ( command == null || command.getPlugin ( ) != this ) {
			command = getServer ( ).getPluginCommand ( description.getHandle ( ).getName ( )
															   .toLowerCase ( java.util.Locale.ENGLISH )
															   + ":" + alias );
		}
		
		if ( command != null && command.getPlugin ( ) == this ) {
			return command;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onCommand ( CommandSender commandSender , Command command , String s , String[] strings ) {
		// nothing by default
		return false;
	}
	
	@Override
	public List < String > onTabComplete ( CommandSender commandSender , Command command , String s ,
			String[] strings ) {
		// nothing by default
		return null;
	}
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator ( String world_name , String id ) {
		// nothing by default
		return null;
	}
	
	@Override
	public void onLoad ( ) { }
	
	@Override
	public void onDisable ( ) { }
	
	@Override
	public void onEnable ( ) { }
	
	// deprecated members
	@Override
	@Deprecated
	public final FileConfiguration getConfig ( ) {
		throw new UnsupportedOperationException ( );
	}
	
	@Override
	@Deprecated
	public void saveConfig ( ) {
		throw new UnsupportedOperationException ( );
	}
	
	@Override
	@Deprecated
	public void saveDefaultConfig ( ) {
		throw new UnsupportedOperationException ( );
	}
	
	@Override
	@Deprecated
	public void reloadConfig ( ) {
		throw new UnsupportedOperationException ( );
	}
}
package es.outlook.adriansrj.core.plugin.custom;

import es.outlook.adriansrj.core.main.AdrianSRCore;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Warning;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 18/08/2021 / Time: 03:51 p. m.
 */
@Deprecated
public class PluginCustomLoader implements PluginLoader {
	
	protected static final String PLUGIN_JAR_YML_ENTRY_NAME = "plugin.yml";
	
	protected final Map < String, PluginCustomClassLoader > class_loaders = new LinkedHashMap <> ( );
	protected final Server                                  server;
	
	/**
	 * <p><b>Not meant to be constructed explicitly.</b></p>
	 *
	 * @param server the server instance.
	 */
	public PluginCustomLoader ( Server server ) {
		this.server = server;
	}
	
	public Server getServer ( ) {
		return server;
	}
	
	@Override
	public Plugin loadPlugin ( File file ) throws InvalidPluginException, UnknownDependencyException {
		Validate.notNull ( file , "file cannot be null" );
		Validate.isTrue ( file.isFile ( ) , "file cannot be a directory" );
		Validate.isTrue ( file.exists ( ) , "file must exists" );
		
		// loading description
		PluginCustomDescription description = null;
		
		try {
			description = new PluginCustomDescription ( file );
		} catch ( InvalidDescriptionException e ) {
			throw new InvalidPluginException ( e );
		}
		
		// checking dependencies
		for ( String dependency_name : description.getHandle ( ).getDepend ( ) ) {
			Plugin current = server.getPluginManager ( ).getPlugin ( dependency_name );
			
			if ( current == null ) {
				throw new UnknownDependencyException (
						"Unknown dependency '" + dependency_name + "'. Please download and install "
								+ dependency_name + " to run " + "this plugin." );
			}
		}
		
		// then loading
		ConsoleUtil.sendMessage ( "[" + description.getHandle ( ).getName ( )
										  + "] Loading " + description.getHandle ( ).getFullName ( ) );
		
		try {
			PluginCustomClassLoader class_loader = new PluginCustomClassLoader (
					this , file , getClass ( ).getClassLoader ( ) , description );
			
			// registering class loader
			class_loaders.put ( description.getHandle ( ).getName ( ) , class_loader );
			
			// then loading
			PluginCustom result = class_loader.load ( );
			result.onLoad ( );
			
			return result;
		} catch ( MalformedURLException e ) {
			throw new InvalidPluginException ( e );
		} catch ( ClassNotFoundException e ) {
			throw new InvalidPluginException ( e );
		}
	}
	
	@Override
	public PluginDescriptionFile getPluginDescription ( File file ) throws InvalidDescriptionException {
		return new PluginCustomDescription ( file ).getHandle ( );
	}
	
	@Override
	public Pattern[] getPluginFileFilters ( ) {
		return new Pattern[] {
				Pattern.compile ( "\\.plugin$" ) ,
		};
	}
	
	@Override
	public Map < Class < ? extends Event >, Set < RegisteredListener > > createRegisteredListeners (
			Listener listener , Plugin plugin ) {
		// since the class JavaPluginLoader is final,
		// we couldn't extend from it to avoid this code.
		Validate.notNull ( plugin , "Plugin can not be null" );
		Validate.notNull ( listener , "Listener can not be null" );
		
		boolean useTimings = server.getPluginManager ( )
				.useTimings ( );
		Map < Class < ? extends Event >, Set < RegisteredListener > > ret = new HashMap < Class < ?
				extends Event >, Set < RegisteredListener > > ( );
		Set < Method > methods;
		try {
			Method[] publicMethods  = listener.getClass ( ).getMethods ( );
			Method[] privateMethods = listener.getClass ( ).getDeclaredMethods ( );
			methods = new HashSet < Method > ( publicMethods.length + privateMethods.length , 1.0f );
			for ( Method method : publicMethods ) {
				methods.add ( method );
			}
			for ( Method method : privateMethods ) {
				methods.add ( method );
			}
		} catch ( NoClassDefFoundError e ) {
			plugin.getLogger ( ).severe ( "Plugin " + plugin.getDescription ( )
					.getFullName ( ) + " has failed to register events for " + listener.getClass ( ) + " because "
												  + e.getMessage ( ) + " does not exist." );
			return ret;
		}
		
		for ( final Method method : methods ) {
			final EventHandler eh = method.getAnnotation ( EventHandler.class );
			if ( eh == null ) {
				continue;
			}
			// Do not register bridge or synthetic methods to avoid event duplication
			// Fixes SPIGOT-893
			if ( method.isBridge ( ) || method.isSynthetic ( ) ) {
				continue;
			}
			final Class < ? > checkClass;
			if ( method.getParameterTypes ( ).length != 1 || !Event.class.isAssignableFrom (
					checkClass = method.getParameterTypes ( )[ 0 ] ) ) {
				plugin.getLogger ( ).severe ( plugin.getDescription ( )
													  .getFullName ( ) + " attempted to register an invalid " +
													  "EventHandler method signature \"" + method.toGenericString ( )
													  + "\" in " + listener.getClass ( ) );
				continue;
			}
			final Class < ? extends Event > eventClass = checkClass.asSubclass ( Event.class );
			method.setAccessible ( true );
			Set < RegisteredListener > eventSet = ret.get ( eventClass );
			if ( eventSet == null ) {
				eventSet = new HashSet < RegisteredListener > ( );
				ret.put ( eventClass , eventSet );
			}
			
			for ( Class < ? > clazz = eventClass; Event.class.isAssignableFrom ( clazz );
				  clazz = clazz.getSuperclass ( ) ) {
				// This loop checks for extending deprecated events
				if ( clazz.getAnnotation ( Deprecated.class ) != null ) {
					Warning              warning      = clazz.getAnnotation ( Warning.class );
					Warning.WarningState warningState = server.getWarningState ( );
					if ( !warningState.printFor ( warning ) ) {
						break;
					}
					plugin.getLogger ( ).log (
							Level.WARNING ,
							String.format (
									"\"%s\" has registered a listener for %s on method \"%s\", but the event is " +
											"Deprecated. \"%s\"; please notify the authors %s." ,
									plugin.getDescription ( ).getFullName ( ) ,
									clazz.getName ( ) ,
									method.toGenericString ( ) ,
									( warning != null && warning.reason ( ).length ( ) != 0 ) ? warning.reason ( ) :
											"Server performance will be affected" ,
									Arrays.toString ( plugin.getDescription ( ).getAuthors ( ).toArray ( ) ) ) ,
							warningState == Warning.WarningState.ON ? new AuthorNagException ( null ) : null );
					break;
				}
			}
			
			EventExecutor executor = new EventExecutor ( ) {
				@Override
				public void execute ( Listener listener , Event event ) throws EventException {
					try {
						if ( !eventClass.isAssignableFrom ( event.getClass ( ) ) ) {
							return;
						}
						method.invoke ( listener , event );
					} catch ( InvocationTargetException ex ) {
						throw new EventException ( ex.getCause ( ) );
					} catch ( Throwable t ) {
						throw new EventException ( t );
					}
				}
			};
			if ( useTimings ) {
				eventSet.add ( new TimedRegisteredListener ( listener , executor , eh.priority ( ) , plugin ,
															 eh.ignoreCancelled ( ) ) );
			} else {
				eventSet.add ( new RegisteredListener ( listener , executor , eh.priority ( ) , plugin ,
														eh.ignoreCancelled ( ) ) );
			}
		}
		return ret;
	}
	
	@Override
	public void enablePlugin ( Plugin plugin ) {
		Validate.isTrue ( plugin instanceof PluginCustom ,
						  "this loader only works with " + PluginCustom.class.getName ( ) + " plugins" );
		
		if ( !plugin.isEnabled ( ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.GREEN , "Enabling "
					+ plugin.getDescription ( ).getFullName ( ) , plugin );
			
			PluginCustom instance = ( PluginCustom ) plugin;
			
			try {
				instance.enabled = true;
				instance.onEnable ( );
			} catch ( Throwable ex ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED ,
												"Error occurred while enabling " + plugin.getDescription ( )
														.getFullName ( ) , AdrianSRCore.getInstance ( ) );
			}
			
			// firing event
			server.getPluginManager ( ).callEvent ( new PluginEnableEvent ( plugin ) );
		}
	}
	
	@Override
	public void disablePlugin ( Plugin plugin ) {
		Validate.isTrue ( plugin instanceof PluginCustom ,
						  "this loader only works with " + PluginCustom.class.getName ( ) + " plugins" );
		
		if ( plugin.isEnabled ( ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.YELLOW , "Disabling "
					+ plugin.getDescription ( ).getFullName ( ) , plugin );
			
			// firing event
			server.getPluginManager ( ).callEvent ( new PluginDisableEvent ( plugin ) );
			
			// then disabling
			PluginCustom instance     = ( PluginCustom ) plugin;
			ClassLoader  class_loader = instance.getClassLoader ( );
			
			try {
				instance.enabled = false;
				instance.onDisable ( );
			} catch ( Throwable ex ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED ,
												"Error occurred while disabling " + plugin.getDescription ( )
														.getFullName ( ) , AdrianSRCore.getInstance ( ) );
			}
			
			// removing class loader
			if ( class_loader instanceof PluginCustomClassLoader ) {
				class_loaders.remove ( class_loader );
			}
		}
	}
	
	// classes handling
	
	protected final Map < String, Class < ? > > classes = new HashMap < String, Class < ? > > ( );
	
	protected Class < ? > getClassByName ( final String name ) {
		Class < ? > cachedClass = classes.get ( name );
		
		if ( cachedClass != null ) {
			return cachedClass;
		} else {
			for ( String current : class_loaders.keySet ( ) ) {
				PluginCustomClassLoader loader = class_loaders.get ( current );
				
				try {
					cachedClass = loader.findClass ( name , false );
				} catch ( ClassNotFoundException ex ) {
					// ignored
				}
				
				if ( cachedClass != null ) {
					return cachedClass;
				}
			}
		}
		return null;
	}
	
	protected void setClass ( final String name , final Class < ? > clazz ) {
		if ( !classes.containsKey ( name ) ) {
			classes.put ( name , clazz );
			
			// must be registered in case it is serializable
			if ( ConfigurationSerializable.class.isAssignableFrom ( clazz ) ) {
				ConfigurationSerialization.registerClass ( clazz.asSubclass ( ConfigurationSerializable.class ) );
			}
		}
	}
	
	protected void removeClass ( String name ) {
		Class < ? > clazz = classes.remove ( name );
		
		try {
			// must be unregistered in case it is serializable
			if ( ( clazz != null ) && ( ConfigurationSerializable.class.isAssignableFrom ( clazz ) ) ) {
				ConfigurationSerialization.unregisterClass ( clazz.asSubclass ( ConfigurationSerializable.class ) );
			}
		} catch ( NullPointerException ex ) {
			// Boggle!
			// (Native methods throwing NPEs is not fun when you can't stop it before-hand)
		}
	}
}

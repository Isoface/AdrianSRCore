package es.outlook.adriansrj.core.plugin.custom;

import es.outlook.adriansrj.core.dependency.MavenDependency;
import es.outlook.adriansrj.core.dependency.MavenDependencyResolver;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginLogger;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 18/08/2021 / Time: 04:43 p. m.
 */
@Deprecated
public class PluginCustomClassLoader extends URLClassLoader {
	
	// the library system implemented here is meant to be
	// compatible with the bukkit library system. so in the
	// far future, we will not need to use those classes
	// anymore, but the plugin.yml will be still working.
	
	// single dependency resolver instance
	private static final MavenDependencyResolver DEPENDENCY_RESOLVER;
	
	static {
		ClassLoader.registerAsParallelCapable ( );
		
		DEPENDENCY_RESOLVER = new MavenDependencyResolver ( new File (
				Bukkit.getWorldContainer ( ) , "libraries" ) );
	}
	
	/**
	 * Custom class resolver.
	 *
	 * @author AdrianSR / 18/08/2021 / Time: 05:15 p. m.
	 */
	public interface ClassResolver {
		
		/**
		 * <p>Called from the {@link PluginCustomClassLoader}, asking for a certain class to be provided.<p/>
		 *
		 * @param name the name of the class to resolve.
		 *
		 * @return the class.
		 */
		Class < ? > resolve ( String name );
	}
	
	// unlike the legacy bukkit plugin class loader, this class loader is meant
	// to be very flexible.
	protected final Set < ClassResolver >       resolvers = new LinkedHashSet <> ( );
	// map responsible for caching classes
	protected final Map < String, Class < ? > > classes   = new HashMap < String, Class < ? > > ( );
	
	protected final PluginCustomLoader      loader;
	protected final File                    file;
	protected final PluginCustomDescription description;
	protected       URLClassLoader          library_loader;
	protected       PluginCustom            result;
	
	public PluginCustomClassLoader ( PluginCustomLoader loader , File file , ClassLoader parent ,
			PluginCustomDescription description )
			throws MalformedURLException {
		super ( new URL[] { file.toURI ( ).toURL ( ) } , parent );
		
		this.loader      = loader;
		this.file        = file;
		this.description = description;
	}
	
	protected void initialize ( PluginCustom plugin ) {
		plugin.loader       = loader;
		plugin.file         = file;
		plugin.data_folder  = file.getParentFile ( );
		plugin.description  = description;
		plugin.class_loader = this;
		plugin.logger       = new PluginLogger ( plugin );
	}
	
	public PluginCustom load ( ) throws ClassNotFoundException, InvalidPluginException {
		if ( result == null ) {
			Class < ? > main_class = Class.forName ( description.getHandle ( ).getMain ( ) ,
													 true , this );
			
			if ( PluginCustom.class.isAssignableFrom ( main_class ) ) {
				try {
					// downloading libraries
					Set < MavenDependency > libraries = description.getLibraries ( );
					
					if ( libraries.size ( ) > 0 ) {
						ConsoleUtil.sendMessage (
								"Downloading " + description.getHandle ( ).getName ( ) + " libraries..." );
						
						library_loader = DEPENDENCY_RESOLVER.createLoader ( libraries );
					}
					
					// then initializing
					Constructor < ? extends PluginCustom > constructor =
							main_class.asSubclass ( PluginCustom.class ).getConstructor ( );
					constructor.setAccessible ( true );
					
					result = constructor.newInstance ( );
				} catch ( NoSuchMethodException e ) {
					throw new InvalidPluginException (
							"A constructor with no parameters couldn't be found in the plugin" +
									" main class '" + main_class.getName ( ) + "'" );
				} catch ( InstantiationException e ) {
					throw new InvalidPluginException ( "Abnormal plugin type" , e );
				} catch ( IllegalAccessException e ) {
					throw new InvalidPluginException ( "Couldn't access to the constructor of the plugin main class." +
															   " Declare it as public to solve this problem" , e );
				} catch ( InvocationTargetException e ) {
					throw new InvalidPluginException ( e );
				}
			} else {
				throw new InvalidPluginException (
						"plugin main class '" + main_class.getName ( ) + " must extend " + PluginCustom.class );
			}
		}
		
		return result;
	}
	
	@Override
	protected Class < ? > loadClass ( String name , boolean resolve ) throws ClassNotFoundException {
		try {
			return super.loadClass ( name , resolve );
		} catch ( ClassNotFoundException ex ) {
			Class < ? > result = null;
			
			// loading from libraries
			if ( ( result = loadClassFromLibraries ( name ) ) != null ) {
				return result;
			}
			
			// loading from libraries of plugins this depends on
			List < String > depend = new ArrayList <> ( );
			
			depend.addAll ( description.getHandle ( ).getDepend ( ) );
			depend.addAll ( description.getHandle ( ).getSoftDepend ( ) );
			depend.addAll ( description.getHandle ( ).getLoadBefore ( ) );
			
			for ( String plugin_name : depend ) {
				org.bukkit.plugin.Plugin plugin = Bukkit.getServer ( ).getPluginManager ( )
						.getPlugin ( plugin_name );
				
				if ( plugin != null && plugin.getPluginLoader ( ) instanceof PluginCustomLoader ) {
					PluginCustomLoader loader = ( PluginCustomLoader ) plugin.getPluginLoader ( );
					
					for ( PluginCustomClassLoader other : loader.class_loaders.values ( ) ) {
						if ( ( result = other.loadClassFromLibraries ( name ) ) != null ) {
							return result;
						}
					}
				}
			}
			
			// at last, we try with the resolvers
			for ( ClassResolver resolver : this.resolvers ) {
				if ( ( result = resolver.resolve ( name ) ) != null ) {
					return result;
				}
			}
		}
		
		throw new ClassNotFoundException ( name );
	}
	
	@Override
	protected Class < ? > findClass ( String name ) throws ClassNotFoundException {
		return findClass ( name , true );
	}
	
	protected Class < ? > findClass ( String name , boolean check_global ) throws ClassNotFoundException {
		Class < ? > result = classes.get ( name );
		
		if ( result == null ) {
			if ( check_global ) {
				result = loader.getClassByName ( name );
			}
			
			if ( result == null ) {
				result = super.findClass ( name );
				
				if ( result != null ) {
					loader.setClass ( name , result );
				}
			}
			
			classes.put ( name , result );
		}
		
		return result;
	}
	
	protected Class < ? > loadClassFromLibraries ( String name ) throws ClassNotFoundException {
		if ( library_loader == null ) {
			library_loader = DEPENDENCY_RESOLVER.createLoader ( description.getLibraries ( ) );
		}
		
		return library_loader.loadClass ( name );
	}
	
	public Set < ClassResolver > getResolvers ( ) {
		return Collections.unmodifiableSet ( resolvers );
	}
	
	public boolean containsResolver ( ClassResolver resolver ) {
		return resolvers.contains ( resolver );
	}
	
	public void registerResolver ( ClassResolver resolver ) {
		Validate.notNull ( resolver , "resolver cannot be null" );
		
		resolvers.add ( resolver );
	}
	
	public void unregisterResolver ( ClassResolver resolver ) {
		resolvers.remove ( resolver );
	}
}
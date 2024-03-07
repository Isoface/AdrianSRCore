package es.outlook.adriansrj.core.util.reflection.general;

import es.outlook.adriansrj.core.util.server.Version;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Useful class for dealing with classes.
 * <p>
 *
 * @author AdrianSR / Sunday 12 April, 2020 / 05:03 PM
 */
public class ClassReflection {
	
	public static final String CRAFT_CLASSES_PACKAGE = "org.bukkit.craftbukkit.";
	public static final String NM_CLASSES_PACKAGE    = "net.minecraft.";
	public static final String NMS_CLASSES_PACKAGE   = "net.minecraft.server.";
	
	/**
	 * Gets the member sub class with the provided name that is hold by the provided {@code root} class.
	 * <p>
	 *
	 * @param root     the class that holds the sub class.
	 * @param name     the name of the sub class.
	 * @param declared whether or not the sub class is declared.
	 *
	 * @return the member sub class.
	 *
	 * @throws ClassNotFoundException if the sub class doesn't exist at the {@code root} class.
	 */
	public static Class < ? > getSubClass ( Class < ? > root , String name , boolean declared )
			throws ClassNotFoundException {
		for ( Class < ? > clazz : declared ? root.getDeclaredClasses ( ) : root.getClasses ( ) ) {
			if ( clazz.getSimpleName ( ).equals ( name ) ) {
				return clazz;
			}
		}
		throw new ClassNotFoundException ( "the sub class " + name + " doesn't exist!" );
	}
	
	/**
	 * Gets the member sub class with the provided name that is hold by the provided {@code root} class. (<strong>No
	 * matter if the class is declared or not</strong>)
	 * <p>
	 *
	 * @param root the class that holds the sub class.
	 * @param name the name of the sub class.
	 *
	 * @return the member sub class.
	 *
	 * @throws ClassNotFoundException if the sub class doesn't exist at the {@code root} class.
	 */
	public static Class < ? > getSubClass ( Class < ? > root , String name ) throws ClassNotFoundException {
		try {
			return getSubClass ( root , name , true );
		} catch ( ClassNotFoundException ex ) {
			try {
				return getSubClass ( root , name , false );
			} catch ( ClassNotFoundException ex_b ) {
			}
		}
		throw new ClassNotFoundException ( "the sub class " + name + " doesn't exist!" );
	}
	
	/**
	 * Gets a class within the craftbukkit package ( {@value #CRAFT_CLASSES_PACKAGE} ) or within a sub-package of it.
	 * <p>
	 *
	 * @param name         the name of the class to get.
	 * @param package_name the name of the sub-package or null if the class is not within a sub-package.
	 *
	 * @return the class with the provided name.
	 *
	 * @throws ClassNotFoundException it the class doesn't exist.
	 */
	public static Class < ? > getCraftClass ( String name , String package_name ) throws ClassNotFoundException {
		return Class.forName ( CRAFT_CLASSES_PACKAGE + Version.getServerVersion ( ).name ( ) + "."
									   + ( StringUtils.isBlank ( package_name ) ? "" :
				package_name.toLowerCase ( ) + "." ) + name );
	}
	
	/**
	 * Gets a class within the minecraft server ( {@value #NMS_CLASSES_PACKAGE} ) package.
	 * <p>
	 *
	 * @param name the name of the class to get.
	 *
	 * @return the class with the provided name.
	 *
	 * @throws ClassNotFoundException it the class doesn't exist.
	 */
	public static Class < ? > getNmsClass ( String name ) throws ClassNotFoundException {
		return Class.forName ( NMS_CLASSES_PACKAGE + Version.getServerVersion ( ).name ( ) + "." + name );
	}
	
	/**
	 * Gets a class within the net.minecraft ( {@value #NM_CLASSES_PACKAGE} ) package.
	 * <p>
	 *
	 * @param name the name of the class to get.
	 *
	 * @return the class with the provided name.
	 *
	 * @throws ClassNotFoundException it the class doesn't exist.
	 */
	public static Class < ? > getNmClass ( String name ) throws ClassNotFoundException {
		return Class.forName ( NM_CLASSES_PACKAGE + name );
	}
	
	/**
	 * Gets a class within the net.minecraft package ( {@value #NM_CLASSES_PACKAGE} ) or within a sub-package of it.
	 * <p>
	 *
	 * @param name         the name of the class to get.
	 * @param package_name the name of the sub-package or null if the class is not within a sub-package.
	 *
	 * @return the class with the provided name.
	 *
	 * @throws ClassNotFoundException it the class doesn't exist.
	 */
	public static Class < ? > getNmClass ( String name , String package_name ) throws ClassNotFoundException {
		return Class.forName ( NM_CLASSES_PACKAGE + ( StringUtils.isBlank ( package_name )
				? "" : package_name.toLowerCase ( ) + "." ) + name );
	}
	
	/**
	 * Gets a class by name, within the package of the server (<b>'net.minecraft'</b> or <b>'net.minecraft.server
	 * .%version%',</b> depending on the version).
	 *
	 * @param name the name of the class.
	 * @param package_name the name of the sub-package the class is within.
	 * @return the class represented by the provided name.
	 * @throws ClassNotFoundException if the class was not found.
	 */
	public static Class < ? > getMinecraftClass ( String name , String package_name ) throws ClassNotFoundException {
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
			return getNmClass ( name , package_name );
		} else {
			return getNmsClass ( name );
		}
	}
	
	/**
	 * Gets a class by name, within the package of the server (<b>'net.minecraft'</b> or <b>'net.minecraft.server
	 * .%version%',</b> depending on the version).
	 *
	 * @param name the name of the class.
	 * @return the class represented by the provided name.
	 * @throws ClassNotFoundException if the class was not found.
	 */
	public static Class < ? > getMinecraftClass ( String name ) throws ClassNotFoundException {
		return getMinecraftClass ( name , null );
	}
	
	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 *
	 * @return The classes
	 *
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Class < ? >[] getClasses ( String packageName )
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread ( ).getContextClassLoader ( );
		
		assert classLoader != null;
		String              path      = packageName.replace ( '.' , '/' );
		Enumeration < URL > resources = classLoader.getResources ( path );
		List < File >       dirs      = new ArrayList < File > ( );
		while ( resources.hasMoreElements ( ) ) {
			URL resource = resources.nextElement ( );
			dirs.add ( new File ( resource.getFile ( ) ) );
		}
		ArrayList < Class < ? > > classes = new ArrayList < Class < ? > > ( );
		for ( File directory : dirs ) {
			classes.addAll ( findClasses ( directory , packageName ) );
		}
		return classes.toArray ( new Class[ classes.size ( ) ] );
	}
	
	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 *
	 * @return The classes
	 *
	 * @throws ClassNotFoundException
	 */
	public static List < Class < ? > > findClasses ( File directory , String packageName )
			throws ClassNotFoundException {
		List < Class < ? > > classes = new ArrayList < Class < ? > > ( );
		if ( !directory.exists ( ) ) {
			return classes;
		}
		File[] files = directory.listFiles ( );
		for ( File file : files ) {
			if ( file.isDirectory ( ) ) {
				assert !file.getName ( ).contains ( "." );
				classes.addAll ( findClasses ( file , packageName + "." + file.getName ( ) ) );
			} else if ( file.getName ( ).endsWith ( ".class" ) ) {
				classes.add ( Class.forName (
						packageName + '.' + file.getName ( ).substring ( 0 , file.getName ( ).length ( ) - 6 ) ) );
			}
		}
		return classes;
	}
	
	/**
	 * Scans the names of all the classes within a package contained by the provided
	 * <strong>{@code .jar}</strong>.
	 * <p>
	 *
	 * @param jarFile     the file that represents the .jar
	 * @param packageName the name of the desired package that contains the classes to get, or null to get all the
	 *                    classes contained by the .jar
	 *
	 * @return a set with the name of the classes.
	 */
	public static Set < String > getClassNames ( File jarFile , String packageName ) {
		Set < String > names = new HashSet <> ( );
		try {
			JarFile file = new JarFile ( jarFile );
			for ( Enumeration < JarEntry > entry = file.entries ( ) ; entry.hasMoreElements ( ) ; ) {
				JarEntry jarEntry = entry.nextElement ( );
				String   name     = jarEntry.getName ( ).replace ( "/" , "." );
				if ( ( packageName == null || packageName.trim ( ).isEmpty ( ) || name.startsWith (
						packageName.trim ( ) ) )
						&& name.endsWith ( ".class" ) ) {
					names.add ( name.substring ( 0 , name.lastIndexOf ( ".class" ) ) );
				}
			}
			file.close ( );
		} catch ( Exception e ) {
			e.printStackTrace ( );
		}
		return names;
	}
	
	/**
	 * Scans all the classes within a package contained by the provided
	 * <strong>{@code .jar}</strong>.
	 * <p>
	 *
	 * @param jarFile     the file that represents the .jar
	 * @param packageName the name of the desired package that contains the classes to get, or null to get all the
	 *                    classes contained by the .jar
	 *
	 * @return a set with the scanned classes.
	 */
	public static Set < Class < ? > > getClasses ( File jarFile , String packageName ) {
		Set < Class < ? > > classes = new HashSet < Class < ? > > ( );
		getClassNames ( jarFile , packageName ).forEach ( class_name -> {
			try {
				classes.add ( Class.forName ( class_name ) );
			} catch ( ClassNotFoundException e ) {
				e.printStackTrace ( );
			}
		} );
		return classes;
	}
}
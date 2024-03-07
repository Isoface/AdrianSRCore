package es.outlook.adriansrj.core.plugin.custom;

import es.outlook.adriansrj.core.dependency.MavenDependency;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 18/08/2021 / Time: 05:00 p. m.
 */
@Deprecated
public class PluginCustomDescription {
	
	protected static final String PLUGIN_JAR_YML_ENTRY_NAME = "plugin.yml";
	protected static final String COMPILATION_ID_KEY        = "compid";
	protected static final String LIBRARIES_KEY             = "libraries";
	
	protected final PluginDescriptionFile   handle;
	protected final Set < String >          libraries_coordinates = new LinkedHashSet <> ( );
	protected final Set < MavenDependency > libraries             = new LinkedHashSet <> ( );
	protected       String                  compilation_id;
	
	public PluginCustomDescription ( InputStream input ) throws InvalidDescriptionException {
		byte[] bytes = toByteArray ( input );
		
		this.handle = new PluginDescriptionFile ( input );
		this.loadExtras ( new ByteArrayInputStream ( bytes ) );
		
		try {
			input.close ( );
		} catch ( IOException ex ) {
			ex.printStackTrace ( );
		}
	}
	
	public PluginCustomDescription ( File file ) throws InvalidDescriptionException {
		JarFile     jar_file = null;
		InputStream input    = null;
		
		try {
			jar_file = new JarFile ( file );
			ZipEntry description_entry = jar_file.getEntry ( PLUGIN_JAR_YML_ENTRY_NAME );
			
			if ( description_entry != null ) {
				input = jar_file.getInputStream ( description_entry );
			} else {
				throw new InvalidDescriptionException (
						new FileNotFoundException ( "Jar does not contain " + PLUGIN_JAR_YML_ENTRY_NAME ) );
			}
		} catch ( IOException ex ) {
			throw new InvalidDescriptionException ( ex );
		}
		
		byte[] bytes = toByteArray ( input );
		this.handle = new PluginDescriptionFile ( new ByteArrayInputStream ( bytes ) );
		
		this.loadExtras ( new ByteArrayInputStream ( bytes ) );
		
		// then closing jar file
		if ( jar_file != null ) {
			try {
				jar_file.close ( );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
		
		if ( input != null ) {
			try {
				input.close ( );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	protected byte[] toByteArray ( InputStream input ) {
		ByteArrayOutputStream output = new ByteArrayOutputStream ( );
		byte[]                result = null;
		
		try {
			byte[] buffer = new byte[ 1024 ];
			int    len;
			
			while ( ( len = input.read ( buffer ) ) > -1 ) {
				output.write ( buffer , 0 , len );
			}
			
			output.flush ( );
			result = output.toByteArray ( );
			output.close ( );
		} catch ( Exception ex ) {
			ex.printStackTrace ( );
		} finally {
			try {
				output.close ( );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
		
		return result;
	}
	
	protected void loadExtras ( InputStream input ) {
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration ( new InputStreamReader ( input ) );
		
		// loading compilation id
		this.compilation_id = yaml.getString ( COMPILATION_ID_KEY , String.valueOf ( 0 ) );
		
		// loading libraries
		for ( String coordinates : yaml.getStringList ( LIBRARIES_KEY ) ) {
			libraries_coordinates.add ( coordinates );
			libraries.add ( new MavenDependency ( coordinates ) );
		}
	}
	
	public PluginDescriptionFile getHandle ( ) {
		return handle;
	}
	
	public String getCompilationID ( ) {
		return compilation_id;
	}
	
	public Set < String > getLibrariesCoordinates ( ) {
		return libraries_coordinates;
	}
	
	public Set < MavenDependency > getLibraries ( ) {
		return libraries;
	}
}

package es.outlook.adriansrj.core.util.yaml.comment;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.Field;
import java.util.logging.Level;

/**
 * TODO: Description
 * <p>
 *
 * @author AdrianSR / Thursday 05 August, 2021 / 09:40 PM
 */
public class YamlConfigurationComments extends YamlConfiguration {
	
	/**
	 * Creates a new {@link YamlConfigurationComments}, loading from the given file.
	 * <p>
	 * Any errors loading the Configuration will be logged and then ignored. If the specified input is not a valid
	 * config, a blank config will be returned.
	 * <p>
	 * The encoding used may follow the system dependent default.
	 *
	 * @param file Input file
	 *
	 * @return Resulting configuration
	 *
	 * @throws IllegalArgumentException Thrown if file is null
	 */
	public static YamlConfigurationComments loadConfiguration ( File file ) {
		Validate.notNull ( file , "File cannot be null" );
		
		YamlConfigurationComments config = new YamlConfigurationComments ( );
		
		try {
			config.load ( file );
		} catch ( FileNotFoundException ex ) {
			Bukkit.getLogger ( ).log ( Level.SEVERE , "file not found " + file , ex );
		} catch ( IOException ex ) {
			Bukkit.getLogger ( ).log ( Level.SEVERE , "Cannot load " + file , ex );
		} catch ( InvalidConfigurationException ex ) {
			Bukkit.getLogger ( ).log ( Level.SEVERE , "Cannot load " + file , ex );
		}
		
		return config;
	}
	
	/**
	 * Creates a new {@link YamlConfigurationComments}, loading from the given reader.
	 * <p>
	 * Any errors loading the Configuration will be logged and then ignored. If the specified input is not a valid
	 * config, a blank config will be returned.
	 *
	 * @param reader input
	 *
	 * @return resulting configuration
	 *
	 * @throws IllegalArgumentException Thrown if stream is null
	 */
	public static YamlConfigurationComments loadConfiguration ( Reader reader ) {
		Validate.notNull ( reader , "Stream cannot be null" );
		
		YamlConfigurationComments config = new YamlConfigurationComments ( );
		
		try {
			config.load ( reader );
		} catch ( IOException ex ) {
			Bukkit.getLogger ( ).log ( Level.SEVERE , "Cannot load configuration from stream" , ex );
		} catch ( InvalidConfigurationException ex ) {
			Bukkit.getLogger ( ).log ( Level.SEVERE , "Cannot load configuration from stream" , ex );
		}
		
		return config;
	}
	
	@Override
	public String saveToString ( ) {
		try {
			return new YamlCommentDumper ( options ( ) , options ( ).comment_mapper ,
										   new StringReader ( dump ( ) ) ).dump ( );
		} catch ( IOException e ) {
			e.printStackTrace ( );
			return null;
		}
	}
	
	protected String dump ( ) {
		this.getDumperOptions ( ).setIndent ( this.options ( ).indent ( ) );
		
		try {
			this.getDumperOptions ( ).setIndicatorIndent ( this.options ( ).indent ( ) );
		} catch ( NoSuchMethodError ex ) {
			// some servers are using an old
			// version of the snakeyaml
		}
		
		this.getDumperOptions ( ).setAllowUnicode ( this.options ( ).isUnicode ( ) );
		this.getDumperOptions ( ).setDefaultFlowStyle ( DumperOptions.FlowStyle.BLOCK );
		this.getRepresenter ( ).setDefaultFlowStyle ( DumperOptions.FlowStyle.BLOCK );
		
		String dump = ( ( Yaml ) extract ( "yaml" ) ).dump ( this.getValues ( false ) );
		
		if ( dump.equals ( YamlConfiguration.BLANK_CONFIG ) ) {
			dump = "";
		}
		
		return dump;
	}
	
	protected DumperOptions getDumperOptions ( ) {
		return ( DumperOptions ) extract ( "yamlOptions" );
	}
	
	protected Representer getRepresenter ( ) {
		return ( Representer ) extract ( "yamlRepresenter" );
	}
	
	protected Object extract ( String field_name ) {
		try {
			Field field = YamlConfiguration.class.getDeclaredField ( field_name );
			field.setAccessible ( true );
			
			return field.get ( this );
		} catch ( NoSuchFieldException | IllegalAccessException e ) {
			e.printStackTrace ( );
		}
		return null;
	}
	
	@Override
	public void loadFromString ( String contents ) throws InvalidConfigurationException {
		super.loadFromString ( contents );
		
		// then parsing comments
		parseComments ( contents );
	}
	
	@Override
	public YamlConfigurationOptionsComments options ( ) {
		if ( options == null ) {
			options = new YamlConfigurationOptionsComments ( this );
		}
		return ( YamlConfigurationOptionsComments ) options;
	}
	
	/**
	 * Parse comments from a string.
	 *
	 * @param contents Contents of a Configuration to parse.
	 *
	 * @return a comment mapper with comments parsed
	 *
	 * @throws InvalidConfigurationException if it hasn't been possible to read the contents
	 */
	protected void parseComments ( final String contents ) throws InvalidConfigurationException {
		try {
			if ( contents != null ) {
				// parsing
				YamlCommentParser parser = new YamlCommentParser ( options ( ) , new StringReader ( contents ) );
				parser.parse ( );
				
				// updating comment mapper for options
				options ( ).comment_mapper = parser;
			}
		} catch ( IOException e ) {
			throw new InvalidConfigurationException ( e );
		}
	}
}
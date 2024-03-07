package es.outlook.adriansrj.core.util.yaml.comment;

import org.bukkit.configuration.file.YamlConfigurationOptions;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * TODO: Description
 * <p>
 *
 * @author AdrianSR / Thursday 05 August, 2021 / 09:40 PM
 */
public class YamlConfigurationOptionsComments extends YamlConfigurationOptions {
	
	protected Charset           charset;
	protected YamlCommentMapper comment_mapper;
	
	protected YamlConfigurationOptionsComments ( YamlConfigurationComments configuration ) {
		super ( configuration );
		
		this.charset = StandardCharsets.UTF_8;
		// default comment mapper, responsible for storing comments.
		this.comment_mapper = new YamlCommentMapper ( this );
	}
	
	@Override
	public YamlConfigurationComments configuration ( ) {
		return ( YamlConfigurationComments ) super.configuration ( );
	}
	
	public Charset charset ( ) {
		return this.charset;
	}
	
	public YamlConfigurationOptionsComments charset ( final Charset charset ) {
		this.charset = charset;
		return this;
	}
	
	public boolean isUnicode ( ) {
		return this.charset.name ( ).startsWith ( "UTF" );
	}
	
	/**
	 * Retrieve the comment of the section or value selected by path.
	 *
	 * @param path path of desired section or value
	 *
	 * @return the comment of the section or value selected by path, or null if that path does not have any comment of
	 * this type
	 */
	public String getComment ( final String path ) {
		return comment_mapper.getComment ( path );
	}
	
	/**
	 * Adds a comment to the section or value selected by path. Comment will be indented automatically. Multi-line
	 * comments can be provided using \n character.
	 *
	 * @param path    path of desired section or value
	 * @param comment the comment to add, # symbol is not needed
	 */
	public YamlConfigurationComments comment ( String path , String comment ) {
		comment_mapper.setComment ( path , comment );
		return configuration ( );
	}
}
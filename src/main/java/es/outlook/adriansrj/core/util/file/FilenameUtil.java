package es.outlook.adriansrj.core.util.file;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.Validate;

/**
 * Useful class for dealing with file names.
 * <p>
 * @author AdrianSR / Thursday 16 April, 2020 / 01:23 PM
 */
public class FilenameUtil extends FilenameUtils {
	
	/**
	 * Gets the name of the provided file excluding its extension.
	 * <p>
	 * @param file the file to get.
	 * @return the name of the file excluding its extension.
	 */
	public static String getBaseName ( File file ) {
		return getBaseName ( file.getName ( ) );
	}
	
	/**
	 * Gets the names of all the elements within desired {@code directory},
	 * including the files within the sub-directories of the desired directory.
	 * <p>
	 * @param directory the desired directory.
	 * @return the names of all the elements within the directory and its
	 *         sub-directories.
	 */
	public static Set < String > getElementNames ( File directory ) {
		Validate.isTrue ( directory.isDirectory ( ) , "the provided file is not a valid directory!" );
		
		final Set < String > elements = new HashSet <> ( );
		for ( File file : directory.listFiles ( ) ) {
			if ( file.isDirectory ( ) ) {
				elements.addAll ( getElementNames ( file ) ); // recursive.
			} else {
				elements.add ( getBaseName ( file.getName ( ) ) );
			}
		}
		return elements;
	}
	
	/**
	 * Fixes the provided path by removing redundant path information before the "plugins" folder.
	 * <br>
	 * Example:
	 * <br>
	 * <b>C:/Users/PC/Documents/Servers/MyServer/plugins/AdrianSRCore</b>
	 * <br>
	 * Will result in:
	 * <br>
	 * <b>plugins/AdrianSRCore</b>
	 *
	 * @param path the path to fix.
	 * @return the resulting path.
	 */
	public static String filePathSubPlugins ( String path ) {
		int index = path.indexOf ( "plugins" );
		
		if ( index != -1 ) {
			return path.substring ( index );
		} else {
			return path;
		}
	}
}
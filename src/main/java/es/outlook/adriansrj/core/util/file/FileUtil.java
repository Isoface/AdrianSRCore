package es.outlook.adriansrj.core.util.file;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;

/**
 * Useful class for dealing with files.
 * <p>
 * @author AdrianSR / Thursday 16 April, 2020 / 12:57 PM
 */
public class FileUtil extends FileUtils {

	/**
	 * Gets all the elements within desired {@code directory}, including the files
	 * within the sub-directories of the desired directory.
	 * <p>
	 * @param directory the desired directory.
	 * @return all the elements within the directory and its sub-directories.
	 */
	public static Set < File > getElements ( File directory ) {
		Validate.isTrue ( directory.isDirectory ( ) , "the provided file is not a valid directory!" );
		
		final Set < File > elements = new HashSet <>();
		for ( File file : directory.listFiles ( ) ) {
			if ( file.isDirectory ( ) ) {
				elements.addAll ( getElements ( file ) ); // recursive.
			} else {
				elements.add ( file );
			}
		}
		return elements;
	}
	
	/**
	 * Gets the length of the provided {@code directory}. The returned value
	 * represents the sum of the lengths of all the elements within the desired
	 * directory.
	 * <p>
	 * Also if the provided {@link File} is a file ({@link File#isFile()}), then the
	 * returned value will be the length the that file.
	 * <p>
	 * @param directory the desired directory.
	 * @return the sum of the sum of the lengths of all the elements within the
	 *         directory.
	 */
	public static long getLength ( File directory ) {
		if ( directory.isFile ( ) ) {
			return directory.length ( );
		}
		
		final Set < File > elements = getElements ( directory );
		long                 length = 0L;
		return elements.stream()
				.map ( ( e ) -> e.length ( ) )
				.reduce ( length , ( accumulator, _item ) -> accumulator + _item );
	}
}
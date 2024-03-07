package es.outlook.adriansrj.core.util.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * A filter for PNG files pathnames.
 * <p>
 * @author AdrianSR
 */
public class PngFileFilter implements FileFilter {
	
	public static final String PNG_EXTENSION = "png";

	@Override
	public boolean accept(File file) {
		return FileExtensionFilter.of(PNG_EXTENSION).accept(file);
	}
}

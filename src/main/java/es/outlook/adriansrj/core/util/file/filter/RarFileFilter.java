package es.outlook.adriansrj.core.util.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * A filter for ZIP files pathnames.
 * <p>
 * @author AdrianSR / Thursday 16 April, 2020 / 12:56 PM
 */
public class RarFileFilter implements FileFilter {
	
	public static final String RAR_EXTENSION = "rar";

	@Override
	public boolean accept ( File file ) {
		return FileExtensionFilter.of ( RAR_EXTENSION ).accept ( file );
	}
}

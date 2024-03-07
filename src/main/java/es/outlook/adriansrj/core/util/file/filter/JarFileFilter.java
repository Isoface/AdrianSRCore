package es.outlook.adriansrj.core.util.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * A filter for JAR files pathnames.
 * <p>
 * @author AdrianSR
 */
public class JarFileFilter implements FileFilter {
	
	public static final String JAR_EXTENSION = "jar";
    
    @Override
    public boolean accept(File file) {
        return FileExtensionFilter.of(JAR_EXTENSION).accept(file);
    }
}

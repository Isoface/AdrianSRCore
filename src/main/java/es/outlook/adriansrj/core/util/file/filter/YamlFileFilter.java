package es.outlook.adriansrj.core.util.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * A filter for YML files pathnames.
 * <p>
 * @author AdrianSR
 */
public class YamlFileFilter implements FileFilter {
	
	public static final String YML_EXTENSION = "yml";
    
    @Override
    public boolean accept(File file) {
        return FileExtensionFilter.of ( YML_EXTENSION ).accept ( file );
    }
}
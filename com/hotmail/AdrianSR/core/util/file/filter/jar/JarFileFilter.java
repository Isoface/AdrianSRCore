package com.hotmail.AdrianSR.core.util.file.filter.jar;

import java.io.File;
import java.io.FileFilter;

import com.hotmail.AdrianSR.core.util.file.filter.CustomFileExtensionFilter;

/**
 * A filter for abstract JAR files pathnames.
 * <p>
 * @author AdrianSR
 */
public class JarFileFilter implements FileFilter {
	
	public static final String JAR_EXTENSION = "jar";
    
    @Override
    public boolean accept(File file) {
        return CustomFileExtensionFilter.of(JAR_EXTENSION).accept(file);
    }
}

package com.hotmail.AdrianSR.core.util.file.filter.yml;

import java.io.File;
import java.io.FileFilter;

import com.hotmail.AdrianSR.core.util.file.filter.CustomFileExtensionFilter;

/**
 * A filter for abstract YML files pathnames.
 * <p>
 * @author AdrianSR
 */
public class YmlFileFilter implements FileFilter {
	
	public static final String YML_EXTENSION = "yml";
    
    @Override
    public boolean accept(File file) {
        return CustomFileExtensionFilter.of(YML_EXTENSION).accept(file);
    }
}
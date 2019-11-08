package com.hotmail.AdrianSR.core.util.file.filter.png;

import java.io.File;
import java.io.FileFilter;

import com.hotmail.AdrianSR.core.util.file.filter.CustomFileExtensionFilter;

/**
 * A filter for abstract PNG files pathnames.
 * <p>
 * @author AdrianSR
 */
public class PngFileFilter implements FileFilter {
	
	public static final String PNG_EXTENSION = "png";

	@Override
	public boolean accept(File file) {
		return CustomFileExtensionFilter.of(PNG_EXTENSION).accept(file);
	}
}

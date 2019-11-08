package com.hotmail.AdrianSR.core.util.file.filter.gif;

import java.io.File;
import java.io.FileFilter;

import com.hotmail.AdrianSR.core.util.file.filter.CustomFileExtensionFilter;

/**
 * A filter for abstract GIF files pathnames.
 * <p>
 * @author AdrianSR
 */
public class GifFileFilter implements FileFilter {
	
	public static final String GIF_EXTENSION = "gif";

	@Override
	public boolean accept(File file) {
		return CustomFileExtensionFilter.of(GIF_EXTENSION).accept(file);
	}
}

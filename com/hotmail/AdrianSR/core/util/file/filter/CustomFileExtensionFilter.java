package com.hotmail.AdrianSR.core.util.file.filter;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * A filter for abstract pathnames.
 * <p> 
 * Instances of this abstract class may be passed to the <code>{@link
 * File#listFiles(java.io.FileFilter) listFiles(FileFilter)}</code> method
 * of the <code>{@link java.io.File}</code> class.
 * <p>
 * @author AdrianSR
 */
public abstract class CustomFileExtensionFilter implements FileFilter {
	
	/**
	 * Returns a new {@link CustomFileExtensionFilter} for the given
	 * file extension.
	 * <p>
	 * @param file_extension the file extension. Example: '.jar'
	 * @return the {@link CustomFileExtensionFilter} for the given file extension.
	 */
	public static CustomFileExtensionFilter of(final String file_extension) {
		return new CustomFileExtensionFilter() {
			
			@Override
			public boolean accept(File file) {
				return Pattern.compile("\\" + (file_extension.startsWith(".") ? file_extension.toLowerCase()
						: ("." + file_extension.toLowerCase())) + "$").matcher(file.getName()).find();
			}
		};
	}
}

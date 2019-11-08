package com.hotmail.AdrianSR.core.util.file.filter.directory;

import java.io.File;
import java.io.FileFilter;

public class FileDirectoryFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isDirectory();
	}
}

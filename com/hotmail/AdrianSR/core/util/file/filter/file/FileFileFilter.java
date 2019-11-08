package com.hotmail.AdrianSR.core.util.file.filter.file;

import java.io.File;
import java.io.FileFilter;

public class FileFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isFile();
	}
}

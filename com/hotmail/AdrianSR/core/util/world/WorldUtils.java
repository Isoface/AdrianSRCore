package com.hotmail.AdrianSR.core.util.world;

import java.io.File;

import com.hotmail.AdrianSR.core.util.file.FileUtils;

public class WorldUtils {
	
	public static final String LEVEL_DATA_FILE_NAME = "level.dat";
	public static final String   REGION_FOLDER_NAME = "region";

	public static boolean isValidWorldFolder(File world_folder) {
		if (!world_folder.isDirectory() || !new File(world_folder, LEVEL_DATA_FILE_NAME).isFile()) {
			return false;
		}
		
		File region_folder = new File(world_folder, REGION_FOLDER_NAME);
		if (FileUtils.calculateLength(region_folder) <= 0L) {
			return false;
		}
		return true;
	}
}

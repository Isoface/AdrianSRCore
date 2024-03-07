package es.outlook.adriansrj.core.util.world;

import org.bukkit.World;

import java.io.File;

/**
 * Useful class for dealing with worlds.
 * <p>
 *
 * @author AdrianSR / Tuesday 28 July, 2020 / 05:55 PM
 */
public class WorldUtil {
	
	public static final String LEVEL_DATA_FILE_NAME     = "level.dat";
	public static final String LEVEL_DATA_OLD_FILE_NAME = "level.dat_old";
	public static final String REGION_FOLDER_NAME       = "region";
	
	public static boolean worldFolderCheck ( File world_folder ) {
		if ( world_folder.isDirectory ( ) && world_folder.exists ( ) ) {
			File dat    = new File ( world_folder , LEVEL_DATA_FILE_NAME );
			File region = new File ( world_folder , REGION_FOLDER_NAME );
			
			return dat.exists ( ) && region.exists ( )
					&& region.isDirectory ( ) && region.listFiles ( ).length > 0;
			//			return new File ( world_folder , LEVEL_DATA_FILE_NAME ).exists ( )
			//					&& new File ( world_folder , REGION_FOLDER_NAME ).isDirectory ( );
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the minimum height of the provided world.
	 * <br>
	 * the <b>getMinHeight</b> method was not implemented
	 * in legacy Minecraft versions; that's why this method
	 * results useful.
	 *
	 * @param world the world to get.
	 * @return the minimum height of the provided world.
	 */
	public static int getMinHeight ( World world ) {
		try {
			return world.getMinHeight ( );
		} catch ( NoSuchMethodError ex ) {
			// legacy world
			return 0;
		}
	}
}

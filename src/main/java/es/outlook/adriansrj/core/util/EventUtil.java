package es.outlook.adriansrj.core.util;

import org.bukkit.event.block.Action;

/**
 * Useful class for dealing with events.
 * <p>
 * @author AdrianSR / Tuesday 28 July, 2020 / 06:47 PM
 */
public class EventUtil {

	public static boolean isRightClick(Action action) {
		return action != null && ( action.name ( ).contains ( "RIGHT_" ) );
	}

	public static boolean isLeftClick(Action action) {
		return action != null && ( action.name ( ).contains ( "LEFT_" ) );
	}
	
	public static boolean isClickingBlock(Action action) {
		return action != null && ( action.name ( ).contains ( "_BLOCK" ) );
	}
	
	public static boolean isClickingAir(Action action) {
		return action != null && ( action.name ( ).contains ( "_AIR" ) );
	}
}

package com.hotmail.AdrianSR.core.util;

import org.bukkit.event.block.Action;

public class EventUtils {

	public static boolean isRightClick(Action action) {
		return action != null ? (action.name().contains("RIGHT_")) : false;
	}

	public static boolean isLeftClick(Action action) {
		return action != null ? (action.name().contains("LEFT_")) : false;
	}
	
	public static boolean isClickingBlock(Action action) {
		return action != null ? (action.name().contains("_BLOCK")) : false;
	}
	
	public static boolean isClickingAir(Action action) {
		return action != null ? (action.name().contains("_AIR")) : false;
	}
}

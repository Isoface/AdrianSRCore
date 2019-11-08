package com.hotmail.AdrianSR.core.util;

public class ColorUtils {
	
	public static java.awt.Color getJavaColorFromID(int id) {
		switch (id) {
		case 1:
			return java.awt.Color.WHITE;
		case 2:
			return java.awt.Color.YELLOW;
		case 3:
			return java.awt.Color.BLACK;
		case 4:
			return java.awt.Color.BLUE;
		case 5:
			return java.awt.Color.GRAY;
		case 6:
			return java.awt.Color.GREEN;
		case 7:
			return java.awt.Color.ORANGE;
		case 8:
			return java.awt.Color.RED;
		}
		return java.awt.Color.WHITE;
	}

	public static org.bukkit.Color getColorFromID(int id) {
		switch (id) {
		case 1:
			return org.bukkit.Color.TEAL;
		case 2:
			return org.bukkit.Color.WHITE;
		case 3:
			return org.bukkit.Color.YELLOW;
		case 4:
			return org.bukkit.Color.AQUA;
		case 5:
			return org.bukkit.Color.BLACK;
		case 6:
			return org.bukkit.Color.BLUE;
		case 7:
			return org.bukkit.Color.FUCHSIA;
		case 8:
			return org.bukkit.Color.GRAY;
		case 9:
			return org.bukkit.Color.GREEN;
		case 10:
			return org.bukkit.Color.LIME;
		case 11:
			return org.bukkit.Color.MAROON;
		case 12:
			return org.bukkit.Color.NAVY;
		case 13:
			return org.bukkit.Color.OLIVE;
		case 14:
			return org.bukkit.Color.ORANGE;
		case 15:
			return org.bukkit.Color.PURPLE;
		case 16:
			return org.bukkit.Color.RED;
		}
		return org.bukkit.Color.WHITE;
	}
}
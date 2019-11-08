package com.hotmail.AdrianSR.core.util.tps;

import org.bukkit.ChatColor;

import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.util.PrintUtils;
import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;

public final class TPS {
	
	/**
	 * Get recent Server Ticks per second.
	 * <p>
	 * @param plugin the CustomPlugin instance.
	 * @return recent Server Ticks per second.
	 */
	public static long getTicksPerSecond(final CustomPlugin plugin) {
		try {
			// make new StringBuilder.
			final StringBuilder builder = new StringBuilder();
			
			// append tps in builder.
			for (double tps : readRecentTps(plugin)) {
				builder.append(format(tps));
				builder.append(", ");
			}
			
			// get builder result.
			final String builder_result = builder.substring(0, (builder.length() - 2));
			
			// get tps.
			return Long.valueOf(builder_result.split(",")[0].trim()).longValue();
		} catch (Throwable t) {
			PrintUtils.print(ChatColor.RED, "Could not get Server tps: ", plugin);
			t.printStackTrace();
			return 0L;
		}
	}
	
	/**
	 * Get MinecraftServer class.
	 * <p>
	 * @return Get MinecraftServer class.
	 */
	private static Class<?> getMinecrafServer() {
		return ReflectionUtils.getCraftClass("MinecraftServer");
	}
	
	/**
	 * Read recent MinecrafServer ticks per second.
	 * <p>
	 * @param plugin the CustomPlugin instance.
	 * @return recent MinecraftServer ticks per second.
	 */
	private static double[] readRecentTps(final CustomPlugin plugin) {
		try {
			return (double[]) getMinecrafServer().getField("recentTps").get(getMinecrafServer());
		} catch (Throwable t) {
			PrintUtils.print(ChatColor.RED, "Could not get Server tps: ", plugin);
			t.printStackTrace();
			return new double[0];
		}
	}
	
	/**
	 * @param tps getted tps from query.
	 * @return the tps with a format.
	 */
	private static String format(double tps) {
		return ((tps > 18.0) ? "" : (tps > 16.0) ? "" : "").toString() + ((tps > 20.0) ? "" : "")
				+ Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
	}
}
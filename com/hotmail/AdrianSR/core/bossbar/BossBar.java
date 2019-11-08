package com.hotmail.AdrianSR.core.bossbar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.bossbar.versions.latest.CustomBossBar;
import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.util.ClientVersion;
import com.hotmail.AdrianSR.core.util.Schedulers;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;

/**
 * Represets a custom
 * Boss Bar for Players.
 * <p>
 * @author AdrianSR
 */
public interface BossBar {
	
	/**
	 * Global class values.
	 */
	public static float MIN_BOSS_BAR_PROGRESS = 0.00000000000000000000001F; // 3.0E-21F;
	public static float MAX_BOSS_BAR_PROGRESS = 1.0F;
	
	/**
	 * All Players Boss Bars.
	 */
	public static final Map<UUID, BossBar> CACHE = new HashMap<UUID, BossBar>();
	
	/**
	 * Initialize Boss Bars managament.
	 */
	public static void initialize(final CustomPlugin plugin) {
		// start updater task.
		Schedulers.syncRepeating(() -> {
			for (BossBar bar : CACHE.values()) {
				// update bar location, name and progress.
				bar.update();
			}
		}, 0, plugin);
	}
	
	/**
	 * Get a new {@link BossBar}
	 * for a specific {@link Player}.
	 * <p>
	 * @param viewer the Player viewer.
	 * @param title the Message to show.
	 * @param progress the Progress to show. This must be between 0.0D and 1.0D.
	 */
	public static BossBar createBossBar(final Player viewer, final String title, final double progress) {
		Validate.isTrue((progress >= 0.0D && progress <= 1.0D), "Progress must be between 0.0 and 1.0 (" + progress + ")!");

		// check is not already have.
		if (CACHE.containsKey(viewer.getUniqueId())) { // check contains key.
			if (CACHE.get(viewer.getUniqueId()) != null) { // check is valid BossBar.
				// get cached.
				final BossBar bar = CACHE.get(viewer.getUniqueId());
				
				// update data.
				bar.setTitle(title);
				bar.setProgress(progress);
				
				// return cached.
				return bar;
			}
		}
		
		// get new custom boss bar.
		if (ServerVersion.serverNewerEqualsThan(ServerVersion.v1_9_R1)) {
			final BossBar bar = new CustomBossBar(viewer, title, (float) progress);
			// save bar in cache.
			CACHE.put(viewer.getUniqueId(), bar);
			return bar;
		} else { /* get boss bar depending of version */
			try {
				// get class from version.
				final Class<? extends BossBar> getter = Class.forName("com.hotmail.AdrianSR.core.bossbar.versions."
						+ ServerVersion.getVersion().toString() + ".CustomBossBar").asSubclass(BossBar.class);
				
				// make BossBar
				final BossBar bar = getter.getConstructor(Player.class, String.class, float.class)
						.newInstance(viewer, title, (float) progress);
				
				// save bar in cache.
				CACHE.put(viewer.getUniqueId(), bar);
				return bar;
			} catch (Throwable t) { /* unsupported */
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error creating BossBars! Unsupported Server Version!");
				t.printStackTrace();
				return null;
			}
		}
	}
	
    /**
     * Sets the title of this boss bar
     *<p>
     * @param title the title of the bar
     */
    void setTitle(String title);

    /**
     * Sets the progress of the bar. Values should be between 0.0 (empty) and
     * 1.0 (full)
     *<p>
     * @param progress the progress of the bar
     */
    void setProgress(double progress);
    
    /**
     * Set if the boss bar is displayed to attached players.
     *<p>
     * @param visible visible status
     */
    void setVisible(boolean visible);
    
    /**
     * Update BossBar.
     */
    void update();
    
    /**
     * Returns the title of this boss bar
     *<p>
     * @return the title of the bar
     */
    String getTitle();

    /**
     * Returns the progress of the bar between 0.0 and 1.0
     *<p>
     * @return the progress of the bar
     */
    double getProgress();
    
    /**
     * Returns the players viewer of this boss bar
     *<p>
     * @return the Player viewer.
     */
    Player getPlayer();

    /**
     * Return if the boss bar is displayed to attached players.
     *<p>
     * @return visible status
     */
    boolean isVisible();
    
    /**
     * Destroy this BossBar.
     */
    void destroy();
    
    /**
     * Returns the color of this boss bar
     *<p>
     * @return the color of the bar
     */
    BarColor getColor();

    /**
     * Sets the color of this boss bar.
     *<p>
     * @param color the color of the bar
     */
    void setColor(BarColor color);
    
    /**
     * Returns the style of this boss bar
     *<p>
     * @return the style of the bar
     */
    BarStyle getStyle();

    /**
     * Sets the bar style of this boss bar
     *<p>
     * @param style the style of the bar
     */
    void setStyle(BarStyle style);

    /**
     * Remove an existing flag on this boss bar
     *<p>
     * @param flag the existing flag to remove
     */
    void removeFlag(BarFlag flag);

    /**
     * Add an optional flag to this boss bar
     *<p>
     * @param flag an optional flag to set on the boss bar
     */
    void addFlag(BarFlag flag);
    
    /**
     * Returns whether this boss bar as the passed flag set
     *<p>
     * @param flag the flag to check
     * @return whether it has the flag
     */
    boolean hasFlag(BarFlag flag);
    
    /**
     * Get the distance between
     * {@link Player} viewer, and
     * the Wither.
     * <p>
     * @return the distance depending
     * of the player client version.
     */
    default double getDistanceBetween() {
    	// get client version getter.
    	final ClientVersion getter = new ClientVersion(getPlayer());
    	// return distance depending of version number.
		return (getter.get() > 47 ? 35 : 20);
    }
    
    /**
     * Get the wither location
     * with distance between player
     * and it'self.
     * <p>
     * @return the Wither location to set.
     */
    default Location getWitherLocation() {
    	final Location  player = getPlayer().getLocation();
    	final Vector direction = player.getDirection().multiply(getDistanceBetween()); /* get player direction */
    	
    	// return wither location.
		return player.add(direction);
    }
}

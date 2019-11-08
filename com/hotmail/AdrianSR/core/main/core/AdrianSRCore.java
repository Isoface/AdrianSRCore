package com.hotmail.AdrianSR.core.main.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.bossbar.BossBar;
import com.hotmail.AdrianSR.core.logger.combo.ComboLogger;
import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.riding.movables.Movable;
import com.hotmail.AdrianSR.core.util.dependence.PluginDependence;
import com.hotmail.AdrianSR.core.util.lang.CustomPluginLanguageEnumContainer;
import com.hotmail.AdrianSR.core.util.world.rule.manager.GameRuleManager;
import com.hotmail.AdrianSR.core.version.CoreVersion;

/**
 * A Core plugin developed
 * by AdrianSR.
 * <p>
 * @author AdrianSR
 */
public final class AdrianSRCore extends CustomPlugin {
	
	/**
	 * Update checker data.
	 */
	private static final String SPIGOT_RESOURCES_VERSION_ULR = "https://api.spigotmc.org/legacy/update.php?resource=";
	private static final String             CORE_RESOURCE_ID = "64289";
	
	/**
	 * Core plugin instance.
	 * <p>
	 * @return core plugin instance.
	 */
	public static AdrianSRCore getInstance() {
		return CustomPlugin.getCustomPlugin(AdrianSRCore.class);
	}

	@Override
	public boolean setUp() {
		ComboLogger logger = getComboLogger();
		try {
			logger.log(Level.INFO, "You are using the version " + getDescription().getVersion());
			logger.log(Level.INFO, "Checking for updates...");
			
			final URL               version_url = new URL(SPIGOT_RESOURCES_VERSION_ULR + CORE_RESOURCE_ID);
			final URLConnection      connection = version_url.openConnection();
			final String    latest_version_name = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
			CoreVersion          latest_version = CoreVersion.of(CoreVersion.formatVersionName(latest_version_name));
			if (latest_version == null || latest_version.isNewerThan(CoreVersion.getVersion())) {
				logger.log(Level.INFO, "A new version of AdrianSR Core has been found!");
			} else {
				logger.log(Level.INFO, "This is the latest version of AdrianSR Core :)");
			}
		} catch(Throwable t) { /* info: could not check for updates */
			logger.log(Level.WARNING, "Could not check for updates :(", t);
		}
		return true;
	}
	
	@Override
	public void initManagers() {
		new GameRuleManager(this);
	}
	
	@Override
	public CoreVersion getRequiredCoreVersion() {
		return null;
	}

	@Override
	public String getRequiredCoreVersionMessage() {
		return null;
	}
	
	@Override
	public PluginDependence[] getDependencies() {
		return null;
	}
	
	@Override
	public Class<? extends Enum<? extends CustomPluginLanguageEnumContainer>> getLanguageContainer() {
		return null;
	}
	
	@Override
	public String getLanguageResourcesPackage() {
		return null;
	}
	
	@Override
	public void onDisable() {
		try {
			// eject players from his vehicles.
			for (Player p : Bukkit.getOnlinePlayers()) {
				try {
					p.getVehicle().eject();
					p.leaveVehicle();
					p.eject();
				} catch(Throwable t) {
					// ignore.
				}
			}
			
			// destroy movables.
			for (Movable movable : Movable.MOVABLES) {
				if (movable != null) {
					movable.destroy();
				}
			}
			
			// destroy BossBars.
			for (BossBar bar : BossBar.CACHE.values()) {
				if (bar != null) {
					bar.destroy();
				}
			}
		}
		catch(Throwable t) {
			/* ignore */
		}
	}
}
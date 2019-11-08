package com.hotmail.AdrianSR.core.bossbar.versions.latest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.bossbar.BarColor;
import com.hotmail.AdrianSR.core.bossbar.BarFlag;
import com.hotmail.AdrianSR.core.bossbar.BarStyle;
import com.hotmail.AdrianSR.core.bossbar.BossBar;

/**
 * Represents a Custom Boss Bar 
 * in latest Spigot versions. (>= 1.9)
 * <p>
 * @author AdrianSR
 */
public class CustomBossBar implements BossBar {

	/**
	 * Class values.
	 */
	private final org.bukkit.boss.BossBar handle;

	/**
	 * Construct a new Custom Boss Bar 
	 * for a specific {@link Player}.
	 * <p>
	 * @param viewer the Player viewer.
	 * @param text the Message to show.
	 * @param progress the Progress to show.
	 */
	public CustomBossBar(final Player viewer, final String text, final float progress) {
		// make handle.
		this.handle = Bukkit.getServer().createBossBar(text, 
				org.bukkit.boss.BarColor.PINK,
				org.bukkit.boss.BarStyle.SOLID);
		
		// set progress.
		this.handle.setProgress(progress);

		// show.
		this.handle.addPlayer(viewer);
	}

	@Override
	public void setTitle(String title) {
		this.handle.setTitle(title);
	}

	@Override
	public void setProgress(double progress) {
		this.handle.setProgress(progress);
	}

	@Override
	public void setVisible(boolean visible) {
		this.handle.setVisible(visible);
	}

	@Override
	public void update() {
		// nothing.
	}

	@Override
	public String getTitle() {
		return this.handle.getTitle();
	}

	@Override
	public double getProgress() {
		return this.handle.getProgress();
	}

	@Override
	public Player getPlayer() {
		return this.handle.getPlayers().get(0);
	}

	@Override
	public boolean isVisible() {
		return this.handle.isVisible();
	}

	@Override
	public void destroy() {
		this.handle.removeAll();
	}

	@Override
	public BarColor getColor() {
		return BarColor.valueOf(this.handle.getColor().name());
	}

	@Override
	public void setColor(BarColor color) {
		this.handle.setColor(org.bukkit.boss.BarColor.valueOf(color.name()));
	}

	@Override
	public BarStyle getStyle() {
		return BarStyle.valueOf(this.handle.getStyle().name());
	}

	@Override
	public void setStyle(BarStyle style) {
		this.handle.setStyle(org.bukkit.boss.BarStyle.valueOf(style.name()));
	}

	@Override
	public void removeFlag(BarFlag flag) {
		this.handle.removeFlag(org.bukkit.boss.BarFlag.valueOf(flag.name()));
	}

	@Override
	public void addFlag(BarFlag flag) {
		this.handle.addFlag(org.bukkit.boss.BarFlag.valueOf(flag.name()));
	}

	@Override
	public boolean hasFlag(BarFlag flag) {
		return this.handle.hasFlag(org.bukkit.boss.BarFlag.valueOf(flag.name()));
	}
}
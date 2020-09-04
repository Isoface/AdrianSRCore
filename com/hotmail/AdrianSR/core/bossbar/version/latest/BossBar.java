package com.hotmail.adriansr.core.bossbar.version.latest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.hotmail.adriansr.core.bossbar.BarColor;
import com.hotmail.adriansr.core.bossbar.BarFlag;
import com.hotmail.adriansr.core.bossbar.BarStyle;
import com.hotmail.adriansr.core.bossbar.base.BossBarBase;
import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.server.Version;

/**
 * A {@code BossBar} intended for server versions <strong>{@code >=}</strong>
 * {@link Version#v1_9_R1}.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 09:48 AM
 */
public class BossBar extends BossBarBase {
	
	/**
	 * The Bukkit equivalent.
	 */
	protected final org.bukkit.boss.BossBar handle;
	
	/**
	 * Constructs the {@code BossBar}.
	 * <p>
	 * @param title the initial title.
	 * @param progress the initial progress ( must be between {@code 0.0} and {@code 1.0} ).
	 * @param player the viewer player.
	 */
	public BossBar ( String title , double progress , Player player ) {
		this.handle = Bukkit.createBossBar ( StringUtil.EMPTY , org.bukkit.boss.BarColor.PINK ,
				org.bukkit.boss.BarStyle.SOLID );
		this.handle.addPlayer ( player );
		
		this.setTitle    ( title );
		this.setProgress ( progress );
	}

	@Override
	public String getTitle() {
		return this.handle.getTitle();
	}

	@Override
	public void setTitle ( String title ) {
		this.handle.setTitle ( title );
	}

	@Override
	public BarColor getColor() {
		return BarColor.valueOf ( handle.getColor().name ( ) );
	}

	@Override
	public void setColor ( BarColor color ) {
		this.handle.setColor ( org.bukkit.boss.BarColor.valueOf ( color.name ( ) ) );
	}

	@Override
	public BarStyle getStyle() {
		return BarStyle.valueOf ( handle.getStyle().name ( ) );
	}

	@Override
	public void setStyle ( BarStyle style ) {
		this.handle.setStyle ( org.bukkit.boss.BarStyle.valueOf ( style.name ( ) ) );
	}

	@Override
	public void removeFlag ( BarFlag flag ) {
		this.handle.removeFlag ( org.bukkit.boss.BarFlag.valueOf ( flag.name ( ) ) );
	}

	@Override
	public void addFlag ( BarFlag flag ) {
		this.handle.addFlag ( org.bukkit.boss.BarFlag.valueOf ( flag.name ( ) ) );
	}

	@Override
	public boolean hasFlag ( BarFlag flag ) {
		return this.handle.hasFlag ( org.bukkit.boss.BarFlag.valueOf ( flag.name ( ) ) );
	}

	@Override
	public void setProgress ( double progress ) {
		this.checkProgress      ( progress );
		this.handle.setProgress ( progress );
	}

	@Override
	public double getProgress() {
		return this.handle.getProgress();
	}

	@Override
	public Player getPlayer() {
		return this.handle.getPlayers().get ( 0 );
	}

	@Override
	public void setVisible ( boolean visible ) {
		this.handle.setVisible (visible  );
	}

	@Override
	public boolean isVisible() {
		return this.handle.isVisible();
	}
}
package com.hotmail.adriansr.core.bossbar.version.oldest;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.hotmail.adriansr.core.bossbar.BarColor;
import com.hotmail.adriansr.core.bossbar.BarFlag;
import com.hotmail.adriansr.core.bossbar.BarStyle;
import com.hotmail.adriansr.core.bossbar.base.BossBarBase;
import com.hotmail.adriansr.core.main.AdrianSRCore;
import com.hotmail.adriansr.core.util.entity.UUIDPlayer;
import com.hotmail.adriansr.core.util.server.Version;

/**
 * Base class for {@code BossBar}s for server versions {@code <=}
 * {@link Version#v1_8_R3}.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 11:23 AM
 */
public abstract class BossBarOldest extends BossBarBase {
	
	protected static final float MINIMUM_PROGRESS = 0.00000000000000000000001F;
	protected static final float MAXIMUM_PROGRESS = 1F;
	
	protected static final String    DEFAULT_TITLE = "untitled";
	protected static final double DEFAULT_PROGRESS = MAXIMUM_PROGRESS;
	
	/** the bar's viewer */
	protected final UUIDPlayer player;
	/** the bar's title */
	protected String title = DEFAULT_TITLE;
	/** The bar's progress */
	protected double progress = DEFAULT_PROGRESS;
	/** the bar's visibility status */
	private boolean visible = true;
	/** flag to determine if the bar has been destroyed because of the player was offline */
	private boolean offline = false;
	/** the bar's update task id */
	protected final int updater_id;
	/** the bar's updater task */
	protected final Runnable updater = () -> {
		if ( isVisible ( ) ) {
			if ( getPlayer() == null || !getPlayer().isOnline() ) {
				if ( !offline ) {
					this.destroy();
					this.offline = true;
				}
			} else {
				if ( offline ) {
					this.create();
					this.offline = false;
				}
			}
			
			if ( !offline ) {
				this.update();
			}
		} else {
			this.destroy();
		}
	};
	
	public BossBarOldest ( String title , double progress , Player player ) {
		Validate.isTrue ( player.isOnline ( ) , "the player must be online!" );
		
		this.player     = new UUIDPlayer ( player );
		this.updater_id = Bukkit.getScheduler().scheduleSyncRepeatingTask ( AdrianSRCore.getInstance ( ) , updater , 0L , 0L );
		
		/* updater task scheduling failed */
		if ( updater_id == -1 ) {
			throw new IllegalStateException ( "the boss bar couln't be created!" );
		}
		
		this.setTitle    ( title );
		this.setProgress ( progress );
		this.create      ( );
	}
	
	/**
	 * Creates the bar.
	 */
	protected abstract void create();
	
	/**
	 * Updates the bar. This updates the the title and the progress of the bar.
	 */
	protected abstract void update();
	
	/**
	 * Destroys the bar.
	 */
	protected abstract void destroy();
	
	@Override
	public String getTitle() {
		return this.title;
	}
	
	@Override
	public void setTitle ( String title ) {
		this.checkTitle ( title );
		this.title = title;
	}
	
	@Override
	public double getProgress() {
		return this.progress;
	}
	
	@Override
	public void setProgress ( double progress ) {
		this.checkProgress ( progress );
		this.progress = progress;
	}
	
	@Override
	public final boolean isVisible() {
		return this.visible;
	}
	
	@Override
	public final void setVisible ( boolean visible ) {
		if ( visible != this.visible ) {
			if ( visible ) {
				this.create();
			} else {
				this.destroy();
			}
			
			this.visible = visible;
		}
	}
	
	@Override
	public final Player getPlayer() {
		return player.get ( );
	}
	
	@Override public final BarColor getColor() { return BarColor.PINK; }
	@Override public final void setColor ( BarColor color ) {}
	@Override public final BarStyle getStyle() { return BarStyle.SOLID; }
	@Override public final void setStyle ( BarStyle style ) {}
	@Override public final void removeFlag ( BarFlag flag ) {}
	@Override public final void addFlag ( BarFlag flag ) {}
	@Override public final boolean hasFlag ( BarFlag flag ) { return false; }
	
	/**
	 * Checks the provided title is valid.
	 * <p>
	 * @param title the title to check.
	 */
	protected void checkTitle ( String title ) {
		Validate.notNull ( title , "the title cannot be null!" );
	}
	
	/**
	 * Calculates and gets the location for the handle entity.
	 * <p>
	 * @return location for the handle entity.
	 */
	protected Location calculateHandleLocation() {
		return getPlayer().getLocation().add ( getPlayer().getLocation().getDirection ( )
				.multiply ( 20D /* the distance between the handle entity and the viewer player */ ) );
	}
}
package es.outlook.adriansrj.core.bossbar;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import es.outlook.adriansrj.core.util.server.Version;

/**
 * A {@code BossBar} is a bar that is displayed at the top of the screen.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 09:37 AM
 */
public abstract class BossBar {
	
	/**
	 * The package that holds the different versions of {@code BossBar}.
	 */
	protected static final String VERSIONS_PACKAGE = "es.outlook.adriansrj.core.bossbar.version.";
	
	/**
	 * Creates a boss bar instance to display to players.
	 * <p>
	 * @param player   the player viewer.
	 * @param title    the title of the boss bar.
	 * @param progress the progress of the boss bar.
	 * @param color    the color of the boss bar.
	 * @param style    the style of the boss bar.
	 * @param flags    an optional list of flags to set on the boss bar.
	 * @return the created boss bar.
	 */
	public static BossBar createBossBar ( Player player , String title , double progress , BarColor color , BarStyle style ,
			BarFlag... flags ) {
		if ( Version.getServerVersion().isNewerEquals ( Version.v1_9_R1 ) ) {
			final BossBar bossbar = new es.outlook.adriansrj.core.bossbar.version.latest.BossBar ( title , progress , player );
			bossbar.setColor ( color );
			bossbar.setStyle ( style );
			for ( BarFlag flag : flags ) {
				bossbar.addFlag ( flag );
			}
			
			return bossbar;
		}
		
		try {
			final Class < ? >                c0 = Class.forName ( VERSIONS_PACKAGE + Version.getServerVersion().name() + ".BossBar" );
			final Class < ? extends BossBar> c1 = c0.asSubclass ( BossBar.class );
			
			final BossBar bossbar = c1.getConstructor ( String.class , double.class , Player.class )
					.newInstance ( title , progress , player ); // colors, styles, flags, are not supported.
			
			return bossbar;
		} catch ( ClassNotFoundException ex ) {
			throw new UnsupportedOperationException ( "unsupported server version!" );
		} catch ( InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException ex_b ) {
			ex_b.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Creates a boss bar instance to display to players. The progress defaults to
	 * {@code 1.0}.
	 * <p>
	 * @param player the player viewer.
	 * @param title  the title of the boss bar.
	 * @param color  the color of the boss bar.
	 * @param style  the style of the boss bar.
	 * @param flags  an optional list of flags to set on the boss bar.
	 * @return the created boss bar.
	 */
	public static BossBar createBossBar ( Player player , String title , BarColor color , BarStyle style , BarFlag... flags ) {
		return createBossBar ( player , title , 1F , color , style , flags );
	}
	
	/**
	 * Creates a boss bar instance to display to players. The color defaults to
	 * {@link BarColor#PINK}. The style defaults to {@link BarStyle#SOLID}.
	 * <p>
	 * @param player   the player viewer.
	 * @param title    the title of the boss bar.
	 * @param progress the progress of the boss bar.
	 * @return the created boss bar.
	 */
	public static BossBar createBossBar ( Player player , String title , double progress ) {
		return createBossBar ( player , title , 1F , BarColor.PINK , BarStyle.SOLID );
	}
	
	/**
	 * Creates a boss bar instance to display to players. The color defaults to
	 * {@link BarColor#PINK}. The style defaults to {@link BarStyle#SOLID}. The
	 * progress defaults to {@code 1.0}.
	 * <p>
	 * @param player the player viewer.
	 * @param title  the title of the boss bar.
	 * @return the created boss bar.
	 */
	public static BossBar createBossBar ( Player player , String title ) {
		return createBossBar ( player , title , 1F );
	}
	
	/**
	 * Returns the title of this boss bar
	 * <p>
	 * @return the title of the bar
	 */
	public abstract String getTitle();

	/**
	 * Sets the title of this boss bar
	 * <p>
	 * @param title the title of the bar
	 */
	public abstract void setTitle ( String title );

	/**
	 * Returns the color of this boss bar
	 * <p>
	 * @return the color of the bar
	 */
	public abstract BarColor getColor();

	/**
	 * Sets the color of this boss bar.
	 * <p>
	 * @param color the color of the bar
	 */
	public abstract void setColor ( BarColor color );

	/**
	 * Returns the style of this boss bar
	 * <p>
	 * @return the style of the bar
	 */
	public abstract BarStyle getStyle();

	/**
	 * Sets the bar style of this boss bar
	 * <p>
	 * @param style the style of the bar
	 */
	public abstract void setStyle ( BarStyle style );

	/**
	 * Remove an existing flag on this boss bar
	 * <p>
	 * @param flag the existing flag to remove
	 */
	public abstract void removeFlag ( BarFlag flag );

	/**
	 * Add an optional flag to this boss bar
	 * <p>
	 * @param flag an optional flag to set on the boss bar
	 */
	public abstract void addFlag ( BarFlag flag );

	/**
	 * Returns whether this boss bar as the passed flag set
	 * <p>
	 * @param flag the flag to check
	 * @return whether it has the flag
	 */
	public abstract boolean hasFlag ( BarFlag flag );

	/**
	 * Returns the progress of the bar between 0.0 and 1.0
	 * <p>
	 * @return the progress of the bar
	 */
	public abstract double getProgress();
	
	/**
	 * Sets the progress of the bar. Values should be between 0.0 (empty) and 1.0
	 * (full)
	 * <p>
	 * @param progress the progress of the bar
	 */
	public abstract void setProgress ( double progress );

	/**
	 * Returns the player viewing this boss bar
	 * <p>
	 * @return the player viewing this bar.
	 */
	public abstract Player getPlayer();

	/**
	 * Set if the boss bar is displayed to attached player.
	 * <p>
	 * @param visible visible status
	 */
	public abstract void setVisible ( boolean visible );

	/**
	 * Return if the boss bar is displayed to attached player.
	 * <p>
	 * @return visible status
	 */
	public abstract boolean isVisible();

	/**
	 * Shows the previously hidden boss bar to all attached player
	 * <p>
	 * This is the equivalent of calling {@link #setVisible(boolean)} providing
	 * <strong>{@code false}</strong>.
	 */
	public void show ( ) {
		setVisible ( true );
	}

	/**
	 * Hides this boss bar from all attached player
	 * <p>
	 * This is the equivalent of calling {@link #setVisible(boolean)} providing
	 * <strong>{@code true}</strong>.
	 */
	public void hide() {
		setVisible ( false );
	}
}
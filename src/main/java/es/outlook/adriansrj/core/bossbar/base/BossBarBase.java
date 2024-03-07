package es.outlook.adriansrj.core.bossbar.base;

import org.apache.commons.lang.Validate;

import es.outlook.adriansrj.core.bossbar.BossBar;

/**
 * {@code BossBar}s base class.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 10:31 AM
 */
public abstract class BossBarBase extends BossBar {

	/**
	 * Checks the progress is between {@code 0.0} and {@code 1.0}.
	 * <p>
	 * @param progress the progress to check.
	 */
	protected void checkProgress ( double progress ) {
		Validate.isTrue ( progress >= 0D && progress <= 1D , "progress must be between 0.0 and 1.0!" );
	}
}
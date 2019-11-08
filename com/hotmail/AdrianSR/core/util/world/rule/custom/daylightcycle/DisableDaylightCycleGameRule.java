package com.hotmail.AdrianSR.core.util.world.rule.custom.daylightcycle;

import org.bukkit.World;

import com.hotmail.AdrianSR.core.util.world.rule.GameRule;
import com.hotmail.AdrianSR.core.util.world.rule.GameRuleType;

/**
 * The {@link GameRule} that disables the
 * worlds daylight cycle.
 * <p>
 * @author AdrianSR
 */
public class DisableDaylightCycleGameRule extends GameRule {
	
	/**
	 * The default final time worlds with this rule will have.
	 */
	public static final long DEFAULT_FINAL_TIME = 500L;
	
	/**
	 * The final time the world
	 * will have permanently.
	 */
	private final long final_time;
	
	/**
	 * Construct a new {@link GameRule} that disables the daylight cycle of the
	 * worlds. With the default final time
	 * {@link DisableDaylightCycleGameRule#DEFAULT_FINAL_TIME}
	 */
	public DisableDaylightCycleGameRule() {
		this(DisableDaylightCycleGameRule.DEFAULT_FINAL_TIME);
	}

	/**
	 * Construct a new {@link GameRule} that disables the daylight cycle of the
	 * worlds.
	 * <p>
	 * @param final_time the final time world will have permanently.
	 */
	public DisableDaylightCycleGameRule(long final_time) {
		super(GameRuleType.DAYLIGHT_CYCLE, false);
		this.final_time = final_time;
	}
	
	@Override
	public World apply(World world) {
		world = super.apply(world);
		world.setTime(final_time);
		return world;
	}
}
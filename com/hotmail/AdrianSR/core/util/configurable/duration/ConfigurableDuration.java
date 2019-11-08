package com.hotmail.AdrianSR.core.util.configurable.duration;

import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;
import com.hotmail.AdrianSR.core.util.file.YmlUtils;
import com.hotmail.AdrianSR.core.util.loadable.Loadable;
import com.hotmail.AdrianSR.core.util.saveable.Saveable;
import com.hotmail.AdrianSR.core.util.time.Duration;

/**
 * Represents a {@link Duration} that is 'Configurable'
 * because can be loaded from/saved on a {@link ConfigurationSection}.
 * <p>
 * @author AdrianSR
 */
public class ConfigurableDuration extends Duration implements Loadable, Saveable {
	
	public static final String DURATION_KEY = "duration";
	public static final String     UNIT_KEY = "unit";
	
	/**
	 * Construct a new uninitialized duration.
	 * <p>
	 * This constructor is used only for {@link ConfigurableDuration}s that will be
	 * initialized after constructing by using the method
	 * {@link ConfigurableDuration#load(ConfigurationSection)}.
	 */
	public ConfigurableDuration() { // uninitialized
		this(-1L, null);
	}
	
	/**
	 * Construct duration.
	 * <p>
	 * @param duration the duration.
	 * @param unit the time unit.
	 */
	public ConfigurableDuration(long duration, TimeUnit unit) {
		super(duration, unit);
	}
	
	/**
	 * Construct duration from milliseconds.
	 * <p>
	 * @param millis the duration in milliseconds.
	 */
	public ConfigurableDuration(long millis) {
		this(millis, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Construct duration using the values of another {@link Duration}.
	 * <p>
	 * @param copy the {@link Duration} to copy.
	 */
	public ConfigurableDuration(Duration copy) {
		this(copy.getDuration(), copy.getUnit());
	}
	
	@Override
	public ConfigurableDuration load(ConfigurationSection section) {
		this.duration = section.getLong(DURATION_KEY, -1L);
		this.unit     = ReflectionUtils.getEnumConstant(TimeUnit.class, section.getString(UNIT_KEY, null));
		return this;
	}
	
	@Override
	public boolean isValid() {
		return this.getDuration() > -1L && this.getUnit() != null;
	}

	@Override
	public boolean isInvalid() {
		return !this.isValid();
	}

	@Override
	public int save(ConfigurationSection section) {
		return YmlUtils.setNotEqual(section, DURATION_KEY, this.getDuration())
				+ (this.getUnit() != null ? YmlUtils.setNotEqual(section, UNIT_KEY, this.getUnit().name()) : 0);
	}
}
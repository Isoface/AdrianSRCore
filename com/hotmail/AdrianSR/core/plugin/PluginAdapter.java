package com.hotmail.adriansr.core.plugin;

import com.hotmail.adriansr.core.util.lang.PluginInternalLanguageEnumContainer;
import com.hotmail.adriansr.core.version.CoreVersion;

/**
 * An convenience implementation of {@link Plugin}. Derive from this and only
 * override what you need.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 05:38 PM
 */
public abstract class PluginAdapter extends Plugin {

	@Override
	public CoreVersion getRequiredCoreVersion() {
		return null;
	}

	@Override
	public PluginDependence[] getDependences() {
		return null;
	}
	
	@Override
	public Class < ? extends Enum < ? extends PluginInternalLanguageEnumContainer > > getInternalLanguageContainer ( ) {
		return null;
	}
	
	@Override
	public String getInternalLanguageResourcesPackage ( ) {
		return null;
	}

	@Override
	protected boolean setUpConfig() {
		return true;
	}

	@Override
	protected boolean setUpHandlers() {
		return true;
	}

	@Override
	protected boolean setUpCommands() {
		return true;
	}

	@Override
	protected boolean setUpListeners() {
		return true;
	}
}
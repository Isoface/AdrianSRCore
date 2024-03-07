package es.outlook.adriansrj.core.plugin.custom;

import es.outlook.adriansrj.core.plugin.PluginDependency;
import es.outlook.adriansrj.core.util.lang.PluginInternalLanguageEnumContainer;
import es.outlook.adriansrj.core.version.CoreVersion;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 18/08/2021 / Time: 11:49 p. m.
 */
@Deprecated
public abstract class PluginCustomComplexAdapter extends PluginCustomComplex {
	
	@Override
	public CoreVersion getRequiredCoreVersion ( ) {
		return null;
	}
	
	@Override
	public PluginDependency[] getDependences ( ) {
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
	protected boolean setUpConfig ( ) {
		return true;
	}
	
	@Override
	protected boolean setUpHandlers ( ) {
		return true;
	}
	
	@Override
	protected boolean setUpCommands ( ) {
		return true;
	}
	
	@Override
	protected boolean setUpListeners ( ) {
		return true;
	}
}

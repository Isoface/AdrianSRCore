package es.outlook.adriansrj.core.plugin;

import es.outlook.adriansrj.core.util.lang.PluginInternalLanguageEnumContainer;
import es.outlook.adriansrj.core.version.CoreVersion;

/**
 * An convenience implementation of {@link Plugin}. Derive from this and only override what you need.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 05:38 PM
 */
public abstract class PluginAdapter extends Plugin {
	
	public PluginAdapter ( ) {
		super ( );
	}
	
	@Override
	public CoreVersion getRequiredCoreVersion ( ) {
		return null;
	}
	
	@Override
	public PluginDependency[] getDependencies ( ) {
		return null;
	}

//	@Override
//	public MavenDependencyRepository[] getLibraryRepositories ( ) { return null; }
//
//	@Override
//	public MavenDependency[] getLibraries ( ) { return null; }
	
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
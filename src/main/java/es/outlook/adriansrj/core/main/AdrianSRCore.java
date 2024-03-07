package es.outlook.adriansrj.core.main;

import es.outlook.adriansrj.core.item.ActionItemHandler;
import es.outlook.adriansrj.core.plugin.Plugin;
import es.outlook.adriansrj.core.plugin.PluginAdapter;
import es.outlook.adriansrj.core.plugin.custom.PluginCustomManager;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import es.outlook.adriansrj.core.util.world.GameRuleHandler;
import es.outlook.adriansrj.core.version.CoreVersion;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * <strong>AdrianSRCore</strong> plugin main class.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 11:55 AM
 */
public final class AdrianSRCore extends PluginAdapter {
	
	private static final String CHECK_UPDATES_KEY = "check-updates";
	
	private static final String SPIGOT_RESOURCES_VERSION_ULR = "https://api.spigotmc.org/legacy/update.php?resource=";
	private static final String CORE_RESOURCE_ID             = "64289";
	
	/**
	 * Gets the {@link AdrianSRCore} plugin instance.
	 * <p>
	 * @return the core instance.
	 */
	public static AdrianSRCore getInstance ( ) {
		return Plugin.getPlugin ( AdrianSRCore.class );
	}

//	@Override
//	public MavenDependencyRepository[] getLibraryRepositories ( ) {
//		if ( Version.getServerVersion ( ).isOlder ( Version.v1_16_R3 ) ) {
//			return new MavenDependencyRepository[] {
//					new MavenDependencyRepository ( "Maven Central" , "default" ,
//													"https://repo1.maven.org/maven2/" ) ,
//			};
//		} else {
//			return new MavenDependencyRepository[ 0 ];
//		}
//	}
//
//	@Override
//	public MavenDependency[] getLibraries ( ) {
//		if ( Version.getServerVersion ( ).isOlder ( Version.v1_16_R3 ) ) {
//			return new MavenDependency[] {
//					new MavenDependency ( "com.elmakers.math:exp4j:1.0" ) ,
//					new MavenDependency ( "com.elmakers.mine.bukkit:EffectLib:9.0" ) ,
//					new MavenDependency ( "commons-io:commons-io:2.6" ) ,
//					new MavenDependency ( "xyz.xenondevs:particle:1.6.4" ) ,
//			};
//		} else {
//			return new MavenDependency[ 0 ];
//		}
//	}
	
	@Override
	protected boolean setUp ( ) {
		saveDefaultConfig ( );
		
		// initializing custom plugin manager
		new PluginCustomManager ( this );
		
		// checking updates
		FileConfiguration config = getConfig ( );
		
		if ( config.getBoolean ( CHECK_UPDATES_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( "Using the version" + getDescription ( ).getVersion ( ) , this );
			ConsoleUtil.sendPluginMessage ( "Checking for updates..." , this );
			
			try {
				URL           version_url = new URL ( SPIGOT_RESOURCES_VERSION_ULR + CORE_RESOURCE_ID );
				URLConnection connection  = version_url.openConnection ( );
				String latest_version_name = new BufferedReader (
						new InputStreamReader ( connection.getInputStream ( ) ) ).readLine ( );
				CoreVersion latest_version = EnumReflection.getEnumConstant ( CoreVersion.class ,
																			  format ( latest_version_name ) );
				if ( latest_version == null || latest_version.isNewer ( CoreVersion.getCoreVersion ( ) ) ) {
					ConsoleUtil.sendPluginMessage ( ChatColor.GREEN , "A new version of AdrianSRCore is available!" ,
													this );
				} else {
					ConsoleUtil.sendPluginMessage ( ChatColor.GREEN , "This is the latest version of AdrianSRCore :)" ,
													this );
				}
			} catch ( Throwable ex ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , "Could not check for updates :(" , this );
			}
		}
		return true;
	}
	
	private static String format ( String suppose_version ) {
		return ( "v" + suppose_version.trim ( ).toLowerCase ( ).replace ( '.' , '_' )
				.replace ( 'v' , ( char ) 0 ) );
	}
	
	@Override
	protected boolean setUpHandlers ( ) {
		new GameRuleHandler ( this );
		new ActionItemHandler ( this );
		return true;
	}
}
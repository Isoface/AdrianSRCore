package es.outlook.adriansrj.core.util.configurable.location;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import es.outlook.adriansrj.core.util.Initializable;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;

public class ConfigurableLocation extends org.bukkit.Location implements Configurable, Initializable {
	
	public static final String                WORLD_UID_KEY = "world-uid";
	public static final String                        X_KEY = "x";
	public static final String                        Y_KEY = "y";
	public static final String                        Z_KEY = "z";
	public static final String                      YAW_KEY = "yaw";
	public static final String                    PITCH_KEY = "pitch";
	public static final String[] CONFIGURABLE_LOCATION_KEYS = { WORLD_UID_KEY, X_KEY, Y_KEY, Z_KEY, YAW_KEY, PITCH_KEY };
	
	/**
	 * Returns a {@link ConfigurableLocation} loaded from the given {@link ConfigurationSection},
	 * or null if there is no any valid {@link ConfigurableLocation} stored on the given {@link ConfigurationSection}.
	 * <p>
	 * Note that this method checks the given configuration section calling {@link #isConfigurableLocation(ConfigurationSection)}.
	 * <p>
	 * @param section the section to parse.
	 * @return the parsed location.
	 */
	public static ConfigurableLocation of(ConfigurationSection section) {
		return ( isConfigurableLocation(section) ? new ConfigurableLocation().load(section) : null );
	}
	
	/**
	 * Return true if and only if there is a valid {@link ConfigurableLocation}
	 * stored on the given {@link ConfigurationSection}
	 * <p>
	 * @param section the {@link ConfigurationSection} where the supposed
	 *                {@link ConfigurableLocation} is stored.
	 * @return true if is.
	 */
	public static boolean isConfigurableLocation ( ConfigurationSection section ) {
		for ( String key : CONFIGURABLE_LOCATION_KEYS ) {
			switch ( key ) {
			case WORLD_UID_KEY:
				break;
				
			default:
				if ( !( section.get ( key ) instanceof Number ) ) {
					return false;
				}
				break;
			}
		}
		
		if ( section.isString ( WORLD_UID_KEY ) ) {
			try {
				// this will thrown an exception if the UUID is invalid.
				UUID.fromString ( section.getString ( WORLD_UID_KEY ) );
				return true;
			} catch ( IllegalArgumentException ex ) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/** whether {@link #load(ConfigurationSection)} method has been called */
	protected boolean initialized;

	public ConfigurableLocation ( ) { // uninitialized
		super(null, 0, 0, 0, 0, 0);
	}
	
	public ConfigurableLocation(World world, double x, double y, double z, float yaw, float pitch) {
		super(world, x, y, z, yaw, pitch);
		this.initialized = true;
	}
	
	public ConfigurableLocation(World world, double x, double y, double z) {
		this(world, x, y, z, 0.0F, 0.0F);
	}
	
	public ConfigurableLocation(Location copy) {
		this(copy.getWorld(), copy.getX(), copy.getY(), copy.getZ(), copy.getYaw(), copy.getPitch());
	}

	@Override
	public ConfigurableLocation load(ConfigurationSection section) {
		this.setWorld ( Bukkit.getWorld ( UUID.fromString ( section.getString ( WORLD_UID_KEY ) ) ) );
		this.setX(section.getDouble(X_KEY, 0));
		this.setY(section.getDouble(Y_KEY, 0));
		this.setZ(section.getDouble(Z_KEY, 0));
		this.setYaw((float) section.getDouble(YAW_KEY, 0));
		this.setPitch((float) section.getDouble(PITCH_KEY, 0));
		this.initialized = true;
		return this;
	}
	
	@Override
	public int save(ConfigurationSection section) {
		return 	(YamlUtil.setNotEqual(section, WORLD_UID_KEY, ( getWorld ( ) != null ? getWorld ( ).getUID ( ).toString ( ) : "" ) ) ? 1 : 0)
			+	(YamlUtil.setNotEqual(section, X_KEY, getX()) ? 1 : 0)
			+	(YamlUtil.setNotEqual(section, Y_KEY, getY()) ? 1 : 0) 
			+	(YamlUtil.setNotEqual(section, Z_KEY, getZ()) ? 1 : 0) 
			+	(YamlUtil.setNotEqual(section, YAW_KEY, getYaw()) ? 1 : 0)
			+	(YamlUtil.setNotEqual(section, PITCH_KEY, getPitch()) ? 1 : 0);
	}
	
	/**
	 * Gets a clone of this location with the specified {@link World world}.
	 * <p>
	 * @param world the new world for the location.
	 * @return a clone of this location with the specified {@code world}.
	 */
	public ConfigurableLocation withWorld ( World world ) {
		ConfigurableLocation location = clone ( );
		location.setWorld ( world );
		return location;
	}
	
	@Override
	public boolean isInitialized() {
		return initialized;
	}
	
	@Override
	public boolean isValid() {
		return isInitialized() && getWorld() != null;
	}

	@Override
	public boolean isInvalid() {
		return !isValid();
	}
	
	@Override
	public ConfigurableLocation clone ( ) {
		return (ConfigurableLocation) super.clone ( );
	}
}
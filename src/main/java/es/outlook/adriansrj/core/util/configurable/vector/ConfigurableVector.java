package es.outlook.adriansrj.core.util.configurable.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Tuesday 25 May, 2021 / 09:54 AM
 */
public class ConfigurableVector extends Vector implements Configurable {
	
	private static final String X_KEY = "x";
	private static final String Y_KEY = "y";
	private static final String Z_KEY = "z";
	
	public static final String[] CONFIGURABLE_VECTOR_KEYS = {
			X_KEY , Y_KEY , Z_KEY
	};
	
	/**
	 * Returns a {@link ConfigurableVector} loaded from the given {@link ConfigurationSection},
	 * or null if there is no any valid {@link ConfigurableVector} stored on the given {@link ConfigurationSection}.
	 * <p>
	 * Note that this method checks the given configuration section calling
	 * {@link #isConfigurableVector(ConfigurationSection)}.
	 * <p>
	 * @param section the section to parse.
	 * @return the parsed vector.
	 */
	public static ConfigurableVector of ( ConfigurationSection section ) {
		return ( isConfigurableVector ( section ) ? new ConfigurableVector ( ).load ( section ) : null );
	}
	
	/**
	 * Return true if and only if there is a valid {@link ConfigurableVector}
	 * stored on the given {@link ConfigurationSection}
	 * <p>
	 * @param section the {@link ConfigurationSection} where the supposed
	 *                {@link ConfigurableVector} is stored.
	 * @return true if is.
	 */
	public static boolean isConfigurableVector ( ConfigurationSection section ) {
		for ( String key : CONFIGURABLE_VECTOR_KEYS ) {
			if ( !( section.get ( key ) instanceof Number ) ) {
				return false;
			}
		}
		
		return true;
	}
	
	public ConfigurableVector ( ) {
		super ( );
	}
	
	public ConfigurableVector ( double x , double y , double z ) {
		super ( x , y , z );
	}
	
	public ConfigurableVector ( float x , float y , float z ) {
		super ( x , y , z );
	}
	
	public ConfigurableVector ( int x , int y , int z ) {
		super ( x , y , z );
	}
	
	public ConfigurableVector ( Vector copy ) {
		this ( copy.getX ( ) , copy.getY ( ) , copy.getZ ( ) );
	}
	
	@Override
	public ConfigurableVector load ( ConfigurationSection section ) {
		this.setX ( section.getDouble ( X_KEY ) );
		this.setY ( section.getDouble ( Y_KEY ) );
		this.setZ ( section.getDouble ( Z_KEY ) );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return ( YamlUtil.setNotEqual ( section , X_KEY , getX ( ) ) ? 1 : 0 )
				+ ( YamlUtil.setNotEqual ( section , Y_KEY , getY ( ) ) ? 1 : 0 )
				+ ( YamlUtil.setNotEqual ( section , Z_KEY , getZ ( ) ) ? 1 : 0 );
	}
	
	/**
	 * Gets whether this is a valid vector.
	 * <p>
	 * This will check if the values of this vector are not <strong>NaN</strong> or inifinite.
	 * <p>
	 * @return whether or not this vector is valid.
	 */
	@Override
	public boolean isValid ( ) {
		double[] values = new double[] { x , y , z };
		
		for ( double value : values ) {
			if ( Double.isNaN ( value ) || Double.isInfinite ( value ) ) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}
	
	@Override
	public boolean equals ( Object obj ) {
		if ( obj instanceof Vector ) {
			Vector other = ( Vector ) obj;
			
			return Double.compare ( this.getX ( ) , other.getX ( ) ) == 0
					&& Double.compare ( this.getY ( ) , other.getY ( ) ) == 0
					&& Double.compare ( this.getZ ( ) , other.getZ ( ) ) == 0;
		} else {
			return false;
		}
	}
}
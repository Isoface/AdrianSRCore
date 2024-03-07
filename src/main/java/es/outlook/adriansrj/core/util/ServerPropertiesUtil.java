package es.outlook.adriansrj.core.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;

/**
 * Useful class for dealing with the server properties. (Stored in
 * "server.properties" file).
 * <p>
 * @author AdrianSR / Tuesday 11 August, 2020 / 04:24 PM
 */
public class ServerPropertiesUtil {
	
	private static final Properties PROPERTIES;
	static {
		Properties properties = null;
		try {
			Object server = MethodReflection.invoke ( MethodReflection.getAccessible ( 
					ClassReflection.getNmsClass ( "MinecraftServer" ) , "getServer" ) , (Object) null );
			Object property_manager = FieldReflection.getValue ( server , "propertyManager" );
			
			// then getting properties.
			properties = FieldReflection.getValue ( property_manager , "properties" );
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException | NoSuchFieldException ex ) {
			ex.printStackTrace ( );
		}
		
		PROPERTIES = properties;
	}
	
	public static String getStringProperty ( String key , String default_value ) {
		try {
			return PROPERTIES.getProperty ( key , default_value );
		} catch ( Throwable ex ) {
			return default_value;
		}
	}
	
	public static int getIntProperty ( String key , int default_value ) {
		try {
			return Integer.parseInt ( PROPERTIES.getProperty ( key , String.valueOf ( default_value ) ) );
		} catch ( Throwable ex ) {
			return default_value;
		}
	}
	
	public static long getLongProperty ( String key , long default_value ) {
		try {
			return Long.parseLong ( PROPERTIES.getProperty ( key , String.valueOf ( default_value ) ) );
		} catch ( Throwable ex ) {
			return default_value;
		}
	}
	
	public static boolean getBooleanProperty ( String key , boolean default_value ) {
		try {
			return Boolean.parseBoolean ( PROPERTIES.getProperty ( key , String.valueOf ( default_value ) ) );
		} catch ( Throwable ex ) {
			return default_value;
		}
	}
}
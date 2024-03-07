package es.outlook.adriansrj.core.util;

import java.lang.reflect.Field;

/**
 * Useful class for dealing with the AsyncCatcher of Spigot.
 *
 * @author AdrianSR / 21/09/2021 / 01:48 p. m.
 */
public class AsyncCatcherUtil {
	
	protected static final Class < ? > ASYNC_CATCHER_CLASS;
	protected static final Field       ENABLED_FLAG_FIELD;
	
	static {
		try {
			ASYNC_CATCHER_CLASS = Class.forName ( "org.spigotmc.AsyncCatcher" );
			ENABLED_FLAG_FIELD  = ASYNC_CATCHER_CLASS.getField ( "enabled" );
		} catch ( ClassNotFoundException ex ) {
			throw new IllegalStateException ( "couldn't find AsyncCatcher class: " , ex );
		} catch ( NoSuchFieldException ex ) {
			throw new IllegalStateException ( "couldn't find enable flag: " , ex );
		}
	}
	
	public static boolean isEnabled ( ) {
		try {
			return ENABLED_FLAG_FIELD.getBoolean ( null );
		} catch ( IllegalAccessException ex ) {
			throw new IllegalStateException ( "couldn't access enabled flag: " , ex );
		}
	}
	
	public static void setEnabled ( boolean enabled ) {
		try {
			ENABLED_FLAG_FIELD.setBoolean ( null , enabled );
		} catch ( IllegalAccessException ex ) {
			throw new IllegalStateException ( "couldn't access enabled flag: " , ex );
		}
	}
	
	public static void enable ( ) {
		setEnabled ( true );
	}
	
	public static void disable ( ) {
		setEnabled ( false );
	}
}

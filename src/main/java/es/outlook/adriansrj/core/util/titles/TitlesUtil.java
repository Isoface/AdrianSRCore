package es.outlook.adriansrj.core.util.titles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import es.outlook.adriansrj.core.util.reflection.bukkit.BukkitReflection;
import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import es.outlook.adriansrj.core.util.reflection.general.ConstructorReflection;
import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;

/**
 * Useful class for sending titles to players.
 * <br>
 * <b>Note that the methods implemented here are
 * available since the version {@link Version#v1_11_R1} of Spigot,
 * so this class is actually useful for older versions.</b>
 * <p>
 * @author AdrianSR / Wednesday 15 April, 2020 / 03:50 PM
 */
public class TitlesUtil {
	
	/**
	 * Sends a title and a subtitle message to the player. If either of these values
	 * are null, they will not be sent and the display will remain unchanged. If
	 * they are empty strings, the display will be updated as such. If the strings
	 * contain a new line, only the first line will be sent. All timings values may
	 * take a value of -1 to indicate that they will use the last value sent (or the
	 * defaults if no title has been displayed).
	 * <p>
	 * @param player   the target player.
	 * @param title    Title text
	 * @param subtitle Subtitle text
	 * @param fade_in   time in ticks for titles to fade in. Defaults to 10.
	 * @param stay     time in ticks for titles to stay. Defaults to 70.
	 * @param fade_out  time in ticks for titles to fade out. Defaults to 20.
	 */
	public static void send ( Player player , String title , String subtitle , int fade_in , int stay , int fade_out ) {
		try {
			Class < ? >     packet_class = ClassReflection.getNmsClass ( "PacketPlayOutTitle" );
			Class < ? >  component_class = ClassReflection.getNmsClass ( "IChatBaseComponent" );
			Class < ? >      action_enum = null;
			try {
				action_enum = ClassReflection.getSubClass ( packet_class , "EnumTitleAction" );
			} catch ( ClassNotFoundException ex ) {
				action_enum = ClassReflection.getNmsClass ( "EnumTitleAction" );
			}
			
			Class < ? > serializer_class = null;
			try {
				serializer_class = ClassReflection.getSubClass ( component_class , "ChatSerializer" );
			} catch ( ClassNotFoundException ex ) {
				serializer_class = ClassReflection.getNmsClass ( "ChatSerializer" );
			}
			
			Method          a = MethodReflection.get ( serializer_class , "a" , String.class );
			Object component0 = a.invoke ( serializer_class , "{\"text\":\"" + title + "\"}" );
			Object component1 = a.invoke ( serializer_class , "{\"text\":\"" + subtitle + "\"}" );
			
			Method value_of = MethodReflection.get ( action_enum , "valueOf" , String.class );
			Object  action0 = value_of.invoke ( action_enum , "TITLE" );
			Object  action1 = value_of.invoke ( action_enum , "SUBTITLE" );
			
			Object packet0 = ConstructorReflection.get ( packet_class , int.class , int.class , int.class )
					.newInstance ( fade_in , stay , fade_out ); // times packet construction
			Object packet1 = ConstructorReflection.get ( packet_class , action_enum , component_class )
					.newInstance ( action0 , component0 );
			Object packet2 = ConstructorReflection.get ( packet_class , action_enum , component_class )
					.newInstance ( action1 , component1 );
			
			BukkitReflection.sendPacket ( player , packet0 ); // sending times packet
			if ( title    != null ) { BukkitReflection.sendPacket ( player , packet1 ); } // sending title packet
			if ( subtitle != null ) { BukkitReflection.sendPacket ( player , packet2 ); } // sending subtitle packet
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Sends a title and a subtitle message to the player. If either of these values
	 * are null, they will not be sent and the display will remain unchanged. If
	 * they are empty strings, the display will be updated as such. If the strings
	 * contain a new line, only the first line will be sent. All timings values may
	 * take a value of -1 to indicate that they will use the last value sent (or the
	 * defaults if no title has been displayed).
	 * <p>
	 * @param player   the target player.
	 * @param title    Title text
	 * @param subtitle Subtitle text
	 */
	public static void send ( Player player , String title , String subtitle ) {
		TitlesUtil.send ( player , title , subtitle , 10 , 70 , 20 );
	}
	
	/**
	 * Resets the title displayed to the player. This will clear the displayed
	 * title/subtitle and reset timings to their default values.
	 * <p>
	 * @param player the target player.
	 * @deprecated it seems it is not working anymore.
	 */
	@Deprecated
	public static void reset ( Player player ) {
		try {
			Class < ? >    packet_class = ClassReflection.getNmsClass ( "PacketPlayOutTitle" );
			Class < ? > component_class = ClassReflection.getNmsClass ( "IChatBaseComponent" );
			Class < ? >     action_enum = null;
			try {
				action_enum = ClassReflection.getSubClass ( packet_class , "EnumTitleAction" );
			} catch ( ClassNotFoundException ex ) {
				action_enum = ClassReflection.getNmsClass ( "EnumTitleAction" );
			}
			
			Object action = MethodReflection.get ( action_enum , "valueOf" , String.class ).invoke ( action_enum , "RESET" );
			Object packet = ConstructorReflection.get ( packet_class , action_enum , component_class )
					.newInstance ( action , null );
			
			BukkitReflection.sendPacket ( player , packet );
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			ex.printStackTrace ( );
		}
	}

	/**
	 * Sends a title and subtitle to all the players online.
	 * <p>
	 * @param title    Title text
	 * @param subtitle Subtitle text
	 * @param fade_in  time in ticks for titles to fade in. Defaults to 10.
	 * @param stay     time in ticks for titles to stay. Defaults to 70.
	 * @param fade_out time in ticks for titles to fade out. Defaults to 20.
	 * @see #send(Player, String, int, int, int)
	 */
	public static void broadcast ( String title , String subtitle , int fade_in , int stay , int fade_out ) {
		Bukkit.getOnlinePlayers().forEach ( player -> TitlesUtil.send ( player , title , subtitle , fade_in , stay , fade_out ) );
	}
	
	/**
	 * Sends a title and subtitle to all the players online.
	 * <p>
	 * @param title    Title text
	 * @param subtitle Subtitle text
	 * @see #send(Player, String)
	 */
	public static void broadcast ( String title , String subtitle ) {
		TitlesUtil.broadcast ( title , subtitle , 10 , 70 , 20 );
	}
	
	/**
	 * Resets the title displayed to the online players.
	 * <p>
	 * @deprecated it seems it is not working anymore.
	 */
	@Deprecated
	public static void broadcastReset ( ) {
		Bukkit.getOnlinePlayers ( ).forEach ( player -> TitlesUtil.reset ( player ) );
	}
}
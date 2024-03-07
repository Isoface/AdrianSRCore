package es.outlook.adriansrj.core.util.actionbar;

import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.reflection.bukkit.BukkitReflection;
import es.outlook.adriansrj.core.util.reflection.bukkit.PacketReflection;
import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import es.outlook.adriansrj.core.util.reflection.general.ConstructorReflection;
import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Useful class for sending action bars.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 08:52 AM
 */
public class ActionBarUtil {
	
	/**
	 * Sends an action bar message to the provided {@link Player}.
	 * <p>
	 * Note that the length {@code message} message must be
	 * <strong>{@code < 63}</strong>, if the length of the provided message is
	 * higher than <strong>{@code 63}</strong>, that it will limited.
	 * <p>
	 * @param player  the player that will receive the message.
	 * @param message the message to send.
	 */
	public static void send ( Player player , String message ) {
		try {
			Class < ? >   component_class = ClassReflection.getNmsClass ( "IChatBaseComponent" );
			Class < ? >[] inner_classes   = component_class.getClasses ( );
			Class < ? > chat_serializer = inner_classes.length > 0 ? component_class.getClasses ( )[ 0 ]
					: ClassReflection.getNmsClass ( "ChatSerializer" );
			Class < ? > packet_class = ClassReflection.getNmsClass ( "PacketPlayOutChat" );
			
			Object component = MethodReflection.get ( chat_serializer , "a" , String.class ).invoke (
					chat_serializer , "{\"text\":\"" + StringUtil.limit (
							message ,
							63 ) +
							"\"}" );
			
			// creating the instance
			Object packet = null;
			
			if ( Version.getServerVersion ( ).isOlder ( Version.v1_12_R1 ) ) {
				packet = ConstructorReflection.newInstance (
						packet_class ,
						new Class < ? >[] { component_class , byte.class } ,
						component , ( byte ) 2 );
			} else {
				// here we have decided to use the constructor with no
				// parameters, and set the fields directly
				Class < ? > chat_type_class = ClassReflection.getNmsClass ( "ChatMessageType" );
				packet = packet_class.newInstance ( );
				
				PacketReflection.setValueByType ( packet , component_class , 0 , component );
				PacketReflection.setValueByType ( packet , chat_type_class , 0 , MethodReflection
						.get ( chat_type_class , "valueOf" , String.class ).invoke ( chat_type_class , "GAME_INFO" ) );
				
				// for any reason and UUID is required since 1.16
				if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_16_R1 ) ) {
					PacketReflection.setUUID ( packet , 0 , UUID.randomUUID ( ) );
				}
			}
			
			// then sending
			BukkitReflection.sendPacket ( player , packet );
		} catch ( ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | InstantiationException e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * Sends an action bar with the provide {@code message} to all the players
	 * online.
	 * <p>
	 * @param message the message to send.
	 * @see #send(Player , String)
	 */
	public static void broadcast ( final String message ) {
		Bukkit.getOnlinePlayers ( ).forEach ( player -> ActionBarUtil.send ( player , message ) );
	}
}
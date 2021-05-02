package com.hotmail.adriansr.core.util.actionbar;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.reflection.bukkit.BukkitReflection;
import com.hotmail.adriansr.core.util.reflection.general.ClassReflection;
import com.hotmail.adriansr.core.util.reflection.general.ConstructorReflection;
import com.hotmail.adriansr.core.util.reflection.general.MethodReflection;
import com.hotmail.adriansr.core.util.server.Version;

/**
 * Useful class for sending action bars to players.
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
			Class < ? > [ ] inner_classes = component_class.getClasses ( );
			Class < ? >   chat_serializer = inner_classes.length > 0 ? component_class.getClasses ( ) [ 0 ] 
					: ClassReflection.getNmsClass ( "ChatSerializer" );
			Class < ? > packet_class = ClassReflection.getNmsClass ( "PacketPlayOutChat" );
			
			Object component = MethodReflection.get ( chat_serializer , "a" , String.class ).invoke ( chat_serializer ,
					"{\"text\":\"" + StringUtil.limit ( message , 63 ) + "\"}" );
			
			Object packet = null;
			if ( Version.getServerVersion ( ).isOlder ( Version.v1_12_R1 ) ) {
				packet = ConstructorReflection.newInstance ( packet_class , new Class < ? > [ ] { component_class , byte.class } , 
						component , (byte) 2 );
			} else {
				Class < ? > chat_type_class = ClassReflection.getNmsClass ( "ChatMessageType" );
				packet                      = ConstructorReflection.newInstance ( packet_class , 
						new Class < ? > [ ] { component_class , chat_type_class } , 
						component , MethodReflection
							.get ( chat_type_class , "valueOf" , String.class ).invoke ( chat_type_class , "GAME_INFO" ) );
			}
			
			BukkitReflection.sendPacket ( player , packet );
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends an action bar with the provide {@code message} to all the players
	 * online.
	 * <p>
	 * @param message the message to send.
	 * @see #send(Player, String)
	 */
	public static void broadcast ( final String message ) {
		Bukkit.getOnlinePlayers().forEach ( player -> ActionBarUtil.send ( player , message ) );
	}
}
package com.hotmail.AdrianSR.core.actionbar;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.util.TextUtils;
import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;

/**
 * Represents a Action Bar
 * message sender class.
 * <p>
 * @author AdrianSR
 */
public class ActionBars {
	
	/**
	 * Send an Action Bar message to {@link Player},
	 * in any Spigot version.
	 * The message lenght have to be
	 * < 63 characters.
	 * <p>
	 * @param player The target player.
	 * @param message The message to send.
	 */
	public static void sendActionBar(final Player player, final String message) {
		ReflectionUtils.checkIsInitializeReflection();
		try {
			Class<?> component_class = ReflectionUtils.getCraftClass("IChatBaseComponent");
			Class<?> chat_serializer = component_class.getClasses()[0];

			// get 'ChatSerializer.a()' method.
			Method a = chat_serializer.getMethod("a", String.class);

			// make IChatBaseComponent.
			Object component = a.invoke(chat_serializer, "{\"text\":\"" + TextUtils.getShortenString(message, 63) + "\"}");

			// get packet class.
			Class<?> packet_class = ReflectionUtils.getCraftClass("PacketPlayOutChat");

			// make packet.
			Object packet;
			
			// when a version < 1_12
			if (ServerVersion.serverOlderThan(ServerVersion.v1_12_R1)) {
				packet = packet_class.getConstructor(component_class, byte.class).newInstance(component, (byte) 2);
			} else {
				// get ChatMessageType class.
				Class<?> chat_message_type_class = ReflectionUtils.getCraftClass("ChatMessageType");
				
				// get 'valueOf' method within the ChatMessageType class.
				Method value_of = chat_message_type_class.getMethod("valueOf", String.class);
				packet          = packet_class.getConstructor(component_class, chat_message_type_class)
						.newInstance(component, value_of.invoke(chat_message_type_class, "GAME_INFO"));
			}

			// send packet.
			ReflectionUtils.sendPacket(player, packet);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Send action bar message
	 * for every online player.
	 * <p>
	 * @param message the message to send.
	 */
	public static void broadCastBar(final String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendActionBar(p, message);
		}
	}
}
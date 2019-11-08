package com.hotmail.AdrianSR.core.titles;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;

/**
 * Represents a Titles messages 
 * sender class.
 * <p>
 * @author AdrianSR
 */
public class Titles {

	/**
	 * Global Class values.
	 */
	private static Class<?> CHAT_BASE_COMPONENT_CLASS;
	private static Class<?> PACKET_PLAY_OUT_TITLE;
	private static Class<?> ENUM_TITLE_ACTION_CLASS;
	private static Method VALUE_OF;
	private static Constructor<?> PACKET_TIMES_CONSTRUCTOR;
	private static Constructor<?> PACKET_MESSAGE_CONSTRUCTOR;

	/**
	 * Check reflection is initialized.
	 */
	private static void checkReflection() {
		// check reflection.
		ReflectionUtils.checkIsInitializeReflection();

		// get classes.
		try {
			// get 'IChatBaseComponent' class.
			CHAT_BASE_COMPONENT_CLASS = Class
					.forName(ReflectionUtils.NMS_PREFIX + ReflectionUtils.VERSION + ".IChatBaseComponent");

			// get 'PacketPlayOutTitle' packet.
			PACKET_PLAY_OUT_TITLE = Class
					.forName(ReflectionUtils.NMS_PREFIX + ReflectionUtils.VERSION + ".PacketPlayOutTitle");

			// get 'EnumTitleAction' enum class.
			ENUM_TITLE_ACTION_CLASS = PACKET_PLAY_OUT_TITLE.getClasses()[0];

			// get 'PacketPlayOutTitle' message constructor.
			PACKET_MESSAGE_CONSTRUCTOR = PACKET_PLAY_OUT_TITLE.getConstructor(ENUM_TITLE_ACTION_CLASS,
					CHAT_BASE_COMPONENT_CLASS);

			// get 'PacketPlayOutTitle' times constructor.
			PACKET_TIMES_CONSTRUCTOR = PACKET_PLAY_OUT_TITLE.getConstructor(ENUM_TITLE_ACTION_CLASS,
					CHAT_BASE_COMPONENT_CLASS, int.class, int.class, int.class);

			// get 'valueOf' Method in the enum 'PacketPlayOutTitle.EnumTitleAction'.
			VALUE_OF = ENUM_TITLE_ACTION_CLASS.getMethod("valueOf", String.class);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Send title and subtitle
	 * messages to {@link Player}, 
	 * in any Spigot version.
	 * <p>
	 * @param player The target Player.
	 * @param title The text displayed in the title.
	 * @param subtitle The text displayed in the subtitle.
	 * @param fadeInTime The time that titles takes to fade in.
	 * @param showTime The time that titles is displayed.
	 * @param fadeOutTime The time that titles takes to fade out.
	 */
	public static void sendTitleMessages(final Player player, final String title, final String subtitle, int fadeInTime,
			int showTime, int fadeOutTime) {
		// send title and subtitle.
		try {
			// check packets.
			checkReflection();

			// send times title packet.
			sendTimesTitle(player, fadeInTime, showTime, fadeOutTime);

			// send packets.
			for (int x = 0; x < 2; x++) {
				// get and check text to send.
				String text = (x == 0 ? title : subtitle);
				if (text == null) {
					continue;
				}

				// get ChatSerializer class.
				Class<?> chat_serializer = CHAT_BASE_COMPONENT_CLASS.getClasses()[0];

				// get 'ChatSerializer.a()' method.
				Method a = chat_serializer.getMethod("a", String.class);

				// make IChatBaseComponent.
				Object component = a.invoke(chat_serializer, "{\"text\":\"" + text + "\"}");

				// make title packet.
				Object packet = PACKET_MESSAGE_CONSTRUCTOR.newInstance(
						VALUE_OF.invoke(ENUM_TITLE_ACTION_CLASS, (x == 0 ? "TITLE" : "SUBTITLE")), component);

				// send packet.
				ReflectionUtils.sendPacket(player, packet);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Reset Titles in a 
	 * {@link Player} Screen.
	 * <p>
	 * @param player The target Player.
	 */
	public static void resetTitle(final Player player) {
		try {
			checkReflection();
			
			// make title reset packet.
			Object packet = PACKET_MESSAGE_CONSTRUCTOR.newInstance(VALUE_OF.invoke(ENUM_TITLE_ACTION_CLASS, "RESET"),
					null);

			// send reset packet.
			ReflectionUtils.sendPacket(player, packet);

			// reset subtitle.
			sendTitleMessages(player, null, "", 0, 0, 0);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Send the Times title to 
	 * change the title fade in time, 
	 * show time and fade out time.
	 * <p>
	 * @param player  The target {@link Player}.
	 * @param fadeInTime The time that titles takes to fade in.
	 * @param showTime The time that titles is displayed.
	 * @param fadeOutTime The time that titles takes to fade out.
	 */
	private static void sendTimesTitle(final Player player, int fadeInTime, int showTime, int fadeOutTime) {
		try {
			checkReflection();
			
			Object packet = PACKET_TIMES_CONSTRUCTOR.newInstance(VALUE_OF.invoke(ENUM_TITLE_ACTION_CLASS, "TIMES"),
					null, fadeInTime, showTime, fadeOutTime);
			ReflectionUtils.sendPacket(player, packet);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}

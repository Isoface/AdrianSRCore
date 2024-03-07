package es.outlook.adriansrj.core.util.reflection.bukkit;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;

import io.netty.channel.Channel;

/**
 * Useful class for dealing with the reflection of {@link Player}s.
 * <p>
 * @author AdrianSR / Saturday 18 April, 2020 / 12:04 PM
 */
public class PlayerReflection {
	
	/**
	 * Gets the handle ( the represented nms player by the craftbukkit player ) of
	 * the provided {@code player}.
	 * <p>
	 * @param player the player to get.
	 * @return the handle of the provided craftbukkit player.
	 * @throws SecurityException             reflection exception...
	 * @throws InvocationTargetException     reflection exception...
	 * @throws IllegalArgumentException      reflection exception...
	 * @throws IllegalAccessException        reflection exception...
	 * @throws UnsupportedOperationException if couldn't get the handle of the
	 *                                       provided player.
	 * @see BukkitReflection#getHandle(Object)
	 */
	public static Object getHandle ( Player player ) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException {
		return BukkitReflection.getHandle ( player );
	}
	
	/**
	 * Gets player's connection.
	 * <p>
	 * @param player the player to get.
	 * @return the player's connection.
	 * @throws SecurityException         reflection exception...
	 * @throws NoSuchFieldException      reflection exception...
	 * @throws IllegalArgumentException  reflection exception...
	 * @throws IllegalAccessException    reflection exception...
	 * @throws InvocationTargetException reflection exception...
	 */
	public static Object getPlayerConnection ( Player player ) throws SecurityException, NoSuchFieldException, 
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return FieldReflection.getValue ( BukkitReflection.getHandle ( player ) , "playerConnection" );
	}
	
	/**
	 * Gets player's network manager.
	 * <p>
	 * @param player the player to get.
	 * @return the player's network manager.
	 * @throws SecurityException         reflection exception...
	 * @throws NoSuchFieldException      reflection exception...
	 * @throws IllegalArgumentException  reflection exception...
	 * @throws IllegalAccessException    reflection exception...
	 * @throws InvocationTargetException reflection exception...
	 */
	public static Object getNetworkManager ( Player player ) throws SecurityException, NoSuchFieldException, 
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return FieldReflection.getValue ( getPlayerConnection ( player ) , "networkManager" );
	}
	
	/**
	 * Gets player's channel.
	 * <p>
	 * @param player the player to get.
	 * @return the player's channel.
	 * @throws SecurityException         reflection exception...
	 * @throws NoSuchFieldException      reflection exception...
	 * @throws IllegalArgumentException  reflection exception...
	 * @throws IllegalAccessException    reflection exception...
	 * @throws InvocationTargetException reflection exception...
	 */
	public static Channel getChannel ( Player player ) throws SecurityException, NoSuchFieldException, 
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return FieldReflection.getValue ( getNetworkManager ( player ) , "channel" );
	}
}
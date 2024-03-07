package es.outlook.adriansrj.core.util.reflection.bukkit;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.World;
import org.bukkit.entity.Player;

import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;

/**
 * Useful class for dealing with reflection of servers based on Bukkit.
 * <p>
 * @author AdrianSR / Sunday 12 April, 2020 / 11:30 PM
 */
public class BukkitReflection {

	/**
	 * Gets the handle ( the represented nms object by a craftbukkit object ) of the
	 * provided craftbukkit object.
	 * <p>
	 * @param object the object to get.
	 * @return the handle of the provided craftbukkit object.
	 * @throws SecurityException reflection exception...
	 * @throws InvocationTargetException reflection exception...
	 * @throws IllegalArgumentException reflection exception...
	 * @throws IllegalAccessException reflection exception...
	 * @throws UnsupportedOperationException if couldn't get the handle of the provided object.
	 */
	public static Object getHandle ( Object object ) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException  {
		try {
			return object.getClass().getMethod ( "getHandle" ).invoke ( object );
		} catch ( NoSuchMethodException ex_a ) {
			throw new UnsupportedOperationException ( "cannot get the handle of the provided object!" );
		}
	}
	
	/**
	 * Sends the provided packet to the desired player.
	 * <p>
	 * @param player the player that will receive the packet.
	 * @param packet the packet instance to send.
	 */
	public static void sendPacket ( Player player , Object packet ) {
		PacketReflection.sendPacket ( player , packet );
	}
	
	/**
	 * Sets the MOTD of the current running server.
	 * <p>
	 * @param motd the new MOTD for this server.
	 */
	public static void setMotd ( String motd ) {
		try {
			Class<?> server_class = ClassReflection.getNmsClass ( "MinecraftServer" );
			Object         server = MethodReflection.invokeAccessible ( MethodReflection.get ( server_class , "getServer" ) , server_class );
			
			MethodReflection.invokeAccessible ( MethodReflection.get ( server_class , "setMotd" , String.class ) , server , motd );
		} catch ( ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Clears the world border of the desired {@link World}.
	 * <p>
	 * @param world the world with the desired border to clear.
	 */
	public static void clearBorder ( World world ) {
		world.getWorldBorder().reset();
		
		try {
			FieldReflection.setValue ( world , "worldBorder", null );
		} catch (SecurityException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
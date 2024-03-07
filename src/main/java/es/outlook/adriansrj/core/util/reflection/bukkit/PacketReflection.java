package es.outlook.adriansrj.core.util.reflection.bukkit;

import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;
import es.outlook.adriansrj.core.util.server.Version;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

/**
 * Useful class for dealing with NMS packets.
 * <p>
 * Here we implement some methods that are handy for setting the value of the
 * fields of a certain packet. <strong>Those methods don't set fields by name,
 * but by index</strong>. Developers will not have to worry about filds names
 * anymore, but for field index.
 * <p>
 * @author AdrianSR / Saturday 19 June, 2021 / 02:22 PM
 */
public class PacketReflection {
	
	public static final String PLAYER_CONNECTION_FIELD_NAME;
	public static final String NETWORK_MANAGER_FIELD_NAME;
	public static final String CHANNEL_FIELD_NAME;
	
	static {
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
			PLAYER_CONNECTION_FIELD_NAME = "b";
			NETWORK_MANAGER_FIELD_NAME   = "a";
			CHANNEL_FIELD_NAME           = "k";
		} else {
			PLAYER_CONNECTION_FIELD_NAME = "playerConnection";
			NETWORK_MANAGER_FIELD_NAME   = "networkManager";
			CHANNEL_FIELD_NAME           = "channel";
		}
	}
	
	/**
	 * Sends the provided packet to the desired player.
	 * <p>
	 * @param player the player that will receive the packet.
	 * @param packet the packet instance to send.
	 */
	public static void sendPacket ( Player player , Object packet ) {
		try {
			Object      nms_player   = BukkitReflection.getHandle ( player );
			Object      connection   = FieldReflection.getValue ( nms_player , PLAYER_CONNECTION_FIELD_NAME );
			Class < ? > packet_class = null;
			
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
				packet_class = ClassReflection.getNmClass ( "Packet" , "network.protocol" );
			} else {
				packet_class = ClassReflection.getNmsClass ( "Packet" );
			}
			
			MethodReflection.getAccessible ( connection.getClass ( ) ,
											 "sendPacket" , packet_class ).invoke ( connection , packet );
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | ClassNotFoundException | NoSuchFieldException e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * Sets the value of the field that holds a integer; at the provided index.
	 * <p>
	 * @param packet he instance of the packet.
	 * @param index the index of the field to set.
	 * @param value the integer value to set.
	 */
	public static void setInteger ( Object packet , int index , int value ) {
		setValueByType ( packet , int.class , index , value );
	}
	
	/**
	 * Sets the value of the field that holds a double; at the provided index.
	 * <p>
	 * @param packet he instance of the packet.
	 * @param index the index of the field to set.
	 * @param value the double value to set.
	 */
	public static void setDouble ( Object packet , int index , double value ) {
		setValueByType ( packet , double.class , index , value );
	}
	
	/**
	 * Sets the value of the field that holds a float; at the provided index.
	 * <p>
	 * @param packet he instance of the packet.
	 * @param index the index of the field to set.
	 * @param value the float value to set.
	 */
	public static void setFloat ( Object packet , int index , float value ) {
		setValueByType ( packet , float.class , index , value );
	}
	
	/**
	 * Sets the value of the field that holds a byte; at the provided index.
	 * <p>
	 * @param packet he instance of the packet.
	 * @param index the index of the field to set.
	 * @param value the byte value to set.
	 */
	public static void setByte ( Object packet , int index , byte value ) {
		setValueByType ( packet , byte.class , index , value );
	}
	
	/**
	 * Sets the value of the field that holds a boolean; at the provided index.
	 * <p>
	 * @param packet he instance of the packet.
	 * @param index the index of the field to set.
	 * @param value the boolean value to set.
	 */
	public static void setBoolean ( Object packet , int index , boolean value ) {
		setValueByType ( packet , boolean.class , index , value );
	}
	
	/**
	 * Sets the value of the field that holds an array; at the provided index.
	 * <p>
	 * @param packet he instance of the packet.
	 * @param index the index of the field to set.
	 * @param value the array value to set.
	 */
	public static void setArray ( Object packet , int index , Object value ) {
		int current_index = 0;
		
		for ( Field field : extractFields ( packet ) ) {
			if ( field.getType ( ).isArray ( ) ) {
				if ( current_index == index ) {
					try {
						FieldReflection.setValue ( packet , field.getName ( ) , value );
					} catch ( SecurityException | NoSuchFieldException | IllegalAccessException ex ) {
						ex.printStackTrace ( );
					}
					break;
				}
				
				current_index++;
			}
		}
	}
	
	/**
	 * Sets the value of the field that holds a {@link List}; at the provided index.
	 * <p>
	 * @param packet he instance of the packet.
	 * @param index the index of the field to set.
	 * @param value the {@link List} value to set.
	 */
	public static void setList ( Object packet , int index , Object value ) {
		setValueByType ( packet , List.class , index , value );
	}
	
	/**
	 * Sets the value of the field that holds a {@link UUID}; at the provided index.
	 * <p>
	 * @param packet he instance of the packet.
	 * @param index the index of the field to set.
	 * @param value the {@link UUID} value to set.
	 */
	public static void setUUID ( Object packet , int index , UUID value ) {
		setValueByType ( packet , UUID.class , index , value );
	}
	
	/**
	 * Sets the value of the field at the specified {@code index}.
	 * <p>
	 * This will extract the fields of the provided packet (by type, using the
	 * provided {@code type_clazz} as reference), and set the value of the value
	 * at the provided index.
	 * <p>
	 * @param packet the instance of the packet.
	 * @param type_clazz the type of field that is to be set.
	 * @param index the index of the field to set.
	 * @param value the value to set.
	 */
	public static void setValueByType ( Object packet , Class < ? > type_clazz , int index , Object value ) {
		int current_index = 0;
		
		for ( Field field : extractFields ( packet ) ) {
			if ( field.getType ( ).isAssignableFrom ( type_clazz ) ) {
				if ( current_index == index ) {
					try {
						FieldReflection.setValue ( packet , field.getName ( ) , value );
					} catch ( SecurityException | NoSuchFieldException | IllegalAccessException ex ) {
						ex.printStackTrace ( );
					}
					break;
				}
				
				current_index++;
			}
		}
	}
	
	/**
	 * Extracts the fields from the provided packet instance.
	 * <p>
	 * @param packet the packet instance to extract from.
	 * @return an array with the extracted fields.
	 */
	public static Field[] extractFields ( Object packet ) {
		Field[] fields = new Field[ 0 ];
		
		for ( Field field : packet.getClass ( ).getFields ( ) ) {
			fields = ( Field[] ) ArrayUtils.add ( fields , field );
		}
		
		for ( Field field : packet.getClass ( ).getDeclaredFields ( ) ) {
			fields = ( Field[] ) ArrayUtils.add ( fields , field );
		}
		return fields;
	}
}
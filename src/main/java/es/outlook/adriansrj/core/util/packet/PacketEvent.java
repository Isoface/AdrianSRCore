package es.outlook.adriansrj.core.util.packet;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

/**
 * Simple event called whenever a packet will be sent-received.
 * <p>
 * @author AdrianSR / Saturday 14 March, 2020 / 01:58 PM
 */
public class PacketEvent {
	
	protected final Player player;
	protected       Object packet;
	
	protected boolean cancelled;
	
	/**
	 * Constructs the packet event.
	 * <p>
	 * @param player the player sending-receiving the packet.
	 * @param packet the packet.
	 */
	public PacketEvent ( final Player player , final Object packet ) {
		this.player = player;
		this.packet = packet;
	}
	
	/**
	 * Gets the player sending-receiving the packet.
	 * <p>
	 * @return the player.
	 */
	public Player getPlayer ( ) {
		return player;
	}
	
	/**
	 * The packet, that is an instance of a nms packet like "PacketPlayInSteerVehicle".
	 * <p>
	 * @return the packet.
	 */
	public Object getPacket ( ) {
		return packet;
	}
	
	/**
	 * Sets the packet that is to be sent/received.
	 *
	 * @param packet the packet to be sent/received.
	 */
	public void setPacket ( Object packet ) {
		Validate.notNull ( packet , "packet cannot be null" );
		Validate.isTrue ( this.packet.getClass ( ).isAssignableFrom ( packet.getClass ( ) ) ,
						  packet.getClass ( ) + "not assignable from " + packet.getClass ( ) );
		
		this.packet = packet;
	}
	
	/**
	 * Gets whether this event has been cancelled.
	 * <p>
	 * @return true if cancelled.
	 */
	public boolean isCancelled ( ) {
		return cancelled;
	}
	
	/**
	 * Canceling this event will avoid the packet to be sent-received.
	 * <p>
	 * @param flag true to cancel.
	 */
	public void setCancelled ( boolean flag ) {
		this.cancelled = flag;
	}
}
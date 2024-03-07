package es.outlook.adriansrj.core.events.player;

import org.bukkit.entity.Player;

import es.outlook.adriansrj.core.events.CustomEvent;

/**
 * A player related event.
 * <p>
 * @author AdrianSR / Monday 13 April, 2020 / 08:08 PM
 */
public abstract class CustomPlayerEvent extends CustomEvent {

	/**
	 * The player involved in this event.
	 */
	protected final Player player;
	
	/**
	 * Constructing a player event.
	 * <p>
	 * @param player the player involved in this event.
	 * @param async true indicates the event will fire asynchronously, false by
	 *              default from default constructor
	 */
	public CustomPlayerEvent ( Player player , boolean async ) {
		super ( async ); this.player = player;
	}
	
	/**
	 * Constructing a synchronous player event.
	 * <p>
	 * @param player the player involved in this event.
	 */
	public CustomPlayerEvent ( final Player player ) {
		this ( player , false );
	}
	
	/**
	 * Gets the player involved in this event.
	 * <p>
	 * @return the player who is involved in this event.
	 */
	public Player getPlayer() {
		return player;
	}
}
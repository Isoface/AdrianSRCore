package com.hotmail.AdrianSR.core.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Represents a Custom Player event.
 * <p>
 * @author AdrianSR
 */
public abstract class CancellableCustomPlayerEvent extends CustomPlayerEvent implements Cancellable {

	protected boolean cancelled;

	/**
	 * Construct a new Player Event.
	 * <p>
	 * @param who the player
	 */
	public CancellableCustomPlayerEvent(Player who) {
		super(who);
	}

	/**
	 * Gets the cancellation state of this event. A cancelled event will not be
	 * executed in the server, but will still pass to other plugins
	 *
	 * @return true if this event is cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Sets the cancellation state of this event. A cancelled event will not be
	 * executed in the server, but will still pass to other plugins.
	 * <p>
	 * 
	 * @param cancel true if you wish to cancel this event
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
package com.hotmail.AdrianSR.core.events.cancellable;

import org.bukkit.event.Cancellable;

import com.hotmail.AdrianSR.core.events.CustomEvent;

/**
 * Represents a Custom Event that can
 * be cancelled.
 * <p>
 * @author AdrianSR
 */
public abstract class CancellableCustomEvent extends CustomEvent implements Cancellable {

	protected boolean cancelled;
	
	/**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    public boolean isCancelled() {
    	return cancelled;
    }
	
	/**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     * <p>
     * @param cancel true if you wish to cancel this event
     */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
package com.hotmail.AdrianSR.core.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents a Custom Bukkit event.
 * 
 * @author AdrianSR
 */
public abstract class CustomEvent extends Event {

	/**
	 * The global events handler list.
	 */
	private static final HandlerList HANDLER_LIST = new HandlerList();
	
	/**
	 * Call an {@link Event}.
	 * 
	 * @param event the Event.
	 */
	public static void callEvent(final Event event) {
		Bukkit.getPluginManager().callEvent(event);
	}
	
	/**
	 * Construct a new Bukkit Event.
	 */
	public CustomEvent() {
		// nothing by default.
	}
	
	/**
	 * Call this.
	 */
	public CustomEvent call() {
		callEvent(this);
		return this;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	/**
	 * @return the Handler list.
	 */
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}
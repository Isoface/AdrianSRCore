package com.hotmail.AdrianSR.core.events.player;

import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.events.CustomEvent;

/**
 * Represents a Custom Player event.
 * <p>
 * @author AdrianSR
 */
public abstract class CustomPlayerEvent extends CustomEvent {

	protected Player player;
	
	/**
	 * Construct a new Player Event.
	 * <p>
	 * @param who the player
	 */
	public CustomPlayerEvent(Player who) {
		this.player = who;
	}
	
    /**
     * Returns the player involved in this event
     * <p>
     * @return Player who is involved in this event
     */
    public Player getPlayer() {
        return player;
    }
}
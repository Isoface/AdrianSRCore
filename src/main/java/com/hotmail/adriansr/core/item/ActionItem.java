package com.hotmail.adriansr.core.item;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Friday 30 April, 2021 / 11:44 AM
 */
public interface ActionItem {

	/**
	 * TODO: Description
	 * <p>
	 * @author AdrianSR / Friday 30 April, 2021 / 11:56 AM
	 */
	public enum EnumAction {

		LEFT_CLICK ,
		LEFT_CLICK_SNEAKING ,
		LEFT_CLICK_SPRINTING ,
		
		RIGHT_CLICK ,
		RIGHT_CLICK_SNEAKING ,
		RIGHT_CLICK_SPRINTING ,
		;
	}
	
	public String getDisplayName ( );
	
	public List < String > getLore ( );
	
	public Material getMaterial ( );
	
	public EventPriority getPriority ( );
	
	public ItemStack toItemStack ( );
	
	public boolean isThis ( ItemStack item );
	
	public void onActionPerform ( Player player , EnumAction action , PlayerInteractEvent event );
}
package es.outlook.adriansrj.core.item;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * TODO: Description
 * <p>
 *
 * @author AdrianSR / Friday 30 April, 2021 / 11:44 AM
 */
public interface ActionItem {
	
	/**
	 * Enumerates all the possible actions that can be performed with an action item.
	 *
	 * @author AdrianSR / Friday 30 April, 2021 / 11:56 AM
	 */
	enum EnumAction {
		
		LEFT_CLICK,
		LEFT_CLICK_SNEAKING,
		LEFT_CLICK_SPRINTING,
		
		RIGHT_CLICK,
		RIGHT_CLICK_SNEAKING,
		RIGHT_CLICK_SPRINTING,
		;
		
		public boolean isLeftClick ( ) {
			switch ( this ) {
				case LEFT_CLICK:
				case LEFT_CLICK_SNEAKING:
				case LEFT_CLICK_SPRINTING:
					return true;
				
				default:
					return false;
			}
		}
		
		public boolean isRightClick ( ) {
			switch ( this ) {
				case RIGHT_CLICK:
				case RIGHT_CLICK_SNEAKING:
				case RIGHT_CLICK_SPRINTING:
					return true;
				
				default:
					return false;
			}
		}
		
		public boolean isSneaking ( ) {
			switch ( this ) {
				case LEFT_CLICK_SNEAKING:
				case RIGHT_CLICK_SNEAKING:
					return true;
				
				default:
					return false;
			}
		}
		
		public boolean isSprinting ( ) {
			switch ( this ) {
				case LEFT_CLICK_SPRINTING:
				case RIGHT_CLICK_SPRINTING:
					return true;
				
				default:
					return false;
			}
		}
	}
	
	String getDisplayName ( );
	
	List < String > getLore ( );
	
	Material getMaterial ( );
	
	EventPriority getPriority ( );
	
	ItemStack toItemStack ( );
	
	boolean isThis ( ItemStack item );
	
	void onActionPerform ( Player player , EnumAction action , PlayerInteractEvent event );
}
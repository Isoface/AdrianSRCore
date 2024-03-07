package es.outlook.adriansrj.core.menu.item.action.open;

import org.bukkit.inventory.ItemStack;

import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.item.action.ItemAction;
import es.outlook.adriansrj.core.menu.item.action.ItemActionPriority;

public class OpenMenuActionItem extends ActionItem {
	
	protected ItemMenu menu;

	public OpenMenuActionItem(String name, ItemStack icon, String... lore) {
		super(name, icon, lore); addDefaultAction();
	}
	
	public OpenMenuActionItem(ItemStack icon) {
		super(icon); addDefaultAction();
	}
	
	/**
	 * Adds the default action of opening other menu.
	 */
	protected void addDefaultAction() {
		addAction(new ItemAction() {
			
			@Override
			public ItemActionPriority getPriority() {
				return ItemActionPriority.LOW;
			}

			@Override
			public void onClick(ItemClickAction action) {
				action.getMenu().getHandler().delayedClose(action.getPlayer(), 1);
				menu.getHandler().delayedOpen(action.getPlayer(), 3);
			}
		});
	}
	
	/**
	 * Sets the {@link ItemMenu} that will
	 * be opened when clicking this.
	 * <p>
	 * @param menu the menu to open when this clicked.
	 * @return this Object, for chaining.
	 */
	public OpenMenuActionItem setMenu(ItemMenu menu) {
		this.menu = menu;
		return this;
	}
}
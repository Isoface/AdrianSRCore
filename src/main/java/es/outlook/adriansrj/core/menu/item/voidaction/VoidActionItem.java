package es.outlook.adriansrj.core.menu.item.voidaction;

import org.bukkit.inventory.ItemStack;

import es.outlook.adriansrj.core.menu.Item;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;

public final class VoidActionItem extends Item {

	public VoidActionItem(String name, ItemStack icon, String... lore) {
		super(name, icon, lore);
	}

	@Override
	public final void onClick(ItemClickAction action) {
		/* do nothing */
	}
}

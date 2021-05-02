package com.hotmail.adriansr.core.menu.item.voidaction;

import org.bukkit.inventory.ItemStack;

import com.hotmail.adriansr.core.menu.Item;
import com.hotmail.adriansr.core.menu.action.ItemClickAction;

public final class VoidActionItem extends Item {

	public VoidActionItem(String name, ItemStack icon, String... lore) {
		super(name, icon, lore);
	}

	@Override
	public final void onClick(ItemClickAction action) {
		/* do nothing */
	}
}

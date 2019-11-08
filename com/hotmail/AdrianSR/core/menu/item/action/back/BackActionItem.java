package com.hotmail.AdrianSR.core.menu.item.action.back;

import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.menu.action.ItemClickAction;
import com.hotmail.AdrianSR.core.menu.item.action.ActionItem;
import com.hotmail.AdrianSR.core.menu.item.action.ItemAction;
import com.hotmail.AdrianSR.core.menu.item.action.ItemActionPriority;

public class BackActionItem extends ActionItem {

	public BackActionItem(String name, ItemStack icon, String[] lore) {
		super(name, icon, lore);
		addAction(new ItemAction() {
			
			@Override
			public ItemActionPriority getPriority() {
				return ItemActionPriority.LOW;
			}
			
			@Override
			public void onClick(ItemClickAction action) {
				action.setGoBack(true);
			}
		});
	}
}
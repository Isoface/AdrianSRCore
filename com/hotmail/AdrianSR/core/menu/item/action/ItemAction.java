package com.hotmail.adriansr.core.menu.item.action;

import com.hotmail.adriansr.core.menu.action.ItemClickAction;

public interface ItemAction {

	public ItemActionPriority getPriority();
	
	public void onClick(ItemClickAction action);
}

package com.hotmail.AdrianSR.core.menu.item.action;

import com.hotmail.AdrianSR.core.menu.action.ItemClickAction;

public interface ItemAction {

	public ItemActionPriority getPriority();
	
	public void onClick(ItemClickAction action);
}

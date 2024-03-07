package es.outlook.adriansrj.core.menu.item.action;

import es.outlook.adriansrj.core.menu.action.ItemClickAction;

public interface ItemAction {
	
	ItemActionPriority getPriority ( );
	
	void onClick ( ItemClickAction action );
}

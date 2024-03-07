package es.outlook.adriansrj.core.menu.item.action;

/**
 * @author AdrianSR / 28/08/2021 / 07:13 p. m.
 */
public interface ItemActionAdapter extends ItemAction {
	
	@Override
	default ItemActionPriority getPriority ( ) {
		return ItemActionPriority.NORMAL;
	}
}
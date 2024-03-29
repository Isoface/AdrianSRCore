package es.outlook.adriansrj.core.menu.item.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import es.outlook.adriansrj.core.menu.Item;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;

public class ActionItem extends Item {
	
	private final Set < ItemAction > actions;
	
	public ActionItem ( String name , ItemStack icon , String... lore ) {
		super ( name , icon , lore );
		this.actions = new HashSet <> ( );
	}
	
	public ActionItem ( String name , ItemStack icon , List < String > lore ) {
		super ( name , icon , lore );
		
		this.actions = new HashSet <> ( );
	}
	
	public ActionItem ( ItemStack icon ) {
		super ( icon );
		this.actions = new HashSet <> ( );
	}
	
	public ActionItem addAction ( ItemAction action ) {
		Validate.notNull ( action , "The action cannot be null!" );
		Validate.notNull ( action.getPriority ( ) , "The action priority cannot be null!" );
		
		actions.add ( action );
		return this;
	}
	
	public ActionItem addAction ( ItemActionAdapter action ) {
		// could seem redundant, but is helpful when using lambda
		return addAction ( ( ItemAction ) action );
	}
	
	public ActionItem removeAction ( ItemAction action ) {
		actions.remove ( action );
		return this;
	}
	
	@Override
	public void onClick ( ItemClickAction action ) {
		for ( int i = ItemActionPriority.values ( ).length - 1; i >= 0; i-- ) { // call in order, from high to low
			ItemActionPriority priority = ItemActionPriority.values ( )[ i ];
			actions.stream ( )
					.filter ( act -> act != null && priority.equals ( act.getPriority ( ) ) )
					.forEach ( act -> act.onClick ( action ) );
		}
	}
}

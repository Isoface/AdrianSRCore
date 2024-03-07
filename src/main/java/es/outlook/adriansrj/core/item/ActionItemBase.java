package es.outlook.adriansrj.core.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Objects;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.itemstack.ItemMetaBuilder;
import es.outlook.adriansrj.core.util.itemstack.ItemStackUtil;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Friday 30 April, 2021 / 11:47 AM
 */
public abstract class ActionItemBase implements ActionItem {

	protected final String          display_name;
	protected final List < String > lore;
	protected final Material        material;
	protected final EventPriority   priority;

	public ActionItemBase ( String display_name , Collection < String > lore , Material material , EventPriority priority ) {
		this.display_name = StringUtil.translateAlternateColorCodes ( display_name );
		this.lore         = StringUtil
				.translateAlternateColorCodes ( StringUtil.translateAlternateColorCodes ( new ArrayList <> ( lore ) ) );
		this.material     = material;
		this.priority     = priority;
	}
	
	public ActionItemBase ( String display_name , Collection < String > lore , Material material ) {
		this ( display_name , lore , material , EventPriority.NORMAL );
	}

	@Override
	public String getDisplayName ( ) {
		return display_name;
	}

	@Override
	public List < String > getLore ( ) {
		return lore;
	}

	@Override
	public Material getMaterial ( ) {
		return material;
	}
	
	@Override
	public EventPriority getPriority ( ) {
		return priority;
	}

	@Override
	public ItemStack toItemStack ( ) {
		return new ItemMetaBuilder ( getMaterial ( ) )
				.withDisplayName ( getDisplayName ( ) )
				.withLore ( getLore ( ) ).toItemStack ( );
	}

	@Override
	public boolean isThis ( ItemStack item ) {
		if ( item != null ) {
			return item.getType ( ) == getMaterial ( )
					&& Objects.equal ( ItemStackUtil.extractName ( item , false ) , getDisplayName ( ) )
					&& Objects.equal ( ItemStackUtil.extractLore ( item , false ) , getLore ( ) );
		} else {
			return false;
		}
	}
}
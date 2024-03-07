package es.outlook.adriansrj.core.menu.item.action.close;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.item.action.ItemAction;
import es.outlook.adriansrj.core.menu.item.action.ItemActionPriority;
import es.outlook.adriansrj.core.util.itemstack.safe.SafeItemStack;
import es.outlook.adriansrj.core.util.itemstack.stainedglass.StainedGlassColor;
import es.outlook.adriansrj.core.util.itemstack.stainedglass.StainedGlassItemStack;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;

public class CloseMenuActionItem extends ActionItem {
	
	@Deprecated // Compatibility with server versions <= 1.8
	public static final ItemStack DEFAULT_ICON = EnumReflection.getEnumConstant(Material.class, "BARRIER") != null
			? new SafeItemStack(Material.BARRIER)
			: new StainedGlassItemStack(StainedGlassColor.RED, true);
	
	public CloseMenuActionItem(String... lore) {
		this(ChatColor.RED + "Close", lore);
	}
	
	public CloseMenuActionItem(String name, String... lore) {
		this(name, DEFAULT_ICON, lore);
	}

	public CloseMenuActionItem(String name, ItemStack icon, String... lore) {
		super(name, icon, lore);
		addAction(new ItemAction() {
			
			@Override
			public ItemActionPriority getPriority() {
				return ItemActionPriority.LOW;
			}
			
			@Override
			public void onClick(ItemClickAction action) {
				action.setClose(true);
			}
		});
	}
}
package es.outlook.adriansrj.core.util.itemstack;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an enchantment that can be applied to an {@link ItemStack}.
 * <p>
 * @author AdrianSR / Wednesday 09 June, 2021 / 10:52 AM
 */
public class ItemStackEnchantment {

	protected final Enchantment enchantment;
	protected final int         level;
	
	public ItemStackEnchantment ( Enchantment enchantment , int level ) {
		this.enchantment = enchantment;
		this.level       = level;
	}

	public Enchantment getEnchantment ( ) {
		return enchantment;
	}

	public int getLevel ( ) {
		return level;
	}
	
	public ItemStack applyTo ( ItemStack stack ) {
		return ItemStackUtil.addEnchantment ( stack , enchantment , level );
	}
}
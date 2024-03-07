package es.outlook.adriansrj.core.util.itemstack.safe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import es.outlook.adriansrj.core.util.itemstack.custom.CustomItemStack;
import es.outlook.adriansrj.core.util.material.MaterialUtils;

/**
 * A {@link ItemStack} that is safe because of uses
 * the method {@link MaterialUtils#getRightMaterial(Material)}.
 * <p>
 * @author AdrianSR
 */
public class SafeItemStack extends CustomItemStack {

	/**
	 * Construct new {@link SafeItemStack}.
	 * <p>
	 * @param material the type.
	 */
	public SafeItemStack(Material material) {
		this(material, 1);
	}
	
	/**
	 * Construct new {@link SafeItemStack}.
	 * <p>
	 * @param material the type.
	 * @param amount the stack size.
	 */
	public SafeItemStack(Material material, int amount) {
		super.setType(MaterialUtils.getRightMaterial(material));
		this.setAmount(amount);
	}
}

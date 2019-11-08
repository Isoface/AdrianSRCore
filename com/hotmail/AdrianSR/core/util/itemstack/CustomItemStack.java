package com.hotmail.AdrianSR.core.util.itemstack;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public interface CustomItemStack {

	/**
	 * Checks for existence of a display name.
	 * <p>
	 * @return true if this has a display name
	 */
	public boolean hasDisplayName();

	/**
	 * Gets the display name that is set.
	 * <p>
	 * Plugins should check that hasDisplayName() returns <code>true</code> before
	 * calling this method.
	 * <p>
	 * @return the display name that is set
	 */
	public String getDisplayName();

	/**
	 * Sets the display name.
	 * <p>
	 * @param name the name to set
	 */
	public CustomItemStack setDisplayName(String name);

	/**
	 * Checks for existence of lore.
	 * v
	 * @return true if this has lore
	 */
	public boolean hasLore();

	/**
	 * Gets the lore that is set.
	 * <p>
	 * Plugins should check if hasLore() returns <code>true</code> before calling
	 * this method.
	 * <p>
	 * @return a list of lore that is set
	 */
	public List<String> getLore();

	/**
	 * Sets the lore for this item. Removes lore when given null.
	 * <p>
	 * @param lore the lore that will be set
	 */
	public CustomItemStack setLore(List<String> lore);
	
	/**
	 * Sets the lore for this item. Removes lore when given null.
	 * <p>
	 * @param lore the lore that will be set
	 */
	public CustomItemStack setLore(String[] lore);

	/**
	 * Checks for the existence of any enchantments.
	 * <p>
	 * @return true if an enchantment exists on this meta
	 */
	public boolean hasEnchants();

	/**
	 * Checks for existence of the specified enchantment.
	 * <p>
	 * @param ench enchantment to check
	 * @return true if this enchantment exists for this meta
	 */
	public boolean hasEnchant(Enchantment ench);

	/**
	 * Checks for the level of the specified enchantment.
	 * <p>
	 * @param ench enchantment to check
	 * @return The level that the specified enchantment has, or 0 if none
	 */
	public int getEnchantLevel(Enchantment ench);

	/**
	 * Returns a copy the enchantments in this ItemMeta. <br>
	 * Returns an empty map if none.
	 * <p>
	 * @return An immutable copy of the enchantments
	 */
	public Map<Enchantment, Integer> getEnchants();

	/**
	 * Adds the specified enchantment to this item meta.
	 * <p>
	 * @param ench                   Enchantment to add
	 * @param level                  Level for the enchantment
	 * @param ignoreLevelRestriction this indicates the enchantment should be
	 *                               applied, ignoring the level limit
	 * @return true if the item meta changed as a result of this call, false
	 *         otherwise
	 */
	public boolean addEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction);

	/**
	 * Removes the specified enchantment from this item meta.
	 * <p>
	 * @param ench Enchantment to remove
	 * @return true if the item meta changed as a result of this call, false
	 *         otherwise
	 */
	public boolean removeEnchant(Enchantment ench);

	/**
	 * Checks if the specified enchantment conflicts with any enchantments in this
	 * ItemMeta.
	 * <p>
	 * @param ench enchantment to test
	 * @return true if the enchantment conflicts, false otherwise
	 */
	public boolean hasConflictingEnchant(Enchantment ench);

	/**
	 * Set itemflags which should be ignored when rendering a ItemStack in the
	 * Client. This Method does silently ignore double set itemFlags.
	 * <p>
	 * @param itemFlags The hideflags which shouldn't be rendered
	 */
	public CustomItemStack addItemFlags(ItemFlag... itemFlags);

	/**
	 * Remove specific set of itemFlags. This tells the Client it should render it
	 * again. This Method does silently ignore double removed itemFlags.
	 * <p>
	 * @param itemFlags Hideflags which should be removed
	 */
	public CustomItemStack removeItemFlags(ItemFlag... itemFlags);

	/**
	 * Get current set itemFlags. The collection returned is unmodifiable.
	 * <p>
	 * @return A set of all itemFlags set
	 */
	public Set<ItemFlag> getItemFlags();

	/**
	 * Check if the specified flag is present on this item.
	 * <p>
	 * @param flag the flag to check
	 * @return if it is present
	 */
	public boolean hasItemFlag(ItemFlag flag);
}

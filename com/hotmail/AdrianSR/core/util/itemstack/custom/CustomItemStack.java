package com.hotmail.AdrianSR.core.util.itemstack.custom;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class CustomItemStack extends ItemStack implements com.hotmail.AdrianSR.core.util.itemstack.CustomItemStack {
	
	@Override
	public boolean hasDisplayName() {
		return this.getItemMeta().hasDisplayName();
	}

	@Override
	public String getDisplayName() {
		return this.getItemMeta().getDisplayName();
	}

	@Override
	public CustomItemStack setDisplayName(String name) {
		this.getItemMeta().setDisplayName(name);
		return this;
	}

	@Override
	public boolean hasLore() {
		return this.getItemMeta().hasLore();
	}

	@Override
	public List<String> getLore() {
		return this.getItemMeta().getLore();
	}

	@Override
	public CustomItemStack setLore(List<String> lore) {
		this.getItemMeta().setLore(lore);
		return this;
	}
	
	@Override
	public CustomItemStack setLore(String[] lore) {
		return this.setLore(Arrays.asList(lore));
	}

	@Override
	public boolean hasEnchants() {
		return this.getItemMeta().hasEnchants();
	}

	@Override
	public boolean hasEnchant(Enchantment ench) {
		return this.getItemMeta().hasEnchant(ench);
	}

	@Override
	public int getEnchantLevel(Enchantment ench) {
		return this.getItemMeta().getEnchantLevel(ench);
	}

	@Override
	public Map<Enchantment, Integer> getEnchants() {
		return this.getItemMeta().getEnchants();
	}

	@Override
	public boolean addEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction) {
		return this.getItemMeta().addEnchant(ench, level, ignoreLevelRestriction);
	}

	@Override
	public boolean removeEnchant(Enchantment ench) {
		return this.getItemMeta().removeEnchant(ench);
	}

	@Override
	public boolean hasConflictingEnchant(Enchantment ench) {
		return this.getItemMeta().hasConflictingEnchant(ench);
	}

	@Override
	public CustomItemStack addItemFlags(ItemFlag... itemFlags) {
		this.getItemMeta().addItemFlags(itemFlags);
		return this;
	}

	@Override
	public CustomItemStack removeItemFlags(ItemFlag... itemFlags) {
		this.getItemMeta().removeItemFlags(itemFlags);
		return this;
	}

	@Override
	public Set<ItemFlag> getItemFlags() {
		return this.getItemMeta().getItemFlags();
	}

	@Override
	public boolean hasItemFlag(ItemFlag flag) {
		return this.getItemMeta().hasItemFlag(flag);
	}
}
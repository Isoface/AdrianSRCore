package com.hotmail.AdrianSR.core.menu.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.menu.action.ItemClickAction;
import com.hotmail.AdrianSR.core.util.TextUtils;
import com.hotmail.AdrianSR.core.util.itemstack.ItemMetaBuilder;
import com.hotmail.AdrianSR.core.util.itemstack.ItemStackUtils;
import com.hotmail.AdrianSR.core.util.material.MaterialUtils;

public abstract class Item {

	protected String       name;
	protected ItemStack    icon;
	protected List<String> lore;

	public Item(String name, ItemStack icon, String... lore) {
		Validate.notNull(icon, "The icon cannot be null!");
		this.name = TextUtils.getNotNull(name, "null name");
		this.icon = icon;
		this.lore = Arrays.asList(lore != null ? lore : new String[0]);
	}
	
	public Item(ItemStack icon) {
		this(TextUtils.getNotNull((icon.getItemMeta() != null ? icon.getItemMeta().getDisplayName() : null), "null name"),
				icon,
			( ItemStackUtils.extractLore(icon, false).toArray(new String[ItemStackUtils.extractLore(icon, false).size()]) ) );
	}

	public String getName() {
		return name;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public ItemStack getDisplayIcon() {
		return new ItemMetaBuilder(MaterialUtils.getRightMaterial(getIcon()))
				.withDisplayName(TextUtils.translateColors(getName()))
				.withLore(getLore()).applyTo(getIcon().clone());
	}

	public List<String> getLore() {
		return lore;
	}

	public Item setName(String name) {
		this.name = name;
		return this;
	}

	public Item setIcon(ItemStack icon) {
		Validate.notNull(icon, "The icon cannot be null!");
		this.icon = icon;
		return this;
	}

	public Item setLore(List<String> lore) {
		this.lore = lore != null ? lore : new ArrayList<String>();
		return this;
	}
	
	public abstract void onClick(final ItemClickAction action);
}
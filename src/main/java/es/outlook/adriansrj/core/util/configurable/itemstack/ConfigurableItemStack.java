package es.outlook.adriansrj.core.util.configurable.itemstack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.enchantment.ConfigurableItemStackEnchantment;
import es.outlook.adriansrj.core.util.itemstack.ItemMetaBuilder;
import es.outlook.adriansrj.core.util.itemstack.ItemStackEnchantment;
import es.outlook.adriansrj.core.util.loadable.LoadableCollectionEntry;
import es.outlook.adriansrj.core.util.material.MaterialUtils;
import es.outlook.adriansrj.core.util.saveable.SavableCollectionEntry;
import es.outlook.adriansrj.core.util.saveable.SavableEntry;

public class ConfigurableItemStack implements Configurable {
	
	public static final String TYPE_KEY         = "type";
	public static final String SIZE_KEY         = "size";
	public static final String NAME_KEY         = "name";
	public static final String LORE_KEY         = "lore";
	public static final String DATA_KEY         = "data";
	public static final String ENCHANTS_SECTION = "enchantments";
	public static final String ENCHANT_SUBSECTION_PREFIX = "enchantment-";
	
	public @SavableEntry (key = TYPE_KEY) String       type;
	public @SavableEntry (key = SIZE_KEY) int          size;
	public @SavableEntry (key = NAME_KEY) String       name;
	public @SavableEntry (key = LORE_KEY) List<String> lore;
	public @SavableEntry (key = DATA_KEY) short        data;
	
	@LoadableCollectionEntry(subsection = ENCHANTS_SECTION)
	@SavableCollectionEntry ( subsection = ENCHANTS_SECTION, subsectionprefix = ENCHANT_SUBSECTION_PREFIX)
	public final Set<ConfigurableItemStackEnchantment> enchants = new HashSet<>();
	
	/**
	 * Construct a new uninitialized {@link ConfigurableItemStack}.
	 */
	public ConfigurableItemStack() {
		/* uninitialized */
	}
	
	/**
	 * Construct a {@link ConfigurableItemStack}
	 * getting the default values from the
	 * given {@link ItemStack}.
	 * <p>
	 * @param stack the {@link ItemStack} to get the default values.
	 */
	@SuppressWarnings("deprecation")
	public ConfigurableItemStack(ItemStack stack) {
		this(MaterialUtils.getRightMaterial(stack).name(), stack.getAmount(), stack.getItemMeta().getDisplayName(),
				stack.getItemMeta().getLore(), stack.getDurability());
	}
	
	/**
	 * Construct a {@link ConfigurableItemStack}
	 * with default values.
	 * <p>
	 * @param type default type.
	 * @param size default size.
	 * @param name default name.
	 * @param lore default lore.
	 * @param data default data.
	 */
	public ConfigurableItemStack(String type, int size, String name, List<String> lore, short data) {
		this.type = type;
		this.size = size;
		this.name = name != null ? StringUtil.untranslateAlternateColorCodes ( name ) : name;
		this.lore = lore != null ? StringUtil.untranslateAlternateColorCodes ( new ArrayList < > ( lore ) ) : null;
		this.data = data;
	}
	
	/**
	 * Construct a {@link ConfigurableItemStack}
	 * with default values.
	 * <p>
	 * @param type default type.
	 * @param size default size.
	 * @param name default name.
	 * @param lore default lore.
	 * @param data default data.
	 */
	public ConfigurableItemStack(String type, int size, String name, String[] lore, short data) {
		this(type, size, name, Arrays.asList(lore), data);
	}

	/**
	 * Load values from the given {@link ConfigurationSection}.
	 * <p>
	 * @param section the {@link ConfigurationSection} to load values.
	 * @return this Object, for chaining.
	 */
	@Override
	public ConfigurableItemStack load(ConfigurationSection section) {
		Validate.notNull(section, "The section cannot be null!");
		this.type = section.getString(TYPE_KEY, null);
		this.size = section.getInt(SIZE_KEY, -1);
		this.name = StringUtil.untranslateAlternateColorCodes ( section.getString ( NAME_KEY , StringUtil.EMPTY ) );
		this.lore = section.isList ( LORE_KEY )
				? StringUtil.untranslateAlternateColorCodes ( section.getStringList ( LORE_KEY ) ) : null;
		this.data = (short) section.getInt(DATA_KEY, 0);
		return (ConfigurableItemStack) this.loadEntries(section); // load enchantments by loading entries
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return this.saveEntries ( section );
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.setType(type, true);
	}
	
	public void setType(String type, boolean overwrite) {
		this.type = overwrite ? type : ( this.type == null ? type : this.type );
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.setSize(size, true);
	}
	
	public void setSize(int size, boolean overwrite) {
		this.size = overwrite ? size : ( this.size == -1 ? size : this.size );
	}

	public String getName ( ) {
		return name != null ? StringUtil.translateAlternateColorCodes ( name ) : name;
	}
	
	public void setName ( String name ) {
		this.setName ( name , true );
	}

	public void setName ( String name , boolean overwrite ) {
		name      = name != null ? StringUtil.untranslateAlternateColorCodes ( name ) : "";
		this.name = overwrite ? name : ( this.name == null ? name : this.name );
	}
	
	public List < String > getLore ( ) {
		return lore != null ? StringUtil.translateAlternateColorCodes ( new ArrayList <> ( lore ) ) : null;
	}
	
	public void setLore ( List < String > lore ) {
		this.setLore ( lore , true );
	}

	public void setLore ( List < String > lore , boolean overwrite ) {
		lore      = lore != null ? StringUtil.untranslateAlternateColorCodes ( new ArrayList <> ( lore ) ) : null;
		this.lore = overwrite ? lore : ( this.lore == null ? lore : this.lore );
	}
	
	public short getData() {
		return data;
	}
	
	public void setData(short data) {
		this.setData(data, true);
	}
	
	public void setData(short data, boolean overwrite) {
		this.data = overwrite ? data : ( this.data == -1 ? data : this.data );
	}
	
	public Set<ConfigurableItemStackEnchantment> getEnchantments() {
		return enchants;
	}
	
	public void addEnchantment(ConfigurableItemStackEnchantment enchantment) {
		Validate.notNull(enchantment, "The enchantment cannot be null!");
		Validate.isTrue(enchantment.isInvalid(), "The enchantment is invalid!");
		this.enchants.add(enchantment);
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack toItemStack() {
		ItemMetaBuilder builder = new ItemMetaBuilder(MaterialUtils.getRightMaterial(Material.valueOf(getType())));
		
		// applying enchantments
		getEnchantments ( ).stream ( ).filter ( ConfigurableItemStackEnchantment :: isValid ).forEach ( enchantment -> {
			ItemStackEnchantment wrapper = enchantment.toEnchantment ( );
			
			builder.withEnchantment ( wrapper.getEnchantment ( ) , wrapper.getLevel ( ) );
		});
		
		return builder.withDisplayName(getName())
				.withLore(getLore())
				.applyTo(new ItemStack(MaterialUtils.getRightMaterial(Material.valueOf(getType())), getSize(), getData()));
	}
	
	@Override
	public boolean isValid() {
		return     getType() != null && Material.valueOf(getType()) != null
				&& getName() != null 
				&& getSize() > -1;
	}

	@Override
	public boolean isInvalid() {
		return !isValid();
	}
}
package es.outlook.adriansrj.core.util.itemstack;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents an useful class for handling ItemStacks.
 * <p>
 * @author AdrianSR
 */
public class ItemStackUtil {
	
	/**
	 * Whether the field 'durability' is present in the {@link ItemStack} class of
	 * this server version.
	 */
	public static final boolean AVAILABLE_DURABILITY_FIELD;
	
	static {
		/* durability field */
		boolean durability_field = false;
		try {
			durability_field = new ItemStack ( Material.AIR ).getClass ( )
					.getDeclaredField ( "durability" ) != null;
		} catch ( NoSuchFieldException | SecurityException e ) {
			durability_field = false;
		}
		AVAILABLE_DURABILITY_FIELD = durability_field;
	}
	
	public static ItemStack ofMaterial ( Material material , int amount ) {
		if ( Version.getServerVersion ( ).isNewer ( Version.v1_12_R1 ) ) {
			return new ItemStack ( material );
		}
		return new ItemStack ( material , amount );
	}
	
	public static ItemStack ofUniversalMaterial ( UniversalMaterial material , int amount ) {
		if ( material.getMaterial ( ) == null ) {
			return null;
		}
		
		if ( Version.getServerVersion ( ).isNewer ( Version.v1_12_R1 ) ) {
			return new ItemStack ( material.getMaterial ( ) );
		}
		return new ItemStack ( material.getMaterial ( ) , amount );
	}
	
	/**
	 * Gets the {@link ItemMeta} of the given {@link ItemStack}.
	 * <p>
	 * A new {@link ItemMeta} will created if necessary.
	 * <p>
	 * @param stack the {@link ItemStack} to get.
	 * @return the {@link ItemMeta} of the given {@link ItemMeta}.
	 */
	public static ItemMeta getItemMeta ( ItemStack stack ) {
		ItemMeta meta = stack.getItemMeta ( );
		if ( meta == null ) {
			meta = Bukkit.getItemFactory ( ).getItemMeta ( stack.getType ( ) );
		}
		return meta;
	}
	
	/**
	 * Convert {@link ItemStack} to Soulbound item.
	 *
	 * @param stack the ItemStack to convert.
	 * @return
	 */
	public static ItemStack addSoulbound ( ItemStack stack ) {
		if ( stack == null ) {
			return stack;
		}
		
		ItemMeta meta = stack.getItemMeta ( );
		if ( meta == null ) {
			meta = Bukkit.getItemFactory ( ).getItemMeta ( stack.getType ( ) );
		}
		
		List < String > lore = meta.getLore ( );
		if ( lore == null ) {
			lore = new ArrayList < String > ( );
		}
		
		lore.add ( "Soulbound" );
		meta.setLore ( lore );
		stack.setItemMeta ( meta );
		return stack;
	}
	
	/**
	 * Check if {@link ItemStack} is Soulbound.
	 *
	 * @param stack the ItemStack to check.
	 * @return true if is a Soulboud.
	 */
	public static boolean isSoulbound ( ItemStack stack ) {
		if ( stack == null ) {
			return false;
		}
		
		ItemMeta meta = stack.getItemMeta ( );
		if ( meta == null ) {
			return false;
		}
		
		List < String > lore = meta.getLore ( );
		if ( lore == null ) {
			return false;
		}
		return lore.contains ( "Soulbound" );
	}
	
	/**
	 * Extracts the name from the {@link ItemMeta} of an {@link ItemStack}.
	 * <p>
	 * @param stack the {@link ItemStack} to extract.
	 * @param strip_colors strip colors?
	 * @return the display name of the given {@link ItemStack} or an empty string if it doesn't have name.
	 */
	public static String extractName ( ItemStack stack , boolean strip_colors ) {
		if ( stack == null || stack.getItemMeta ( ) == null ) {
			return "";
		}
		
		String displayName = stack.getItemMeta ( ).getDisplayName ( );
		return displayName == null ? "" : ( strip_colors ? StringUtil.stripColors ( displayName ) : displayName );
	}
	
	/**
	 * Extracts the lore from the {@link ItemMeta} of an {@link ItemStack}.
	 * <p>
	 * @param stack the {@link ItemStack} to extract.
	 * @param strip_colors strip colors of the lore?
	 * @return the lore of the given {@link ItemStack} or an empty list if it doesn't have lore.
	 */
	public static List < String > extractLore ( ItemStack stack , boolean strip_colors ) {
		List < String > lore = new ArrayList <> ( );
		if ( stack != null && stack.getItemMeta ( ) != null && stack.getItemMeta ( ).getLore ( ) != null ) {
			lore = new ArrayList <> ( stack.getItemMeta ( ).getLore ( ) );
			if ( strip_colors ) {
				for ( int i = 0 ; i < lore.size ( ) ; i++ ) {
					lore.set ( i , StringUtil.stripColors ( lore.get ( i ) ) );
				}
			}
		}
		return lore;
	}
	
	/**
	 * Extracts the enchantments from the provided {@link ItemStack}.
	 * <p>
	 * @param stack the stack to extract from.
	 * @return the extracted enchantments.
	 */
	public static List < ItemStackEnchantment > extractEnchantments ( ItemStack stack ) {
		List < ItemStackEnchantment > result = new ArrayList < ItemStackEnchantment > ( );
		
		stack.getEnchantments ( )
				.forEach ( ( type , level ) -> result.add ( new ItemStackEnchantment ( type , level ) ) );
		return result;
	}
	
	/**
	 * Set {@link ItemStack} name and lore.
	 *
	 * @param itemStack the ItemStack to modify.
	 * @param name new name.
	 * @param lore new lore.
	 * @return modified ItemStack.
	 */
	public static ItemStack setNameLore ( ItemStack itemStack , String name , List < String > lore ) {
		ItemStack ot = itemStack;
		if ( name != null ) {
			ot = setName ( itemStack , name );
		}
		return setLore ( ot , lore );
	}
	
	/**
	 * Set {@link ItemStack} set ItemStack name.
	 *
	 * @param itemStack the ItemStack to modify.
	 * @param name new name.
	 * @return modified ItemStack.
	 */
	public static ItemStack setName ( ItemStack itemStack , String name ) {
		ItemMeta meta = itemStack.getItemMeta ( );
		if ( meta == null ) {
			meta = Bukkit.getItemFactory ( ).getItemMeta ( itemStack.getType ( ) );
		}
		
		if ( meta == null ) {
			return itemStack;
		}
		
		meta.setDisplayName ( StringUtil.translateAlternateColorCodes ( name ) );
		itemStack.setItemMeta ( meta );
		return itemStack;
	}
	
	/**
	 * Set {@link ItemStack} set ItemStack lore.
	 *
	 * @param itemStack the ItemStack to modify.
	 * @param lore new lore.
	 * @return modified ItemStack.
	 */
	public static ItemStack setLore ( ItemStack itemStack , List < String > lore ) {
		if ( lore == null || lore.isEmpty ( ) ) {
			return itemStack;
		}
		
		ItemMeta meta = itemStack.getItemMeta ( );
		if ( meta == null ) {
			meta = Bukkit.getItemFactory ( ).getItemMeta ( itemStack.getType ( ) );
		}
		
		if ( meta == null ) {
			return itemStack;
		}
		
		for ( int x = 0 ; x < lore.size ( ) ; x++ ) {
			lore.set ( x , StringUtil.translateAlternateColorCodes ( lore.get ( x ) ) );
		}
		
		meta.setLore ( lore );
		itemStack.setItemMeta ( meta );
		return itemStack;
	}
	
	@SuppressWarnings ( "deprecation" )
	public static ItemStack setData ( ItemStack item , int data ) {
		item.setData ( item.getType ( ).getNewData ( ( byte ) data ) );
		
		if ( Version.getServerVersion ( ).isOlder ( Version.v1_13_R1 ) ) {
			item.setDurability ( ( short ) data );
		}
		
		return item;
	}

	/**
	 * Add enchant to {@link ItemStack}.
	 *
	 * @param stack the ItemStack.
	 * @param enchant the Enchantment.
	 * @param level the enchant level.
	 * @return a enchanted ItemStack.
	 */
	public static ItemStack addEnchantment ( final ItemStack stack , final Enchantment enchant , int level ) {
		// get item meta.
		ItemMeta meta = stack.getItemMeta ( );
		if ( meta == null ) {
			meta = Bukkit.getItemFactory ( ).getItemMeta ( stack.getType ( ) );
		}
		
		// add enchan.
		meta.addEnchant ( enchant , level , true );
		
		// update meta.
		stack.setItemMeta ( meta );
		return stack;
	}
	
	/**
	 * Check two items have the the same lore.
	 *
	 * @param i1 the first ItemStack.
	 * @param i2 the second ItemStack.
	 * @return true if have the same lore.
	 */
	public static boolean equalsLore ( final ItemStack i1 , final ItemStack i2 ) {
		// check not null.
		if ( ( i1 != null ) == ( i2 != null ) ) {
			// check meta.
			if ( ( i1.getItemMeta ( ) != null ) == ( i2.getItemMeta ( ) != null ) ) {
				// check has item meta.
				if ( i1.getItemMeta ( ) == null ) {
					return true;
				}
				
				// check meta lore.
				if ( i1.getItemMeta ( ).hasLore ( ) == i2.getItemMeta ( ).hasLore ( ) ) {
					// check has lore.
					if ( !i1.getItemMeta ( ).hasLore ( ) ) {
						return true;
					}
					
					// get lores.
					final List < String > lore1 = i1.getItemMeta ( ).getLore ( );
					final List < String > lore2 = i2.getItemMeta ( ).getLore ( );
					
					// compare lores.
					for ( String line : lore1 ) {
						// check not null
						if ( line == null ) {
							continue;
						}
						
						// check if the other contains.
						if ( !lore2.contains ( line ) ) {
							return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Get the player textured skull {@link ItemStack}.
	 *
	 * @param p the player texture owner.
	 * @return a skull ItemStack textured with the player skin.
	 */
	public static ItemStack getSkull ( final Player p ) {
		return p != null ? createSkull ( getTexture ( p.getName ( ) ) , p.getName ( ) ) : getSkullMaterial ( 1 );
	}
	
	/**
	 * Create a Skull ItemStack.
	 *
	 * @param texture the texture.
	 * @param displayname the item display name.
	 * @return the textured skull item stack.
	 */
	private static ItemStack createSkull ( String texture , String displayname ) {
		// get item and get meta.
		final ItemStack stack = getSkullMaterial ( 1 );
		final SkullMeta meta  = setSkullMeta ( ( ( SkullMeta ) stack.getItemMeta ( ) ) , texture );
		
		// set display name.
		meta.setDisplayName ( StringUtil.translateAlternateColorCodes ( displayname ) );
		
		// update meta.
		stack.setItemMeta ( meta );
		return stack;
	}
	
	/**
	 * Get texture from owner name.
	 *
	 * @param owner the skull owner.
	 * @return the texture.
	 */
	@SuppressWarnings ( "deprecation" )
	private static String getTexture ( String owner ) {
		// get Game Profile and return property.
		OfflinePlayer offPlayer = Bukkit.getOfflinePlayer ( owner );
		GameProfile   profile   = new GameProfile ( offPlayer.getUniqueId ( ) , owner );
		return profile.getProperties ( ).get ( "textures" ).iterator ( ).next ( ).getValue ( );
	}
	
	/**
	 * Sets the skull texture property.
	 *
	 * @param skullMeta the {@link SkullMeta}.
	 * @param texture the texture.
	 * @return textured SkullMeta.
	 */
	private static SkullMeta setSkullMeta ( final SkullMeta skullMeta , final String texture ) {
		// get profile.
		GameProfile profile = new GameProfile ( UUID.randomUUID ( ) , "HPXHead" );
		
		// put textures property.
		profile.getProperties ( ).put ( "textures" , new Property ( "texture" , texture ) );
		
		// set field.
		Field profileField = null;
		try {
			profileField = skullMeta.getClass ( ).getDeclaredField ( "profile" );
		} catch ( NoSuchFieldException | SecurityException e ) {
			e.printStackTrace ( );
		}
		profileField.setAccessible ( true );
		try {
			profileField.set ( skullMeta , profile );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace ( );
		}
		return skullMeta;
	}
	
	/**
	 * Get a Skull Item stack.
	 *
	 * @param amount the stack amount.
	 * @return a Skull Item Stack.
	 */
	@SuppressWarnings ( "deprecation" )
	private static ItemStack getSkullMaterial ( int amount ) {
		return new ItemStack ( Material.getMaterial ( "SKULL_ITEM" ) , amount , ( byte ) 3 );
	}
	
	/**
	 * Get {@link PlayerInventory} contents. 
	 *
	 * @param inventory the inventory.
	 * @return the PlayerInventory list of contents.
	 */
	public static final List < ItemStack > getAllContents ( final PlayerInventory inventory ,
			boolean addArmorContents ) {
		final List < ItemStack > contents = new ArrayList < ItemStack > ( );
		for ( int x = 0 ; x < 2 ; x++ ) {
			if ( x > 0 && !addArmorContents ) {
				break;
			}
			
			for ( ItemStack stack : ( x == 0 ? inventory.getContents ( ) : inventory.getArmorContents ( ) ) ) {
				contents.add ( stack );
			}
		}
		return contents;
	}
	
	/**
	 * Returns an empty {@link ItemStack}.
	 * <p>
	 * @return empty {@link ItemStack}.
	 */
	public static ItemStack getEmptyStack ( ) {
		return new ItemStack ( Material.AIR );
	}
	
	/**
	 * Sets whether the provided {@link ItemStack} is unbreakable.
	 * <p>
	 * @param item the item to set.
	 * @param unbreakable whether the provided {@link ItemStack} will be unbreakable or not.
	 * @return the resulting item.
	 */
	@SuppressWarnings ( "deprecation" )
	public static ItemStack setUnbreakable ( ItemStack item , boolean unbreakable ) {
		item.setItemMeta ( setUnbreakable ( getItemMeta ( item ) , unbreakable ) );
		return item;
	}
	
	/**
	 * Sets whether the provided {@link ItemMeta} is unbreakable or not.
	 * <p>
	 * @param meta the meta to set.
	 * @param unbreakable whether the provided {@link ItemMeta} will be unbreakable or not.
	 * @return the same Object, for chaining.
	 */
	public static ItemMeta setUnbreakable ( ItemMeta meta , boolean unbreakable ) {
		try {
			meta.setUnbreakable ( unbreakable );
		} catch ( NoSuchMethodError ex ) {
			// legacy versions support
			try {
				Object      spigot       = ItemMeta.class.getMethod ( "spigot" ).invoke ( meta );
				Class < ? > spigot_class = ItemMeta.class.getClasses ( )[ 0 ];
				
				spigot_class.getMethod ( "setUnbreakable" , boolean.class ).invoke ( spigot , unbreakable );
			} catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
				e.printStackTrace ( );
			}
		}
		return meta;
	}
}
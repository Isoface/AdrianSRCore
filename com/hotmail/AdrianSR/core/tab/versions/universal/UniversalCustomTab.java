package com.hotmail.AdrianSR.core.tab.versions.universal;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.tab.TabList;
import com.hotmail.AdrianSR.core.util.Schedulers;
import com.hotmail.AdrianSR.core.util.TextUtils;
import com.hotmail.AdrianSR.core.util.UpdatableEntity;
import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;
import com.mojang.authlib.GameProfile;

/**
 * Represents the Custom tab class
 * that is supported by all the Spigot version
 * at the moment when was created.
 * <p> 
 * @author AdrianSR
 */
public class UniversalCustomTab implements TabList {
	
	private static final String    ADD_PLAYER_ENUM_CONSTANT_NAME = "ADD_PLAYER";
	private static final String REMOVE_PLAYER_ENUM_CONSTANT_NAME = "REMOVE_PLAYER";

	/**
	 * Tab data.
	 */
	private final UpdatableEntity       owner;
	private final Player      nullable_player;
	private String[]                   header;
	private String[]                   footer;
	private final CustomPlugin         plugin;
	private final Map<String, Object> entries; // Map<String, net.minecraft.server.EntityPlayer>
	private boolean                     setup;

	/**
	 * Construct a new custom tab.
	 * <p>
	 * @param player the tab Player viewer.
	 * @param header the tab header.
	 * @param footer the tab footer.
	 */
	public UniversalCustomTab(Player player, String[] header, String[] footer, CustomPlugin plugin) {
		// load tab data.
		this.owner           = new UpdatableEntity(player);
		this.nullable_player = player;
		this.header  = TextUtils.translateColors(Arrays.asList(header)).toArray(new String[header.length]);
		this.footer  = TextUtils.translateColors(Arrays.asList(footer)).toArray(new String[footer.length]);
		this.plugin  = plugin;
		this.entries = new HashMap<>();
		
		// setup.
		Schedulers.async(() -> {
			setup();
		}, plugin);
	}
	
	@Override
	public TabList setup() {
		final Player player = owner.get();
		if (setup || player == null || !player.isOnline()) {
			return this;
		}
		
		/* create and send packet */
		try {
			Class<?> packet_class = ReflectionUtils.getCraftClass("PacketPlayOutPlayerListHeaderFooter");
			Object         packet = packet_class.getConstructor().newInstance();
			ReflectionUtils.setField(packet, "a", getChatComponent("{\"text\":\"" + splitByTabSpliter(header) + "\"}"));
			ReflectionUtils.setField(packet, "b", getChatComponent("{\"text\":\"" + splitByTabSpliter(footer) + "\"}"));
			ReflectionUtils.sendPacket(player, packet);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | InstantiationException | NoSuchFieldException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		this.setup = true; // set is set up
		return this;
	}
	
	@Override
	public Player getPlayer() {
		return nullable_player;
	}

	@Override
	public String[] getHeader() {
		return header;
	}

	@Override
	public TabList setHeader(final String[] header) {
		this.header = header;
		return refresh();
	}

	@Override
	public String[] getFooter() {
		return footer;
	}

	@Override
	public TabList setFooter(final String[] footer) {
		this.footer = footer;
		return refresh();
	}

	@Override
	public TabList clearHeaderFooter() {
		this.header = new String[0];
		this.footer = new String[0];
		return refresh();
	}
	
	@Override
	public TabList addPlayer(Player player) {
		if (player != null && owner.get() instanceof Player && ((Player) owner.get()).isOnline()) {
			try {
				ReflectionUtils.sendPacket(owner.get(),
						getPacketPlayOutPlayerInfo(UniversalCustomTab.ADD_PLAYER_ENUM_CONSTANT_NAME,
								ReflectionUtils.getHandle(player)));
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException
					| ClassNotFoundException | InstantiationException | IllegalArgumentException
					| SecurityException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	@Override
	public TabList addFakePlayer(final String name, final int ping) {
		if (entries.containsKey(name)) {
			return this;
		}

		try {
			/* EntityPlayer */
			Player owner_player = owner.get();
			Object   nms_player = ReflectionUtils.getHandle(owner_player);
			
			/* MinecraftServer */
			Class<?>  mc_server_class = ReflectionUtils.getCraftClass("MinecraftServer");
			Object mc_server_instance = mc_server_class.getMethod("getServer").invoke(mc_server_class);

			/* NMS World */
			Object world = ReflectionUtils.getHandle(owner_player.getWorld());

			/* fake entry profile */
			GameProfile profile = new GameProfile(UUID.randomUUID(), TextUtils.translateColors(name));

			/* PlayerInteractManager */
			Class<?> pl_interact_manager_class = ReflectionUtils.getCraftClass("PlayerInteractManager");
			Object         pl_interact_manager = pl_interact_manager_class.getConstructor(world.getClass()).newInstance(world);

			/* making the fake entry */
			Object fake_entry = nms_player.getClass()
					.getConstructor(mc_server_class, world.getClass(), profile.getClass(), pl_interact_manager_class)
					.newInstance(mc_server_instance, world, profile, pl_interact_manager);

			/* change ping and list name of the fake entry */
			ReflectionUtils.setField(fake_entry, "ping", ping); // field 'fake_entry.ping'
			ReflectionUtils.setField(fake_entry, "listName",
					getChatComponent("{\"text\":\"" + profile.getName() + "\"}")); // field 'fake_entry.listName'

			/* send packet 3 ticks later */
			Schedulers.scheduleSync(() -> {
				if (owner.get() instanceof Player && ((Player) owner.get()).isOnline()) {
					try {
						ReflectionUtils.sendPacket(owner.get(),
								getPacketPlayOutPlayerInfo(UniversalCustomTab.ADD_PLAYER_ENUM_CONSTANT_NAME, fake_entry));
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
							| NoSuchFieldException | ClassNotFoundException | InstantiationException
							| IllegalArgumentException | SecurityException e) {
						e.printStackTrace();
					}
				}
			}, 3, plugin);
			entries.put(name, fake_entry); // register fake entry
		} catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException ex) {
			ex.printStackTrace();
		}
		return this;
	}
	
	@Override
	public TabList removeFakePlayer(final String name) {
		if (!(this.isInstanceOfEntityPlayer(entries.get(name)))) { // entries.get(name) instanceof EntityPlayer
			throw new UnsupportedOperationException("The entry is not a valid instance of EntityPlayer!");
		}

		if (owner.get() instanceof Player && ((Player) owner.get()).isOnline()) {
			try {
				ReflectionUtils.sendPacket(owner.get(), getPacketPlayOutPlayerInfo(
						UniversalCustomTab.REMOVE_PLAYER_ENUM_CONSTANT_NAME, entries.get(name)));
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException
					| ClassNotFoundException | InstantiationException | IllegalArgumentException
					| SecurityException e) {
				e.printStackTrace();
			}
		}
		entries.remove(name);
		return this;
	}
	
	@Override
	public TabList removePlayer(Player player) {
		if (player != null && owner.get() instanceof Player && ((Player) owner.get()).isOnline()) {
			try {
				ReflectionUtils.sendPacket(owner.get(), getPacketPlayOutPlayerInfo(
						UniversalCustomTab.REMOVE_PLAYER_ENUM_CONSTANT_NAME, ReflectionUtils.getHandle(player)));
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException
					| ClassNotFoundException | InstantiationException | IllegalArgumentException
					| SecurityException e) {
				e.printStackTrace();
			}
		}
		return this;
	}
	
	@Override
	public TabList clearPlayers() {
		for (Player other : Bukkit.getOnlinePlayers()) { // clear online players
			Schedulers.scheduleSync(() -> {
				removePlayer(other);
			}, 2, plugin);
		}
		return this;
	}

	@Override
	public TabList clearFakePlayers() {
		for (String key : entries.keySet()) {
			if (key != null && entries.get(key) != null) {
				removeFakePlayer(key);
			}
		}
		entries.clear();
		return this;
	}
	
	@Override
	public TabList clearAll() {
		clearPlayers();
		clearFakePlayers();
		clearHeaderFooter();
		return this;
	}

	@Override
	public TabList refresh() {
		/* refreshing entries */
		Map<String, Object> entries_copy = new HashMap<>();
		entries_copy.putAll(entries);
		clearFakePlayers();
		for (String key : entries_copy.keySet()) {
			if (entries_copy.get(key) != null) {
				addFakePlayer(key, ReflectionUtils.getField(entries_copy.get(key), "ping"));
			}
		}
		
		/* setup and return. (the setup refresh header and footer) */
		this.setup = false;
		return setup();
	}

	private Object getPacketPlayOutPlayerInfo(String enum_constant_name, Object nms_player)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		Class<?> packet_class = ReflectionUtils.getCraftClass("PacketPlayOutPlayerInfo");
		Class<?>   enum_class = null;
		for (Class<?> sub_class : packet_class.getClasses()) { // getting class 'EnumPlayerInfoAction' inside
																// 'PacketPlayOutPlayerInfo'
			if (sub_class.getSimpleName().equalsIgnoreCase("EnumPlayerInfoAction")) {
				enum_class = sub_class;
				break;
			}
		}

		/* getting enum constant inside the enum class 'EnumPlayerInfoAction' */
		Object add_player_enum = null;
		for (Object enum_constant : enum_class.getEnumConstants()) {
			if (enum_constant.toString().equals(enum_constant_name)) {
				add_player_enum = enum_constant;
				break;
			}
		}
		return packet_class // Constructor: (EnumPlayerInfoAction.class, EntityPlayer.class)
				.getConstructor(enum_class, nms_player.getClass()).newInstance(add_player_enum, nms_player);
	}
	
	private Object getChatComponent(String component) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?>       component_class = ReflectionUtils.getCraftClass("IChatBaseComponent");
		Class<?> chat_serializer_class = component_class.getClasses()[0]; // the class 'ChatSerializer' is inside the
																			// class 'IChatBaseComponent'
		return chat_serializer_class.getMethod("a", String.class).invoke(chat_serializer_class, component);
	}
	
	private Class<?> getEntityPlayerClass() {
		return ReflectionUtils.getCraftClass("EntityPlayer");
	}
	
	private boolean isInstanceOfEntityPlayer(Object instance) {
		return getEntityPlayerClass().equals(instance.getClass())
				|| getEntityPlayerClass().isAssignableFrom(instance.getClass());
	}
}
package es.outlook.adriansrj.core.player;

import es.outlook.adriansrj.core.enums.EnumMessageType;
import es.outlook.adriansrj.core.main.AdrianSRCore;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.reflection.bukkit.BukkitReflection;
import es.outlook.adriansrj.core.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import es.outlook.adriansrj.core.util.reflection.general.ConstructorReflection;
import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;
import es.outlook.adriansrj.core.util.server.Version;
import es.outlook.adriansrj.core.util.titles.TitlesUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.ChannelNotRegisteredException;
import org.bukkit.plugin.messaging.MessageTooLargeException;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.BoundingBox;
import xyz.xenondevs.particle.ParticleEffect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Bukkit {@link Player} wrapper expands the {@link Player} API
 * adding some new useful features, and making the new features
 * of the newer Bukkit API versions work on older versions.
 *
 * @author AdrianSR / 22/09/2021 / 11:17 a. m.
 */
public class PlayerWrapper {
	
	public static PlayerWrapper of ( OfflinePlayer handle ) {
		return new PlayerWrapper ( handle.getUniqueId ( ) , handle.getName ( ) );
	}
	
	// @Override annotation is missing from some methods
	// as it would cause an exception in legacy versions.
	
	// must be careful with lambda expressions:
	// extract ( handle -> handle.getPlayerListFooter ( ) ) must be used instead of
	// extract ( Player :: getPlayerListFooter )
	
	protected final UUID   uuid;
	protected final String name;
	
	protected Player  last_handle;
	protected boolean verbose;
	
	public PlayerWrapper ( UUID uuid , String name ) {
		this.uuid = uuid;
		this.name = name;
	}
	
	/**
	 * Returns the latest bukkit {@link Player} returned from {@link #getBukkitPlayer()}.
	 *
	 * @return latest bukkit {@link Player}. May be <b>null</b>.
	 */
	public Player getLastHandle ( ) {
		return last_handle;
	}
	
	/**
	 * Returns the corresponding bukkit {@link Player}.
	 * <br>
	 * Note that <b>null</b> will be returned if the player is offline.
	 *
	 * @return corresponding bukkit player, or <b>null</b>  if offline.
	 */
	public Player getBukkitPlayer ( ) {
		return last_handle = Bukkit.getPlayer ( uuid );
	}
	
	/**
	 * Returns and {@link Optional} that wraps the corresponding bukkit player, or a <b>null</b> value if offline.
	 *
	 * @return optional bukkit player.
	 */
	public Optional < Player > getBukkitPlayerOptional ( ) {
		return Optional.ofNullable ( getBukkitPlayer ( ) );
	}
	
	/**
	 * Fast access to the handle.
	 * <p>
	 * Will return {@link #last_handle} if the player
	 * is not currently online.
	 *
	 * @return the player, or {@link #last_handle} if not online.
	 */
	protected Player handle ( ) {
		return getBukkitPlayerOptional ( ).orElse ( last_handle );
	}
	
	/**
	 * Fast access to the handle.
	 * <p>
	 * Will return {@link #last_handle} if the player
	 * is not currently online.
	 *
	 * @return the player, or {@link #last_handle} if not online.
	 */
	protected Optional < Player > handleOptional ( ) {
		return Optional.ofNullable ( handle ( ) );
	}
	
	/**
	 * Gets whether verbose mode is enabled.
	 * <p>
	 * <b>If enabled, when a method that is not supported
	 * by the running server version is called, an exception
	 * will be thrown, otherwise the exceptions will be ignored.</b>
	 *
	 * @return whether verbose mode is enabled or not.
	 */
	public boolean isVerbose ( ) {
		return verbose;
	}
	
	/**
	 * Sets whether verbose mode is enabled.
	 * <p>
	 * <b>If enabled, when a method that is not supported
	 * by the running server version is called, an exception
	 * will be thrown, otherwise the exceptions will be ignored.</b>
	 *
	 * @param verbose whether to enable verbose mode or not.
	 */
	public void setVerbose ( boolean verbose ) {
		this.verbose = verbose;
	}
	
	/**
	 * Gets the "friendly" name to display of this player. This may include
	 * color.
	 * <p>
	 * Note that this name will not be displayed in game, only in chat and
	 * places defined by plugins.
	 *
	 * @return the friendly name
	 */
	public String getDisplayName ( ) {
		return handleOptional ( ).map ( Player :: getDisplayName ).orElse ( name );
	}
	
	/**
	 * Sets the "friendly" name to display of this player. This may include
	 * color.
	 * <p>
	 * Note that this name will not be displayed in game, only in chat and
	 * places defined by plugins.
	 *
	 * @param name The new display name.
	 */
	public void setDisplayName ( String name ) {
		handleOptional ( ).ifPresent ( handle -> setDisplayName ( name ) );
	}
	
	/**
	 * Gets the name that is shown on the player list.
	 *
	 * @return the player list name
	 */
	public String getPlayerListName ( ) {
		return handleOptional ( ).map ( Player :: getPlayerListName ).orElse ( name );
	}
	
	/**
	 * Sets the name that is shown on the in-game player list.
	 * <p>
	 * If the value is null, the name will be identical to {@link #getName()}.
	 *
	 * @param name new player list name
	 */
	public void setPlayerListName ( String name ) {
		handleOptional ( ).ifPresent ( handle -> setPlayerListName ( name ) );
	}
	
	/**
	 * Gets the currently displayed player list header for this player.
	 *
	 * @return player list header or null
	 */
	public String getPlayerListHeader ( ) {
		return extract ( handle -> handle.getPlayerListHeader ( ) );
	}
	
	/**
	 * Gets the currently displayed player list footer for this player.
	 *
	 * @return player list header or null
	 */
	public String getPlayerListFooter ( ) {
		return extract ( handle -> handle.getPlayerListFooter ( ) );
	}
	
	/**
	 * Sets the currently displayed player list header for this player.
	 *
	 * @param header player list header, null for empty
	 */
	public void setPlayerListHeader ( String header ) {
		execute ( handle -> handle.setPlayerListHeader ( header ) );
	}
	
	/**
	 * Sets the currently displayed player list footer for this player.
	 *
	 * @param footer player list footer, null for empty
	 */
	public void setPlayerListFooter ( String footer ) {
		execute ( handle -> handle.setPlayerListFooter ( footer ) );
	}
	
	/**
	 * Sets the currently displayed player list header and footer for this
	 * player.
	 *  @param header player list header, null for empty
	 * @param footer player list footer, null for empty
	 */
	public void setPlayerListHeaderFooter ( String header , String footer ) {
		execute ( handle -> handle.setPlayerListHeaderFooter ( header , footer ) );
	}
	
	/**
	 * Set the target of the player's compass.
	 *
	 * @param loc Location to point to
	 */
	public void setCompassTarget ( Location loc ) {
		execute ( handle -> handle.setCompassTarget ( loc ) );
	}
	
	/**
	 * Get the previously set compass target.
	 *
	 * @return location of the target
	 */
	public Location getCompassTarget ( ) {
		return extract ( handle -> handle.getCompassTarget ( ) );
	}
	
	/**
	 * Gets the socket address of this player
	 *
	 * @return the player's address
	 */
	public InetSocketAddress getAddress ( ) {
		return extract ( handle -> handle.getAddress ( ) );
	}
	
	/**
	 * Tests to see of a Conversable object is actively engaged in a
	 * conversation.
	 *
	 * @return True if a conversation is in progress
	 */
	public boolean isConversing ( ) {
		return extract ( handle -> handle.isConversing ( ) );
	}
	
	/**
	 * Accepts input into the active conversation. If no conversation is in
	 * progress, this method does nothing.
	 *
	 * @param input The input message into the conversation
	 */
	public void acceptConversationInput ( String input ) {
		execute ( handle -> handle.acceptConversationInput ( input ) );
	}
	
	/**
	 * Enters into a dialog with a Conversation object.
	 *
	 * @param conversation The conversation to begin
	 * @return True if the conversation should proceed, false if it has been
	 *     enqueued
	 */
	public boolean beginConversation ( Conversation conversation ) {
		return invokeSafe ( "beginConversation" , conversation );
	}
	
	/**
	 * Abandons an active conversation.
	 *
	 * @param conversation The conversation to abandon
	 */
	public void abandonConversation ( Conversation conversation ) {
		execute ( handle -> handle.abandonConversation ( conversation ) );
	}
	
	/**
	 * Abandons an active conversation.
	 *  @param conversation The conversation to abandon
	 * @param details Details about why the conversation was abandoned
	 */
	public void abandonConversation ( Conversation conversation , ConversationAbandonedEvent details ) {
		execute ( handle -> handle.abandonConversation ( conversation , details ) );
	}
	
	/**
	 * Sends this sender a message raw
	 *
	 * @param message Message to be displayed
	 */
	public void sendRawMessage ( String message ) {
		execute ( handle -> handle.sendRawMessage ( message ) );
	}
	
	/**
	 * Sends this sender a message raw
	 *
	 * @param sender The sender of this message
	 * @param message Message to be displayed
	 */
	public void sendRawMessage ( UUID sender , String message ) {
		execute ( handle -> handle.sendRawMessage ( sender , message ) );
	}
	
	/**
	 * Kicks player with custom kick message.
	 *
	 * @param message kick message
	 */
	public void kickPlayer ( String message ) {
		execute ( handle -> handle.kickPlayer ( message ) );
	}
	
	/**
	 * Says a message (or runs a command).
	 *
	 * @param message message to print
	 */
	public void chat ( String message ) {
		execute ( handle -> handle.chat ( message ) );
	}
	
	/**
	 * Makes the player perform the given command
	 *
	 * @param command Command to perform
	 * @return true if the command was successful, otherwise false
	 */
	public boolean performCommand ( String command ) {
		return invokeSafe ( "performCommand" , command );
	}
	
	/**
	 * Gets the entity's current position
	 *
	 * @return a new copy of Location containing the position of this entity
	 */
	public Location getLocation ( ) {
		return extract ( handle -> handle.getLocation ( ) );
	}
	
	/**
	 * Stores the entity's current position in the provided Location object.
	 * <p>
	 * If the provided Location is null this method does nothing and returns
	 * null.
	 *
	 * @param loc the location to copy into
	 * @return The Location object provided or null
	 */
	public Location getLocation ( Location loc ) {
		return extract ( handle -> handle.getLocation ( loc ) );
	}
	
	/**
	 * Sets this entity's velocity in meters per tick
	 *
	 * @param velocity New velocity to travel with
	 */
	public void setVelocity ( org.bukkit.util.Vector velocity ) {
		execute ( handle -> handle.setVelocity ( velocity ) );
	}
	
	/**
	 * Gets this entity's current velocity
	 *
	 * @return Current traveling velocity of this entity
	 */
	public org.bukkit.util.Vector getVelocity ( ) {
		return invokeSafe ( "getVelocity" );
	}
	
	/**
	 * Gets the entity's height
	 *
	 * @return height of entity
	 */
	public double getHeight ( ) {
		// TODO: backwards compatibility required
		return invokeSafe ( "getHeight" );
	}
	
	/**
	 * Gets the entity's width
	 *
	 * @return width of entity
	 */
	public double getWidth ( ) {
		// TODO: backwards compatibility required
		return invokeSafe ( "getWidth" );
	}
	
	/**
	 * Gets the entity's current bounding box.
	 * <p>
	 * The returned bounding box reflects the entity's current location and
	 * size.
	 *
	 * @return the entity's current bounding box
	 */
	public BoundingBox getBoundingBox ( ) {
		// TODO: backwards compatibility required
		return invokeSafe ( "getBoundingBox" );
	}
	
	/**
	 * Returns true if the entity is supported by a block.
	 *
	 * This value is a state updated by the client after each movement.
	 *
	 * @return True if entity is on ground.
	 * @deprecated This value is controlled only by the client and is therefore
	 * unreliable and vulnerable to spoofing and/or desync depending on the
	 * context/time which it is accessed
	 */
	public boolean isOnGround ( ) {
		return invokeSafe ( "isOnGround" );
	}
	
	/**
	 * Returns true if the entity is in water.
	 *
	 * @return <code>true</code> if the entity is in water.
	 */
	public boolean isInWater ( ) {
		// TODO: backwards compatibility required
		return invokeSafe ( "isInWater" );
	}
	
	/**
	 * Gets the current world this entity resides in
	 *
	 * @return World
	 */
	public World getWorld ( ) {
		return invokeSafe ( "getWorld" );
	}
	
	/**
	 * Sets the entity's rotation.
	 * <p>
	 * Note that if the entity is affected by AI, it may override this rotation.
	 *
	 * @param yaw the yaw
	 * @param pitch the pitch
	 * @throws UnsupportedOperationException if used for players
	 */
	public void setRotation ( float yaw , float pitch ) {
		// TODO: backwards compatibility required
		execute ( handle -> handle.setRotation ( yaw , pitch ) );
	}
	
	/**
	 * Teleports this entity to the given location. If this entity is riding a
	 * vehicle, it will be dismounted prior to teleportation.
	 *
	 * @param location New location to teleport this entity to
	 * @return <code>true</code> if the teleport was successful
	 */
	public boolean teleport ( Location location ) {
		return invokeSafe ( "teleport" , location );
	}
	
	/**
	 * Teleports this entity to the given location. If this entity is riding a
	 * vehicle, it will be dismounted prior to teleportation.
	 *
	 * @param location New location to teleport this entity to
	 * @param cause The cause of this teleportation
	 * @return <code>true</code> if the teleport was successful
	 */
	public boolean teleport ( Location location , PlayerTeleportEvent.TeleportCause cause ) {
		return invokeSafe ( "teleport" , location , cause );
	}
	
	/**
	 * Teleports this entity to the target Entity. If this entity is riding a
	 * vehicle, it will be dismounted prior to teleportation.
	 *
	 * @param destination Entity to teleport this entity to
	 * @return <code>true</code> if the teleport was successful
	 */
	public boolean teleport ( Entity destination ) {
		return invokeSafe ( "teleport" , destination );
	}
	
	/**
	 * Teleports this entity to the target Entity. If this entity is riding a
	 * vehicle, it will be dismounted prior to teleportation.
	 *
	 * @param destination Entity to teleport this entity to
	 * @param cause The cause of this teleportation
	 * @return <code>true</code> if the teleport was successful
	 */
	public boolean teleport ( Entity destination , PlayerTeleportEvent.TeleportCause cause ) {
		return invokeSafe ( "teleport" , destination , cause );
	}
	
	/**
	 * Returns a list of entities within a bounding box centered around this
	 * entity
	 *
	 * @param x 1/2 the size of the box along x axis
	 * @param y 1/2 the size of the box along y axis
	 * @param z 1/2 the size of the box along z axis
	 * @return {@code List<Entity>} List of entities nearby
	 */
	public List < Entity > getNearbyEntities ( double x , double y , double z ) {
		return invokeSafe ( "getNearbyEntities" , x , y , z );
	}
	
	/**
	 * Returns a unique id for this entity
	 *
	 * @return Entity id
	 */
	public int getEntityId ( ) {
		return invokeSafe ( "getEntityId" );
	}
	
	/**
	 * Returns the entity's current fire ticks (ticks before the entity stops
	 * being on fire).
	 *
	 * @return int fireTicks
	 */
	public int getFireTicks ( ) {
		return invokeSafe ( "getFireTicks" );
	}
	
	/**
	 * Returns the entity's maximum fire ticks.
	 *
	 * @return int maxFireTicks
	 */
	public int getMaxFireTicks ( ) {
		return invokeSafe ( "getMaxFireTicks" );
	}
	
	/**
	 * Sets the entity's current fire ticks (ticks before the entity stops
	 * being on fire).
	 *
	 * @param ticks Current ticks remaining
	 */
	public void setFireTicks ( int ticks ) {
		execute ( handle -> handle.setFireTicks ( ticks ) );
	}
	
	/**
	 * Sets if the entity has visual fire (it will always appear to be on fire).
	 *
	 * @since 1.17
	 * @param fire whether visual fire is enabled
	 */
	public void setVisualFire ( boolean fire ) {
		execute ( handle -> handle.setVisualFire ( fire ) );
	}
	
	/**
	 * Gets if the entity has visual fire (it will always appear to be on fire).
	 *
	 * @since 1.17
	 * @return whether visual fire is enabled
	 */
	public boolean isVisualFire ( ) {
		return extract ( handle -> handle.isVisualFire ( ) );
	}
	
	/**
	 * Returns the entity's current freeze ticks (amount of ticks the entity has
	 * been in powdered snow).
	 *
	 * @since 1.17
	 * @return int freeze ticks
	 */
	public int getFreezeTicks ( ) {
		return extract ( handle -> handle.getFreezeTicks ( ) );
	}
	
	/**
	 * Returns the entity's maximum freeze ticks (amount of ticks before it will
	 * be fully frozen)
	 *
	 * @since 1.17
	 * @return int max freeze ticks
	 */
	public int getMaxFreezeTicks ( ) {
		return extract ( handle -> handle.getMaxFreezeTicks ( ) );
	}
	
	/**
	 * Sets the entity's current freeze ticks (amount of ticks the entity has
	 * been in powdered snow).
	 *
	 * @since 1.17
	 * @param ticks Current ticks
	 */
	public void setFreezeTicks ( int ticks ) {
		execute ( handle -> handle.setFreezeTicks ( ticks ) );
	}
	
	/**
	 * Gets if the entity is fully frozen (it has been in powdered snow for max
	 * freeze ticks).
	 *
	 * @since 1.17
	 * @return freeze status
	 */
	public boolean isFrozen ( ) {
		return extract ( handle -> handle.isFrozen ( ) );
	}
	
	/**
	 * Mark the entity's removal.
	 */
	public void remove ( ) {
		execute ( handle -> handle.remove ( ) );
	}
	
	/**
	 * Returns true if this entity has been marked for removal.
	 *
	 * @return True if it is dead.
	 */
	public boolean isDead ( ) {
		return extract ( handle -> handle.isDead ( ) );
	}
	
	/**
	 * Returns false if the entity has died or been despawned for some other
	 * reason.
	 *
	 * @return True if valid.
	 */
	public boolean isValid ( ) {
		return extract ( handle -> handle.isValid ( ) );
	}
	
	/**
	 * Sends this sender a message
	 *
	 * @param message Message to be displayed
	 */
	public void sendMessage ( String message ) {
		execute ( handle -> handle.sendMessage ( message ) );
	}
	
	/**
	 * Sends this sender multiple messages
	 *
	 * @param messages An array of messages to be displayed
	 */
	public void sendMessage ( String... messages ) {
		execute ( handle -> handle.sendMessage ( messages ) );
	}
	
	/**
	 * Sends this sender a message
	 *
	 * @since 1.16.3
	 * @param sender The sender of this message
	 * @param message Message to be displayed
	 */
	public void sendMessage ( UUID sender , String message ) {
		execute ( handle -> handle.sendMessage ( sender , message ) );
	}
	
	/**
	 * Sends this sender multiple messages
	 *
	 * @since 1.16.3
	 * @param sender The sender of this message
	 * @param messages An array of messages to be displayed
	 */
	public void sendMessage ( UUID sender , String... messages ) {
		execute ( handle -> handle.sendMessage ( sender , messages ) );
	}
	
	/**
	 * Displays the provided message at the specified position in the screen.
	 *
	 * @param position the position in the screen.
	 * @param message the message to display.
	 */
	public void sendMessage ( EnumMessageType position , String message ) {
		Validate.notNull ( position , "position cannot be null" );
		Validate.notNull ( message , "message cannot be null" );
		
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_12_R1 ) ) {
			org.bukkit.entity.Player.Spigot spigot = spigot ( );
			
			if ( spigot != null ) {
				// this will also work on versions v1_9_R4+, but there is a problem with the colors,
				// so we will instead use nms for that versions.
				spigot.sendMessage ( net.md_5.bungee.api.ChatMessageType.valueOf ( position.name ( ) ) ,
									 net.md_5.bungee.api.chat.TextComponent.fromLegacyText ( message ) );
			}
		} else {
			try {
				Class < ? >   component_class = ClassReflection.getNmsClass ( "IChatBaseComponent" );
				Class < ? >[] inner_classes   = component_class.getClasses ( );
				Class < ? > chat_serializer = inner_classes.length > 0 ? component_class.getClasses ( )[ 0 ]
						: ClassReflection.getNmsClass ( "ChatSerializer" );
				Class < ? > packet_class = ClassReflection.getNmsClass ( "PacketPlayOutChat" );
				Object component = MethodReflection.get ( chat_serializer , "a" , String.class )
						.invoke ( chat_serializer ,
								  "{\"text\":\"" + StringUtil.limit ( message , 63 ) + "\"}" );
				
				// https://wiki.vg/index.php?title=Protocol&oldid=8313
				byte type_value;
				
				switch ( position ) {
					case CHAT: // 0: chat (chat box)
						type_value = ( byte ) 0;
						break;
					case SYSTEM: // 1: system message (chat box)
						type_value = ( byte ) 1;
						break;
					case ACTION_BAR: // 2: above hotbar
						type_value = ( byte ) 2;
						break;
					default:
						throw new IllegalStateException ( "unsupported: " + position );
				}
				
				// then sending
				Object packet = ConstructorReflection.newInstance (
						packet_class , new Class < ? >[] { component_class , byte.class } ,
						component , type_value );
				
				handleOptional ( ).ifPresent ( player -> BukkitReflection.sendPacket ( player , packet ) );
			} catch ( ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | InstantiationException e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	/**
	 * Gets the {@link Server} that contains this Entity
	 *
	 * @return Server instance running this Entity
	 */
	public Server getServer ( ) {
		return extract ( handle -> handle.getServer ( ) );
	}
	
	/**
	 * Returns true if the entity gets persisted.
	 * <p>
	 * By default all entities are persistent. An entity will also not get
	 * persisted, if it is riding an entity that is not persistent.
	 * <p>
	 * The persistent flag on players controls whether or not to save their
	 * playerdata file when they quit. If a player is directly or indirectly
	 * riding a non-persistent entity, the vehicle at the root and all its
	 * passengers won't get persisted.
	 * <p>
	 * <b>This should not be confused with
	 * {@link LivingEntity#setRemoveWhenFarAway(boolean)} which controls
	 * despawning of living entities. </b>
	 *
	 * @return true if this entity is persistent
	 */
	public boolean isPersistent ( ) {
		return extract ( handle -> handle.isPersistent ( ) );
	}
	
	/**
	 * Sets whether or not the entity gets persisted.
	 *
	 * @param persistent the persistence status
	 * @see #isPersistent()
	 */
	public void setPersistent ( boolean persistent ) {
		execute ( handle -> handle.setPersistent ( persistent ) );
	}
	
	/**
	 * Gets the primary passenger of a vehicle. For vehicles that could have
	 * multiple passengers, this will only return the primary passenger.
	 *
	 * @return an entity
	 * @deprecated entities may have multiple passengers, use
	 * {@link #getPassengers()}
	 */
	public Entity getPassenger ( ) {
		return extract ( handle -> handle.getPassenger ( ) );
	}
	
	/**
	 * Set the passenger of a vehicle.
	 *
	 * @param passenger The new passenger.
	 * @return false if it could not be done for whatever reason
	 * @deprecated entities may have multiple passengers, use
	 * {@link #addPassenger(Entity)}
	 */
	public boolean setPassenger ( Entity passenger ) {
		return invokeSafe ( "setPassenger" , passenger );
	}
	
	/**
	 * Gets a list of passengers of this vehicle.
	 * <p>
	 * The returned list will not be directly linked to the entity's current
	 * passengers, and no guarantees are made as to its mutability.
	 *
	 * @return list of entities corresponding to current passengers.
	 */
	public List < Entity > getPassengers ( ) {
		return extract ( handle -> handle.getPassengers ( ) );
	}
	
	/**
	 * Add a passenger to the vehicle.
	 *
	 * @param passenger The passenger to add
	 * @return false if it could not be done for whatever reason
	 */
	public boolean addPassenger ( Entity passenger ) {
		try {
			return invoke ( "addPassenger" , new Class < ? >[] { Entity.class } , passenger );
		} catch ( NoSuchMethodException ex ) {// legacy versions
			setPassenger ( passenger );
			return true;
		}
	}
	
	/**
	 * Remove a passenger from the vehicle.
	 *
	 * @param passenger The passenger to remove
	 * @return false if it could not be done for whatever reason
	 */
	public boolean removePassenger ( Entity passenger ) {
		try {
			return invoke ( "removePassenger" , new Class < ? >[] { Entity.class } , passenger );
		} catch ( NoSuchMethodException ex ) { // legacy versions
			return eject ( );
		}
	}
	
	/**
	 * Check if a vehicle has passengers.
	 *
	 * @return True if the vehicle has no passengers.
	 */
	public boolean isEmpty ( ) {
		return extract ( handle -> handle.isEmpty ( ) );
	}
	
	/**
	 * Eject any passenger.
	 *
	 * @return True if there was a passenger.
	 */
	public boolean eject ( ) {
		return invokeSafe ( "eject" );
	}
	
	/**
	 * Returns the distance this entity has fallen
	 *
	 * @return The distance.
	 */
	public float getFallDistance ( ) {
		return extract ( handle -> handle.getFallDistance ( ) );
	}
	
	/**
	 * Sets the fall distance for this entity
	 *
	 * @param distance The new distance.
	 */
	public void setFallDistance ( float distance ) {
		execute ( handle -> handle.setFallDistance ( distance ) );
	}
	
	/**
	 * Record the last {@link EntityDamageEvent} inflicted on this entity
	 *
	 * @param event a {@link EntityDamageEvent}
	 */
	public void setLastDamageCause ( EntityDamageEvent event ) {
		execute ( handle -> handle.setLastDamageCause ( event ) );
	}
	
	/**
	 * Retrieve the last {@link EntityDamageEvent} inflicted on this entity.
	 * This event may have been cancelled.
	 *
	 * @return the last known {@link EntityDamageEvent} or null if hitherto
	 *     unharmed
	 */
	public EntityDamageEvent getLastDamageCause ( ) {
		return extract ( handle -> handle.getLastDamageCause ( ) );
	}
	
	/**
	 * Returns a unique and persistent id for this entity
	 *
	 * @return unique id
	 */
	public UUID getUniqueId ( ) {
		return extract ( handle -> handle.getUniqueId ( ) );
	}
	
	/**
	 * Gets the amount of ticks this entity has lived for.
	 * <p>
	 * This is the equivalent to "age" in entities.
	 *
	 * @return Age of entity
	 */
	public int getTicksLived ( ) {
		return extract ( handle -> handle.getTicksLived ( ) );
	}
	
	/**
	 * Sets the amount of ticks this entity has lived for.
	 * <p>
	 * This is the equivalent to "age" in entities. May not be less than one
	 * tick.
	 *
	 * @param value Age of entity
	 */
	public void setTicksLived ( int value ) {
		execute ( handle -> handle.setTicksLived ( value ) );
	}
	
	/**
	 * Performs the specified {@link EntityEffect} for this entity.
	 * <p>
	 * This will be viewable to all players near the entity.
	 * <p>
	 * If the effect is not applicable to this class of entity, it will not play.
	 *
	 * @param type Effect to play.
	 */
	public void playEffect ( EntityEffect type ) {
		execute ( handle -> handle.playEffect ( type ) );
	}
	
	/**
	 * Get the type of the entity.
	 *
	 * @return The entity type.
	 */
	public EntityType getType ( ) {
		return extract ( handle -> handle.getType ( ) );
	}
	
	/**
	 * Returns whether this entity is inside a vehicle.
	 *
	 * @return True if the entity is in a vehicle.
	 */
	public boolean isInsideVehicle ( ) {
		return extract ( handle -> handle.isInsideVehicle ( ) );
	}
	
	/**
	 * Leave the current vehicle. If the entity is currently in a vehicle (and
	 * is removed from it), true will be returned, otherwise false will be
	 * returned.
	 *
	 * @return True if the entity was in a vehicle.
	 */
	public boolean leaveVehicle ( ) {
		return invokeSafe ( "leaveVehicle" );
	}
	
	/**
	 * Get the vehicle that this player is inside. If there is no vehicle,
	 * null will be returned.
	 *
	 * @return The current vehicle.
	 */
	public Entity getVehicle ( ) {
		return extract ( handle -> handle.getVehicle ( ) );
	}
	
	/**
	 * Sets whether or not to display the mob's custom name client side. The
	 * name will be displayed above the mob similarly to a player.
	 * <p>
	 * This value has no effect on players, they will always display their
	 * name.
	 *
	 * @param flag custom name or not
	 */
	public void setCustomNameVisible ( boolean flag ) {
		execute ( handle -> handle.setCustomNameVisible ( flag ) );
	}
	
	/**
	 * Gets whether or not the mob's custom name is displayed client side.
	 * <p>
	 * This value has no effect on players, they will always display their
	 * name.
	 *
	 * @return if the custom name is displayed
	 */
	public boolean isCustomNameVisible ( ) {
		return extract ( handle -> handle.isCustomNameVisible ( ) );
	}
	
	/**
	 * Sets whether the entity has a team colored (default: white) glow.
	 *
	 * <b>nb: this refers to the 'Glowing' entity property, not whether a
	 * glowing potion effect is applied</b>
	 *
	 * @since 1.9
	 * @param flag if the entity is glowing
	 */
	public void setGlowing ( boolean flag ) {
		execute ( handle -> handle.setGlowing ( flag ) );
	}
	
	/**
	 * Gets whether the entity is glowing or not.
	 *
	 * <b>nb: this refers to the 'Glowing' entity property, not whether a
	 * glowing potion effect is applied</b>
	 *
	 * @since 1.9
	 * @return whether the entity is glowing
	 */
	public boolean isGlowing ( ) {
		return extract ( handle -> handle.isGlowing ( ) );
	}
	
	/**
	 * Sets whether the entity is invulnerable or not.
	 * <p>
	 * When an entity is invulnerable it can only be damaged by players in
	 * creative mode.
	 *
	 * @param flag if the entity is invulnerable
	 */
	public void setInvulnerable ( boolean flag ) {
		try {
			invokeSafe ( "setInvulnerable" , flag );
		} catch ( NoSuchMethodError ex ) { // legacy
			handleOptional ( ).ifPresent (
					handle -> EntityReflection.setInvulnerable ( handle , flag ) );
		}
	}
	
	/**
	 * Gets whether the entity is invulnerable or not.
	 *
	 * @return whether the entity is
	 */
	public boolean isInvulnerable ( ) {
		try {
			return invokeSafe ( "isInvulnerable" );
		} catch ( NoSuchMethodError ex ) { // legacy
			Player handle = handleOptional ( ).orElse ( null );
			
			return handle != null && EntityReflection.isInvulnerable ( handle );
		}
	}
	
	/**
	 * Gets whether the entity is silent or not.
	 *
	 * @return whether the entity is silent.
	 */
	public boolean isSilent ( ) {
		try {
			return invokeSafe ( "isSilent" );
		} catch ( NoSuchMethodError ex ) { // legacy
			Player handle = handleOptional ( ).orElse ( null );
			
			return handle != null && EntityReflection.isSilent ( handle );
		}
	}
	
	/**
	 * Sets whether the entity is silent or not.
	 * <p>
	 * When an entity is silent it will not produce any sound.
	 *
	 * @param flag if the entity is silent
	 */
	public void setSilent ( boolean flag ) {
		try {
			invokeSafe ( "setSilent" , flag );
		} catch ( NoSuchMethodError ex ) { // legacy
			handleOptional ( ).ifPresent (
					handle -> EntityReflection.setSilent ( handle , flag ) );
		}
	}
	
	/**
	 * Returns whether gravity applies to this entity.
	 *
	 * @return whether gravity applies
	 */
	public boolean hasGravity ( ) {
		return extract ( handle -> handle.hasGravity ( ) );
	}
	
	/**
	 * Sets whether gravity applies to this entity.
	 *
	 * @since 1.10
	 * @param gravity whether gravity should apply
	 */
	public void setGravity ( boolean gravity ) {
		invokeSafe ( "setGravity" , gravity );
	}
	
	/**
	 * Gets the period of time (in ticks) before this entity can use a portal.
	 *
	 * @return portal cooldown ticks
	 */
	public int getPortalCooldown ( ) {
		return extract ( handle -> handle.getPortalCooldown ( ) );
	}
	
	/**
	 * Sets the period of time (in ticks) before this entity can use a portal.
	 *
	 * @param cooldown portal cooldown ticks
	 */
	public void setPortalCooldown ( int cooldown ) {
		invokeSafe ( "setPortalCooldown" , cooldown );
	}
	
	/**
	 * Returns a set of tags for this entity.
	 * <br>
	 * Entities can have no more than 1024 tags.
	 *
	 * @return a set of tags for this entity
	 */
	public Set < String > getScoreboardTags ( ) {
		return extract ( handle -> handle.getScoreboardTags ( ) );
	}
	
	/**
	 * Add a tag to this entity.
	 * <br>
	 * Entities can have no more than 1024 tags.
	 *
	 * @param tag the tag to add
	 * @return true if the tag was successfully added
	 */
	public boolean addScoreboardTag ( String tag ) {
		return invokeSafe ( "addScoreboardTag" , tag );
	}
	
	/**
	 * Removes a given tag from this entity.
	 *
	 * @param tag the tag to remove
	 * @return true if the tag was successfully removed
	 */
	public boolean removeScoreboardTag ( String tag ) {
		return invokeSafe ( "removeScoreboardTag" , tag );
	}
	
	/**
	 * Returns the reaction of the entity when moved by a piston.
	 *
	 * @return reaction
	 */
	public PistonMoveReaction getPistonMoveReaction ( ) {
		return extract ( handle -> handle.getPistonMoveReaction ( ) );
	}
	
	/**
	 * Get the closest cardinal {@link BlockFace} direction an entity is
	 * currently facing.
	 * <br>
	 * This will not return any non-cardinal directions such as
	 * {@link BlockFace#UP} or {@link BlockFace#DOWN}.
	 * <br>
	 * {@link Hanging} entities will override this call and thus their behavior
	 * may be different.
	 *
	 * @return the entity's current cardinal facing.
	 * @see Hanging
	 */
	public BlockFace getFacing ( ) {
		try {
			return invokeSafe ( "getFacing" );
		} catch ( NoSuchMethodError ex ) { // legacy
			return DirectionUtil.getBlockFace90 ( getLocation ( ).getYaw ( ) , false );
		}
	}
	
	/**
	 * Gets the entity's current pose.
	 *
	 * <b>Note that the pose is only updated at the end of a tick, so may be
	 * inconsistent with other methods. eg {@link Player#isSneaking()} being
	 * true does not imply the current pose will be {@link Pose#SNEAKING}</b>
	 *
	 * @return current pose
	 */
	public Pose getPose ( ) {
		return invokeSafe ( "getPose" );
	}
	
	/**
	 * Returns if the player is in sneak mode
	 *
	 * @return true if player is in sneak mode
	 */
	public boolean isSneaking ( ) {
		return extract ( handle -> handle.isSneaking ( ) );
	}
	
	/**
	 * Sets the sneak mode the player
	 *
	 * @param sneak true if player should appear sneaking
	 */
	public void setSneaking ( boolean sneak ) {
		execute ( handle -> handle.setSneaking ( sneak ) );
	}
	
	/**
	 * Gets whether the player is sprinting or not.
	 *
	 * @return true if player is sprinting.
	 */
	public boolean isSprinting ( ) {
		return extract ( handle -> handle.isSprinting ( ) );
	}
	
	/**
	 * Sets whether the player is sprinting or not.
	 *
	 * @param sprinting true if the player should be sprinting
	 */
	public void setSprinting ( boolean sprinting ) {
		execute ( handle -> handle.setSprinting ( sprinting ) );
	}
	
	/**
	 * Saves the players current location, health, inventory, motion, and
	 * other information into the username.dat file, in the world/player
	 * folder
	 */
	public void saveData ( ) {
		invokeSafe ( "saveData" );
	}
	
	/**
	 * Loads the players current location, health, inventory, motion, and
	 * other information from the username.dat file, in the world/player
	 * folder.
	 * <p>
	 * Note: This will overwrite the players current inventory, health,
	 * motion, etc, with the state from the saved dat file.
	 */
	public void loadData ( ) {
		invokeSafe ( "loadData" );
	}
	
	/**
	 * Sets whether the player is ignored as not sleeping. If everyone is
	 * either sleeping or has this flag set, then time will advance to the
	 * next day. If everyone has this flag set but no one is actually in bed,
	 * then nothing will happen.
	 *
	 * @param isSleeping Whether to ignore.
	 */
	public void setSleepingIgnored ( boolean isSleeping ) {
		execute ( handle -> handle.setSleepingIgnored ( isSleeping ) );
	}
	
	/**
	 * Returns whether the player is sleeping ignored.
	 *
	 * @return Whether player is ignoring sleep.
	 */
	public boolean isSleepingIgnored ( ) {
		return extract ( handle -> handle.isSleepingIgnored ( ) );
	}
	
	/**
	 * Checks if this player is currently online
	 *
	 * @return true if they are online
	 */
	public boolean isOnline ( ) {
		return getBukkitPlayerOptional ( ).isPresent ( );
	}
	
	/**
	 * Checks if this player is banned or not
	 *
	 * @return true if banned, otherwise false
	 */
	public boolean isBanned ( ) {
		return extract ( handle -> handle.isBanned ( ) );
	}
	
	/**
	 * Checks if this player is whitelisted or not
	 *
	 * @return true if whitelisted
	 */
	public boolean isWhitelisted ( ) {
		return extract ( handle -> handle.isWhitelisted ( ) );
	}
	
	/**
	 * Sets if this player is whitelisted or not
	 *
	 * @param value true if whitelisted
	 */
	public void setWhitelisted ( boolean value ) {
		execute ( handle -> handle.setWhitelisted ( value ) );
	}
	
	/**
	 * Gets a {@link Player} object that this represents, if there is one
	 * <p>
	 * If the player is online, this will return that player. Otherwise,
	 * it will return null.
	 *
	 * @return Online player
	 */
	public Player getPlayer ( ) {
		return getBukkitPlayer ( );
	}
	
	/**
	 * Gets the first date and time that this player was witnessed on this
	 * server.
	 * <p>
	 * If the player has never played before, this will return 0. Otherwise,
	 * it will be the amount of milliseconds since midnight, January 1, 1970
	 * UTC.
	 *
	 * @return Date of first log-in for this player, or 0
	 */
	public long getFirstPlayed ( ) {
		return extract ( handle -> handle.getFirstPlayed ( ) );
	}
	
	/**
	 * Gets the last date and time that this player was witnessed on this
	 * server.
	 * <p>
	 * If the player has never played before, this will return 0. Otherwise,
	 * it will be the amount of milliseconds since midnight, January 1, 1970
	 * UTC.
	 *
	 * @return Date of last log-in for this player, or 0
	 */
	public long getLastPlayed ( ) {
		return extract ( handle -> handle.getLastPlayed ( ) );
	}
	
	/**
	 * Checks if this player has played on this server before.
	 *
	 * @return True if the player has played before, otherwise false
	 */
	public boolean hasPlayedBefore ( ) {
		return extract ( handle -> handle.hasPlayedBefore ( ) );
	}
	
	/**
	 * Gets the Location where the player will spawn at their bed, null if
	 * they have not slept in one or their current bed spawn is invalid.
	 *
	 * @return Bed Spawn Location if bed exists, otherwise null.
	 */
	public Location getBedSpawnLocation ( ) {
		return extract ( handle -> handle.getBedSpawnLocation ( ) );
	}
	
	/**
	 * Increments the given statistic for this player.
	 * <p>
	 * This is equivalent to the following code:
	 * <code>incrementStatistic(Statistic, 1)</code>
	 *
	 * @param statistic Statistic to increment
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if the statistic requires an
	 *     additional parameter
	 */
	public void incrementStatistic ( Statistic statistic ) throws IllegalArgumentException {
		execute ( handle -> handle.incrementStatistic ( statistic ) );
	}
	
	/**
	 * Decrements the given statistic for this player.
	 * <p>
	 * This is equivalent to the following code:
	 * <code>decrementStatistic(Statistic, 1)</code>
	 *
	 * @param statistic Statistic to decrement
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if the statistic requires an
	 *     additional parameter
	 */
	public void decrementStatistic ( Statistic statistic ) throws IllegalArgumentException {
		execute ( handle -> handle.decrementStatistic ( statistic ) );
	}
	
	/**
	 * Increments the given statistic for this player.
	 *
	 * @param statistic Statistic to increment
	 * @param amount Amount to increment this statistic by
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if amount is negative
	 * @throws IllegalArgumentException if the statistic requires an
	 *     additional parameter
	 */
	public void incrementStatistic ( Statistic statistic , int amount ) throws IllegalArgumentException {
		execute ( handle -> handle.incrementStatistic ( statistic , amount ) );
	}
	
	/**
	 * Decrements the given statistic for this player.
	 *
	 * @param statistic Statistic to decrement
	 * @param amount Amount to decrement this statistic by
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if amount is negative
	 * @throws IllegalArgumentException if the statistic requires an
	 *     additional parameter
	 */
	public void decrementStatistic ( Statistic statistic , int amount ) throws IllegalArgumentException {
		execute ( handle -> handle.decrementStatistic ( statistic , amount ) );
	}
	
	/**
	 * Sets the given statistic for this player.
	 *
	 * @param statistic Statistic to set
	 * @param newValue The value to set this statistic to
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if newValue is negative
	 * @throws IllegalArgumentException if the statistic requires an
	 *     additional parameter
	 */
	public void setStatistic ( Statistic statistic , int newValue ) throws IllegalArgumentException {
		execute ( handle -> handle.setStatistic ( statistic , newValue ) );
	}
	
	/**
	 * Gets the value of the given statistic for this player.
	 *
	 * @param statistic Statistic to check
	 * @return the value of the given statistic
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if the statistic requires an
	 *     additional parameter
	 */
	public int getStatistic ( Statistic statistic ) throws IllegalArgumentException {
		return extract ( handle -> handle.getStatistic ( statistic ) );
	}
	
	/**
	 * Increments the given statistic for this player for the given material.
	 * <p>
	 * This is equivalent to the following code:
	 * <code>incrementStatistic(Statistic, Material, 1)</code>
	 *
	 * @param statistic Statistic to increment
	 * @param material Material to offset the statistic with
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if material is null
	 * @throws IllegalArgumentException if the given parameter is not valid
	 *     for the statistic
	 */
	public void incrementStatistic ( Statistic statistic , Material material ) throws IllegalArgumentException {
		execute ( handle -> handle.incrementStatistic ( statistic , material ) );
	}
	
	/**
	 * Decrements the given statistic for this player for the given material.
	 * <p>
	 * This is equivalent to the following code:
	 * <code>decrementStatistic(Statistic, Material, 1)</code>
	 *
	 * @param statistic Statistic to decrement
	 * @param material Material to offset the statistic with
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if material is null
	 * @throws IllegalArgumentException if the given parameter is not valid
	 *     for the statistic
	 */
	public void decrementStatistic ( Statistic statistic , Material material ) throws IllegalArgumentException {
		execute ( handle -> handle.decrementStatistic ( statistic , material ) );
	}
	
	/**
	 * Gets the value of the given statistic for this player.
	 *
	 * @param statistic Statistic to check
	 * @param material Material offset of the statistic
	 * @return the value of the given statistic
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if material is null
	 * @throws IllegalArgumentException if the given parameter is not valid
	 *     for the statistic
	 */
	public int getStatistic ( Statistic statistic , Material material ) throws IllegalArgumentException {
		return extract ( handle -> handle.getStatistic ( statistic , material ) );
	}
	
	/**
	 * Increments the given statistic for this player for the given material.
	 *
	 * @param statistic Statistic to increment
	 * @param material Material to offset the statistic with
	 * @param amount Amount to increment this statistic by
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if material is null
	 * @throws IllegalArgumentException if amount is negative
	 * @throws IllegalArgumentException if the given parameter is not valid
	 *     for the statistic
	 */
	public void incrementStatistic ( Statistic statistic , Material material , int amount )
			throws IllegalArgumentException {
		execute ( handle -> handle.incrementStatistic ( statistic , material , amount ) );
	}
	
	/**
	 * Decrements the given statistic for this player for the given material.
	 *
	 * @param statistic Statistic to decrement
	 * @param material Material to offset the statistic with
	 * @param amount Amount to decrement this statistic by
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if material is null
	 * @throws IllegalArgumentException if amount is negative
	 * @throws IllegalArgumentException if the given parameter is not valid
	 *     for the statistic
	 */
	public void decrementStatistic ( Statistic statistic , Material material , int amount )
			throws IllegalArgumentException {
		execute ( handle -> handle.decrementStatistic ( statistic , material , amount ) );
	}
	
	/**
	 * Sets the given statistic for this player for the given material.
	 *
	 * @param statistic Statistic to set
	 * @param material Material to offset the statistic with
	 * @param newValue The value to set this statistic to
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if material is null
	 * @throws IllegalArgumentException if newValue is negative
	 * @throws IllegalArgumentException if the given parameter is not valid
	 *     for the statistic
	 */
	public void setStatistic ( Statistic statistic , Material material , int newValue )
			throws IllegalArgumentException {
		execute ( handle -> handle.setStatistic ( statistic , material , newValue ) );
	}
	
	/**
	 * Increments the given statistic for this player for the given entity.
	 * <p>
	 * This is equivalent to the following code:
	 * <code>incrementStatistic(Statistic, EntityType, 1)</code>
	 *
	 * @param statistic Statistic to increment
	 * @param entityType EntityType to offset the statistic with
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if entityType is null
	 * @throws IllegalArgumentException if the given parameter is not valid
	 *     for the statistic
	 */
	public void incrementStatistic ( Statistic statistic , EntityType entityType ) throws IllegalArgumentException {
		execute ( handle -> handle.incrementStatistic ( statistic , entityType ) );
	}
	
	/**
	 * Decrements the given statistic for this player for the given entity.
	 * <p>
	 * This is equivalent to the following code:
	 * <code>decrementStatistic(Statistic, EntityType, 1)</code>
	 *
	 * @param statistic Statistic to decrement
	 * @param entityType EntityType to offset the statistic with
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if entityType is null
	 * @throws IllegalArgumentException if the given parameter is not valid
	 *     for the statistic
	 */
	public void decrementStatistic ( Statistic statistic , EntityType entityType ) throws IllegalArgumentException {
		execute ( handle -> handle.decrementStatistic ( statistic , entityType ) );
	}
	
	/**
	 * Gets the value of the given statistic for this player.
	 *
	 * @param statistic Statistic to check
	 * @param entityType EntityType offset of the statistic
	 * @return the value of the given statistic
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if entityType is null
	 * @throws IllegalArgumentException if the given parameter is not valid
	 *     for the statistic
	 */
	public int getStatistic ( Statistic statistic , EntityType entityType ) throws IllegalArgumentException {
		return extract ( handle -> handle.getStatistic ( statistic , entityType ) );
	}
	
	/**
	 * Increments the given statistic for this player for the given entity.
	 *
	 * @param statistic Statistic to increment
	 * @param entityType EntityType to offset the statistic with
	 * @param amount Amount to increment this statistic by
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if entityType is null
	 * @throws IllegalArgumentException if amount is negative
	 * @throws IllegalArgumentException if the given parameter is not valid
	 *     for the statistic
	 */
	public void incrementStatistic ( Statistic statistic , EntityType entityType , int amount )
			throws IllegalArgumentException {
		execute ( handle -> handle.incrementStatistic ( statistic , entityType , amount ) );
	}
	
	/**
	 * Decrements the given statistic for this player for the given entity.
	 *
	 * @param statistic Statistic to decrement
	 * @param entityType EntityType to offset the statistic with
	 * @param amount Amount to decrement this statistic by
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if entityType is null
	 * @throws IllegalArgumentException if amount is negative
	 * @throws IllegalArgumentException if the given parameter is not valid
	 *     for the statistic
	 */
	public void decrementStatistic ( Statistic statistic , EntityType entityType , int amount ) {
		execute ( handle -> handle.decrementStatistic ( statistic , entityType , amount ) );
	}
	
	/**
	 * Sets the given statistic for this player for the given entity.
	 *
	 * @param statistic Statistic to set
	 * @param entityType EntityType to offset the statistic with
	 * @param newValue The value to set this statistic to
	 * @throws IllegalArgumentException if statistic is null
	 * @throws IllegalArgumentException if entityType is null
	 * @throws IllegalArgumentException if newValue is negative
	 * @throws IllegalArgumentException if the given parameter is not valid
	 *     for the statistic
	 */
	public void setStatistic ( Statistic statistic , EntityType entityType , int newValue ) {
		execute ( handle -> handle.setStatistic ( statistic , entityType , newValue ) );
	}
	
	/**
	 * Sets the Location where the player will spawn at their bed.
	 *
	 * @param location where to set the respawn location
	 */
	public void setBedSpawnLocation ( Location location ) {
		execute ( handle -> handle.setBedSpawnLocation ( location ) );
	}
	
	/**
	 * Sets the Location where the player will spawn at their bed.
	 *  @param location where to set the respawn location
	 * @param force whether to forcefully set the respawn location even if a
	 */
	public void setBedSpawnLocation ( Location location , boolean force ) {
		execute ( handle -> handle.setBedSpawnLocation ( location , force ) );
	}
	
	/**
	 * Play a note for a player at a location. This requires a note block
	 * at the particular location (as far as the client is concerned). This
	 * will not work without a note block. This will not work with cake.
	 *
	 * @param loc The location of a note block.
	 * @param instrument The instrument ID.
	 * @param note The note ID.
	 * @deprecated Magic value
	 */
	public void playNote ( Location loc , byte instrument , byte note ) {
		execute ( handle -> handle.playNote ( loc , instrument , note ) );
	}
	
	/**
	 * Play a note for a player at a location. This requires a note block
	 * at the particular location (as far as the client is concerned). This
	 * will not work without a note block. This will not work with cake.
	 *  @param loc The location of a note block
	 * @param instrument The instrument
	 * @param note The note
	 */
	public void playNote ( Location loc , Instrument instrument , Note note ) {
		execute ( handle -> handle.playNote ( loc , instrument , note ) );
	}
	
	/**
	 * Play a sound for a player at the location.
	 * <p>
	 * This function will fail silently if Location or Sound are null.
	 *  @param location The location to play the sound
	 * @param sound The sound to play
	 * @param volume The volume of the sound
	 * @param pitch The pitch of the sound
	 */
	public void playSound ( Location location , Sound sound , float volume , float pitch ) {
		execute ( handle -> handle.playSound ( location , sound , volume , pitch ) );
	}
	
	/**
	 * Play a sound for a player at the location.
	 * <p>
	 * This function will fail silently if Location or Sound are null. No
	 * sound will be heard by the player if their client does not have the
	 * respective sound for the value passed.
	 *  @param location the location to play the sound
	 * @param sound the internal sound name to play
	 * @param volume the volume of the sound
	 * @param pitch the pitch of the sound
	 */
	public void playSound ( Location location , String sound , float volume , float pitch ) {
		execute ( handle -> handle.playSound ( location , sound , volume , pitch ) );
	}
	
	/**
	 * Play a sound for a player at the location.
	 * <p>
	 * This function will fail silently if Location or Sound are null.
	 *  @param location The location to play the sound
	 * @param sound The sound to play
	 * @param category The category of the sound
	 * @param volume The volume of the sound
	 * @param pitch The pitch of the sound
	 */
	public void playSound ( Location location , Sound sound , SoundCategory category , float volume , float pitch ) {
		execute ( handle -> handle.playSound ( location , sound , category , volume , pitch ) );
	}
	
	/**
	 * Play a sound for a player at the location.
	 * <p>
	 * This function will fail silently if Location or Sound are null. No sound
	 * will be heard by the player if their client does not have the respective
	 * sound for the value passed.
	 *  @param location the location to play the sound
	 * @param sound the internal sound name to play
	 * @param category The category of the sound
	 * @param volume the volume of the sound
	 * @param pitch the pitch of the sound
	 */
	public void playSound ( Location location , String sound , SoundCategory category , float volume , float pitch ) {
		execute ( handle -> handle.playSound ( location , sound , category , volume , pitch ) );
	}
	
	/**
	 * Stop the specified sound from playing.
	 *
	 * @param sound the sound to stop
	 */
	public void stopSound ( Sound sound ) {
		execute ( handle -> handle.stopSound ( sound ) );
	}
	
	/**
	 * Stop the specified sound from playing.
	 *
	 * @param sound the sound to stop
	 */
	public void stopSound ( String sound ) {
		execute ( handle -> handle.stopSound ( sound ) );
	}
	
	/**
	 * Stop the specified sound from playing.
	 *  @param sound the sound to stop
	 * @param category the category of the sound
	 */
	public void stopSound ( Sound sound , SoundCategory category ) {
		execute ( handle -> handle.stopSound ( sound , category ) );
	}
	
	/**
	 * Stop the specified sound from playing.
	 *  @param sound the sound to stop
	 * @param category the category of the sound
	 */
	public void stopSound ( String sound , SoundCategory category ) {
		execute ( handle -> handle.stopSound ( sound , category ) );
	}
	
	/**
	 * Plays an effect to just this player.
	 *
	 * @param loc the location to play the effect at
	 * @param effect the {@link Effect}
	 * @param data a data bit needed for some effects
	 * @deprecated Magic value
	 */
	public void playEffect ( Location loc , Effect effect , int data ) {
		execute ( handle -> handle.playEffect ( loc , effect , data ) );
	}
	
	/**
	 * Plays an effect to just this player.
	 *  @param loc the location to play the effect at
	 * @param effect the {@link Effect}
	 * @param data a data bit needed for some effects
	 */
	public < T > void playEffect ( Location loc , Effect effect , T data ) {
		execute ( handle -> handle.playEffect ( loc , effect , data ) );
	}
	
	/**
	 * Force this player to break a Block using the item in their main hand.
	 *
	 * This method will respect enchantments, handle item durability (if
	 * applicable) and drop experience and the correct items according to the
	 * tool/item in the player's hand.
	 * <p>
	 * Note that this method will call a <b>BlockBreakEvent</b>, meaning that
	 * this method may not be successful in breaking the block if the event was
	 * cancelled by a third party plugin. Care should be taken if running this
	 * method in a BlockBreakEvent listener as recursion may be possible if it
	 * is invoked on the same {@link Block} being broken in the event.
	 * <p>
	 * Additionally, a <b>BlockDropItemEvent</b> is called for the items
	 * dropped by this method (if successful).
	 * <p>
	 * The block must be in the same world as the player.
	 *
	 * @param block the block to break
	 *
	 * @return true if the block was broken, false if the break failed
	 */
	public boolean breakBlock ( Block block ) {
		return invokeSafe ( "breakBlock" , block );
	}
	
	/**
	 * Send a block change. This fakes a block change packet for a user at a
	 * certain location. This will not actually change the world in any way.
	 *
	 * @param loc The location of the changed block
	 * @param material The new block
	 * @param data The block data
	 * @deprecated Magic value
	 */
	public void sendBlockChange ( Location loc , Material material , byte data ) {
		execute ( handle -> handle.sendBlockChange ( loc , material , data ) );
	}
	
	/**
	 * Send a block change. This fakes a block change packet for a user at a
	 * certain location. This will not actually change the world in any way.
	 *  @param loc The location of the changed block
	 * @param block The new block
	 */
	public void sendBlockChange ( Location loc , BlockData block ) {
		execute ( handle -> handle.sendBlockChange ( loc , block ) );
	}
	
	/**
	 * Send block damage. This fakes block break progress for a user at a
	 * certain location. This will not actually change the block's break
	 * progress in any way.
	 *  @param loc the location of the damaged block
	 * @param progress the progress from 0.0 - 1.0 where 0 is no damage and
	 */
	public void sendBlockDamage ( Location loc , float progress ) {
		execute ( handle -> handle.sendBlockDamage ( loc , progress ) );
	}
	
	/**
	 * Send a chunk change. This fakes a chunk change packet for a user at a
	 * certain location. The updated cuboid must be entirely within a single
	 * chunk. This will not actually change the world in any way.
	 * <p>
	 * At least one of the dimensions of the cuboid must be even. The size of
	 * the data buffer must be 2.5*sx*sy*sz and formatted in accordance with
	 * the Packet51 format.
	 *
	 * @param loc The location of the cuboid
	 * @param sx The x size of the cuboid
	 * @param sy The y size of the cuboid
	 * @param sz The z size of the cuboid
	 * @param data The data to be sent
	 * @return true if the chunk change packet was sent
	 * @deprecated Magic value
	 */
	public boolean sendChunkChange ( Location loc , int sx , int sy , int sz , byte[] data ) {
		return invokeSafe ( "sendChunkChange" , loc , sx , sy , sz , data );
	}
	
	/**
	 * Send a sign change. This fakes a sign change packet for a user at
	 * a certain location. This will not actually change the world in any way.
	 * This method will use a sign at the location's block or a faked sign
	 * sent via
	 * {@link #sendBlockChange(Location , Material , byte)}.
	 * <p>
	 * If the client does not have a sign at the given location it will
	 * display an error message to the user.
	 *
	 * @param loc the location of the sign
	 * @param lines the new text on the sign or null to clear it
	 * @throws IllegalArgumentException if location is null
	 * @throws IllegalArgumentException if lines is non-null and has a length less than 4
	 */
	public void sendSignChange ( Location loc , String[] lines ) throws IllegalArgumentException {
		execute ( handle -> handle.sendSignChange ( loc , lines ) );
	}
	
	/**
	 * Send a sign change. This fakes a sign change packet for a user at
	 * a certain location. This will not actually change the world in any way.
	 * This method will use a sign at the location's block or a faked sign
	 * sent via
	 * {@link #sendBlockChange(Location , Material , byte)}.
	 * <p>
	 * If the client does not have a sign at the given location it will
	 * display an error message to the user.
	 *
	 * @param loc the location of the sign
	 * @param lines the new text on the sign or null to clear it
	 * @param dyeColor the color of the sign
	 * @throws IllegalArgumentException if location is null
	 * @throws IllegalArgumentException if dyeColor is null
	 * @throws IllegalArgumentException if lines is non-null and has a length less than 4
	 */
	public void sendSignChange ( Location loc , String[] lines , DyeColor dyeColor ) throws IllegalArgumentException {
		execute ( handle -> handle.sendSignChange ( loc , lines , dyeColor ) );
	}
	
	/**
	 * Send a sign change. This fakes a sign change packet for a user at
	 * a certain location. This will not actually change the world in any way.
	 * This method will use a sign at the location's block or a faked sign
	 * sent via
	 * {@link #sendBlockChange(Location , Material , byte)}.
	 * <p>
	 * If the client does not have a sign at the given location it will
	 * display an error message to the user.
	 *
	 * @param loc the location of the sign
	 * @param lines the new text on the sign or null to clear it
	 * @param dyeColor the color of the sign
	 * @param hasGlowingText if the sign's text should be glowing
	 * @throws IllegalArgumentException if location is null
	 * @throws IllegalArgumentException if dyeColor is null
	 * @throws IllegalArgumentException if lines is non-null and has a length less than 4
	 */
	public void sendSignChange ( Location loc , String[] lines , DyeColor dyeColor , boolean hasGlowingText )
			throws IllegalArgumentException {
		execute ( handle -> handle.sendSignChange ( loc , lines , dyeColor , hasGlowingText ) );
	}
	
	/**
	 * Render a map and send it to the player in its entirety. This may be
	 * used when streaming the map in the normal manner is not desirable.
	 *
	 * @param map The map to be sent
	 */
	public void sendMap ( MapView map ) {
		execute ( handle -> handle.sendMap ( map ) );
	}
	
	/**
	 * Forces an update of the player's entire inventory.
	 *
	 */
	public void updateInventory ( ) {
		execute ( handle -> handle.updateInventory ( ) );
	}
	
	/**
	 * Sets the current time on the player's client. When relative is true the
	 * player's time will be kept synchronized to its world time with the
	 * specified offset.
	 * <p>
	 * When using non relative time the player's time will stay fixed at the
	 * specified time parameter. It's up to the caller to continue updating
	 * the player's time. To restore player time to normal use
	 * resetPlayerTime().
	 *  @param time The current player's perceived time or the player's time
	 *     offset from the server time.
	 * @param relative When true the player time is kept relative to its world
	 */
	public void setPlayerTime ( long time , boolean relative ) {
		execute ( handle -> handle.setPlayerTime ( time , relative ) );
	}
	
	/**
	 * Returns the player's current timestamp.
	 *
	 * @return The player's time
	 */
	public long getPlayerTime ( ) {
		return extract ( handle -> handle.getPlayerTime ( ) );
	}
	
	/**
	 * Returns the player's current time offset relative to server time, or
	 * the current player's fixed time if the player's time is absolute.
	 *
	 * @return The player's time
	 */
	public long getPlayerTimeOffset ( ) {
		return extract ( handle -> handle.getPlayerTimeOffset ( ) );
	}
	
	/**
	 * Returns true if the player's time is relative to the server time,
	 * otherwise the player's time is absolute and will not change its current
	 * time unless done so with setPlayerTime().
	 *
	 * @return true if the player's time is relative to the server time.
	 */
	public boolean isPlayerTimeRelative ( ) {
		return extract ( handle -> handle.isPlayerTimeRelative ( ) );
	}
	
	/**
	 * Restores the normal condition where the player's time is synchronized
	 * with the server time.
	 * <p>
	 * Equivalent to calling setPlayerTime(0, true).
	 */
	public void resetPlayerTime ( ) {
		execute ( handle -> handle.resetPlayerTime ( ) );
	}
	
	/**
	 * Sets the type of weather the player will see.  When used, the weather
	 * status of the player is locked until {@link #resetPlayerWeather()} is
	 * used.
	 *
	 * @param type The WeatherType enum type the player should experience
	 */
	public void setPlayerWeather ( WeatherType type ) {
		execute ( handle -> handle.setPlayerWeather ( type ) );
	}
	
	/**
	 * Returns the type of weather the player is currently experiencing.
	 *
	 * @return The WeatherType that the player is currently experiencing or
	 *     null if player is seeing server weather.
	 */
	public WeatherType getPlayerWeather ( ) {
		return extract ( handle -> handle.getPlayerWeather ( ) );
	}
	
	/**
	 * Restores the normal condition where the player's weather is controlled
	 * by server conditions.
	 */
	public void resetPlayerWeather ( ) {
		execute ( handle -> handle.resetPlayerWeather ( ) );
	}
	
	/**
	 * Gives the player the amount of experience specified.
	 *
	 * @param amount Exp amount to give
	 */
	public void giveExp ( int amount ) {
		execute ( handle -> handle.giveExp ( amount ) );
	}
	
	/**
	 * Gives the player the amount of experience levels specified. Levels can
	 * be taken by specifying a negative amount.
	 *
	 * @param amount amount of experience levels to give or take
	 */
	public void giveExpLevels ( int amount ) {
		execute ( handle -> handle.giveExpLevels ( amount ) );
	}
	
	/**
	 * Gets the players current experience points towards the next level.
	 * <p>
	 * This is a percentage value. 0 is "no progress" and 1 is "next level".
	 *
	 * @return Current experience points
	 */
	public float getExp ( ) {
		return extract ( handle -> handle.getExp ( ) );
	}
	
	/**
	 * Sets the players current experience points towards the next level
	 * <p>
	 * This is a percentage value. 0 is "no progress" and 1 is "next level".
	 *
	 * @param exp New experience points
	 */
	public void setExp ( float exp ) {
		execute ( handle -> handle.setExp ( exp ) );
	}
	
	/**
	 * Gets the players current experience level
	 *
	 * @return Current experience level
	 */
	public int getLevel ( ) {
		return extract ( handle -> handle.getLevel ( ) );
	}
	
	/**
	 * Sets the players current experience level
	 *
	 * @param level New experience level
	 */
	public void setLevel ( int level ) {
		execute ( handle -> handle.setLevel ( level ) );
	}
	
	/**
	 * Gets the players total experience points.
	 * <br>
	 * This refers to the total amount of experience the player has collected
	 * over time and is not currently displayed to the client.
	 *
	 * @return Current total experience points
	 */
	public int getTotalExperience ( ) {
		return extract ( handle -> handle.getTotalExperience ( ) );
	}
	
	/**
	 * Sets the players current experience points.
	 * <br>
	 * This refers to the total amount of experience the player has collected
	 * over time and is not currently displayed to the client.
	 *
	 * @param exp New total experience points
	 */
	public void setTotalExperience ( int exp ) {
		execute ( handle -> handle.setTotalExperience ( exp ) );
	}
	
	/**
	 * Send an experience change.
	 *
	 * This fakes an experience change packet for a user. This will not actually
	 * change the experience points in any way.
	 *
	 * @param progress Experience progress percentage (between 0.0 and 1.0)
	 * @see #setExp(float)
	 */
	public void sendExperienceChange ( float progress ) {
		execute ( handle -> handle.sendExperienceChange ( progress ) );
	}
	
	/**
	 * Send an experience change.
	 *
	 * This fakes an experience change packet for a user. This will not actually
	 * change the experience points in any way.
	 *
	 * @param progress New experience progress percentage (between 0.0 and 1.0)
	 * @param level New experience level
	 *
	 * @see #setExp(float)
	 * @see #setLevel(int)
	 */
	public void sendExperienceChange ( float progress , int level ) {
		execute ( handle -> handle.sendExperienceChange ( progress , level ) );
	}
	
	/**
	 * Determines if the Player is allowed to fly via jump key double-tap like
	 * in creative mode.
	 *
	 * @return True if the player is allowed to fly.
	 */
	public boolean getAllowFlight ( ) {
		return extract ( handle -> handle.getAllowFlight ( ) );
	}
	
	/**
	 * Sets if the Player is allowed to fly via jump key double-tap like in
	 * creative mode.
	 *
	 * @param flight If flight should be allowed.
	 */
	public void setAllowFlight ( boolean flight ) {
		execute ( handle -> handle.setAllowFlight ( flight ) );
	}
	
	/**
	 * Hides a player from this player
	 *
	 * @param player Player to hide
	 */
	@SuppressWarnings ( "deprecation" )
	public void hidePlayer ( Player player ) {
		try {
			invoke ( "hidePlayer" , new Class[] { Plugin.class , Player.class } ,
					 AdrianSRCore.getInstance ( ) , player );
		} catch ( NoSuchMethodException ex ) { // legacy
			execute ( handle -> handle.hidePlayer ( player ) );
		}
	}
	
	/**
	 * Hides a player from this player
	 *  @param plugin Plugin that wants to hide the player
	 * @param player Player to hide
	 */
	public void hidePlayer ( Plugin plugin , Player player ) {
		try {
			invoke ( "hidePlayer" , new Class[] { Plugin.class , Player.class } , plugin , player );
		} catch ( NoSuchMethodException ex ) { // legacy
			hidePlayer ( player );
		}
	}
	
	/**
	 * Allows this player to see a player that was previously hidden
	 *
	 * @param player Player to show
	 */
	@SuppressWarnings ( "deprecation" )
	public void showPlayer ( Player player ) {
		try {
			invoke ( "showPlayer" , new Class[] { Plugin.class , Player.class } ,
					 AdrianSRCore.getInstance ( ) , player );
		} catch ( NoSuchMethodException ex ) { // legacy
			execute ( handle -> handle.showPlayer ( player ) );
		}
	}
	
	/**
	 * Allows this player to see a player that was previously hidden. If
	 * another another plugin had hidden the player too, then the player will
	 * remain hidden until the other plugin calls this method too.
	 *  @param plugin Plugin that wants to show the player
	 * @param player Player to show
	 */
	public void showPlayer ( Plugin plugin , Player player ) {
		try {
			invoke ( "showPlayer" , new Class[] { Plugin.class , Player.class } , plugin , player );
		} catch ( NoSuchMethodException ex ) { // legacy
			showPlayer ( player );
		}
	}
	
	/**
	 * Checks to see if a player has been hidden from this player
	 *
	 * @param player Player to check
	 * @return True if the provided player is not being hidden from this
	 *     player
	 */
	public boolean canSee ( Player player ) {
		return extract ( handle -> handle.canSee ( player ) );
	}
	
	/**
	 * Checks to see if this player is currently flying or not.
	 *
	 * @return True if the player is flying, else false.
	 */
	public boolean isFlying ( ) {
		return extract ( handle -> handle.isFlying ( ) );
	}
	
	/**
	 * Makes this player start or stop flying.
	 *
	 * @param value True to fly.
	 */
	public void setFlying ( boolean value ) {
		execute ( handle -> handle.setFlying ( value ) );
	}
	
	/**
	 * Sets the speed at which a client will fly. Negative values indicate
	 * reverse directions.
	 *
	 * @param value The new speed, from -1 to 1.
	 * @throws IllegalArgumentException If new speed is less than -1 or
	 *     greater than 1
	 */
	public void setFlySpeed ( float value ) throws IllegalArgumentException {
		execute ( handle -> handle.setFlySpeed ( value ) );
	}
	
	/**
	 * Sets the speed at which a client will walk. Negative values indicate
	 * reverse directions.
	 *
	 * @param value The new speed, from -1 to 1.
	 * @throws IllegalArgumentException If new speed is less than -1 or
	 *     greater than 1
	 */
	public void setWalkSpeed ( float value ) throws IllegalArgumentException {
		execute ( handle -> handle.setWalkSpeed ( value ) );
	}
	
	/**
	 * Gets the current allowed speed that a client can fly.
	 *
	 * @return The current allowed speed, from -1 to 1
	 */
	public float getFlySpeed ( ) {
		return extract ( handle -> handle.getFlySpeed ( ) );
	}
	
	/**
	 * Gets the current allowed speed that a client can walk.
	 *
	 * @return The current allowed speed, from -1 to 1
	 */
	public float getWalkSpeed ( ) {
		return extract ( handle -> handle.getWalkSpeed ( ) );
	}
	
	/**
	 * Request that the player's client download and switch texture packs.
	 * <p>
	 * The player's client will download the new texture pack asynchronously
	 * in the background, and will automatically switch to it once the
	 * download is complete. If the client has downloaded and cached the same
	 * texture pack in the past, it will perform a file size check against
	 * the response content to determine if the texture pack has changed and
	 * needs to be downloaded again. When this request is sent for the very
	 * first time from a given server, the client will first display a
	 * confirmation GUI to the player before proceeding with the download.
	 * <p>
	 * Notes:
	 * <ul>
	 * <li>Players can disable server textures on their client, in which
	 *     case this method will have no affect on them. Use the
	 *     <b>PlayerResourcePackStatusEvent</b> to figure out whether or not
	 *     the player loaded the pack!
	 * <li>There is no concept of resetting texture packs back to default
	 *     within Minecraft, so players will have to relog to do so or you
	 *     have to send an empty pack.
	 * <li>The request is send with "null" as the hash. This might result
	 *     in newer versions not loading the pack correctly.
	 * </ul>
	 *
	 * @param url The URL from which the client will download the texture
	 *     pack. The string must contain only US-ASCII characters and should
	 *     be encoded as per RFC 1738.
	 * @throws IllegalArgumentException Thrown if the URL is null.
	 * @throws IllegalArgumentException Thrown if the URL is too long.
	 * @deprecated Minecraft no longer uses textures packs. Instead you
	 *     should use {@link #setResourcePack(String)}.
	 */
	public void setTexturePack ( String url ) {
		execute ( handle -> handle.setTexturePack ( url ) );
	}
	
	/**
	 * Request that the player's client download and switch resource packs.
	 * <p>
	 * The player's client will download the new resource pack asynchronously
	 * in the background, and will automatically switch to it once the
	 * download is complete. If the client has downloaded and cached the same
	 * resource pack in the past, it will perform a file size check against
	 * the response content to determine if the resource pack has changed and
	 * needs to be downloaded again. When this request is sent for the very
	 * first time from a given server, the client will first display a
	 * confirmation GUI to the player before proceeding with the download.
	 * <p>
	 * Notes:
	 * <ul>
	 * <li>Players can disable server resources on their client, in which
	 *     case this method will have no affect on them. Use the
	 *     <b>PlayerResourcePackStatusEvent</b> to figure out whether or not
	 *     the player loaded the pack!
	 * <li>There is no concept of resetting resource packs back to default
	 *     within Minecraft, so players will have to relog to do so or you
	 *     have to send an empty pack.
	 * <li>The request is send with "null" as the hash. This might result
	 *     in newer versions not loading the pack correctly.
	 * </ul>
	 *
	 * @param url The URL from which the client will download the resource
	 *     pack. The string must contain only US-ASCII characters and should
	 *     be encoded as per RFC 1738.
	 * @throws IllegalArgumentException Thrown if the URL is null.
	 * @throws IllegalArgumentException Thrown if the URL is too long. The
	 *     length restriction is an implementation specific arbitrary value.
	 */
	public void setResourcePack ( String url ) {
		try {
			invoke ( "setResourcePack" , new Class[] { String.class } , url );
		} catch ( NoSuchMethodException e ) { // legacy
			setTexturePack ( url );
		}
	}
	
	/**
	 * Request that the player's client download and switch resource packs.
	 * <p>
	 * The player's client will download the new resource pack asynchronously
	 * in the background, and will automatically switch to it once the
	 * download is complete. If the client has downloaded and cached a
	 * resource pack with the same hash in the past it will not download but
	 * directly apply the cached pack. When this request is sent for the very
	 * first time from a given server, the client will first display a
	 * confirmation GUI to the player before proceeding with the download.
	 * <p>
	 * Notes:
	 * <ul>
	 * <li>Players can disable server resources on their client, in which
	 *     case this method will have no affect on them. Use the
	 *     <b>PlayerResourcePackStatusEvent</b> to figure out whether or not
	 *     the player loaded the pack!
	 * <li>There is no concept of resetting resource packs back to default
	 *     within Minecraft, so players will have to relog to do so or you
	 *     have to send an empty pack.
	 * </ul>
	 *
	 * @param url The URL from which the client will download the resource
	 *     pack. The string must contain only US-ASCII characters and should
	 *     be encoded as per RFC 1738.
	 * @param hash The sha1 hash sum of the resource pack file which is used
	 *     to apply a cached version of the pack directly without downloading
	 *     if it is available. Hast to be 20 bytes long!
	 * @throws IllegalArgumentException Thrown if the URL is null.
	 * @throws IllegalArgumentException Thrown if the URL is too long. The
	 *     length restriction is an implementation specific arbitrary value.
	 * @throws IllegalArgumentException Thrown if the hash is null.
	 * @throws IllegalArgumentException Thrown if the hash is not 20 bytes
	 *     long.
	 */
	public void setResourcePack ( String url , byte[] hash ) {
		try {
			invoke ( "setResourcePack" , new Class[] { String.class , byte[].class } , url , hash );
		} catch ( NoSuchMethodException e ) { // legacy
			setTexturePack ( url );
		}
	}
	
	/**
	 * Gets the Scoreboard displayed to this player
	 *
	 * @return The current scoreboard seen by this player
	 */
	public Scoreboard getScoreboard ( ) {
		return extract ( handle -> handle.getScoreboard ( ) );
	}
	
	/**
	 * Sets the player's visible Scoreboard.
	 *
	 * @param scoreboard New Scoreboard for the player
	 * @throws IllegalArgumentException if scoreboard is null
	 * @throws IllegalArgumentException if scoreboard was not created by the
	 *     {@link ScoreboardManager scoreboard manager}
	 * @throws IllegalStateException if this is a player that is not logged
	 *     yet or has logged out
	 */
	public void setScoreboard ( Scoreboard scoreboard ) throws IllegalArgumentException, IllegalStateException {
		execute ( handle -> handle.setScoreboard ( scoreboard ) );
	}
	
	/**
	 * Gets if the client is displayed a 'scaled' health, that is, health on a
	 * scale from 0-{@link #getHealthScale()}.
	 *
	 * @return if client health display is scaled
	 * @see Player#setHealthScaled(boolean)
	 */
	public boolean isHealthScaled ( ) {
		return extract ( handle -> handle.isHealthScaled ( ) );
	}
	
	/**
	 * Sets if the client is displayed a 'scaled' health, that is, health on a
	 * scale from 0-{@link #getHealthScale()}.
	 * <p>
	 * Displayed health follows a simple formula <code>displayedHealth =
	 * getHealth() / getMaxHealth() * getHealthScale()</code>.
	 *
	 * @param scale if the client health display is scaled
	 */
	public void setHealthScaled ( boolean scale ) {
		execute ( handle -> handle.setHealthScaled ( scale ) );
	}
	
	/**
	 * Sets the number to scale health to for the client; this will also
	 * {@link #setHealthScaled(boolean) setHealthScaled(true)}.
	 * <p>
	 * Displayed health follows a simple formula <code>displayedHealth =
	 * getHealth() / getMaxHealth() * getHealthScale()</code>.
	 *
	 * @param scale the number to scale health to
	 * @throws IllegalArgumentException if scale is &lt;0
	 * @throws IllegalArgumentException if scale is {@link Double#NaN}
	 * @throws IllegalArgumentException if scale is too high
	 */
	public void setHealthScale ( double scale ) throws IllegalArgumentException {
		execute ( handle -> handle.setHealthScale ( scale ) );
	}
	
	/**
	 * Gets the number that health is scaled to for the client.
	 *
	 * @return the number that health would be scaled to for the client if
	 *     HealthScaling is set to true
	 * @see Player#setHealthScale(double)
	 * @see Player#setHealthScaled(boolean)
	 */
	public double getHealthScale ( ) {
		return extract ( handle -> handle.getHealthScale ( ) );
	}
	
	/**
	 * Gets the entity which is followed by the camera when in
	 * {@link GameMode#SPECTATOR}.
	 *
	 * @return the followed entity, or null if not in spectator mode or not
	 * following a specific entity.
	 */
	public Entity getSpectatorTarget ( ) {
		return extract ( handle -> handle.getSpectatorTarget ( ) );
	}
	
	/**
	 * Sets the entity which is followed by the camera when in
	 * {@link GameMode#SPECTATOR}.
	 *
	 * @param entity the entity to follow or null to reset
	 * @throws IllegalStateException if the player is not in
	 * {@link GameMode#SPECTATOR}
	 */
	public void setSpectatorTarget ( Entity entity ) {
		execute ( handle -> handle.setSpectatorTarget ( entity ) );
	}
	
	/**
	 * Sends a title and a subtitle message to the player. If either of these
	 * values are null, they will not be sent and the display will remain
	 * unchanged. If they are empty strings, the display will be updated as
	 * such. If the strings contain a new line, only the first line will be
	 * sent. The titles will be displayed with the client's default timings.
	 *
	 * @param title Title text
	 * @param subtitle Subtitle text
	 */
	public void sendTitle ( String title , String subtitle ) {
		sendTitle ( title , subtitle , 10 , 70 , 20 );
	}
	
	/**
	 * Sends a title and a subtitle message to the player. If either of these
	 * values are null, they will not be sent and the display will remain
	 * unchanged. If they are empty strings, the display will be updated as
	 * such. If the strings contain a new line, only the first line will be
	 * sent. All timings values may take a value of -1 to indicate that they
	 * will use the last value sent (or the defaults if no title has been
	 * displayed).
	 *  @param title Title text
	 * @param subtitle Subtitle text
	 * @param fadeIn time in ticks for titles to fade in. Defaults to 10.
	 * @param stay time in ticks for titles to stay. Defaults to 70.
	 * @param fadeOut time in ticks for titles to fade out. Defaults to 20.
	 */
	public void sendTitle ( String title , String subtitle , int fadeIn , int stay , int fadeOut ) {
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_11_R1 ) ) {
			execute ( handle -> handle.sendTitle ( title , subtitle , fadeIn , stay , fadeOut ) );
		} else {
			getBukkitPlayerOptional ( ).ifPresent (
					player -> TitlesUtil.send ( player , title , subtitle , fadeIn , stay , fadeOut ) );
		}
	}
	
	/**
	 * Resets the title displayed to the player. This will clear the displayed
	 * title / subtitle and reset timings to their default values.
	 */
	public void resetTitle ( ) {
		execute ( handle -> handle.resetTitle ( ) );
	}
	
	/**
	 * Spawns the particle (the number of times specified by count)
	 * at the target location.
	 *  @param particle the particle to spawn
	 * @param location the location to spawn at
	 * @param count the number of particles
	 */
	public void spawnParticle ( Particle particle , Location location , int count ) {
		handleOptional ( ).ifPresent ( handle -> {
			of ( particle ).display ( location , 0.0F , 0.0F , 0.0F , 1.0F , count , null , handle );
		} );
	}
	
	/**
	 * Spawns the particle (the number of times specified by count)
	 * at the target location.
	 *  @param particle the particle to spawn
	 * @param x the position on the x axis to spawn at
	 * @param y the position on the y axis to spawn at
	 * @param z the position on the z axis to spawn at
	 * @param count the number of particles
	 */
	public void spawnParticle ( Particle particle , double x , double y , double z , int count ) {
		handleOptional ( ).ifPresent ( handle -> {
			of ( particle ).display ( new Location ( getWorld ( ) , x , y , z ) ,
									  0.0F , 0.0F , 0.0F , 1.0F , count , null , handle );
		} );
	}
	
	/**
	 * Spawns the particle (the number of times specified by count)
	 * at the target location.
	 *  @param particle the particle to spawn
	 * @param location the location to spawn at
	 * @param count the number of particles
	 * @param data the data to use for the particle or null,
	 *             the type of this depends on {@link Particle#getDataType()}
	 */
	public < T > void spawnParticle ( Particle particle , Location location , int count , T data ) {
		throw new UnsupportedOperationException ( );
	}
	
	/**
	 * Spawns the particle (the number of times specified by count)
	 * at the target location.
	 *  @param particle the particle to spawn
	 * @param x the position on the x axis to spawn at
	 * @param y the position on the y axis to spawn at
	 * @param z the position on the z axis to spawn at
	 * @param count the number of particles
	 * @param data the data to use for the particle or null,
	 *             the type of this depends on {@link Particle#getDataType()}
	 */
	public < T > void spawnParticle ( Particle particle , double x , double y , double z , int count , T data ) {
		throw new UnsupportedOperationException ( );
	}
	
	/**
	 * Spawns the particle (the number of times specified by count)
	 * at the target location. The position of each particle will be
	 * randomized positively and negatively by the offset parameters
	 * on each axis.
	 *  @param particle the particle to spawn
	 * @param location the location to spawn at
	 * @param count the number of particles
	 * @param offsetX the maximum random offset on the X axis
	 * @param offsetY the maximum random offset on the Y axis
	 * @param offsetZ the maximum random offset on the Z axis
	 */
	public void spawnParticle ( Particle particle , Location location , int count ,
			double offsetX , double offsetY , double offsetZ ) {
		handleOptional ( ).ifPresent ( handle -> {
			of ( particle ).display ( location ,
									  ( float ) offsetX , ( float ) offsetY , ( float ) offsetZ ,
									  1.0F , count , null , handle );
		} );
	}
	
	/**
	 * Spawns the particle (the number of times specified by count)
	 * at the target location. The position of each particle will be
	 * randomized positively and negatively by the offset parameters
	 * on each axis.
	 *  @param particle the particle to spawn
	 * @param x the position on the x axis to spawn at
	 * @param y the position on the y axis to spawn at
	 * @param z the position on the z axis to spawn at
	 * @param count the number of particles
	 * @param offsetX the maximum random offset on the X axis
	 * @param offsetY the maximum random offset on the Y axis
	 * @param offsetZ the maximum random offset on the Z axis
	 */
	public void spawnParticle ( Particle particle , double x , double y , double z , int count , double offsetX ,
			double offsetY , double offsetZ ) {
		handleOptional ( ).ifPresent ( handle -> {
			of ( particle ).display ( new Location ( getWorld ( ) , x , y , z ) ,
									  ( float ) offsetX , ( float ) offsetY , ( float ) offsetZ ,
									  1.0F , count , null , handle );
		} );
	}
	
	/**
	 * Spawns the particle (the number of times specified by count)
	 * at the target location. The position of each particle will be
	 * randomized positively and negatively by the offset parameters
	 * on each axis.
	 *  @param particle the particle to spawn
	 * @param location the location to spawn at
	 * @param count the number of particles
	 * @param offsetX the maximum random offset on the X axis
	 * @param offsetY the maximum random offset on the Y axis
	 * @param offsetZ the maximum random offset on the Z axis
	 * @param data the data to use for the particle or null,
	 *             the type of this depends on {@link Particle#getDataType()}
	 */
	public < T > void spawnParticle ( Particle particle , Location location , int count , double offsetX ,
			double offsetY , double offsetZ , T data ) {
		throw new UnsupportedOperationException ( );
	}
	
	/**
	 * Spawns the particle (the number of times specified by count)
	 * at the target location. The position of each particle will be
	 * randomized positively and negatively by the offset parameters
	 * on each axis.
	 *  @param particle the particle to spawn
	 * @param x the position on the x axis to spawn at
	 * @param y the position on the y axis to spawn at
	 * @param z the position on the z axis to spawn at
	 * @param count the number of particles
	 * @param offsetX the maximum random offset on the X axis
	 * @param offsetY the maximum random offset on the Y axis
	 * @param offsetZ the maximum random offset on the Z axis
	 * @param data the data to use for the particle or null,
	 *             the type of this depends on {@link Particle#getDataType()}
	 */
	public < T > void spawnParticle ( Particle particle , double x , double y , double z , int count , double offsetX ,
			double offsetY , double offsetZ , T data ) {
		throw new UnsupportedOperationException ( );
	}
	
	/**
	 * Spawns the particle (the number of times specified by count)
	 * at the target location. The position of each particle will be
	 * randomized positively and negatively by the offset parameters
	 * on each axis.
	 *  @param particle the particle to spawn
	 * @param location the location to spawn at
	 * @param count the number of particles
	 * @param offsetX the maximum random offset on the X axis
	 * @param offsetY the maximum random offset on the Y axis
	 * @param offsetZ the maximum random offset on the Z axis
	 * @param extra the extra data for this particle, depends on the
	 */
	public void spawnParticle ( Particle particle , Location location , int count , double offsetX , double offsetY ,
			double offsetZ , double extra ) {
		handleOptional ( ).ifPresent ( handle -> {
			of ( particle ).display ( location ,
									  ( float ) offsetX , ( float ) offsetY , ( float ) offsetZ ,
									  1.0F , count , null , handle );
		} );
	}
	
	/**
	 * Spawns the particle (the number of times specified by count)
	 * at the target location. The position of each particle will be
	 * randomized positively and negatively by the offset parameters
	 * on each axis.
	 *  @param particle the particle to spawn
	 * @param x the position on the x axis to spawn at
	 * @param y the position on the y axis to spawn at
	 * @param z the position on the z axis to spawn at
	 * @param count the number of particles
	 * @param offsetX the maximum random offset on the X axis
	 * @param offsetY the maximum random offset on the Y axis
	 * @param offsetZ the maximum random offset on the Z axis
	 * @param extra the extra data for this particle, depends on the
	 * @deprecated
	 * @see #spawnParticle(Particle , double , double , double , int , double , double , double)
	 */
	public void spawnParticle ( Particle particle , double x , double y , double z , int count , double offsetX ,
			double offsetY , double offsetZ , double extra ) {
		spawnParticle ( particle , x , y , z , count , offsetX , offsetY , offsetZ );
	}
	
	/**
	 * Spawns the particle (the number of times specified by count)
	 * at the target location. The position of each particle will be
	 * randomized positively and negatively by the offset parameters
	 * on each axis.
	 *  @param particle the particle to spawn
	 * @param location the location to spawn at
	 * @param count the number of particles
	 * @param offsetX the maximum random offset on the X axis
	 * @param offsetY the maximum random offset on the Y axis
	 * @param offsetZ the maximum random offset on the Z axis
	 * @param extra the extra data for this particle, depends on the
	 *              particle used (normally speed)
	 * @param data the data to use for the particle or null,
	 *             the type of this depends on {@link Particle#getDataType()}
	 */
	public < T > void spawnParticle ( Particle particle , Location location , int count , double offsetX ,
			double offsetY , double offsetZ , double extra , T data ) {
		throw new UnsupportedOperationException ( );
	}
	
	/**
	 * Spawns the particle (the number of times specified by count)
	 * at the target location. The position of each particle will be
	 * randomized positively and negatively by the offset parameters
	 * on each axis.
	 *  @param particle the particle to spawn
	 * @param x the position on the x axis to spawn at
	 * @param y the position on the y axis to spawn at
	 * @param z the position on the z axis to spawn at
	 * @param count the number of particles
	 * @param offsetX the maximum random offset on the X axis
	 * @param offsetY the maximum random offset on the Y axis
	 * @param offsetZ the maximum random offset on the Z axis
	 * @param extra the extra data for this particle, depends on the
	 *              particle used (normally speed)
	 * @param data the data to use for the particle or null,
	 *             the type of this depends on {@link Particle#getDataType()}
	 */
	public < T > void spawnParticle ( Particle particle , double x , double y , double z , int count , double offsetX ,
			double offsetY , double offsetZ , double extra , T data ) {
		throw new UnsupportedOperationException ( );
	}
	
	protected ParticleEffect of ( Particle particle ) {
		return ParticleEffect.valueOf ( particle.name ( ) );
	}
	
	/**
	 * Return the player's progression on the specified advancement.
	 *
	 * @param advancement advancement
	 * @return object detailing the player's progress
	 */
	public AdvancementProgress getAdvancementProgress ( Advancement advancement ) {
		return extract ( handle -> getAdvancementProgress ( advancement ) );
	}
	
	/**
	 * Get the player's current client side view distance.
	 * <br>
	 * Will default to the server view distance if the client has not yet
	 * communicated this information,
	 *
	 * @return client view distance as above
	 */
	public int getClientViewDistance ( ) {
		return extract ( handle -> getClientViewDistance ( ) );
	}
	
	/**
	 * Gets the player's estimated ping in milliseconds.
	 *
	 * In Vanilla this value represents the average of the response time to the
	 * last four application layer ping packets sent. This value does not
	 * represent the network round trip time and as such may have less
	 * granularity and be impacted by other sources. For these reasons it
	 * <b>should not</b> be used for anti-cheat purposes. Its recommended use is
	 * only as a <b>qualitative</b> indicator of connection quality (Vanilla
	 * uses it for this purpose in the tab list).
	 *
	 * @return player ping
	 */
	public int getPing ( ) {
		// TODO: backwards compatibility required
		return extract ( handle -> getPing ( ) );
	}
	
	/**
	 * Gets the player's current locale.
	 *
	 * The value of the locale String is not defined properly.
	 * <br>
	 * The vanilla Minecraft client will use lowercase language / country pairs
	 * separated by an underscore, but custom resource packs may use any format
	 * they wish.
	 *
	 * @return the player's locale
	 */
	public String getLocale ( ) {
		return extract ( handle -> getLocale ( ) );
	}
	
	/**
	 * Update the list of commands sent to the client.
	 * <br>
	 * Generally useful to ensure the client has a complete list of commands
	 * after permission changes are done.
	 */
	public void updateCommands ( ) {
		execute ( handle -> handle.updateCommands ( ) );
	}
	
	/**
	 * Open a {@link Material#WRITTEN_BOOK} for a Player
	 *
	 * @param book The book to open for this player
	 */
	public void openBook ( ItemStack book ) {
		execute ( handle -> handle.openBook ( book ) );
	}
	
	/**
	 * Creates a Map representation of this class.
	 * <p>
	 * This class must provide a method to restore this class, as defined in
	 * the {@link ConfigurationSerializable} interface javadocs.
	 *
	 * @return Map containing the current state of this class
	 */
	public Map < String, Object > serialize ( ) {
		return extract ( handle -> handle.serialize ( ) );
	}
	
	/**
	 * Returns the name of this player
	 *
	 * @return Player name
	 */
	public String getName ( ) {
		return extract ( handle -> handle.getName ( ) );
	}
	
	/**
	 * Get the player's inventory.
	 *
	 * @return The inventory of the player, this also contains the armor
	 *     slots.
	 */
	public PlayerInventory getInventory ( ) {
		return extract ( handle -> handle.getInventory ( ) );
	}
	
	/**
	 * Get the player's EnderChest inventory
	 *
	 * @return The EnderChest of the player
	 */
	public Inventory getEnderChest ( ) {
		return extract ( handle -> handle.getEnderChest ( ) );
	}
	
	/**
	 * Gets the player's selected main hand
	 *
	 * @return the players main hand
	 */
	public MainHand getMainHand ( ) {
		return extract ( handle -> handle.getMainHand ( ) );
	}
	
	/**
	 * If the player currently has an inventory window open, this method will
	 * set a property of that window, such as the state of a progress bar.
	 *
	 * @param prop The property.
	 * @param value The value to set the property to.
	 * @return True if the property was successfully set.
	 */
	public boolean setWindowProperty ( InventoryView.Property prop , int value ) {
		return invokeSafe ( "setWindowProperty" ,
							new Class[] { InventoryView.Property.class , int.class } , value );
	}
	
	/**
	 * Gets the inventory view the player is currently viewing. If they do not
	 * have an inventory window open, it returns their internal crafting view.
	 *
	 * @return The inventory view.
	 */
	public InventoryView getOpenInventory ( ) {
		return extract ( handle -> handle.getOpenInventory ( ) );
	}
	
	/**
	 * Opens an inventory window with the specified inventory on the top and
	 * the player's inventory on the bottom.
	 *
	 * @param inventory The inventory to open
	 * @return The newly opened inventory view
	 */
	public InventoryView openInventory ( Inventory inventory ) {
		return extract ( handle -> handle.openInventory ( inventory ) );
	}
	
	/**
	 * Opens an empty workbench inventory window with the player's inventory
	 * on the bottom.
	 *
	 * @param location The location to attach it to. If null, the player's
	 *     location is used.
	 * @param force If false, and there is no workbench block at the location,
	 *     no inventory will be opened and null will be returned.
	 * @return The newly opened inventory view, or null if it could not be
	 *     opened.
	 */
	public InventoryView openWorkbench ( Location location , boolean force ) {
		return extract ( handle -> handle.openWorkbench ( location , force ) );
	}
	
	/**
	 * Opens an empty enchanting inventory window with the player's inventory
	 * on the bottom.
	 *
	 * @param location The location to attach it to. If null, the player's
	 *     location is used.
	 * @param force If false, and there is no enchanting table at the
	 *     location, no inventory will be opened and null will be returned.
	 * @return The newly opened inventory view, or null if it could not be
	 *     opened.
	 */
	public InventoryView openEnchanting ( Location location , boolean force ) {
		return extract ( handle -> handle.openEnchanting ( location , force ) );
	}
	
	/**
	 * Opens an inventory window to the specified inventory view.
	 *
	 * @param inventory The view to open
	 */
	public void openInventory ( InventoryView inventory ) {
		execute ( handle -> handle.openInventory ( inventory ) );
	}
	
	/**
	 * Starts a trade between the player and the villager.
	 *
	 * Note that only one player may trade with a villager at once. You must use
	 * the force parameter for this.
	 *
	 * @param trader The merchant to trade with. Cannot be null.
	 * @param force whether to force the trade even if another player is trading
	 * @return The newly opened inventory view, or null if it could not be
	 * opened.
	 */
	public InventoryView openMerchant ( Villager trader , boolean force ) {
		return extract ( handle -> handle.openMerchant ( trader , force ) );
	}
	
	/**
	 * Starts a trade between the player and the merchant.
	 *
	 * Note that only one player may trade with a merchant at once. You must use
	 * the force parameter for this.
	 *
	 * @param merchant The merchant to trade with. Cannot be null.
	 * @param force whether to force the trade even if another player is trading
	 * @return The newly opened inventory view, or null if it could not be
	 * opened.
	 */
	public InventoryView openMerchant ( Merchant merchant , boolean force ) {
		return extract ( handle -> handle.openMerchant ( merchant , force ) );
	}
	
	/**
	 * Force-closes the currently open inventory view for this player, if any.
	 */
	public void closeInventory ( ) {
		execute ( handle -> handle.closeInventory ( ) );
	}
	
	/**
	 * Returns the ItemStack currently in your hand, can be empty.
	 *
	 * @return The ItemStack of the item you are currently holding.
	 */
	public ItemStack getItemInHand ( ) {
		Player handle = handle ( );
		
		if ( handle != null ) {
			try {
				return ( ItemStack ) safeGetMethod (
						PlayerInventory.class , "getItemInMainHand" , new Class[ 0 ] ).invoke (
						handle.getInventory ( ) );
			} catch ( Exception e ) {
				try {
					return ( ItemStack ) safeGetMethod (
							PlayerInventory.class , "getItemInHand" , new Class[ 0 ] ).invoke (
							handle.getInventory ( ) );
				} catch ( IllegalAccessException | InvocationTargetException ex ) {
					ex.printStackTrace ( );
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Sets the item to the given ItemStack, this will replace whatever the
	 * user was holding.
	 *
	 * @param item The ItemStack which will end up in the hand
	 */
	public void setItemInHand ( ItemStack item ) {
		Player handle = handle ( );
		
		if ( handle != null ) {
			try {
				safeGetMethod ( PlayerInventory.class , "setItemInMainHand" , new Class[] { ItemStack.class } )
						.invoke ( handle.getInventory ( ) , item );
			} catch ( Exception e ) {
				try {
					safeGetMethod ( PlayerInventory.class , "setItemInHand" , new Class[] { ItemStack.class } )
							.invoke ( handle.getInventory ( ) , item );
				} catch ( IllegalAccessException | InvocationTargetException ex ) {
					ex.printStackTrace ( );
				}
			}
		}
	}
	
	/**
	 * Returns the ItemStack currently on your cursor, can be empty. Will
	 * always be empty if the player currently has no open window.
	 *
	 * @return The ItemStack of the item you are currently moving around.
	 */
	public ItemStack getItemOnCursor ( ) {
		return extract ( handle -> handle.getItemOnCursor ( ) );
	}
	
	/**
	 * Sets the item to the given ItemStack, this will replace whatever the
	 * user was moving. Will always be empty if the player currently has no
	 * open window.
	 *
	 * @param item The ItemStack which will end up in the hand
	 */
	public void setItemOnCursor ( ItemStack item ) {
		execute ( handle -> handle.setItemOnCursor ( item ) );
	}
	
	/**
	 * Check whether a cooldown is active on the specified material.
	 *
	 * @param material the material to check
	 * @return if a cooldown is active on the material
	 */
	public boolean hasCooldown ( Material material ) {
		return extract ( handle -> handle.hasCooldown ( material ) );
	}
	
	/**
	 * Get the cooldown time in ticks remaining for the specified material.
	 *
	 * @param material the material to check
	 * @return the remaining cooldown time in ticks
	 */
	public int getCooldown ( Material material ) {
		return extract ( handle -> handle.getCooldown ( material ) );
	}
	
	/**
	 * Set a cooldown on the specified material for a certain amount of ticks.
	 * ticks. 0 ticks will result in the removal of the cooldown.
	 * <p>
	 * Cooldowns are used by the server for items such as ender pearls and
	 * shields to prevent them from being used repeatedly.
	 * <p>
	 * Note that cooldowns will not by themselves stop an item from being used
	 * for attacking.
	 *  @param material the material to set the cooldown for
	 * @param ticks the amount of ticks to set or 0 to remove
	 */
	public void setCooldown ( Material material , int ticks ) {
		execute ( handle -> handle.setCooldown ( material , ticks ) );
	}
	
	/**
	 * Get the sleep ticks of the player. This value may be capped.
	 *
	 * @return slumber ticks
	 */
	public int getSleepTicks ( ) {
		return extract ( handle -> handle.getSleepTicks ( ) );
	}
	
	/**
	 * Attempts to make the entity sleep at the given location.
	 * <br>
	 * The location must be in the current world and have a bed placed at the
	 * location. The game may also enforce other requirements such as proximity
	 * to bed, monsters, and dimension type if force is not set.
	 *
	 * @param location the location of the bed
	 * @param force whether to try and sleep at the location even if not
	 * normally possible
	 * @return whether the sleep was successful
	 */
	public boolean sleep ( Location location , boolean force ) {
		return invokeSafe ( "sleep" , new Class[] { Location.class , boolean.class } , location , force );
	}
	
	/**
	 * Causes the player to wakeup if they are currently sleeping.
	 *
	 * @param setSpawnLocation whether to set their spawn location to the bed
	 * they are currently sleeping in
	 * @throws IllegalStateException if not sleeping
	 */
	public void wakeup ( boolean setSpawnLocation ) {
		execute ( handle -> handle.wakeup ( setSpawnLocation ) );
	}
	
	/**
	 * Gets the location of the bed the player is currently sleeping in
	 *
	 * @return location
	 * @throws IllegalStateException if not sleeping
	 */
	public Location getBedLocation ( ) {
		return extract ( handle -> handle.getBedLocation ( ) );
	}
	
	/**
	 * Gets this human's current {@link GameMode}
	 *
	 * @return Current game mode
	 */
	public GameMode getGameMode ( ) {
		return extract ( handle -> handle.getGameMode ( ) );
	}
	
	/**
	 * Sets this human's current {@link GameMode}
	 *
	 * @param mode New game mode
	 */
	public void setGameMode ( GameMode mode ) {
		execute ( handle -> handle.setGameMode ( mode ) );
	}
	
	/**
	 * Check if the player is currently blocking (ie with a shield).
	 *
	 * @return Whether they are blocking.
	 */
	public boolean isBlocking ( ) {
		return extract ( handle -> handle.isBlocking ( ) );
	}
	
	/**
	 * Check if the player currently has their hand raised (ie about to begin
	 * blocking).
	 *
	 * @return Whether their hand is raised
	 */
	public boolean isHandRaised ( ) {
		return extract ( handle -> handle.isHandRaised ( ) );
	}
	
	/**
	 * Gets the item that the player is using (eating food, drawing back a bow,
	 * blocking, etc.)
	 *
	 * @return the item being used by the player, or null if they are not using
	 * an item
	 */
	public ItemStack getItemInUse ( ) {
		return extract ( handle -> handle.getItemInUse ( ) );
	}
	
	/**
	 * Get the total amount of experience required for the player to level
	 *
	 * @return Experience required to level up
	 */
	public int getExpToLevel ( ) {
		return extract ( handle -> handle.getExpToLevel ( ) );
	}
	
	/**
	 * Gets the current cooldown for a player's attack.
	 *
	 * This is used to calculate damage, with 1.0 representing a fully charged
	 * attack and 0.0 representing a non-charged attack
	 *
	 * @return A float between 0.0-1.0 representing the progress of the charge
	 */
	public float getAttackCooldown ( ) {
		return extract ( handle -> handle.getAttackCooldown ( ) );
	}
	
	/**
	 * Discover a recipe for this player such that it has not already been
	 * discovered. This method will add the key's associated recipe to the
	 * player's recipe book.
	 *
	 * @param recipe the key of the recipe to discover
	 *
	 * @return whether or not the recipe was newly discovered
	 */
	public boolean discoverRecipe ( NamespacedKey recipe ) {
		return invokeSafe ( "discoverRecipe" , recipe );
	}
	
	/**
	 * Discover a collection of recipes for this player such that they have not
	 * already been discovered. This method will add the keys' associated
	 * recipes to the player's recipe book. If a recipe in the provided
	 * collection has already been discovered, it will be silently ignored.
	 *
	 * @param recipes the keys of the recipes to discover
	 *
	 * @return the amount of newly discovered recipes where 0 indicates that
	 * none were newly discovered and a number equal to {@code recipes.size()}
	 * indicates that all were new
	 */
	public int discoverRecipes ( Collection < NamespacedKey > recipes ) {
		return invokeSafe ( "discoverRecipe" , new Class[] { Collection.class } , recipes );
	}
	
	/**
	 * Undiscover a recipe for this player such that it has already been
	 * discovered. This method will remove the key's associated recipe from the
	 * player's recipe book.
	 *
	 * @param recipe the key of the recipe to undiscover
	 *
	 * @return whether or not the recipe was successfully undiscovered (i.e. it
	 * was previously discovered)
	 */
	public boolean undiscoverRecipe ( NamespacedKey recipe ) {
		return invokeSafe ( "undiscoverRecipe" , recipe );
	}
	
	/**
	 * Undiscover a collection of recipes for this player such that they have
	 * already been discovered. This method will remove the keys' associated
	 * recipes from the player's recipe book. If a recipe in the provided
	 * collection has not yet been discovered, it will be silently ignored.
	 *
	 * @param recipes the keys of the recipes to undiscover
	 *
	 * @return the amount of undiscovered recipes where 0 indicates that none
	 * were undiscovered and a number equal to {@code recipes.size()} indicates
	 * that all were undiscovered
	 */
	public int undiscoverRecipes ( Collection < NamespacedKey > recipes ) {
		return invokeSafe ( "undiscoverRecipes" , new Class[] { Collection.class } , recipes );
	}
	
	/**
	 * Check whether or not this entity has discovered the specified recipe.
	 *
	 * @param recipe the key of the recipe to check
	 *
	 * @return true if discovered, false otherwise
	 */
	public boolean hasDiscoveredRecipe ( NamespacedKey recipe ) {
		return extract ( handle -> handle.hasDiscoveredRecipe ( recipe ) );
	}
	
	/**
	 * Get an immutable set of recipes this entity has discovered.
	 *
	 * @return all discovered recipes
	 */
	public Set < NamespacedKey > getDiscoveredRecipes ( ) {
		return extract ( handle -> handle.getDiscoveredRecipes ( ) );
	}
	
	/**
	 * Gets the entity currently perched on the left shoulder or null if no
	 * entity.
	 * <br>
	 * The returned entity will not be spawned within the world, so most
	 * operations are invalid unless the entity is first spawned in.
	 *
	 * @return left shoulder entity
	 * @deprecated There are currently no well defined semantics regarding
	 * serialized entities in Bukkit. Use with care.
	 */
	public Entity getShoulderEntityLeft ( ) {
		return extract ( handle -> handle.getShoulderEntityLeft ( ) );
	}
	
	/**
	 * Sets the entity currently perched on the left shoulder, or null to
	 * remove. This method will remove the entity from the world.
	 * <br>
	 * Note that only a copy of the entity will be set to display on the
	 * shoulder.
	 * <br>
	 * Also note that the client will currently only render {@link Parrot}
	 * entities.
	 *
	 * @param entity left shoulder entity
	 * @deprecated There are currently no well defined semantics regarding
	 * serialized entities in Bukkit. Use with care.
	 */
	public void setShoulderEntityLeft ( Entity entity ) {
		execute ( handle -> handle.setShoulderEntityLeft ( entity ) );
	}
	
	/**
	 * Gets the entity currently perched on the right shoulder or null if no
	 * entity.
	 * <br>
	 * The returned entity will not be spawned within the world, so most
	 * operations are invalid unless the entity is first spawned in.
	 *
	 * @return right shoulder entity
	 * @deprecated There are currently no well defined semantics regarding
	 * serialized entities in Bukkit. Use with care.
	 */
	public Entity getShoulderEntityRight ( ) {
		return extract ( handle -> handle.getShoulderEntityRight ( ) );
	}
	
	/**
	 * Sets the entity currently perched on the right shoulder, or null to
	 * remove. This method will remove the entity from the world.
	 * <br>
	 * Note that only a copy of the entity will be set to display on the
	 * shoulder.
	 * <br>
	 * Also note that the client will currently only render {@link Parrot}
	 * entities.
	 *
	 * @param entity right shoulder entity
	 * @deprecated There are currently no well defined semantics regarding
	 * serialized entities in Bukkit. Use with care.
	 */
	public void setShoulderEntityRight ( Entity entity ) {
		execute ( handle -> handle.setShoulderEntityRight ( entity ) );
	}
	
	/**
	 * Make the entity drop the item in their hand.
	 * <br>
	 * This will force the entity to drop the item they are holding with
	 * an option to drop the entire {@link ItemStack} or just 1 of the items.
	 *
	 * @param dropAll True to drop entire stack, false to drop 1 of the stack
	 * @return True if item was dropped successfully
	 */
	public boolean dropItem ( boolean dropAll ) {
		return invokeSafe ( "dropItem" , dropAll );
	}
	
	/**
	 * Gets the players current exhaustion level.
	 * <p>
	 * Exhaustion controls how fast the food level drops. While you have a
	 * certain amount of exhaustion, your saturation will drop to zero, and
	 * then your food will drop to zero.
	 *
	 * @return Exhaustion level
	 */
	public float getExhaustion ( ) {
		return extract ( handle -> handle.getExhaustion ( ) );
	}
	
	/**
	 * Sets the players current exhaustion level
	 *
	 * @param value Exhaustion level
	 */
	public void setExhaustion ( float value ) {
		execute ( handle -> handle.setExhaustion ( value ) );
	}
	
	/**
	 * Gets the players current saturation level.
	 * <p>
	 * Saturation is a buffer for food level. Your food level will not drop if
	 * you are saturated {@literal >} 0.
	 *
	 * @return Saturation level
	 */
	public float getSaturation ( ) {
		return extract ( handle -> handle.getSaturation ( ) );
	}
	
	/**
	 * Sets the players current saturation level
	 *
	 * @param value Saturation level
	 */
	public void setSaturation ( float value ) {
		execute ( handle -> handle.setSaturation ( value ) );
	}
	
	/**
	 * Gets the players current food level
	 *
	 * @return Food level
	 */
	public int getFoodLevel ( ) {
		return extract ( handle -> handle.getFoodLevel ( ) );
	}
	
	/**
	 * Sets the players current food level
	 *
	 * @param value New food level
	 */
	public void setFoodLevel ( int value ) {
		execute ( handle -> handle.setFoodLevel ( value ) );
	}
	
	/**
	 * Get the regeneration rate (1 health per x ticks) of
	 * the HumanEntity when they have saturation and
	 * their food level is {@literal >=} 20. Default is 10.
	 *
	 * @return the regeneration rate
	 */
	public int getSaturatedRegenRate ( ) {
		return extract ( handle -> handle.getSaturatedRegenRate ( ) );
	}
	
	/**
	 * Set the regeneration rate (1 health per x ticks) of
	 * the HumanEntity when they have saturation and
	 * their food level is {@literal >=} 20. Default is 10.
	 * Not affected if the world's difficulty is peaceful.
	 *
	 * @param ticks the amount of ticks to gain 1 health.
	 */
	public void setSaturatedRegenRate ( int ticks ) {
		execute ( handle -> handle.setSaturatedRegenRate ( ticks ) );
	}
	
	/**
	 * Get the regeneration rate (1 health per x ticks) of
	 * the HumanEntity when they have no saturation and
	 * their food level is {@literal >=} 18. Default is 80.
	 *
	 * @return the regeneration rate
	 */
	public int getUnsaturatedRegenRate ( ) {
		return extract ( handle -> handle.getUnsaturatedRegenRate ( ) );
	}
	
	/**
	 * Get the regeneration rate (1 health per x ticks) of
	 * the HumanEntity when they have no saturation and
	 * their food level is {@literal >=} 18. Default is 80.
	 * Not affected if the world's difficulty is peaceful.
	 *
	 * @param ticks the amount of ticks to gain 1 health.
	 */
	public void setUnsaturatedRegenRate ( int ticks ) {
		execute ( handle -> handle.setUnsaturatedRegenRate ( ticks ) );
	}
	
	/**
	 * Get the starvation rate (1 health per x ticks) of
	 * the HumanEntity. Default is 80.
	 *
	 * @return the starvation rate
	 */
	public int getStarvationRate ( ) {
		return extract ( handle -> handle.getStarvationRate ( ) );
	}
	
	/**
	 * Get the starvation rate (1 health per x ticks) of
	 * the HumanEntity. Default is 80.
	 *
	 * @param ticks the amount of ticks to lose 1 health
	 */
	public void setStarvationRate ( int ticks ) {
		execute ( handle -> handle.setStarvationRate ( ticks ) );
	}
	
	/**
	 * Gets the height of the living entity's eyes above its Location.
	 *
	 * @return height of the living entity's eyes above its location
	 */
	public double getEyeHeight ( ) {
		return extract ( handle -> handle.getEyeHeight ( ) );
	}
	
	/**
	 * Gets the height of the living entity's eyes above its Location.
	 *
	 * @param ignorePose if set to true, the effects of pose changes, eg
	 *     sneaking and gliding will be ignored
	 * @return height of the living entity's eyes above its location
	 */
	public double getEyeHeight ( boolean ignorePose ) {
		return extract ( handle -> handle.getEyeHeight ( ignorePose ) );
	}
	
	/**
	 * Get a Location detailing the current eye position of the living entity.
	 *
	 * @return a location at the eyes of the living entity
	 */
	public Location getEyeLocation ( ) {
		return extract ( handle -> handle.getEyeLocation ( ) );
	}
	
	/**
	 * Gets all blocks along the living entity's line of sight.
	 * <p>
	 * This list contains all blocks from the living entity's eye position to
	 * target inclusive. This method considers all blocks as 1x1x1 in size.
	 *
	 * @param transparent Set containing all transparent block Materials (set to
	 *     null for only air)
	 * @param maxDistance this is the maximum distance to scan (may be limited
	 *     by server by at least 100 blocks, no less)
	 * @return list containing all blocks along the living entity's line of
	 *     sight
	 */
	public List < Block > getLineOfSight ( Set < Material > transparent , int maxDistance ) {
		return invokeSafe ( "getLineOfSight" ,
							new Class[] { Set.class , int.class } , transparent , maxDistance );
	}
	
	/**
	 * Gets the block that the living entity has targeted.
	 * <p>
	 * This method considers all blocks as 1x1x1 in size. To take exact block
	 * collision shapes into account, see {@link #getTargetBlockExact(int ,
	 * FluidCollisionMode)}.
	 *
	 * @param transparent Set containing all transparent block Materials (set to
	 *     null for only air)
	 * @param maxDistance this is the maximum distance to scan (may be limited
	 *     by server by at least 100 blocks, no less)
	 * @return block that the living entity has targeted
	 */
	public Block getTargetBlock ( Set < Material > transparent , int maxDistance ) {
		return invokeSafe ( "getTargetBlock" ,
							new Class[] { Set.class , int.class } , transparent , maxDistance );
	}
	
	/**
	 * Gets the last two blocks along the living entity's line of sight.
	 * <p>
	 * The target block will be the last block in the list. This method
	 * considers all blocks as 1x1x1 in size.
	 *
	 * @param transparent Set containing all transparent block Materials (set to
	 *     null for only air)
	 * @param maxDistance this is the maximum distance to scan. This may be
	 *     further limited by the server, but never to less than 100 blocks
	 * @return list containing the last 2 blocks along the living entity's
	 *     line of sight
	 */
	public List < Block > getLastTwoTargetBlocks ( Set < Material > transparent , int maxDistance ) {
		return invokeSafe ( "getLastTwoTargetBlocks" ,
							new Class[] { Set.class , int.class } , transparent , maxDistance );
	}
	
	/**
	 * Gets the block that the living entity has targeted.
	 * <p>
	 * This takes the blocks' precise collision shapes into account. Fluids are
	 * ignored.
	 * <p>
	 * This may cause loading of chunks! Some implementations may impose
	 * artificial restrictions on the maximum distance.
	 *
	 * @param maxDistance the maximum distance to scan
	 * @return block that the living entity has targeted
	 * @see #getTargetBlockExact(int , FluidCollisionMode)
	 */
	public Block getTargetBlockExact ( int maxDistance ) {
		return extract ( handle -> handle.getTargetBlockExact ( maxDistance ) );
	}
	
	/**
	 * Gets the block that the living entity has targeted.
	 * <p>
	 * This takes the blocks' precise collision shapes into account.
	 * <p>
	 * This may cause loading of chunks! Some implementations may impose
	 * artificial restrictions on the maximum distance.
	 *
	 * @param maxDistance the maximum distance to scan
	 * @param fluidCollisionMode the fluid collision mode
	 * @return block that the living entity has targeted
	 * @see #rayTraceBlocks(double , FluidCollisionMode)
	 */
	public Block getTargetBlockExact ( int maxDistance , FluidCollisionMode fluidCollisionMode ) {
		return extract ( handle -> handle.getTargetBlockExact ( maxDistance , fluidCollisionMode ) );
	}
	
	/**
	 * Performs a ray trace that provides information on the targeted block.
	 * <p>
	 * This takes the blocks' precise collision shapes into account. Fluids are
	 * ignored.
	 * <p>
	 * This may cause loading of chunks! Some implementations may impose
	 * artificial restrictions on the maximum distance.
	 *
	 * @param maxDistance the maximum distance to scan
	 * @return information on the targeted block, or <code>null</code> if there
	 *     is no targeted block in range
	 * @see #rayTraceBlocks(double , FluidCollisionMode)
	 */
	public org.bukkit.util.RayTraceResult rayTraceBlocks ( double maxDistance ) {
		return extract ( handle -> handle.rayTraceBlocks ( maxDistance ) );
	}
	
	/**
	 * Performs a ray trace that provides information on the targeted block.
	 * <p>
	 * This takes the blocks' precise collision shapes into account.
	 * <p>
	 * This may cause loading of chunks! Some implementations may impose
	 * artificial restrictions on the maximum distance.
	 *
	 * @param maxDistance the maximum distance to scan
	 * @param fluidCollisionMode the fluid collision mode
	 * @return information on the targeted block, or <code>null</code> if there
	 *     is no targeted block in range
	 * @see <b><code>World.rayTraceBlocks(Location , Vector , double , FluidCollisionMode)</code></b>
	 */
	public org.bukkit.util.RayTraceResult rayTraceBlocks ( double maxDistance ,
			FluidCollisionMode fluidCollisionMode ) {
		return extract ( handle -> handle.rayTraceBlocks ( maxDistance , fluidCollisionMode ) );
	}
	
	/**
	 * Returns the amount of air that the living entity has remaining, in
	 * ticks.
	 *
	 * @return amount of air remaining
	 */
	public int getRemainingAir ( ) {
		return extract ( handle -> handle.getRemainingAir ( ) );
	}
	
	/**
	 * Sets the amount of air that the living entity has remaining, in ticks.
	 *
	 * @param ticks amount of air remaining
	 */
	public void setRemainingAir ( int ticks ) {
		execute ( handle -> handle.setRemainingAir ( ticks ) );
	}
	
	/**
	 * Returns the maximum amount of air the living entity can have, in ticks.
	 *
	 * @return maximum amount of air
	 */
	public int getMaximumAir ( ) {
		return extract ( handle -> handle.getMaximumAir ( ) );
	}
	
	/**
	 * Sets the maximum amount of air the living entity can have, in ticks.
	 *
	 * @param ticks maximum amount of air
	 */
	public void setMaximumAir ( int ticks ) {
		execute ( handle -> handle.setMaximumAir ( ticks ) );
	}
	
	/**
	 * Gets the time in ticks until the next arrow leaves the entity's body.
	 *
	 * @return ticks until arrow leaves
	 */
	public int getArrowCooldown ( ) {
		return extract ( handle -> handle.getArrowCooldown ( ) );
	}
	
	/**
	 * Sets the time in ticks until the next arrow leaves the entity's body.
	 *
	 * @param ticks time until arrow leaves
	 */
	public void setArrowCooldown ( int ticks ) {
		execute ( handle -> handle.setArrowCooldown ( ticks ) );
	}
	
	/**
	 * Gets the amount of arrows in an entity's body.
	 *
	 * @return amount of arrows in body
	 */
	public int getArrowsInBody ( ) {
		return extract ( handle -> handle.getArrowsInBody ( ) );
	}
	
	/**
	 * Set the amount of arrows in the entity's body.
	 *
	 * @param count amount of arrows in entity's body
	 */
	public void setArrowsInBody ( int count ) {
		execute ( handle -> handle.setArrowsInBody ( count ) );
	}
	
	/**
	 * Returns the living entity's current maximum no damage ticks.
	 * <p>
	 * This is the maximum duration in which the living entity will not take
	 * damage.
	 *
	 * @return maximum no damage ticks
	 */
	public int getMaximumNoDamageTicks ( ) {
		return extract ( handle -> handle.getMaximumNoDamageTicks ( ) );
	}
	
	/**
	 * Sets the living entity's current maximum no damage ticks.
	 *
	 * @param ticks maximum amount of no damage ticks
	 */
	public void setMaximumNoDamageTicks ( int ticks ) {
		execute ( handle -> handle.setMaximumNoDamageTicks ( ticks ) );
	}
	
	/**
	 * Returns the living entity's last damage taken in the current no damage
	 * ticks time.
	 * <p>
	 * Only damage higher than this amount will further damage the living
	 * entity.
	 *
	 * @return damage taken since the last no damage ticks time period
	 */
	public double getLastDamage ( ) {
		return extract ( handle -> handle.getLastDamage ( ) );
	}
	
	/**
	 * Sets the damage dealt within the current no damage ticks time period.
	 *
	 * @param damage amount of damage
	 */
	public void setLastDamage ( double damage ) {
		execute ( handle -> handle.setLastDamage ( damage ) );
	}
	
	/**
	 * Returns the living entity's current no damage ticks.
	 *
	 * @return amount of no damage ticks
	 */
	public int getNoDamageTicks ( ) {
		return extract ( handle -> handle.getNoDamageTicks ( ) );
	}
	
	/**
	 * Sets the living entity's current no damage ticks.
	 *
	 * @param ticks amount of no damage ticks
	 */
	public void setNoDamageTicks ( int ticks ) {
		execute ( handle -> handle.setNoDamageTicks ( ticks ) );
	}
	
	/**
	 * Gets the player identified as the killer of the living entity.
	 * <p>
	 * May be null.
	 *
	 * @return killer player, or null if none found
	 */
	public Player getKiller ( ) {
		return extract ( handle -> handle.getKiller ( ) );
	}
	
	/**
	 * Adds the given {@link PotionEffect} to the living entity.
	 *
	 * @param effect PotionEffect to be added
	 * @return whether the effect could be added
	 */
	public boolean addPotionEffect ( PotionEffect effect ) {
		return invokeSafe ( "addPotionEffect" , new Class[] { PotionEffect.class } , effect );
	}
	
	/**
	 * Adds the given {@link PotionEffect} to the living entity.
	 * <p>
	 * Only one potion effect can be present for a given {@link
	 * PotionEffectType}.
	 *
	 * @param effect PotionEffect to be added
	 * @param force whether conflicting effects should be removed
	 * @return whether the effect could be added
	 * @deprecated no need to force since multiple effects of the same type are
	 * now supported.
	 */
	public boolean addPotionEffect ( PotionEffect effect , boolean force ) {
		return invokeSafe ( "addPotionEffect" ,
							new Class[] { PotionEffect.class , boolean.class } , effect , force );
	}
	
	/**
	 * Attempts to add all of the given {@link PotionEffect} to the living
	 * entity.
	 *
	 * @param effects the effects to add
	 * @return whether all of the effects could be added
	 */
	public boolean addPotionEffects ( Collection < PotionEffect > effects ) {
		return invokeSafe ( "addPotionEffects" , new Class[] { Collection.class } , effects );
	}
	
	/**
	 * Returns whether the living entity already has an existing effect of
	 * the given {@link PotionEffectType} applied to it.
	 *
	 * @param type the potion type to check
	 * @return whether the living entity has this potion effect active on them
	 */
	public boolean hasPotionEffect ( PotionEffectType type ) {
		return extract ( handle -> handle.hasPotionEffect ( type ) );
	}
	
	/**
	 * Returns the active {@link PotionEffect} of the specified type.
	 * <p>
	 * If the effect is not present on the entity then null will be returned.
	 *
	 * @param type the potion type to check
	 * @return the effect active on this entity, or null if not active.
	 */
	public PotionEffect getPotionEffect ( PotionEffectType type ) {
		return extract ( handle -> handle.getPotionEffect ( type ) );
	}
	
	/**
	 * Removes any effects present of the given {@link PotionEffectType}.
	 *
	 * @param type the potion type to remove
	 */
	public void removePotionEffect ( PotionEffectType type ) {
		execute ( handle -> handle.removePotionEffect ( type ) );
	}
	
	/**
	 * Returns all currently active {@link PotionEffect}s on the living
	 * entity.
	 *
	 * @return a collection of {@link PotionEffect}s
	 */
	public Collection < PotionEffect > getActivePotionEffects ( ) {
		return extract ( handle -> handle.getActivePotionEffects ( ) );
	}
	
	/**
	 * Checks whether the living entity has block line of sight to another.
	 * <p>
	 * This uses the same algorithm that hostile mobs use to find the closest
	 * player.
	 *
	 * @param other the entity to determine line of sight to
	 * @return true if there is a line of sight, false if not
	 */
	public boolean hasLineOfSight ( Entity other ) {
		return extract ( handle -> handle.hasLineOfSight ( other ) );
	}
	
	/**
	 * Returns if the living entity despawns when away from players or not.
	 * <p>
	 * By default, animals are not removed while other mobs are.
	 *
	 * @return true if the living entity is removed when away from players
	 */
	public boolean getRemoveWhenFarAway ( ) {
		return extract ( handle -> handle.getRemoveWhenFarAway ( ) );
	}
	
	/**
	 * Sets whether or not the living entity despawns when away from players
	 * or not.
	 *
	 * @param remove the removal status
	 */
	public void setRemoveWhenFarAway ( boolean remove ) {
		execute ( handle -> handle.setRemoveWhenFarAway ( remove ) );
	}
	
	/**
	 * Gets the inventory with the equipment worn by the living entity.
	 *
	 * @return the living entity's inventory
	 */
	public EntityEquipment getEquipment ( ) {
		return extract ( handle -> handle.getEquipment ( ) );
	}
	
	/**
	 * Sets whether or not the living entity can pick up items.
	 *
	 * @param pickup whether or not the living entity can pick up items
	 */
	public void setCanPickupItems ( boolean pickup ) {
		execute ( handle -> handle.setCanPickupItems ( pickup ) );
	}
	
	/**
	 * Gets if the living entity can pick up items.
	 *
	 * @return whether or not the living entity can pick up items
	 */
	public boolean getCanPickupItems ( ) {
		return extract ( handle -> handle.getCanPickupItems ( ) );
	}
	
	/**
	 * Returns whether the entity is currently leashed.
	 *
	 * @return whether the entity is leashed
	 */
	public boolean isLeashed ( ) {
		return extract ( handle -> handle.isLeashed ( ) );
	}
	
	/**
	 * Gets the entity that is currently leading this entity.
	 *
	 * @return the entity holding the leash
	 * @throws IllegalStateException if not currently leashed
	 */
	public Entity getLeashHolder ( ) throws IllegalStateException {
		return extract ( handle -> handle.getLeashHolder ( ) );
	}
	
	/**
	 * Sets the leash on this entity to be held by the supplied entity.
	 * <p>
	 * This method has no effect on EnderDragons, Withers, Players, or Bats.
	 * Non-living entities excluding leashes will not persist as leash
	 * holders.
	 *
	 * @param holder the entity to leash this entity to, or null to unleash
	 * @return whether the operation was successful
	 */
	public boolean setLeashHolder ( Entity holder ) {
		return invokeSafe ( "setLeashHolder" , new Class[] { Entity.class } , holder );
	}
	
	/**
	 * Checks to see if an entity is gliding, such as using an Elytra.
	 * @return True if this entity is gliding.
	 */
	public boolean isGliding ( ) {
		return extract ( handle -> handle.isGliding ( ) );
	}
	
	/**
	 * Makes entity start or stop gliding. This will work even if an Elytra
	 * is not equipped, but will be reverted by the server immediately after
	 * unless an event-cancelling mechanism is put in place.
	 * @param gliding True if the entity is gliding.
	 */
	public void setGliding ( boolean gliding ) {
		execute ( handle -> handle.setGliding ( gliding ) );
	}
	
	/**
	 * Checks to see if an entity is swimming.
	 *
	 * @return True if this entity is swimming.
	 */
	public boolean isSwimming ( ) {
		return extract ( handle -> handle.isSwimming ( ) );
	}
	
	/**
	 * Makes entity start or stop swimming.
	 *
	 * This may have unexpected results if the entity is not in water.
	 *
	 * @param swimming True if the entity is swimming.
	 */
	public void setSwimming ( boolean swimming ) {
		execute ( handle -> handle.setSwimming ( swimming ) );
	}
	
	/**
	 * Checks to see if an entity is currently using the Riptide enchantment.
	 *
	 * @return True if this entity is currently riptiding.
	 */
	public boolean isRiptiding ( ) {
		return extract ( handle -> handle.isRiptiding ( ) );
	}
	
	/**
	 * Returns whether this entity is slumbering.
	 *
	 * @return slumber state
	 */
	public boolean isSleeping ( ) {
		return extract ( handle -> handle.isSleeping ( ) );
	}
	
	/**
	 * Gets if the entity is climbing.
	 *
	 * @return if the entity is climbing
	 */
	public boolean isClimbing ( ) {
		return extract ( handle -> handle.isClimbing ( ) );
	}
	
	/**
	 * Sets whether an entity will have AI.
	 *
	 * The entity will be completely unable to move if it has no AI.
	 *
	 * @param ai whether the mob will have AI or not.
	 */
	public void setAI ( boolean ai ) {
		try {
			invoke ( "setAI" , new Class[] { boolean.class } , ai );
		} catch ( NoSuchMethodException e ) {
			handleOptional ( ).ifPresent ( handle -> EntityReflection.setAI ( handle , ai ) );
		}
	}
	
	/**
	 * Checks whether an entity has AI.
	 *
	 * The entity will be completely unable to move if it has no AI.
	 *
	 * @return true if the entity has AI, otherwise false.
	 */
	public boolean hasAI ( ) {
		// TODO: backwards compatibility required
		return extract ( handle -> handle.hasAI ( ) );
	}
	
	/**
	 * Makes this entity attack the given entity with a melee attack.
	 *
	 * Attack damage is calculated by the server from the attributes and
	 * equipment of this mob, and knockback is applied to {@code target} as
	 * appropriate.
	 *
	 * @param target entity to attack.
	 */
	public void attack ( Entity target ) {
		execute ( handle -> handle.attack ( target ) );
	}
	
	/**
	 * Makes this entity swing their main hand.
	 *
	 * This method does nothing if this entity does not have an animation for
	 * swinging their main hand.
	 */
	public void swingMainHand ( ) {
		execute ( handle -> handle.swingMainHand ( ) );
	}
	
	/**
	 * Makes this entity swing their off hand.
	 *
	 * This method does nothing if this entity does not have an animation for
	 * swinging their off hand.
	 */
	public void swingOffHand ( ) {
		execute ( handle -> handle.swingOffHand ( ) );
	}
	
	/**
	 * Set if this entity will be subject to collisions with other entities.
	 * <p>
	 * Exemptions to this rule can be managed with
	 * {@link #getCollidableExemptions()}
	 *
	 * @param collidable collision status
	 */
	public void setCollidable ( boolean collidable ) {
		execute ( handle -> handle.setCollidable ( collidable ) );
	}
	
	/**
	 * Gets if this entity is subject to collisions with other entities.
	 * <p>
	 * Some entities might be exempted from the collidable rule of this entity.
	 * Use {@link #getCollidableExemptions()} to get these.
	 * <p>
	 * Please note that this method returns only the custom collidable state,
	 * not whether the entity is non-collidable for other reasons such as being
	 * dead.
	 *
	 * @return collision status
	 */
	public boolean isCollidable ( ) {
		return extract ( handle -> handle.isCollidable ( ) );
	}
	
	/**
	 * Gets a mutable set of UUIDs of the entities which are exempt from the
	 * entity's collidable rule and which's collision with this entity will
	 * behave the opposite of it.
	 * <p>
	 * This set can be modified to add or remove exemptions.
	 * <p>
	 * For example if collidable is true and an entity is in the exemptions set
	 * then it will not collide with it. Similarly if collidable is false and an
	 * entity is in this set then it will still collide with it.
	 * <p>
	 * Note these exemptions are not (currently) persistent.
	 *
	 * @return the collidable exemption set
	 */
	public Set < UUID > getCollidableExemptions ( ) {
		return extract ( handle -> handle.getCollidableExemptions ( ) );
	}
	
	/**
	 * Returns the value of the memory specified.
	 * <p>
	 * Note that the value is null when the specific entity does not have that
	 * value by default.
	 *
	 * @param memoryKey memory to access
	 * @return a instance of the memory section value or null if not present
	 */
	public < T > T getMemory ( MemoryKey < T > memoryKey ) {
		return extract ( handle -> handle.getMemory ( memoryKey ) );
	}
	
	/**
	 * Sets the value of the memory specified.
	 * <p>
	 * Note that the value will not be persisted when the specific entity does
	 * not have that value by default.
	 *  @param memoryKey the memory to access
	 * @param memoryValue a typed memory value
	 */
	public < T > void setMemory ( MemoryKey < T > memoryKey , T memoryValue ) {
		execute ( handle -> handle.setMemory ( memoryKey , memoryValue ) );
	}
	
	/**
	 * Get the category to which this entity belongs.
	 *
	 * Categories may subject this entity to additional effects, benefits or
	 * debuffs.
	 *
	 * @return the entity category
	 */
	public EntityCategory getCategory ( ) {
		return extract ( handle -> handle.getCategory ( ) );
	}
	
	/**
	 * Sets whether the entity is invisible or not.
	 *
	 * @param invisible If the entity is invisible
	 */
	public void setInvisible ( boolean invisible ) {
		execute ( handle -> handle.setInvisible ( invisible ) );
	}
	
	/**
	 * Gets whether the entity is invisible or not.
	 *
	 * @return Whether the entity is invisible
	 */
	public boolean isInvisible ( ) {
		return extract ( handle -> handle.isInvisible ( ) );
	}
	
	/**
	 * Gets the specified attribute instance from the object. This instance will
	 * be backed directly to the object and any changes will be visible at once.
	 *
	 * @param attribute the attribute to get
	 * @return the attribute instance or null if not applicable to this object
	 */
	public AttributeInstance getAttribute ( Attribute attribute ) {
		return extract ( handle -> handle.getAttribute ( attribute ) );
	}
	
	/**
	 * Deals the given amount of damage to this entity.
	 *
	 * @param amount Amount of damage to deal
	 */
	public void damage ( double amount ) {
		execute ( handle -> handle.damage ( amount ) );
	}
	
	/**
	 * Deals the given amount of damage to this entity, from a specified
	 * entity.
	 *  @param amount Amount of damage to deal
	 * @param source Entity which to attribute this damage from
	 */
	public void damage ( double amount , Entity source ) {
		execute ( handle -> handle.damage ( amount , source ) );
	}
	
	/**
	 * Gets the entity's health from 0 to {@link #getMaxHealth()}, where 0 is dead.
	 *
	 * @return Health represented from 0 to max
	 */
	public double getHealth ( ) {
		return extract ( handle -> handle.getHealth ( ) );
	}
	
	/**
	 * Sets the entity's health from 0 to {@link #getMaxHealth()}, where 0 is
	 * dead.
	 *
	 * @param health New health represented from 0 to max
	 * @throws IllegalArgumentException Thrown if the health is {@literal < 0 or >}
	 *     {@link #getMaxHealth()}
	 */
	public void setHealth ( double health ) {
		execute ( handle -> handle.setHealth ( health ) );
	}
	
	/**
	 * Gets the entity's absorption amount.
	 *
	 * @return absorption amount from 0
	 */
	public double getAbsorptionAmount ( ) {
		return extract ( handle -> handle.getAbsorptionAmount ( ) );
	}
	
	/**
	 * Sets the entity's absorption amount.
	 *
	 * @param amount new absorption amount from 0
	 * @throws IllegalArgumentException thrown if health is {@literal < 0} or
	 * non-finite.
	 */
	public void setAbsorptionAmount ( double amount ) {
		execute ( handle -> handle.setAbsorptionAmount ( amount ) );
	}
	
	/**
	 * Gets the maximum health this entity has.
	 *
	 * @return Maximum health
	 * @deprecated use {@link Attribute#GENERIC_MAX_HEALTH}.
	 */
	public double getMaxHealth ( ) {
		return extract ( handle -> handle.getMaxHealth ( ) );
	}
	
	/**
	 * Sets the maximum health this entity can have.
	 * <p>
	 * If the health of the entity is above the value provided it will be set
	 * to that value.
	 * <p>
	 * Note: An entity with a health bar ({@link Player}, {@link EnderDragon},
	 * {@link Wither}, etc...} will have their bar scaled accordingly.
	 *
	 * @param health amount of health to set the maximum to
	 * @deprecated use {@link Attribute#GENERIC_MAX_HEALTH}.
	 */
	public void setMaxHealth ( double health ) {
		execute ( handle -> handle.setMaxHealth ( health ) );
	}
	
	/**
	 * Resets the max health to the original amount.
	 * @deprecated use {@link Attribute#GENERIC_MAX_HEALTH}.
	 */
	public void resetMaxHealth ( ) {
		execute ( handle -> handle.resetMaxHealth ( ) );
	}
	
	/**
	 * Gets the custom name on a mob or block. If there is no name this method
	 * will return null.
	 * <p>
	 * This value has no effect on players, they will always use their real
	 * name.
	 *
	 * @return name of the mob/block or null
	 */
	public String getCustomName ( ) {
		return extract ( handle -> handle.getCustomName ( ) );
	}
	
	/**
	 * Sets a custom name on a mob or block. This name will be used in death
	 * messages and can be sent to the client as a nameplate over the mob.
	 * <p>
	 * Setting the name to null or an empty string will clear it.
	 * <p>
	 * This value has no effect on players, they will always use their real
	 * name.
	 *
	 * @param name the name to set
	 */
	public void setCustomName ( String name ) {
		execute ( handle -> handle.setCustomName ( name ) );
	}
	
	/**
	 * Sets a metadata value in the implementing object's metadata store.
	 *
	 * @param metadataKey A unique key to identify this metadata.
	 * @param newMetadataValue The metadata value to apply.
	 * @throws IllegalArgumentException If value is null, or the owning plugin
	 *     is null
	 */
	public void setMetadata ( String metadataKey , MetadataValue newMetadataValue ) {
		execute ( handle -> handle.setMetadata ( metadataKey , newMetadataValue ) );
	}
	
	/**
	 * Returns a list of previously set metadata values from the implementing
	 * object's metadata store.
	 *
	 * @param metadataKey the unique metadata key being sought.
	 * @return A list of values, one for each plugin that has set the
	 *     requested value.
	 */
	public List < MetadataValue > getMetadata ( String metadataKey ) {
		return extract ( handle -> handle.getMetadata ( metadataKey ) );
	}
	
	/**
	 * Tests to see whether the implementing object contains the given
	 * metadata value in its metadata store.
	 *
	 * @param metadataKey the unique metadata key being queried.
	 * @return the existence of the metadataKey within subject.
	 */
	public boolean hasMetadata ( String metadataKey ) {
		return extract ( handle -> handle.hasMetadata ( metadataKey ) );
	}
	
	/**
	 * Removes the given metadata value from the implementing object's
	 * metadata store.
	 *
	 * @param metadataKey the unique metadata key identifying the metadata to
	 *     remove.
	 * @param owningPlugin This plugin's metadata value will be removed. All
	 *     other values will be left untouched.
	 * @throws IllegalArgumentException If plugin is null
	 */
	public void removeMetadata ( String metadataKey , Plugin owningPlugin ) {
		execute ( handle -> handle.removeMetadata ( metadataKey , owningPlugin ) );
	}
	
	/**
	 * Checks if this object contains an override for the specified
	 * permission, by fully qualified name
	 *
	 * @param name Name of the permission
	 * @return true if the permission is set, otherwise false
	 */
	public boolean isPermissionSet ( String name ) {
		return extract ( handle -> handle.isPermissionSet ( name ) );
	}
	
	/**
	 * Checks if this object contains an override for the specified {@link
	 * Permission}
	 *
	 * @param perm Permission to check
	 * @return true if the permission is set, otherwise false
	 */
	public boolean isPermissionSet ( Permission perm ) {
		return extract ( handle -> handle.isPermissionSet ( perm ) );
	}
	
	/**
	 * Gets the value of the specified permission, if set.
	 * <p>
	 * If a permission override is not set on this object, the default value
	 * of the permission will be returned.
	 *
	 * @param name Name of the permission
	 * @return Value of the permission
	 */
	public boolean hasPermission ( String name ) {
		return extract ( handle -> handle.hasPermission ( name ) );
	}
	
	/**
	 * Gets the value of the specified permission, if set.
	 * <p>
	 * If a permission override is not set on this object, the default value
	 * of the permission will be returned
	 *
	 * @param perm Permission to get
	 * @return Value of the permission
	 */
	public boolean hasPermission ( Permission perm ) {
		return extract ( handle -> handle.hasPermission ( perm ) );
	}
	
	/**
	 * Adds a new {@link PermissionAttachment} with a single permission by
	 * name and value
	 *
	 * @param plugin Plugin responsible for this attachment, may not be null
	 *     or disabled
	 * @param name Name of the permission to attach
	 * @param value Value of the permission
	 * @return The PermissionAttachment that was just created
	 */
	public PermissionAttachment addAttachment ( Plugin plugin , String name , boolean value ) {
		return extract ( handle -> handle.addAttachment ( plugin , name , value ) );
	}
	
	/**
	 * Adds a new empty {@link PermissionAttachment} to this object
	 *
	 * @param plugin Plugin responsible for this attachment, may not be null
	 *     or disabled
	 * @return The PermissionAttachment that was just created
	 */
	public PermissionAttachment addAttachment ( Plugin plugin ) {
		return extract ( handle -> handle.addAttachment ( plugin ) );
	}
	
	/**
	 * Temporarily adds a new {@link PermissionAttachment} with a single
	 * permission by name and value
	 *
	 * @param plugin Plugin responsible for this attachment, may not be null
	 *     or disabled
	 * @param name Name of the permission to attach
	 * @param value Value of the permission
	 * @param ticks Amount of ticks to automatically remove this attachment
	 *     after
	 * @return The PermissionAttachment that was just created
	 */
	public PermissionAttachment addAttachment ( Plugin plugin , String name , boolean value , int ticks ) {
		return extract ( handle -> handle.addAttachment ( plugin , name , value , ticks ) );
	}
	
	/**
	 * Temporarily adds a new empty {@link PermissionAttachment} to this
	 * object
	 *
	 * @param plugin Plugin responsible for this attachment, may not be null
	 *     or disabled
	 * @param ticks Amount of ticks to automatically remove this attachment
	 *     after
	 * @return The PermissionAttachment that was just created
	 */
	public PermissionAttachment addAttachment ( Plugin plugin , int ticks ) {
		return extract ( handle -> handle.addAttachment ( plugin , ticks ) );
	}
	
	/**
	 * Removes the given {@link PermissionAttachment} from this object
	 *
	 * @param attachment Attachment to remove
	 * @throws IllegalArgumentException Thrown when the specified attachment
	 *     isn't part of this object
	 */
	public void removeAttachment ( PermissionAttachment attachment ) {
		execute ( handle -> handle.removeAttachment ( attachment ) );
	}
	
	/**
	 * Recalculates the permissions for this object, if the attachments have
	 * changed values.
	 * <p>
	 * This should very rarely need to be called from a plugin.
	 */
	public void recalculatePermissions ( ) {
		execute ( handle -> handle.recalculatePermissions ( ) );
	}
	
	/**
	 * Gets a set containing all of the permissions currently in effect by
	 * this object
	 *
	 * @return Set of currently effective permissions
	 */
	public Set < PermissionAttachmentInfo > getEffectivePermissions ( ) {
		return extract ( handle -> handle.getEffectivePermissions ( ) );
	}
	
	/**
	 * Checks if this object is a server operator
	 *
	 * @return true if this is an operator, otherwise false
	 */
	public boolean isOp ( ) {
		return extract ( handle -> handle.isOp ( ) );
	}
	
	/**
	 * Sets the operator status of this object
	 *
	 * @param value New operator value
	 */
	public void setOp ( boolean value ) {
		execute ( handle -> handle.setOp ( value ) );
	}
	
	/**
	 * Returns a custom tag container capable of storing tags on the object.
	 *
	 * Note that the tags stored on this container are all stored under their
	 * own custom namespace therefore modifying default tags using this
	 * <b>PersistentDataHolder</b> is impossible.
	 *
	 * @return the persistent metadata container
	 */
	public PersistentDataContainer getPersistentDataContainer ( ) {
		return extract ( handle -> handle.getPersistentDataContainer ( ) );
	}
	
	/**
	 * Sends this recipient a Plugin Message on the specified outgoing
	 * channel.
	 * <p>
	 * The message may not be larger than <b><code>Messenger.MAX_MESSAGE_SIZE</code></b>
	 * bytes, and the plugin must be registered to send messages on the
	 * specified channel.
	 *
	 * @param source The plugin that sent this message.
	 * @param channel The channel to send this message on.
	 * @param message The raw message to send.
	 * @throws IllegalArgumentException Thrown if the source plugin is
	 *     disabled.
	 * @throws IllegalArgumentException Thrown if source, channel or message
	 *     is null.
	 * @throws MessageTooLargeException Thrown if the message is too big.
	 * @throws ChannelNotRegisteredException Thrown if the channel is not
	 *     registered for this plugin.
	 */
	public void sendPluginMessage ( Plugin source , String channel , byte[] message ) {
		execute ( handle -> handle.sendPluginMessage ( source , channel , message ) );
	}
	
	/**
	 * Gets a set containing all the Plugin Channels that this client is
	 * listening on.
	 *
	 * @return Set containing all the channels that this client may accept.
	 */
	public Set < String > getListeningPluginChannels ( ) {
		return extract ( handle -> handle.getListeningPluginChannels ( ) );
	}
	
	/**
	 * Launches a {@link Projectile} from the ProjectileSource.
	 *
	 * @param projectile class of the projectile to launch
	 * @return the launched projectile
	 */
	public < T extends Projectile > T launchProjectile ( Class < ? extends T > projectile ) {
		return extract ( handle -> handle.launchProjectile ( projectile ) );
	}
	
	/**
	 * Launches a {@link Projectile} from the ProjectileSource with an
	 * initial velocity.
	 *
	 * @param projectile class of the projectile to launch
	 * @param velocity the velocity with which to launch
	 * @return the launched projectile
	 */
	public < T extends Projectile > T launchProjectile ( Class < ? extends T > projectile ,
			org.bukkit.util.Vector velocity ) {
		return extract ( handle -> handle.launchProjectile ( projectile , velocity ) );
	}
	
	public org.bukkit.entity.Player.Spigot spigot ( ) {
		return extract ( handle -> handle.spigot ( ) );
	}
	
	protected < U > U extract ( Function < ? super Player, ? extends U > mapper ) {
		try {
			return handleOptional ( ).map ( mapper ).orElse ( null );
		} catch ( NoSuchMethodError ex ) {
			if ( verbose ) { // legacy versions
				throw ex;
			}
		}
		return null;
	}
	
	protected < T > T invoke ( String method_name , Class < ? >[] types , Object... args )
			throws NoSuchMethodException {
		Player handle = handleOptional ( ).orElse ( null );
		
		if ( handle != null ) {
			Method method = null;
			
			// searching method
			if ( ( method = safeGetMethod ( Player.class , method_name , types ) ) == null ) {
				// searching in implementing interfaces
				for ( Class < ? > clazz : Player.class.getInterfaces ( ) ) {
					if ( ( method = safeGetMethod ( clazz , method_name , types ) ) != null ) {
						break;
					}
					
					// searching in super-interfaces
					for ( Class < ? > other : clazz.getInterfaces ( ) ) {
						if ( ( method = safeGetMethod ( other , method_name , types ) ) != null ) {
							break;
						}
					}
				}
				
				// searching in super classes
				if ( method == null ) {
					method = safeGetMethodDeep ( Player.class.getSuperclass ( ) , method_name , types );
				}
			}
			
			if ( method != null ) {
				try {
					return ( T ) method.invoke ( handle , args );
				} catch ( IllegalAccessException | InvocationTargetException e ) {
					e.printStackTrace ( );
				}
			} else {
				if ( verbose ) {
					throw new NoSuchMethodException ( "couldn't find method '" + method_name + "'" );
				}
			}
		}
		return null;
	}
	
	protected < T > T invokeSafe ( String method_name , Class < ? >[] types , Object... args ) {
		try {
			return invoke ( method_name , types , args );
		} catch ( NoSuchMethodException ex ) {
			return null;
		}
	}
	
	protected Method safeGetMethodDeep ( Class < ? > clazz , String name , Class < ? >[] types ) {
		Method method = safeGetMethod ( clazz , name , types );
		
		if ( method == null ) {
			Class < ? > current = clazz;
			
			while ( current != null && !Object.class.equals ( current ) ) {
				if ( ( method = safeGetMethod ( current , name , types ) ) != null ) {
					break;
				}
				
				current = current.getSuperclass ( );
			}
		}
		
		return method;
	}
	
	protected Method safeGetMethod ( Class < ? > clazz , String name , Class < ? >[] types ) {
		try {
			return clazz != null ? MethodReflection.getAccessible ( clazz , name , types ) : null;
		} catch ( NoSuchMethodException e ) {
			return null;
		}
	}
	
	protected < T > T invokeSafe ( String method_name , Object... args ) {
		Class < ? >[] types = new Class < ? >[ args.length ];
		
		for ( int i = 0 ; i < args.length ; i++ ) {
			types[ i ] = args[ i ].getClass ( );
		}
		return invokeSafe ( method_name , types , args );
	}
	
	protected void execute ( Consumer < Player > modifier ) {
		execute ( ( ) -> handleOptional ( ).ifPresent ( modifier ) );
	}
	
	protected void execute ( Runnable run ) {
		try {
			run.run ( );
		} catch ( NoSuchMethodError ex ) {
			if ( verbose ) { // legacy versions
				throw ex;
			}
		}
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass ( ) != o.getClass ( ) ) {
			return false;
		}
		PlayerWrapper player = ( PlayerWrapper ) o;
		return uuid.equals ( player.uuid );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( uuid );
	}
	
	@Override
	public String toString ( ) {
		return "Player{" +
				"name='" + name + '\'' +
				", uuid=" + uuid +
				'}';
	}
}
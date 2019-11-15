package com.hotmail.AdrianSR.core.riding.movables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.util.Schedulers;
import com.hotmail.AdrianSR.core.util.UpdatableEntity;
import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;
import com.hotmail.AdrianSR.core.util.localization.DirectionUtils;
import com.hotmail.AdrianSR.core.util.localization.LocationUtils;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;

/**
 * Represents a 3D movable Object.
 * <p>
 * @author AdrianSR
 */
public class Movable {
	
	/**
	 * Global class values.
	 */
	public static final List<Movable> MOVABLES = new ArrayList<Movable>();

	/**
	 * Spawn data.
	 */
	private final Location spawn;
	private final World    world;
	
	/**
	 * Current location.
	 */
	public double x;
	public double y;
	public double z;
	public float yaw;
	public float pitch;
	
	/**
	 * The current added yaw
	 * in rotation.
	 */
	private float addedYaw = 0.0F;
	
	/**
	 * Main data.
	 */
	private UpdatableEntity     main;
	private Location       mainSpawn;
	private boolean visibleMainStand;
	
	/**
	 * Parts data.
	 */
	private final List<UpdatableEntity> parts;
	private final List<Entity> customParts;
	private final Map<UUID, Location>    spawns;
	private final Map<UUID, Boolean> visibilities;
	
	/**
	 * Class data.
	 */
	private BukkitTask auto_updater;
	private boolean destroyOnGround;
	
	/**
	 * Plugin instance.
	 */
	private final CustomPlugin plugin;
	
	/**
	 * Construct a new Movable 
	 * 3D Object.
	 * <p>
	 * @param spawn the spawn {@link Location}.
	 */
	public Movable(final Location spawn, final boolean autoupdate, final boolean auto_model, final CustomPlugin plugin) {
		this(spawn, null, autoupdate, auto_model, plugin);
	}

	/**
	 * Construct a new Movable 
	 * 3D Object.
	 * <p>
	 * @param spawn the spawn {@link Location}.
	 * @param parts the parts {@link Entity} list.
	 */
	public Movable(final Location spawn, final List<Entity> parts, final boolean autoupdate, final boolean auto_model,
			final CustomPlugin plugin) {
		// get plugin.
		this.plugin = plugin;
		
		// save spawn location.
		this.spawn      = spawn;
		this.visibilities = new HashMap<UUID, Boolean>();

		// save world.
		this.world = spawn.getWorld();

		// make parts list.
		this.parts = new ArrayList<UpdatableEntity>();

		// load parts.
		this.spawns = new HashMap<UUID, Location>();
		this.customParts = (parts == null ? new ArrayList<Entity>() : parts);

		// set spawn as current location.
		this.x   = spawn.getX();
		this.y   = spawn.getY();
		this.z   = spawn.getZ();
		this.yaw = spawn.getYaw();
		
		// spawn main stand.
		this.main = new UpdatableEntity(world.spawn(spawn, ArmorStand.class));
		this.mainSpawn = main.get().getLocation();

		// model.
		if (parts == null && auto_model) {
			model();
		}
		
		// start auto update task.
		if (autoupdate) {
			auto_updater = Schedulers.syncRepeating(() -> {
				// check main stand.
				if (this.getMainEntity() == null || this.getMainEntity().get() == null 
						|| this.getMainEntity().get().isDead()) {
					destroy();
					return;
				}
				
				// get main stand.
//				final ArmorStand stand = getMainEntity().get();
				
				// destroy if is on ground.
				if (isOnGround()) {
					if (this.destroyOnGround) {
						destroy();
						return;
					}
				}
				
				// move.
				setLocation(getLocation());
			}, 0, plugin);
		}

		
		// register this.
		MOVABLES.add(this);
	}

	/**
	 * Make 3D model of this {@link Movable}.
	 */
	protected void model() {
		// make main stand.
		main = new UpdatableEntity(world.spawn(spawn, ArmorStand.class));

		// save spawn location.
		mainSpawn = spawn;

		// modify armor stand.
		modify(main.get());

		// spawn parts.
		for (int x = 0; x < 1; x++) {
			// get location to spawn.
			final Location to = spawn.clone().add(0.0D, 0.5D, 0.0D);

			// make part.
			final UpdatableEntity part = new UpdatableEntity(world.spawn(to, ArmorStand.class));

			// save spawn.
			spawns.put(part.getUniqueId(), to);

			// modify.
			modify(part.get());

			// register part.
			parts.add(part);
		}
	}

	/**
	 * Add a new AmorStand as
	 * part.
	 * <p>
	 * @param spawn the spawn {@link Location}.
	 * @param visible will be visible?
	 * @param gravity will have gravity?
	 * @return The added {@link UpdatableEntity} part.
	 */
	public UpdatableEntity addPart(final Location spawn, final boolean visible, boolean gravity) {
		return addPart(ArmorStand.class, spawn, visible, gravity);
	}

	/**
	 * Add a new part with a custom entity 
	 * ('Entity Class Name' + .class) class.
	 * <p>
	 * @param clazz the Class. (Example: 'ArmorStand.class')
	 * @param spawn the spawn {@link Location}.
	 * @param visible will be visible?
	 * @param gravity will have gravity?
	 * @return the added {@link UpdatableEntity} part.
	 */
	public <T> UpdatableEntity addPart(final Class<T> clazz, final Location spawn, final boolean visible, boolean gravity) {
		// get entity type of 'clazz'
		EntityType entityType = null;
		for (EntityType type : EntityType.values()) {
			// check type EntityClass.
			if (type.getEntityClass() == null) {
				continue;
			}

			// check is equals.
			if (type.getEntityClass().isAssignableFrom(clazz)) {
				entityType = type; // use this EntityType.
			}
		}

		// check entity type.
		if (entityType == null) {
			return null;
		}
		
		// spawn entity and change gravity.
		final Entity ent = world.spawnEntity(spawn, entityType);
		if (!gravity && !(ent instanceof ArmorStand)) {
			// when version is > 1.9
			if (ServerVersion.serverNewerThan(ServerVersion.v1_9_R2)) {
				// remove gravity.
				ent.setGravity(false);
			} else {
				// check is a living entity.
				if (ent instanceof LivingEntity) {
					// set no AI to remove gravity.
					ReflectionUtils.setAI((LivingEntity) ent, false);
				}
			}
		}
		
		// add part.
		return addPart(ent, visible);
	}
	
	/**
	 * Add an {@link Entity} as
	 * part.
	 * <p>
	 * @param visible will be visible?
	 * @return The added {@link UpdatableEntity} part.
	 */
	public UpdatableEntity addPart(final Entity entity, final boolean visible) {
		// make.
		final UpdatableEntity part = new UpdatableEntity(entity);

		// save spawn.
		spawns.put(entity.getUniqueId(), entity.getLocation());
		
		// save visibility.
		visibilities.put(entity.getUniqueId(), Boolean.valueOf(visible));

		// modify.
		modify(entity);

		// add.
		parts.add(part);
		return part;
	}
	
	/**
	 * Remove a part from this 
	 * {@link Movable}. parts.
	 * <p>
	 * @param partEntity the {@link Entity} 
	 * part to remove.
	 */
	public void removePart(final Entity partEntity) {
		removePart(new UpdatableEntity(partEntity));
	}
	
	/**
	 * Remove a part from this 
	 * {@link Movable}. parts.
	 * <p>
	 * @param part the part to remove.
	 */
	public void removePart(final UpdatableEntity part) {
		parts.remove(part);
	}

	/**
	 * Start movement.
	 */
	public void startMovement() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				setVelocity(getLocation().getDirection());
			}
		}, 2, 0);
	}
	
	/**
	 * Start rotation.
	 */
	public void startRotation() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				// get to.
				Location to = getLocation();
				
				// set location.
				setLocation(LocationUtils.setYaw(to, (to.getYaw() + 10.0F)));
			}
		}, 2, 0);
	}

	/**
	 * Sets this Movable velocity
	 * <p>
	 * @param velocity - New velocity 
	 * to travel with.
	 */
	public void setVelocity(final Vector velocity) {
		// get to.
		final Location locTo = getLocation().clone().add(velocity);
		
		// set location.
		setLocation(locTo);
	}
	
	/**
	 * Set location (teleport).
	 * <p>
	 * @param to - destination.
	 */
	public void setLocation(final Location to) {
		// get targets list to move. and add main part.
		final List<UpdatableEntity> targets = new ArrayList<UpdatableEntity>(Arrays.asList(main));

		// add other targets.
		if (!customParts.isEmpty()) {
			for (Entity e : customParts) {
				if (e != null) {
					targets.add(new UpdatableEntity(e));
				}
			}
		} else {
			for (UpdatableEntity part : parts) {
				targets.add(part);
			}
		}

		// move.
		for (UpdatableEntity target : targets) {
			// check is not null.
			if (target == null) {
				continue;
			}

			// get and check target ArmorStand.
			Entity entity = target.get();
			if (entity == null) {
				continue;
			}

			// check modified.
			modify(entity);

			// check is the main stand that is moving.
			final boolean isMain = main.getUniqueId().equals(target.getUniqueId());
			
			// get visibility.
			final boolean visible = isMain ? visibleMainStand : visibilities.get(target.getUniqueId());

			// set no visible.
			if (entity instanceof ArmorStand) {
				 // set visible/invisible with his overrided method.
				((ArmorStand)entity).setVisible(!visible);
			} else {
				// set visible/invisible with the NMS Entity method.
				ReflectionUtils.setVisible(entity, !visible);
			}

			// move depending of stand.
			if (isMain) {
				// set location.
				ReflectionUtils.setLocation(entity, to.getX(), to.getY(), to.getZ(), to.getYaw(), 0.0F);
			} else {
				// get part spawn.
				Location partSpawn = spawns.get(target.getUniqueId());

				// set no visible.
				if (entity instanceof ArmorStand) {
					// set visible/invisible with his overrided method.
					((ArmorStand)entity).setVisible(!visible);
				} else {
					// set visible/invisible with the NMS Entity method.
					ReflectionUtils.setVisible(entity, !visible);
				}

				// get original location diference between main and this.
				double difX    = (partSpawn.getX()     - mainSpawn.getX());
				double difY    = (partSpawn.getY()     - mainSpawn.getY());
				double difZ    = (partSpawn.getZ()     - mainSpawn.getZ());
				float difYaw   = (partSpawn.getYaw()   - mainSpawn.getYaw());
//				float difPitch = (partSpawn.getPitch() - mainSpawn.getPitch());
				
				// get Y.
				double Y = (y + difY);
				
				// get yaw and pitch.
				float Yaw   = (yaw + difYaw);
//				float Pitch = (pitch + difPitch);
				
				// get locations.
				Location from = getLocation().clone();
//				Location dest = new Location(world, (x + difX), Y, (z + difZ), Yaw, 0.0F);
				
				// get diference beetwen yaws.
				float lookYaw = DirectionUtils.pointLocationTo(mainSpawn, partSpawn);
				float grados  = (lookYaw < 0 ? (360.0F - Math.abs(lookYaw)) : lookYaw);
				float dife    = ((grados + addedYaw) - yaw);
				
				// get direction.
				Vector locationDirection = DirectionUtils.rotateVector(from.getDirection(), dife, 0F);
				
				// get distance excluding axis Y.
				double distanceSquared = NumberConversions.square(difX) + NumberConversions.square(difZ);
				double distance        = Math.sqrt(distanceSquared);
				
				// get X and Z with direction.
				double X = (x + (locationDirection.getX() * distance));
				double Z = (z + (locationDirection.getZ() * distance));
				
				/* check NaNs */
				if (Double.isNaN(X)) {
					X = (x + difX);
				}
				
				if (Double.isNaN(Z)) {
					Z = (z + difZ);
				}
				
				// set location.
				ReflectionUtils.setLocation(entity, X, Y, Z, Yaw, 0.0F);
			}
			
			// set visible.
			if (entity instanceof ArmorStand) {
				 // set visible/invisible with his overrided method.
				((ArmorStand)entity).setVisible(visible);
			} else {
				// set visible/invisible with the NMS Entity method.
				ReflectionUtils.setVisible(entity, visible);
			}

			// set damagable when is invisible. (ArmorStands)
			if (entity instanceof ArmorStand) {
				ReflectionUtils.h(((ArmorStand)entity));
			}

			// update location.
			if (isMain) {
				// get location.
				final Location currentLocation = ReflectionUtils.getLocation(entity);
				
				// save added yaw.
				addedYaw += (currentLocation.getYaw() - yaw);

				// set location.
				x     = currentLocation.getX();
				y     = currentLocation.getY();
				z     = currentLocation.getZ();
				yaw   = currentLocation.getYaw();
				pitch = currentLocation.getPitch();
			}
		}
	}
	
	/**
	 * Change the gravity of a Entity 
	 * and change 'noclip' field.
	 * <p>
	 * @param entity the Entity to modify.
	 */
	protected void modify(final Entity entity) {
		/* modify */
		if (entity instanceof ArmorStand) {
			((ArmorStand) entity).setGravity(false);
			((ArmorStand) entity).setBasePlate(false);
		}
		
		// set silent.
		ReflectionUtils.setSilent(entity, true);

		// change noclip.
		try {
			ReflectionUtils.NMS_ENTITY_NOCLIP.set(ReflectionUtils.NMS_ENTITY
					.cast(ReflectionUtils.CRAFT_ENTITY_HANDLE.invoke(ReflectionUtils.CRAFT_ENTITY.cast(entity))), false);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Mount a {@link Player}.
	 * <p>
	 * @param player the player to mount.
	 */
	@SuppressWarnings("deprecation")
	public void mount(final Player player) {
		if (this.main != null) { // check not null main.
			main.get().setPassenger(player); // set.
		}
	}
	
	/**
	 * Destroy.
	 */
	public void destroy() {
		// cancel auto updater.
		if (auto_updater != null) {
			auto_updater.cancel();
		}
//		
//		// eject main.
//		if (main != null && main.get() != null) {
//			if (main.get().getPassenger() != null) {
//				main.get().eject();
//			}
//		}
		
		// get targets list to move. and add main part.
		final List<UpdatableEntity> targets = new ArrayList<UpdatableEntity>(Arrays.asList(main));

		// add other targets.
		for (UpdatableEntity part : parts) {
			if (part != null) { // check not null.
				targets.add(part);
			}
		}
		
		// destroy targets.
		for (UpdatableEntity target : targets) {
			if (target.get() != null) {
				target.get().remove(); // remove target.
			}
		}
	}

	/**
	 * Get current {@link Location}.
	 * <p>
	 * @return the current Movable Location.
	 */
	public Location getLocation() {
		// return new location with current coodinates.
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	/**
	 * Check if this movable
	 * is on ground.
	 * <p>
	 * @return true if is on ground.
	 */
	public boolean isOnGround() {
		// check is valid.
		if (this.main == null || this.main.get() == null) {
			return false;
		}
		
		// on ground by 'Entity' class.
		final boolean onground = this.main.get().isOnGround();
		for (double y = this.y; y >= 0.0D; y -= 1.0D) {
			Block block = world.getBlockAt(new Location(world, x, y, z));
			if (block.getType().isSolid()) {
				// check distance.
				if (block.getLocation().distance(getLocation()) <= 2.0D) {
					return true;
				}
			}
		}
		return onground;
	}
	
	/**
	 * Set visible/invisible
	 * main stand.
	 * <p>
	 * @param visible will be visible?
	 */
	public void setVisibleMainStand(boolean visible) {
		this.visibleMainStand = visible;
	}
	
	public CustomPlugin getPlugin() {
		return plugin;
	}
	
	public boolean isDestroyOnGround() {
		return destroyOnGround;
	}
	
	public void setDestroyOnGround(boolean destroyOnGround) {
		this.destroyOnGround = destroyOnGround;
	}
	
	public List<UpdatableEntity> getParts() {
		return parts;
	}
	
	public List<Entity> getCustomParts() {
		return customParts;
	}
	
	public Map<UUID, Location> getSpawns() {
		return spawns;
	}
	
	public Map<UUID, Boolean> getVisibilities() {
		return visibilities;
	}
	
	public UpdatableEntity getMainEntity() {
		return main;
	}
	
	public Location getMainSpawn() {
		return mainSpawn;
	}
	
	public boolean isVisibleMainStand() {
		return visibleMainStand;
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public World getWorld() {
		return world;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
}

package com.hotmail.AdrianSR.core.riding.movables;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.riding.util.StandBlockFace;
import com.hotmail.AdrianSR.core.util.UpdatableEntity;
import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;
import com.hotmail.AdrianSR.core.util.localization.DirectionUtils;
import com.hotmail.AdrianSR.core.util.localization.LocationUtils;

/**
 * Represents a Parachute 
 * for players.
 * <p>
 * @author AdrianSR
 */
public class Parachute extends Movable {
	
	/**
	 * Class values.
	 */
	private final UpdatableEntity player;

	/**
	 * Construct a new Parachute.
	 * <p>
	 * @param player the {@link Player} owner.
	 */
	public Parachute(final Player player, final CustomPlugin plugin) {
		// super.
		super(DirectionUtils.RoundUpYaw(player.getLocation()), null, true, true, plugin);
		
		// save player.
		this.player = new UpdatableEntity(player);
	}

	@Override
	public void model() {
		// change main stand visibility.
		this.setVisibleMainStand(false);

		// get main stand.
//		final ArmorStand main = this.getMainEntity().get();

		// get leashs holder.
		ArmorStand leashHolder = null;

		// get facind directions.
		final BlockFace faceF      = DirectionUtils.getFacingDirection(getYaw()); // get facing direction.
		final BlockFace leftF      = DirectionUtils.getLeftFace(faceF);
		final StandBlockFace face  = StandBlockFace.fromBlockFace(faceF);
		final StandBlockFace left  = StandBlockFace.fromBlockFace(leftF);
		final StandBlockFace right = left.getOppositeFace();

		// model.
		for (int x = 0; x < 100; x++) {
			// get spawn.
			Location spawn = getSpawn().clone();

			// clear pitch.
			spawn.setPitch(0.0F);

			// get part data.
			Class<? extends Entity> clazz = ArmorStand.class;
			boolean gravity = false;
			boolean visible = false;

			// modify coords.
			switch (x) {
			case 0:
				// add at right.
				LocationUtils.add(spawn, right, 2);

				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 6);

				// set rotation.
				spawn.setYaw(spawn.getYaw() + -90);
				break;

			case 1:
				// add at left.
				LocationUtils.add(spawn, left, 2);

				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 6);

				// set rotation.
				spawn.setYaw(spawn.getYaw() + 90);
				break;

			case 2:
				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 6);

				// set rotation.
				spawn.setYaw(spawn.getYaw() + -90);
				break;

			case 3:
				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 6);

				// set rotation.
				spawn.setYaw(spawn.getYaw() + 90);
				break;

			// CENTER
			case 4:
				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 6);

				// add at right.
				LocationUtils.add(spawn, right, 2);

				// set rotation.
				spawn.setYaw(spawn.getYaw() + 90);
				break;

			case 5:
				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 6);

				// add at left.
				LocationUtils.add(spawn, left, 2);

				// set rotation.
				spawn.setYaw(spawn.getYaw() + -90);
				break;

			// LEASH HOLDER:
			case 6:
				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 2);

				// add at left.
				LocationUtils.add(spawn, left, 1.2);

				// add to back.
				LocationUtils.add(spawn, face.getOppositeFace(), 1);
				break;

			// CHICKENS:
			case 7:
				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 6.25);

				// add at right.
				LocationUtils.add(spawn, right, 1.5);

				// add to back.
				LocationUtils.add(spawn, face.getOppositeFace(), 0.2);

				// change entity data.
				clazz = Chicken.class;
				break;

			case 8:
				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 6.25);

				// add at left.
				LocationUtils.add(spawn, left, 1.4);

				// add to back.
				LocationUtils.add(spawn, face.getOppositeFace(), 0.2);

				// change entity data.
				clazz = Chicken.class;
				gravity = false;
				visible = false;
				break;

			case 9:
				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 6.0);

				// add at left.
				LocationUtils.add(spawn, left, 3.4);

				// add to back.
				LocationUtils.add(spawn, face.getOppositeFace(), 0.2);

				// change entity data.
				clazz = Chicken.class;
				break;

			case 10:
				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 6.0);

				// add at right.
				LocationUtils.add(spawn, right, 3.4);

				// add to back.
				LocationUtils.add(spawn, face.getOppositeFace(), 0.2);

				// change entity data.
				clazz = Chicken.class;
				break;

			case 11:
				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 5.75);

				// add at right.
				LocationUtils.add(spawn, right, 5.3);

				// add to back.
				LocationUtils.add(spawn, face.getOppositeFace(), 0.2);

				// change entity data.
				clazz = Chicken.class;
				break;

			case 12:
				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 5.75);

				// add at left.
				LocationUtils.add(spawn, left, 5.3);

				// add to back.
				LocationUtils.add(spawn, face.getOppositeFace(), 0.2);

				// change entity data.
				clazz = Chicken.class;
				break;

			// PARACHUTE:
			case 13:
				// add to up.
				LocationUtils.add(spawn, StandBlockFace.UP, 2);

				// add to back.
				LocationUtils.add(spawn, face.getOppositeFace(), 0.19);

				// set rotation.
				spawn.setYaw(spawn.getYaw() + 180);
				break;

			default:
				spawn = null;
				break;
			}

			// check spawn.
			if (spawn == null) {
				continue;
			}

			// add part.
			final UpdatableEntity part = addPart(clazz, spawn, visible, gravity);

			// modify part
			switch (x) {
			case 4:
			case 5: {
				// get entity
				final ArmorStand stand = part.get();

				// set helmet.
				stand.setHelmet(new ItemStack(Material.valueOf("BANNER"), 1, (short) 4));

				// rotate head.
				stand.setHeadPose(new EulerAngle(4.71, 0, 0));
				break;
			}

			case 2:
			case 3: {
				// get entity
				final ArmorStand stand = part.get();

				// set helmet.
				stand.setHelmet(new ItemStack(Material.valueOf("BANNER"), 1, (short) 4));

				// rotate head.
				stand.setHeadPose(new EulerAngle(4.66, 0, 0));
				break;
			}

			case 0:
			case 1: {
				// get entity
				final ArmorStand stand = part.get();

				// set helmet.
				stand.setHelmet(new ItemStack(Material.valueOf("BANNER"), 1, (short) 4));

				// rotate head.
				stand.setHeadPose(new EulerAngle(4.54, 0, 0));
				break;
			}

			case 6: {
				// get entity
				final ArmorStand stand = part.get();
				leashHolder = stand;
				break;
			}

			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12: {
				// get entity.
				final Chicken ch = part.get();

				// add leash.
				ch.setLeashHolder(leashHolder);
				break;
			}

			case 13: {
				// get entity
				final ArmorStand stand = part.get();

				// set small.
				stand.setSmall(true);

				// set helmet
				stand.setHelmet(new ItemStack(Material.CHEST, 1));
				break;
			}
			}
		}
	}

	/**
	 * Custom Movement.
	 */
	@Override
	public void startMovement() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), new Runnable() {
			@Override
			public void run() {
				// get player.
				final Player p = player.get();
				if (p == null || !p.isOnline()) { // check.
					return;
				}
				
				// set player look direction.
				final Location toSee = getLocation();
				
				// get yaws diference.
				float dif = ReflectionUtils.getLocation(p).getYaw() - toSee.getYaw();
				if (dif > 0) {
					dif = Math.min(dif, 10.0F);
				} else {
					dif = Math.max(dif, -10.0F);
				}
				
				// get yaw to set.
//				float yaw = toSee.getYaw() + dif;
				
				// add test rotation.
//				toSee.setYaw(yaw);
				
				// set to down.
				toSee.setPitch(30.0F);
				
				// get direction.
				final Vector direction = toSee.getDirection();
				
				// decrease velocity.
				direction.multiply(0.7F);
				
				// set location.
				setLocation(toSee.add(direction));
			}
		}, 2, 0);
	}
}

package com.hotmail.AdrianSR.core.bossbar.versions.v1_8_R1;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;
import com.hotmail.AdrianSR.core.bossbar.BarColor;
import com.hotmail.AdrianSR.core.bossbar.BarFlag;
import com.hotmail.AdrianSR.core.bossbar.BarStyle;
import com.hotmail.AdrianSR.core.bossbar.BossBar;
import com.hotmail.AdrianSR.core.util.TextUtils;
import com.hotmail.AdrianSR.core.util.UpdatableEntity;
import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;

import net.minecraft.server.v1_8_R1.DataWatcher;
import net.minecraft.server.v1_8_R1.EntityWither;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R1.WorldServer;

/**
 * Represents a Custom
 * Boss Bar in v1_8_R1
 * of Spigot version.
 * <p>
 * @author AdrianSR
 */
public class CustomBossBar implements BossBar {
	
	/**
	 * Class values.
	 */
	private final UpdatableEntity player;
	private          EntityWither wither;
	private String                  text;
	private float               progress;
	private final World            world;
	private boolean              visible;
	
	/**
	 * Construct a new Custom Boss Bar 
	 * for a specific {@link Player}.
	 * <p>
	 * @param viewer the Player viewer.
	 * @param text the Message to show.
	 * @param progress the Progress to show.
	 */
	public CustomBossBar(final Player viewer, final String text, final float progress) {
		// get data.
		this.player   = new UpdatableEntity(viewer);
		this.world    = viewer.getWorld();
		this.text     = TextUtils.getNotNull(text, "null text");
		this.progress = 150.0F;
		this.visible  = true;
		
		// show.
		show();
		
		// update progress.
		this.setProgress(progress);
	}
	
	/**
	 * Show BossPlayer for
	 * the viewer player.
	 */
	private void show() {
		// get world.
		final WorldServer world = ((CraftWorld) this.world).getHandle();
		
		// make wither.
		wither = new EntityWither(world);
		
		// get wither location.
		final Location location = getWitherLocation();
		
		// get datawatcher.
		final DataWatcher watcher = wither.getDataWatcher();
		
		// modify wither datawatcher.
		watcher.watch(0, (byte) 0x20);
		watcher.watch(2, text);     // set name, (text).
		watcher.watch(3, (byte) 0);
		watcher.watch(6, progress); // set health, (progress).
		watcher.watch(8, (byte) 0);
		watcher.watch(17, 0);
		watcher.watch(18, 0);
		watcher.watch(19, 0);
		watcher.watch(20, 881);
		
		// modify wither.
		wither.setLocation(location.getX(), location.getY(), 
				location.getZ(), 
				location.getYaw(), 
				location.getPitch()); // set location.
		wither.setInvisible(true);    // set invisible.
		wither.canPickUpLoot = false; // cannot pitckup items.
		
		// spawn, sending a spawn packet.
		final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(wither);
		try {
			// send...
			ReflectionUtils.sendPacket(getPlayer(), packet);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void setTitle(String title) {
		// check not equals title.
		if (this.getTitle().equals(title)) {
			return;
		}
		
		// set text.
		this.text = TextUtils.getNotNull(title, "null text");
		
		// check is not empty.
		if (this.text.isEmpty()) {
			this.text = " ";
		}
		
		// update.
		update();
	}
	
	@Override
	public void setProgress(double progress) {
		// check progress
		Preconditions.checkArgument((progress >= 0.0D && progress <= 1.0D), "Progress must be between 0.0 and 1.0 (%s)", progress);
		
		// check not equals progress.
		if (this.getProgress() == progress) {
			return;
		}
		
		// set progress.
		this.progress = Math.min(Math.max((float) progress, MIN_BOSS_BAR_PROGRESS), MAX_BOSS_BAR_PROGRESS) * wither.getMaxHealth();
		
		// update.
		update();
	}
	
	@Override
	public void setVisible(boolean visible) {
		// change visiblity.
		this.visible = visible;
		
		// update.
		update();
	}
	
	@Override
	public void update() {
		// get player.
		final Player p = player.get();
		
		// check is online.
		if (p != null && p.isOnline()) {
			// check is visible.
			if (wither == null) {
				if (visible) {
					show();
				} else {
					return;
				}
			} else if (!visible) {
				destroy(); // bum!!!
				return;
			}
			
			// get datawatcher.
			final DataWatcher watcher = wither.getDataWatcher();
			
			// update data.
			watcher.watch(2, this.text);     // update text.
			watcher.watch(6, this.progress); // update progress.
			
			// update name and progress packet.
			final PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(wither.getId(),
					wither.getDataWatcher(), true);
			try {
				// send metadata packet...
				ReflectionUtils.sendPacket(p, packet);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			
			// get wither location.
			final Location location = getWitherLocation();
			
			// set location.
			wither.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
					location.getPitch());
			
			// update location with packet.
			final PacketPlayOutEntityTeleport location_packet = new PacketPlayOutEntityTeleport(wither.getId(), 
					location.getBlockX() * 32, // X coordinate.
					location.getBlockY() * 32, // Y coordinate.
					location.getBlockZ() * 32, // Z coordinate.
					(byte) ((int) location.getYaw() * 256 / 360), // Yaw
					(byte) ((int) location.getPitch() * 256 / 360), false); // Pitch
			try {
				// send update location packet...
				ReflectionUtils.sendPacket(p, location_packet);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			
		} else {
			// destroy.
			destroy();
		}
	}
	
	@Override
	public void destroy() {
		// check is not null wither.
		if (wither != null) {
			// get player.
			final Player p = player.get();
			
			// send destroy packet.
			if (p != null && p.isOnline()) {
				// make packet.
				final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(wither.getId());
				try {
					// send...
					ReflectionUtils.sendPacket(p, packet);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			// remove.
			wither.getBukkitEntity().remove();
			
			// set null.
			wither = null;
		}
	}
	
	@Override
	public String getTitle() {
		return text;
	}

	@Override
	public double getProgress() {
		return progress;
	}

	@Override
	public Player getPlayer() {
		return player.get();
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public BarColor getColor() {
		return BarColor.PINK;
	}

	@Override
	public void setColor(BarColor color) {
		// nothing.
	}

	@Override
	public BarStyle getStyle() {
		return BarStyle.SOLID;
	}

	@Override
	public void setStyle(BarStyle style) {
		// nothing.
	}

	@Override
	public void removeFlag(BarFlag flag) {
		// nothing.
	}

	@Override
	public void addFlag(BarFlag flag) {
		// nothing.
	}

	@Override
	public boolean hasFlag(BarFlag flag) {
		return false;
	}
}
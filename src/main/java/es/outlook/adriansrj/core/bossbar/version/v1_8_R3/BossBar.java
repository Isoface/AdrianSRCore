package es.outlook.adriansrj.core.bossbar.version.v1_8_R3;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import es.outlook.adriansrj.core.bossbar.version.oldest.BossBarOldest;
import es.outlook.adriansrj.core.util.reflection.bukkit.BukkitReflection;
import es.outlook.adriansrj.core.util.server.Version;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

/**
 * A {@code BossBar} intended for the server version {@link Version#v1_8_R3}.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 10:46 AM
 */
public class BossBar extends BossBarOldest {
	
	/**
	 * The wither entity that holds the bar.
	 */
	protected volatile BossWither handle;

	/**
	 * Constructs the {@code BossBar}.
	 * <p>
	 * @param title the initial title for the bar.
	 * @param progress the initial progress for the bar.
	 * @param player the player viewer.
	 */
	public BossBar ( String title , double progress , Player player ) {
		super ( title , progress , player );
	}

	@Override
	protected synchronized void create ( ) {
		if ( this.handle != null ) {
			this.destroy ( );
		}
		
		// starts the updater executor.
		super.create ( );
		
		this.handle = new BossWither ( player.get ( ).getWorld ( ) );
		
		final DataWatcher data = this.handle.getDataWatcher();
		data.watch ( 0 , (byte) 0x20 );
		data.watch ( 2 , getTitle ( ) );
		data.watch ( 3  , (byte) 0 );
		data.watch ( 6 , Math.min ( Math.max ( (float) getProgress ( ) , MINIMUM_PROGRESS ) , MAXIMUM_PROGRESS ) * handle.getMaxHealth ( ) );
		data.watch ( 8 , (byte) 0 );
		data.watch ( 17 , 0 );
		data.watch ( 18 , 0 );
		data.watch ( 19 , 0 );
		data.watch ( 20 , 881 );
		
		final Location location = calculateHandleLocation();
		final double    x = location.getX();
		final double    y = location.getY();
		final double    z = location.getZ();
		final float   yaw = location.getYaw();
		final float pitch = location.getPitch();
		
		this.handle.setLocation  ( x , y , z , yaw , pitch );
		this.handle.setInvisible ( true );
		this.handle.canPickUpLoot = false;
		this.handle.removeAllEffects ( );
		
		BukkitReflection.sendPacket ( getPlayer ( ) , new PacketPlayOutSpawnEntityLiving ( this.handle ) );
	}
	
	@Override
	protected synchronized void update ( ) {
		if ( this.handle == null || ( handle != null 
				&& !Objects.equals ( getPlayer ( ).getWorld ( ).getUID ( ) , handle.world.getWorld ( ).getUID ( ) ) ) ) {
			this.create ( ); return;
		}
		
		Player    player = getPlayer ( );
		DataWatcher data = this.handle.getDataWatcher();
		data.watch ( 2 , getTitle ( ) );
		data.watch ( 6 , Math.min ( Math.max ( (float) getProgress ( ) , MINIMUM_PROGRESS ) , MAXIMUM_PROGRESS ) * handle.getMaxHealth ( ) );
		
		/* sending title/progress updater packet */
		BukkitReflection.sendPacket ( player ,
				new PacketPlayOutEntityMetadata ( this.handle.getId ( ) , data , true ) );
		
		final Location location = calculateHandleLocation();
		final int             x = location.getBlockX();
		final int             y = location.getBlockY();
		final int             z = location.getBlockZ();
		final float         yaw = location.getYaw();
		final float       pitch = location.getPitch();
		
		/* sending location updater packet */
		BukkitReflection.sendPacket ( player , new PacketPlayOutEntityTeleport ( this.handle.getId ( ) , 
					x * 32,
					y * 32,
					z * 32,
					(byte) ((int) yaw   * 256 / 360 ),
					(byte) ((int) pitch * 256 / 360 ) , false ) );
	}

	@Override
	protected synchronized void destroy ( ) {
		super.destroy ( );
		
		if ( this.handle == null ) {
			return;
		}
		
		final Player player = getPlayer();
		if ( player != null && player.isOnline ( ) ) {
			BukkitReflection.sendPacket ( player , new PacketPlayOutEntityDestroy ( this.handle.getId ( ) ) );
		}
		
		this.handle = null;
	}
}
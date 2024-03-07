package es.outlook.adriansrj.core.util.entity;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * An implementation of {@link UUIDEntity} intended for {@link Player} entities only.
 * <p>
 * @author AdrianSR / Tuesday 14 April, 2020 / 11:11 AM
 */
public class UUIDPlayer extends UUIDEntity < Player > {

	/**
	 * Construct the {@code UUIDPlayer} from its {@link UUID unique id}.
	 * <p>
	 * @param uuid  the player's {@link UUID unique id}.
	 */
	public UUIDPlayer ( final UUID uuid ) {
		super ( uuid , Player.class );
	}

	/**
	 * Construct the {@code UUIDPlayer} from its respective {@link Player player}.
	 * <p>
	 * @param player the respective player.
	 */
	public UUIDPlayer ( final Player player ) {
		this ( player.getUniqueId() );
	}
	
	/**
	 * Gets the {@link Player} associated with the {@link UUIDEntity#uuid}.
	 */
	@Override
	public final Player get() {
		return Bukkit.getPlayer ( getUniqueId ( ) );
	}
}
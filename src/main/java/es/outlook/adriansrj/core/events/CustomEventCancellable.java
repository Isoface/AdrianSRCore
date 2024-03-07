package es.outlook.adriansrj.core.events;

import org.bukkit.event.Cancellable;

/**
 * An event that implements {@link Cancellable}, allowing its cancellation.
 * <p>
 * @author AdrianSR / Monday 13 April, 2020 / 08:10 PM
 */
public abstract class CustomEventCancellable extends CustomEvent implements Cancellable {

	/**
	 * Whether this event is cancelled or not.
	 */
	protected boolean cancelled;

	@Override
	public boolean isCancelled ( ) {
		return cancelled;
	}

	@Override
	public void setCancelled ( boolean cancelled ) {
		this.cancelled = cancelled;
	}
}
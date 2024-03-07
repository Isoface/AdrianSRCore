package es.outlook.adriansrj.core.util.packet;

/**
 * Convenience implementation of {@link PacketListener}. Derive from this and only override the needed.
 * <p>
 * @author AdrianSR / Saturday 14 March, 2020 / 02:53 PM
 */
public class PacketAdapter implements PacketListener {

	@Override
	public void onReceiving ( final PacketEvent event ) {
		/* to override */
	}

	@Override
	public void onSending ( final PacketEvent event ) {
		/* to override */
	}
}
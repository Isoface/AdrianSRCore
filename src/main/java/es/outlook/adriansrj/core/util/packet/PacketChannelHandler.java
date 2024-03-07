package es.outlook.adriansrj.core.util.packet;

import es.outlook.adriansrj.core.main.AdrianSRCore;
import es.outlook.adriansrj.core.util.packet.PacketListener.Priority;
import es.outlook.adriansrj.core.util.reflection.bukkit.BukkitReflection;
import es.outlook.adriansrj.core.util.reflection.bukkit.PacketReflection;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Simple player's channels handler, for handling the receiving-sending packets.
 * <p>
 * @author AdrianSR / Saturday 14 March, 2020 / 01:07 PM
 */
public final class PacketChannelHandler {
	
	/**
	 * All the packet listeners that will be run when a packet is being receive-send.
	 */
	private static final Map < Priority, Set < RegisteredPacketListener > >
			PACKET_LISTENERS = new EnumMap <> ( Priority.class );
	
	/* initialize channels handler by registering a player injector instance */
	static {
		Bukkit.getPluginManager ( ).registerEvents ( new PlayerInjector ( ) , AdrianSRCore.getInstance ( ) );
	}
	
	/**
	 * Registers the provided {@link PacketListener} that will be run when a packet
	 * is being sending or receiving ( packets which its name is the same as the
	 * provided ).
	 * <p>
	 * @param packet_name the name of the packets this listener will manage.
	 * @param priority    the priority of the listener.
	 * @param listener    the listener to run.
	 */
	public static void addPacketListener ( final String packet_name , final Priority priority ,
			final PacketListener listener ) {
		Set < RegisteredPacketListener > by_priority = PACKET_LISTENERS
				.computeIfAbsent ( priority , k -> new HashSet <> ( ) );
		
		by_priority.add ( new RegisteredPacketListener ( packet_name , listener ) );
	}
	
	/**
	 * Unregisters the provided {@link PacketListener}.
	 * <p>
	 * @param listener the listener to unregister.
	 */
	public static void removePacketListener ( final PacketListener listener ) {
		for ( Set < RegisteredPacketListener > by_priority : PACKET_LISTENERS.values ( ) ) {
			by_priority.removeIf ( registered -> Objects.equals ( registered.listener , listener ) );
		}
	}
	
	/**
	 * Class representing the registered version of a {@link PacketListener}.
	 * <p>
	 * @author AdrianSR / Saturday 14 March, 2020 / 01:23 PM
	 */
	private static class RegisteredPacketListener {
		
		private final String         packet_name;
		private final PacketListener listener;
		
		private RegisteredPacketListener ( final String packet_name , final PacketListener listener ) {
			this.packet_name = packet_name;
			this.listener    = listener;
		}
		
		@Override
		public boolean equals ( Object o ) {
			if ( this == o ) { return true; }
			if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
			RegisteredPacketListener that = ( RegisteredPacketListener ) o;
			return Objects.equals ( packet_name , that.packet_name )
					&& Objects.equals ( listener , that.listener );
		}
		
		@Override
		public int hashCode ( ) {
			return Objects.hash ( packet_name , listener );
		}
	}
	
	/**
	 * Listener that injects any player that joins the server.
	 * <p>
	 * Also this class injects the players that are already connected to the server
	 * when is constructed.
	 * <p>
	 * @author AdrianSR / Saturday 14 March, 2020 / 01:51 PM
	 */
	private static class PlayerInjector implements Listener {
		
		private static final String PACKET_INJECTOR_NAME = "ASRCPacketInjector";
		
		private PlayerInjector ( ) {
			Bukkit.getOnlinePlayers ( ).forEach ( player -> inject ( player ) );
		}
		
		@EventHandler ( priority = EventPriority.LOWEST )
		public void onJoin ( final PlayerJoinEvent event ) {
			inject ( event.getPlayer ( ) );
		}
		
		private void inject ( final Player player ) {
			Channel channel = PlayerInjectorReflection.getChannel ( player );
			
			// must be in channel thread to inject
			if ( Bukkit.isPrimaryThread ( ) ) {
				channel.eventLoop ( ).execute ( ( ) -> inject ( player ) );
				return;
			}
			
			// then injecting
			if ( channel.pipeline ( ).get ( PACKET_INJECTOR_NAME ) == null ) {
				PlayerChannelHandler handler = new PlayerChannelHandler ( player );
				
				if ( channel.pipeline ( ).get ( "packet_handler" ) != null ) {
					channel.pipeline ( ).addBefore ( "packet_handler" , PACKET_INJECTOR_NAME , handler );
				} else {
					if ( channel.pipeline ( ).get ( "protocol_lib_encoder" ) != null ) {
						channel.pipeline ( ).addAfter (
								"protocol_lib_encoder" , PACKET_INJECTOR_NAME , handler );
					} else {
						channel.pipeline ( ).addAfter (
								"encoder" , PACKET_INJECTOR_NAME , handler );
					}
				}
			}
		}
	}
	
	/**
	 * Reflection class used by the {@link PlayerInjector} for getting the channel
	 * of a {@link Player}.
	 * <p>
	 * @author AdrianSR / Saturday 14 March, 2020 / 01:52 PM
	 */
	private static class PlayerInjectorReflection {
		
		private static Channel getChannel ( final Player player ) {
			return ( Channel )
					PlayerInjectorReflection.getChannel (
							PlayerInjectorReflection.getNetworkManager (
									PlayerInjectorReflection.getPlayerConnection ( player ) ) );
		}
		
		private static Object getPlayerConnection ( final Player player ) {
			try {
				return FieldReflection.getValue ( BukkitReflection.getHandle ( player ) ,
												  PacketReflection.PLAYER_CONNECTION_FIELD_NAME );
			} catch ( SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException
					| InvocationTargetException ex ) {
				ex.printStackTrace ( );
				return null;
			}
		}
		
		private static Object getNetworkManager ( final Object player_connection ) {
			try {
				return FieldReflection.getValue ( player_connection , PacketReflection.NETWORK_MANAGER_FIELD_NAME );
			} catch ( SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex ) {
				ex.printStackTrace ( );
				return null;
			}
		}
		
		private static Object getChannel ( final Object network_manager ) {
			try {
				return FieldReflection.getValue ( network_manager , PacketReflection.CHANNEL_FIELD_NAME );
			} catch ( SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex ) {
				ex.printStackTrace ( );
				return null;
			}
		}
	}
	
	/**
	 * The duplex channel handler for handling the packets when receiving or sending.
	 * <p>
	 * @author AdrianSR / Saturday 14 March, 2020 / 01:51 PM
	 */
	private static class PlayerChannelHandler extends ChannelDuplexHandler {
		
		private final Player player;
		
		public PlayerChannelHandler ( final Player player ) {
			this.player = player;
		}
		
		//		@Override
		//		public void read(ChannelHandlerContext ctx) throws Exception {
		//			super.read(ctx);
		//
		//			System.out.println ( "ctx = " + ctx );
		//		}
		
		@Override
		public void channelRead ( final ChannelHandlerContext context , final Object packet ) throws Exception {
			final PacketEvent event = new PacketEvent ( player , packet );
			
			synchronized ( PACKET_LISTENERS ) {
				final List < PacketListener > listeners = getListenersFor ( packet );
				
				for ( PacketListener listener : listeners ) {
					// we're taking care of exceptions that can cause a kick.
					try {
						listener.onReceiving ( event );
					} catch ( Exception ex ) {
						ex.printStackTrace ( );
					}
				}
			}
			
			if ( !event.isCancelled ( ) ) {
				super.channelRead ( context , event.getPacket ( ) );
			}
		}
		
		@Override
		public void write ( final ChannelHandlerContext context , final Object packet , final ChannelPromise promise )
				throws Exception {
			final PacketEvent event = new PacketEvent ( player , packet );
			
			synchronized ( PACKET_LISTENERS ) {
				final List < PacketListener > listeners = getListenersFor ( packet );
				
				for ( PacketListener listener : listeners ) {
					// we're taking care of exceptions that can cause a kick.
					try {
						listener.onSending ( event );
					} catch ( Exception ex ) {
						ex.printStackTrace ( );
					}
				}
			}
			
			if ( !event.isCancelled ( ) ) {
				super.write ( context , event.getPacket ( ) , promise );
			}
		}
		
		private List < PacketListener > getListenersFor ( final Object packet ) {
			final List < PacketListener > listeners = new ArrayList <> ( );
			
			for ( Priority priority : PacketListener.Priority.values ( ) ) {
				final Set < RegisteredPacketListener > by_priority = PACKET_LISTENERS.get ( priority );
				
				if ( by_priority != null ) {
					for ( RegisteredPacketListener listener : by_priority ) {
						if ( listener.packet_name.equals ( packet.getClass ( ).getSimpleName ( ) ) ) {
							listeners.add ( listener.listener );
						}
					}
				}
				
				//				// here we avoid the ConcurrentModificationException
				//				for ( int i = 0 ; i < by_priority.size ( ) ; i ++ ) {
				//					RegisteredPacketListener [ ] array = by_priority.toArray ( new
				//					RegisteredPacketListener [ by_priority.size ( ) ] );
				//					RegisteredPacketListener  listener = array [ i ];
				//
				//					if ( listener.packet_name.equals ( packet.getClass ( ).getSimpleName ( ) ) ) {
				//						listeners.add ( listener.listener );
				//					}
				//				}
			}
			return listeners;
		}
	}
	
	private PacketChannelHandler ( ) { }
}
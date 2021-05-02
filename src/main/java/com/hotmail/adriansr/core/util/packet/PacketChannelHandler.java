package com.hotmail.adriansr.core.util.packet;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.hotmail.adriansr.core.main.AdrianSRCore;
import com.hotmail.adriansr.core.util.packet.PacketListener.Priority;
import com.hotmail.adriansr.core.util.reflection.bukkit.BukkitReflection;
import com.hotmail.adriansr.core.util.reflection.general.FieldReflection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * Simple player's channels handler, for handling the receiving-sending packets.
 * <p>
 * @author AdrianSR / Saturday 14 March, 2020 / 01:07 PM
 */
public final class PacketChannelHandler {
	
	/**
	 * All the packet listeners that will be run when a packet is being receive-send.
	 */
	private static final Map < Priority , Set < RegisteredPacketListener > > PACKET_LISTENERS = new HashMap <>();
	
	/* initialize channels handler by registering a player injector instance */
	static {
		Bukkit.getPluginManager().registerEvents ( new PlayerInjector() , AdrianSRCore.getInstance() );
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
	public static void addPacketListener ( final String packet_name , final Priority priority , final PacketListener listener ) {
		Set < RegisteredPacketListener > by_priority = PACKET_LISTENERS.get ( priority );
		if ( by_priority == null ) {
			PACKET_LISTENERS.put ( priority , ( by_priority = new HashSet < > ( ) ) );
		}
		
		by_priority.add ( new RegisteredPacketListener ( packet_name , listener ) );
	}
	
	/**
	 * Unregisters the provided {@link PacketListener}.
	 * <p>
	 * @param listener the listener to unregister.
	 */
	public static void removePacketListener ( final PacketListener listener ) {
		Set < RegisteredPacketListener > containing = null;
		RegisteredPacketListener           removing = null;
		for ( Set < RegisteredPacketListener > by_priority : PACKET_LISTENERS.values() ) {
			for ( RegisteredPacketListener registered : by_priority ) {
				if ( registered.listener.equals ( listener ) ) {
					containing = by_priority;
					removing   = registered;
					break;
				}
			}
			
			if ( removing != null ) {
				break;
			}
		}
		
		containing.remove ( removing );
	}
	
	/**
	 * Class representing the registered version of a {@link PacketListener}.
	 * <p>
	 * @author AdrianSR / Saturday 14 March, 2020 / 01:23 PM
	 */
	private static class RegisteredPacketListener {
		
		private final String      packet_name;
		private final PacketListener listener;
		
		private RegisteredPacketListener ( final String packet_name , final PacketListener listener ) {
			this.packet_name = packet_name;
			this.listener    = listener;
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
		
		private PlayerInjector() {
			Bukkit.getOnlinePlayers().forEach( player -> inject ( player ) );
		}
		
		@EventHandler ( priority = EventPriority.LOWEST )
		public void onJoin ( final PlayerJoinEvent event ) {
			inject ( event.getPlayer() );
		}
		
		private static void inject ( final Player player ) {
			/* unject before injecting */
			unject ( player );
			
			/* injecting */
			final Channel              channel = PlayerInjectorReflection.getChannel ( player );
			final PlayerChannelHandler handler = new PlayerChannelHandler ( player );
			
			channel.pipeline().addBefore ( "packet_handler" , "PacketInjector" , handler );
			
//			channel.pipeline ( ).addFirst ( new ChannelDuplexHandler ( ) {
//				@Override
//				public void read(ChannelHandlerContext ctx) throws Exception {
//					System.out.println ( ">>>>>>>>>> first got something!" );
//				}
//			} );
			
//			channel.eventLoop ( ).submit ( new Runnable () {
//				
//				@Override
//				public void run() {
//					System.out.println ( ">>>>>>>>>>>>>>>> bueno bueno!" );
//				}
//			} );
		}
		
		private static void unject ( final Player player ) {
			final Channel channel = PlayerInjectorReflection.getChannel ( player );
			if ( channel.pipeline().get ( "PacketInjector" ) != null ) {
				channel.pipeline().remove ( "PacketInjector" );
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
		
		private static final String PLAYER_CONNECTION_FIELD_NAME = "playerConnection";
		private static final String   NETWORK_MANAGER_FIELD_NAME = "networkManager";
		private static final String           CHANNEL_FIELD_NAME = "channel";
		
		private static Channel getChannel ( final Player player ) {
			return (Channel) 
					PlayerInjectorReflection.getChannel(
					PlayerInjectorReflection.getNetworkManager(
					PlayerInjectorReflection.getPlayerConnection ( player ) ) );
		}
		
		private static Object getPlayerConnection ( final Player player ) {
			try {
				return FieldReflection.getValue ( BukkitReflection.getHandle ( player ) , PLAYER_CONNECTION_FIELD_NAME );
			} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException
					| InvocationTargetException ex ) {
				ex.printStackTrace();
				return null;
			}
		}
		
		private static Object getNetworkManager ( final Object player_connection ) {
			try {
				return FieldReflection.getValue ( player_connection , NETWORK_MANAGER_FIELD_NAME );
			} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex ) {
				ex.printStackTrace();
				return null;
			}
		}
		
		private static Object getChannel ( final Object network_manager ) {
			try {
				return FieldReflection.getValue ( network_manager , CHANNEL_FIELD_NAME );
			} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex ) {
				ex.printStackTrace();
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
					} catch ( Throwable ex ) {
						ex.printStackTrace ( );
					}
				}
			}
			
			if ( !event.isCancelled ( ) ) {
				super.channelRead ( context , packet );
			}
		}
		
		@Override
		public void write ( final ChannelHandlerContext context , final Object packet , final ChannelPromise promise ) throws Exception {
			final PacketEvent event = new PacketEvent ( player , packet );
			
			synchronized ( PACKET_LISTENERS ) {
				final List < PacketListener > listeners = getListenersFor ( packet );
				for ( PacketListener listener : listeners ) {
					// we're taking care of exceptions that can cause a kick.
					try {
						listener.onSending ( event );
					} catch ( Throwable ex ) {
						ex.printStackTrace ( );
					}
				}
			}
			
			if ( !event.isCancelled ( ) ) {
				super.write ( context , packet , promise );
			}
		}
		
		private List < PacketListener > getListenersFor ( final Object packet ) {
			final List < PacketListener > listeners = new ArrayList <>();
			for ( Priority priority : PacketListener.Priority.values() ) {
				final Set < RegisteredPacketListener > by_priority = PACKET_LISTENERS.get ( priority );
				if ( by_priority == null ) {
					continue;
				}
				
				for ( RegisteredPacketListener listener : by_priority ) {
					if ( listener.packet_name.equals ( packet.getClass().getSimpleName() ) ) {
						listeners.add ( listener.listener );
					}
				}
				
//				// here we avoid the ConcurrentModificationException
//				for ( int i = 0 ; i < by_priority.size ( ) ; i ++ ) {
//					RegisteredPacketListener [ ] array = by_priority.toArray ( new RegisteredPacketListener [ by_priority.size ( ) ] );
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
	
	private PacketChannelHandler() {}
}
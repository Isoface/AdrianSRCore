package es.outlook.adriansrj.core.util.bungeecord;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

/**
 * Useful class for reading reponses.
 * <p>
 * @author AdrianSR / Thursday 16 April, 2020 / 12:02 PM
 */
public class ReadUtil {
	
	public static Object[] read(byte[] message) {
		if (message == null || message.length == 0) { // don't read empty messages
			return new Object[0];
		}
		
		try {
			final DataInputStream in_stream = new DataInputStream(new ByteArrayInputStream(message));
			final String           argument = in_stream.readUTF();
			final Object[]          reponse = readReponseFully(argument, in_stream);
			
			if ( reponse == null || reponse.length == 0 ) {
				return new String [ 0 ];
			}
			
			final Object[] all = new Object[reponse.length + 1];
			for (int x = 0; x < all.length; x++) {
				if (x == 0) {
					all[x] = argument;
				} else {
					all[x] = reponse[(x - 1)];
				}
			}
			return all;
		} catch(Throwable t) {
			t.printStackTrace();
			return new String[0];
		}
	}
	
	public static String readArgument(byte[] message) {
		final Object[] arg_repo = read(message);
		if (arg_repo.length > 0) {
			return (String) arg_repo[0];
		}
		return "";
	}
	
	public static Object[] readResponse(byte[] message) {
		final Object[] arg_repo = read(message);
		if (arg_repo.length > 1) {
			Object[] reponse = new Object[arg_repo.length - 1];
			for (int x = 0; x < reponse.length; x++) {
				reponse[x] = arg_repo[x + 1];
			}
			return reponse;
		}
		return new Object[0];
	}

	/**
	 * Read fully DataInputStreams.
	 * <p>
	 * @param in {@link DataInputStream}.
	 * @return readed.
	 */
	private static Object[] readReponseFully(final String argument, final DataInputStream in) {
		try {
//			String fully = ""; /* readed from data input stream */
			Object[] all = null;
			switch (argument) {
				case MessagingUtil.PLAYER_IP_ARGUMENT:
				case MessagingUtil.SERVER_PLAYER_COUNT_ARGUMENT:
					all    = new Object[] 
					{
						in.readUTF(), 
						in.readInt()
					};
					break;
				case MessagingUtil.SERVER_PLAYER_LIST_ARGUMENT:
					String        server = in.readUTF();
					String[] player_list = in.readUTF().split(", ");
					all                  = new Object[player_list.length + 1];
					all[0]               = server;
					for (int x = 0; x < player_list.length; x++) {
						all[x + 1] = player_list[x];
					}
					break;
				case MessagingUtil.SERVER_NAME_ARGUMENT:
					all = new Object[] 
					{
						in.readUTF() 
					};
					break;
				case MessagingUtil.SERVERS_NAMES_ARGUMENT:
					String[] server_list = in.readUTF().split(", ");
					all                  = new Object[server_list.length];
					for (int x = 0; x < server_list.length; x++) {
						all[x] = server_list[x];
					}
					break;
				case MessagingUtil.SERVER_IP_ARGUMENT:
					all    = new Object[] 
					{
						in.readUTF(), 
						in.readUTF(), 
						in.readUnsignedShort()
					};
					break;
			}
			return all;
		} catch (Throwable t) {
			return new Object[0];
		}
	}
}

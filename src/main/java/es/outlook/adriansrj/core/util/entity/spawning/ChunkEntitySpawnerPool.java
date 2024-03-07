package es.outlook.adriansrj.core.util.entity.spawning;

import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Chunk;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO: Description
 * <br>
 * @author AdrianSR / Friday 05 February, 2021 / 02:56 PM
 */
public class ChunkEntitySpawnerPool implements Runnable {
	
	/**
	 * Spawners pool stack.
	 */
	protected final Set < ChunkEntitySpawner > spawners;
	protected       ExecutorService            executor;
	protected       boolean                    started;
	
	public ChunkEntitySpawnerPool ( ) {
		this.spawners = Collections.newSetFromMap ( new ConcurrentHashMap <> ( ) );
	}
	
	public void start ( ) {
		if ( executor == null ) {
			executor = Executors.newSingleThreadExecutor ( );
			executor.execute ( this );
		}
	}
	
	public void stop ( ) {
		executor.shutdownNow ( );
		executor = null;
	}
	
	public boolean isTerminated ( ) {
		return started && spawners.size ( ) == 0;
	}
	
	public void submit ( ChunkEntitySpawner... spawners ) {
		this.spawners.addAll ( Arrays.asList ( spawners ) );
	}
	
	@SuppressWarnings ( "deprecation" )
	@Override
	public void run ( ) {
		started = true; // marking as started
		
		// calling callbacks
		try {
			while ( spawners.size ( ) > 0 ) {
				Iterator < ChunkEntitySpawner > iterator = spawners.iterator ( );
				
				while ( iterator.hasNext ( ) ) {
					ChunkEntitySpawner spawner = iterator.next ( );
					Chunk              chunk   = spawner.getChunk ( );
					
					if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_14_R1 )
							? chunk.isLoaded ( ) : chunk.getWorld ( ).isChunkInUse ( chunk.getX ( ) , chunk.getZ ( ) ) ) {
						spawner.accept ( chunk );
						iterator.remove ( );
					}
				}
			}
		} catch ( Throwable ex ) {
			// it will not throw exception unless
			// we force it to do so.
			ex.printStackTrace ( );
		}
		
		if ( executor != null ) {
			executor.shutdownNow ( );
		}
		
		executor = null;
	}
}
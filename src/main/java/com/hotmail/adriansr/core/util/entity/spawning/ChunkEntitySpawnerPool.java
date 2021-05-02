package com.hotmail.adriansr.core.util.entity.spawning;

import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Chunk;

import com.hotmail.adriansr.core.util.server.Version;

/**
 * TODO: Description
 * <br>
 * @author AdrianSR / Friday 05 February, 2021 / 02:56 PM
 */
public class ChunkEntitySpawnerPool implements Runnable {
	
	/**
	 * Spawners pool stack.
	 */
	protected final Stack < ChunkEntitySpawner > spawners;
	protected       ExecutorService              executor;
	protected       boolean                       started;

	public ChunkEntitySpawnerPool ( ) {
		this.spawners = new Stack < > ( );
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
		return started ? spawners.size ( ) == 0 : false;
	}
	
	public void submit ( ChunkEntitySpawner... spawners ) {
		for ( ChunkEntitySpawner spawner : spawners ) {
			this.spawners.addElement ( spawner );
		}
	}

	@SuppressWarnings ( "deprecation" ) @Override
	public void run ( ) {
		started = true; // marking as started
		
		while ( spawners.size ( ) > 0 ) {
			Iterator < ChunkEntitySpawner > iterator = spawners.iterator ( );
			
			while ( iterator.hasNext ( ) ) {
				ChunkEntitySpawner spawner = iterator.next ( );
				Chunk                chunk = spawner.getChunk ( );
				
				if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_14_R1 ) ? chunk.isLoaded ( ) 
						: chunk.getWorld ( ).isChunkInUse ( chunk.getX ( ) , chunk.getZ ( ) ) ) {
					
					spawner.accept ( chunk );
					iterator.remove ( );
				}
			}
		}
		
		if ( executor != null ) {
			executor.shutdownNow ( );
		}
		
		executor = null;
	}
}
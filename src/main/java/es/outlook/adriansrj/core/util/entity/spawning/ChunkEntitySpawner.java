package es.outlook.adriansrj.core.util.entity.spawning;

import java.util.function.Consumer;

import org.bukkit.Chunk;

/**
 * TODO: Description
 * <br>
 * @author AdrianSR / Friday 05 February, 2021 / 02:50 PM
 */
public abstract class ChunkEntitySpawner implements Consumer < Chunk > {
	
	protected final Chunk chunk;
	
	public ChunkEntitySpawner ( Chunk chunk ) {
		this.chunk = chunk;
	}
	
	public Chunk getChunk ( ) {
		return chunk;
	}
}
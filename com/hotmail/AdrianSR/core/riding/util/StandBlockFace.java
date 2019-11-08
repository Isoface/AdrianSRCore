package com.hotmail.AdrianSR.core.riding.util;

import org.bukkit.block.BlockFace;

/**
 * Represents the face of a block
 */
public enum StandBlockFace {
	
    NORTH(0, 0, -1),
    EAST(1, 0, 0),
    SOUTH(0, 0, 1),
    WEST(-1, 0, 0),
    UP(0, 1, 0),
    DOWN(0, -1, 0),
    SELF(0, 0, 0);

    private final double modX;
    private final double modY;
    private final double modZ;

    StandBlockFace(final double modX, final double modY, final double modZ) {
        this.modX = modX;
        this.modY = modY;
        this.modZ = modZ;
    }

    StandBlockFace(final StandBlockFace face1, final StandBlockFace face2) {
        this.modX = face1.getModX() + face2.getModX();
        this.modY = face1.getModY() + face2.getModY();
        this.modZ = face1.getModZ() + face2.getModZ();
    }
    
    public static final double RESTADOR = 0.385D;

    /**
     * Get the amount of X-coordinates to modify to get the represented block
     *<p>
     * @return Amount of X-coordinates to modify
     */
    public double getModX() {
        return (modX != 0 ? (modX > 0 ? (modX - RESTADOR) : (modX + RESTADOR)) : modX);
    }

    /**
     * Get the amount of Y-coordinates to modify to get the represented block
     *<p>
     * @return Amount of Y-coordinates to modify
     */
    public double getModY() {
        return (modY != 0 ? ( modY > 0 ? (modY - RESTADOR) : (modY + RESTADOR) ) : modY);
    }

    /**
     * Get the amount of Z-coordinates to modify to get the represented block
     *<p>
     * @return Amount of Z-coordinates to modify
     */
    public double getModZ() {
        return (modZ != 0 ? ( modZ > 0 ? (modZ - RESTADOR) : (modZ + RESTADOR) ) : modZ);
    }

    public StandBlockFace getOppositeFace() {
        switch (this) {
        case NORTH:
            return StandBlockFace.SOUTH;
        case SOUTH:
            return StandBlockFace.NORTH;
        case EAST:
            return StandBlockFace.WEST;
        case WEST:
            return StandBlockFace.EAST;
        case UP:
            return StandBlockFace.DOWN;
        case DOWN:
            return StandBlockFace.UP;
        case SELF:
            return StandBlockFace.SELF;
        }
        return StandBlockFace.SELF;
    }
    
    public static StandBlockFace fromBlockFace(final BlockFace face) {
    	return StandBlockFace.valueOf(face.name());
    }
}

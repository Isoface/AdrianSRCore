package com.hotmail.AdrianSR.core.util.classes;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.degoos.wetsponge.material.blockType.WSBlockType;
import com.degoos.wetsponge.material.blockType.WSBlockTypes;
import com.degoos.wetsponge.util.reflection.NMSUtils;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;

public class HandledUtils {

	public static Object getWorldHandle(World w) {
		try {
			return w.getClass().getMethod("getHandle").invoke(w);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}


	public static Object getPlayerHandle(Player pl) {
		return getEntityHandle(pl);
	}

	public static Object getEntityHandle(Entity entity) {
		try {
			return entity.getClass().getMethod("getHandle").invoke(entity);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getServerHandle(Object object) {
		try {
			return object.getClass().getMethod("getServer").invoke(object);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getHandle(Object object) {
		try {
			return object.getClass().getMethod("getHandle").invoke(object);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getBlockPosition(Location loc) {
		return getBlockPosition(loc.getX(), loc.getY(), loc.getZ());
	}

	public static Object getBlockPosition(Vector3d loc) {
		return getBlockPosition(loc.getX(), loc.getY(), loc.getZ());
	}

	public static Object getBlockPosition(Vector3i loc) {
		return getBlockPosition(loc.getX(), loc.getY(), loc.getZ());
	}


	public static Object getBlockPosition(double x, double y, double z) {
		try {
			return NMSUtils.getNMSClass("BlockPosition").getConstructor(double.class, double.class, double.class).newInstance(x, y, z);
		} catch (Throwable ex) {
			return null;
		}
	}

	public static Object getBlockPosition(int x, int y, int z) {
		try {
			return NMSUtils.getNMSClass("BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(x, y, z);
		} catch (Throwable ex) {
			return null;
		}
	}

	public static Vector3i getBlockPositionVector(Object position) {
		try {
			return new Vector3i((int) position.getClass().getMethod("getX").invoke(position), (int) position.getClass().getMethod("getY").invoke(position), (int)
				position
				.getClass().getMethod("getZ").invoke(position));
		} catch (Throwable ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static WSBlockType getMaterial(Object blockState) {
		try {
			Class<?> iBlockStateClass = NMSUtils.getNMSClass("IBlockData");
			Class<?> blockClass = NMSUtils.getNMSClass("Block");
			Class<?> registryClass = NMSUtils.getNMSClass("RegistryBlocks");

			Object block = iBlockStateClass.getMethod("getBlock").invoke(blockState);
			Object registry = blockClass.getField("REGISTRY").get(null);
			
			int id = (int) ReflectionUtils.getMethod(registryClass, ServerVersion.getVersion().isOlderThan(ServerVersion.v1_9_R1) ? "b" : "a", blockClass)
				.invoke(registry, block);
			int data = (int) blockClass.getMethod("toLegacyData", iBlockStateClass).invoke(block, blockState);

			return WSBlockTypes.getType(id, data).orElse(new WSBlockType(id, "", 64));
		} catch (Throwable ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
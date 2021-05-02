package de.slikey.effectlib.util;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.adriansr.core.util.server.Version;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.util.versions.ParticleDisplay_post13;
import de.slikey.effectlib.util.versions.ParticleDisplay_pre12;

public abstract class ParticleDisplay {
	
    private EffectManager manager;

    public abstract void display(Particle particle, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount, float size, Color color, Material material, byte materialData, double range, List<Player> targetPlayers);

    protected void display(Particle particle, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount, Object data, double range, List<Player> targetPlayers) {
        try {
            if (targetPlayers == null) {
                String worldName = center.getWorld().getName();
                double squared = range * range;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.getWorld().getName().equals(worldName) || player.getLocation().distanceSquared(center) > squared) {
                        continue;
                    }
                    player.spawnParticle(particle, center, amount, offsetX, offsetY, offsetZ, speed, data);
                }
            } else {
                for (Player player : targetPlayers) {
                    player.spawnParticle(particle, center, amount, offsetX, offsetY, offsetZ, speed, data);
                }
            }
        } catch (Exception ex) {
            if (manager != null) {
                manager.onError(ex);
            }
        }
    }

    protected void displayItem(Particle particle, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount, Material material, byte materialData, double range, List<Player> targetPlayers) {
        if (material == null || material == Material.AIR) {
            return;
        }

        ItemStack item = new ItemStack(material);
        item.setDurability(materialData);
        display(particle, center, offsetX, offsetY, offsetZ, speed, amount, item, range, targetPlayers);
    }

    protected void displayLegacyColored(Particle particle, Location center, float speed, Color color, double range, List<Player> targetPlayers) {
        int amount = 0;
        // Colored particles can't have a speed of 0.
        if (speed == 0) {
            speed = 1;
        }
        float offsetX = (float) color.getRed() / 255;
        float offsetY = (float) color.getGreen() / 255;
        float offsetZ = (float) color.getBlue() / 255;

        // The redstone particle reverts to red if R is 0!
        if (offsetX < Float.MIN_NORMAL) {
            offsetX = Float.MIN_NORMAL;
        }

        display(particle, center, offsetX, offsetY, offsetZ, speed, amount, null, range, targetPlayers);
    }

    public void setManager(EffectManager manager) {
        this.manager = manager;
    }

    public static ParticleDisplay newInstance() {
        switch(Version.getServerVersion()) {
		case v1_8_R1:
		case v1_8_R2:
		case v1_8_R3:
		case v1_9_R1:
		case v1_9_R2:
		case v1_10_R1:
		case v1_11_R1:
		case v1_12_R1:
			return new ParticleDisplay_pre12(); /* 1_8 - 1_13_R2 */
		case v1_13_R1:
		case v1_13_R2:
		case v1_14_R1:
			return new ParticleDisplay_post13(); /* 1_13 - 1_14_1 for now */
		default:
			return null;
        }
    }
}

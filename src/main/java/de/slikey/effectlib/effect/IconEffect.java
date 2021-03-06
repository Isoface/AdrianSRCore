package de.slikey.effectlib.effect;

import org.bukkit.Location;
import org.bukkit.Particle;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;

public class IconEffect extends Effect {

    /**
     * ParticleType of spawned particle
     */
    public Particle particle = Particle.VILLAGER_ANGRY;

    public int yOffset = 2;

    public IconEffect(EffectManager effectManager) {
        super(effectManager);
        type = EffectType.REPEATING;
        period = 4;
        iterations = 25;
    }

    @Override
    public void onRun() {
        Location location = getLocation();
        location.add(0, yOffset, 0);
        display(particle, location);
    }
}

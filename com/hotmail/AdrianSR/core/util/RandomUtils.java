package com.hotmail.AdrianSR.core.util;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.util.Vector;

public final class RandomUtils {

    public static final Random RANDOM = new Random(System.nanoTime());

    private RandomUtils() {
        // No instance allowed
    }

    public static Vector getRandomVector() {
        double x, y, z;
        x = RANDOM.nextDouble() * 2 - 1;
        y = RANDOM.nextDouble() * 2 - 1;
        z = RANDOM.nextDouble() * 2 - 1;

        return new Vector(x, y, z).normalize();
    }

    public static Vector getRandomCircleVector() {
        double rnd, x, z;
        rnd = RANDOM.nextDouble() * 2 * Math.PI;
        x = Math.cos(rnd);
        z = Math.sin(rnd);

        return new Vector(x, 0, z);
    }

    public static Material getRandomMaterial(Material[] materials) {
        return materials[RANDOM.nextInt(materials.length)];
    }

    public static double getRandomAngle() {
        return RANDOM.nextDouble() * 2 * Math.PI;
    }
}
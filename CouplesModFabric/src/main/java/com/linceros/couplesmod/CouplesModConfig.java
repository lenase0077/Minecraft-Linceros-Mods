package com.linceros.couplesmod;

import eu.midnightdust.lib.config.MidnightConfig;
import java.util.List;
import java.util.Arrays;

public class CouplesModConfig extends MidnightConfig {
    public static final String EFFECTS = "effects";

    @Entry(category = EFFECTS)
    public static List<String> marriageProximityEffects = Arrays.asList(
        "minecraft:haste;300;1",
        "minecraft:regeneration;300;0",
        "minecraft:speed;300;0",
        "minecraft:health_boost;300;1",
        "minecraft:luck;300;0"
    );

    @Entry(category = EFFECTS)
    public static List<String> sharedBedEffects = Arrays.asList(
        "minecraft:saturation;12000;0",
        "minecraft:regeneration;12000;0"
    );
}

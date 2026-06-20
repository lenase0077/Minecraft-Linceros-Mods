package com.linceros.couplesmod;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public class CouplesModConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> MARRIAGE_PROXIMITY_EFFECTS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SHARED_BED_EFFECTS;

    static {
        BUILDER.push("Marriage Effects");

        MARRIAGE_PROXIMITY_EFFECTS = BUILDER
                .comment("Effects applied to married couples when close to each other. Format: 'modid:effect;duration_ticks;amplifier'")
                .defineListAllowEmpty("marriageProximityEffects", 
                        List.of("minecraft:haste;300;1", "minecraft:regeneration;300;0", "minecraft:speed;300;0", "minecraft:health_boost;300;1", "minecraft:luck;300;0"), 
                        () -> "minecraft:haste;300;1", 
                        o -> o instanceof String);

        SHARED_BED_EFFECTS = BUILDER
                .comment("Effects applied when a married couple sleeps near each other. Format: 'modid:effect;duration_ticks;amplifier'")
                .defineListAllowEmpty("sharedBedEffects", 
                        List.of("minecraft:saturation;12000;0", "minecraft:regeneration;12000;0"), 
                        () -> "minecraft:saturation;12000;0", 
                        o -> o instanceof String);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}

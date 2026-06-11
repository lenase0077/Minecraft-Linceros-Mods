package com.linceros.leveltools;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    public enum HudPosition {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER_ABOVE_HOTBAR
    }

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Whether the tool leveling system is enabled")
            .define("enabled", true);

    public static final ModConfigSpec.BooleanValue ENABLE_EVOLUTIONS = BUILDER
            .comment("Whether tools should evolve to higher tiers (e.g. Wood -> Stone) upon reaching specific levels")
            .define("enableEvolutions", true);

    public static final ModConfigSpec.IntValue EVOLUTION_LEVEL_INTERVAL = BUILDER
            .comment("How many levels are required for a tool to evolve (e.g. 5 means evolution happens at level 5, 10, 15, etc.)")
            .defineInRange("evolutionLevelInterval", 5, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue DURABILITY_LOSS_TO_LEVEL = BUILDER
            .comment("Base amount of XP (actions) required to reach level 1")
            .defineInRange("durabilityLossToLevel", 100.0, 1.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue LEVEL_XP_MULTIPLIER = BUILDER
            .comment("Power exponent applied to the level for calculating XP cost. Formula: Base * (Level+1)^Multiplier. (e.g. 1.5 is a balanced RPG curve)")
            .defineInRange("levelXpMultiplier", 1.5, 1.0, 100.0);

    public static final ModConfigSpec.DoubleValue DURABILITY_BONUS_PERCENT = BUILDER
            .comment("Percentage increase to maximum durability per level")
            .defineInRange("durabilityBonusPercent", 15.0, 0.0, 1000.0);

    public static final ModConfigSpec.DoubleValue SPEED_BONUS_PERCENT = BUILDER
            .comment("Percentage increase to mining speed per level")
            .defineInRange("speedBonusPercent", 5.0, 0.0, 1000.0);

    public static final ModConfigSpec.DoubleValue DAMAGE_BONUS_PERCENT = BUILDER
            .comment("Percentage increase to attack damage per level")
            .defineInRange("damageBonusPercent", 5.0, 0.0, 1000.0);

    public static final ModConfigSpec.DoubleValue ARMOR_XP_GAIN_MULTIPLIER = BUILDER
            .comment("Multiplier for the XP gained by equipped armors when taking damage (e.g. 5.0 means 5x faster than weapons)")
            .defineInRange("armorXpGainMultiplier", 5.0, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue ARMOR_DAMAGE_REDUCTION_PERCENT = BUILDER
            .comment("Percentage of incoming damage reduced per level of armor (e.g. 1.0 means 1% less damage taken per level)")
            .defineInRange("armorDamageReductionPercent", 1.0, 0.0, 25.0);

    public static final ModConfigSpec.DoubleValue WEAPON_XP_MULTIPLIER = BUILDER
            .comment("XP multiplier for weapons to compensate for less frequent use")
            .defineInRange("weaponXPMultiplier", 2.5, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue TOOL_XP_MULTIPLIER = BUILDER
            .comment("XP multiplier for tools")
            .defineInRange("toolXPMultiplier", 1.0, 0.0, 100.0);

    public static final ModConfigSpec.BooleanValue LEVEL_UP_FULL_REPAIR = BUILDER
            .comment("Whether the tool should be fully repaired upon leveling up")
            .define("levelUpFullRepair", true);

    public static final ModConfigSpec.BooleanValue SHOW_LEVEL_UP_MESSAGE = BUILDER
            .comment("Whether to show a chat message to the player when their tool levels up")
            .define("showLevelUpMessage", true);

    public static final ModConfigSpec.BooleanValue SHOW_HUD = BUILDER
            .comment("Whether to show the leveling HUD overlay")
            .define("showHUD", true);

    public static final ModConfigSpec.EnumValue<HudPosition> HUD_POSITION = BUILDER
            .comment("Position of the leveling HUD on the screen")
            .defineEnum("hudPosition", HudPosition.BOTTOM_LEFT);

    static final ModConfigSpec SPEC = BUILDER.build();
}

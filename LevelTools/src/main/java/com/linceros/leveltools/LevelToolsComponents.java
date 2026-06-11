package com.linceros.leveltools;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class LevelToolsComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(LevelTools.MODID);

    // Store the level of the tool. Default is 0.
    public static final Supplier<DataComponentType<Integer>> TOOL_LEVEL = DATA_COMPONENTS.registerComponentType("level", builder -> builder
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    // Store the accumulated XP of the tool (durability lost)
    public static final Supplier<DataComponentType<Float>> TOOL_XP = DATA_COMPONENTS.registerComponentType("xp", builder -> builder
            .persistent(Codec.FLOAT)
            .networkSynchronized(ByteBufCodecs.FLOAT)
    );

}

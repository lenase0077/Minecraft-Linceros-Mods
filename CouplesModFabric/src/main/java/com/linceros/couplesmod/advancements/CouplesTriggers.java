package com.linceros.couplesmod.advancements;

import com.linceros.couplesmod.CouplesMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class CouplesTriggers {
    public static final ActionTrigger KISS_PARTNER = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            ResourceLocation.fromNamespaceAndPath(CouplesMod.MODID, "kiss_partner"),
            new ActionTrigger()
    );
    public static final ActionTrigger GET_MARRIED = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            ResourceLocation.fromNamespaceAndPath(CouplesMod.MODID, "get_married"),
            new ActionTrigger()
    );

    public static void initialize() {}
}

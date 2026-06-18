package com.linceros.couplesmod.advancements;

import com.linceros.couplesmod.CouplesMod;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.Identifier;

public class CouplesTriggers {
    public static final ActionTrigger KISS_PARTNER = CriteriaTriggers.register(
            CouplesMod.MODID + ":kiss_partner",
            new ActionTrigger()
    );
    public static final ActionTrigger GET_MARRIED = CriteriaTriggers.register(
            CouplesMod.MODID + ":get_married",
            new ActionTrigger()
    );

    public static void initialize() {}
}

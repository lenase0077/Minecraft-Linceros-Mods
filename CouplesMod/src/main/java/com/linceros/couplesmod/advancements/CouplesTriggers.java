package com.linceros.couplesmod.advancements;

import com.linceros.couplesmod.CouplesMod;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CouplesTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, CouplesMod.MODID);

    public static final Supplier<ActionTrigger> KISS_PARTNER = TRIGGERS.register("kiss_partner", ActionTrigger::new);
    public static final Supplier<ActionTrigger> GET_MARRIED = TRIGGERS.register("get_married", ActionTrigger::new);
}

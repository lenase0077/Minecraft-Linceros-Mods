package com.linceros.couplesmod.init;

import com.linceros.couplesmod.CouplesMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CouplesParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, CouplesMod.MODID);

    public static final Supplier<SimpleParticleType> HEART_PARTICLE = PARTICLES.register("heart_particle", () -> new SimpleParticleType(true));
}

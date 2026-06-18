package com.linceros.couplesmod.init;

import com.linceros.couplesmod.CouplesMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class CouplesParticles {
    public static final SimpleParticleType HEART_PARTICLE = Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            Identifier.fromNamespaceAndPath(CouplesMod.MODID, "heart_particle"),
            FabricParticleTypes.simple(true)
    );

    public static void initialize() {}
}

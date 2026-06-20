package com.linceros.couplesmod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import com.linceros.couplesmod.init.CouplesItems;
import com.linceros.couplesmod.init.CouplesComponents;
import com.linceros.couplesmod.init.CouplesCreativeTabs;
import com.linceros.couplesmod.init.CouplesAttachments;
import com.linceros.couplesmod.init.CouplesParticles;
import com.linceros.couplesmod.advancements.CouplesTriggers;
import com.linceros.couplesmod.network.CouplesNetworking;
import com.linceros.couplesmod.events.InteractionEvents;

public class CouplesMod implements ModInitializer {
    public static final String MODID = "couplesmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        eu.midnightdust.lib.config.MidnightConfig.init("couplesmod", CouplesModConfig.class);
        CouplesComponents.initialize();
        CouplesAttachments.initialize();
        CouplesItems.initialize();
        CouplesCreativeTabs.initialize();
        CouplesParticles.initialize();
        CouplesTriggers.initialize();
        CouplesNetworking.initialize();
        InteractionEvents.initialize();
        com.linceros.couplesmod.commands.CouplesCommands.initialize();

        LOGGER.info("Couples Mod Initialized (Fabric)!");
    }
}

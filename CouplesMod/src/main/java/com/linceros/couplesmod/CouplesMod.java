package com.linceros.couplesmod;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import com.linceros.couplesmod.init.CouplesItems;
import com.linceros.couplesmod.init.CouplesComponents;
import com.linceros.couplesmod.init.CouplesCreativeTabs;
import com.linceros.couplesmod.init.CouplesAttachments;
import com.linceros.couplesmod.init.CouplesParticles;
import com.linceros.couplesmod.advancements.CouplesTriggers;

@Mod(CouplesMod.MODID)
public class CouplesMod {
    public static final String MODID = "couplesmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CouplesMod(IEventBus modEventBus, ModContainer modContainer) {
        CouplesItems.ITEMS.register(modEventBus);
        CouplesCreativeTabs.CREATIVE_TABS.register(modEventBus);
        CouplesAttachments.ATTACHMENT_TYPES.register(modEventBus);
        CouplesComponents.COMPONENTS.register(modEventBus);
        CouplesParticles.PARTICLES.register(modEventBus);
        CouplesTriggers.TRIGGERS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        
        LOGGER.info("Couples Mod Initialized!");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Setup code here
    }
}

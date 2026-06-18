package com.linceros.couplesmod.init;

import com.linceros.couplesmod.CouplesMod;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;

public class CouplesCreativeTabs {

    public static final CreativeModeTab COUPLES_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(CouplesMod.MODID, "couples_tab"),
            FabricCreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.couplesmod"))
                    .icon(() -> CouplesItems.ENGAGEMENT_RING.getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(CouplesItems.ENGAGEMENT_RING);
                        output.accept(CouplesItems.IRON_ENGAGEMENT_RING);
                        output.accept(CouplesItems.GOLD_ENGAGEMENT_RING);
                        output.accept(CouplesItems.EMERALD_ENGAGEMENT_RING);
                        output.accept(CouplesItems.DANDELION_BOUQUET);
                        output.accept(CouplesItems.POPPY_BOUQUET);
                        output.accept(CouplesItems.BLUE_ORCHID_BOUQUET);
                        output.accept(CouplesItems.ALLIUM_BOUQUET);
                        output.accept(CouplesItems.RED_TULIP_BOUQUET);
                        output.accept(CouplesItems.ORANGE_TULIP_BOUQUET);
                        output.accept(CouplesItems.WHITE_TULIP_BOUQUET);
                        output.accept(CouplesItems.PINK_TULIP_BOUQUET);
                        output.accept(CouplesItems.OXEYE_DAISY_BOUQUET);
                        output.accept(CouplesItems.CORNFLOWER_BOUQUET);
                        output.accept(CouplesItems.LILY_OF_THE_VALLEY_BOUQUET);
                        output.accept(CouplesItems.WITHER_ROSE_BOUQUET);
                        output.accept(CouplesItems.SUNFLOWER_BOUQUET);
                        output.accept(CouplesItems.LILAC_BOUQUET);
                        output.accept(CouplesItems.ROSE_BUSH_BOUQUET);
                        output.accept(CouplesItems.ANNIVERSARY_JOURNAL);
                    })
                    .build()
    );

    public static void initialize() {}
}

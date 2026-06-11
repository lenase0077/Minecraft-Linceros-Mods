package com.linceros.couplesmod.init;

import com.linceros.couplesmod.CouplesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class CouplesCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CouplesMod.MODID);

    public static final Supplier<CreativeModeTab> COUPLES_TAB = CREATIVE_TABS.register("couples_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.couplesmod"))
            .icon(() -> CouplesItems.ENGAGEMENT_RING.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(CouplesItems.ENGAGEMENT_RING.get());
                output.accept(CouplesItems.IRON_ENGAGEMENT_RING.get());
                output.accept(CouplesItems.GOLD_ENGAGEMENT_RING.get());
                output.accept(CouplesItems.EMERALD_ENGAGEMENT_RING.get());
                output.accept(CouplesItems.DANDELION_BOUQUET.get());
                output.accept(CouplesItems.POPPY_BOUQUET.get());
                output.accept(CouplesItems.BLUE_ORCHID_BOUQUET.get());
                output.accept(CouplesItems.ALLIUM_BOUQUET.get());
                output.accept(CouplesItems.RED_TULIP_BOUQUET.get());
                output.accept(CouplesItems.ORANGE_TULIP_BOUQUET.get());
                output.accept(CouplesItems.WHITE_TULIP_BOUQUET.get());
                output.accept(CouplesItems.PINK_TULIP_BOUQUET.get());
                output.accept(CouplesItems.OXEYE_DAISY_BOUQUET.get());
                output.accept(CouplesItems.CORNFLOWER_BOUQUET.get());
                output.accept(CouplesItems.LILY_OF_THE_VALLEY_BOUQUET.get());
                output.accept(CouplesItems.WITHER_ROSE_BOUQUET.get());
                output.accept(CouplesItems.SUNFLOWER_BOUQUET.get());
                output.accept(CouplesItems.LILAC_BOUQUET.get());
                output.accept(CouplesItems.ROSE_BUSH_BOUQUET.get());
                output.accept(CouplesItems.ANNIVERSARY_JOURNAL.get());
            })
            .build()
    );
}

package com.linceros.couplesmod.datagen;

import com.linceros.couplesmod.CouplesMod;
import com.linceros.couplesmod.init.CouplesItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class CouplesItemModelProvider extends ItemModelProvider {
    public CouplesItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CouplesMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(CouplesItems.ENGAGEMENT_RING.get());
        basicItem(CouplesItems.IRON_ENGAGEMENT_RING.get());
        basicItem(CouplesItems.GOLD_ENGAGEMENT_RING.get());
        basicItem(CouplesItems.EMERALD_ENGAGEMENT_RING.get());
        basicItem(CouplesItems.DANDELION_BOUQUET.get());
        basicItem(CouplesItems.POPPY_BOUQUET.get());
        basicItem(CouplesItems.BLUE_ORCHID_BOUQUET.get());
        basicItem(CouplesItems.ALLIUM_BOUQUET.get());
        basicItem(CouplesItems.RED_TULIP_BOUQUET.get());
        basicItem(CouplesItems.ORANGE_TULIP_BOUQUET.get());
        basicItem(CouplesItems.WHITE_TULIP_BOUQUET.get());
        basicItem(CouplesItems.PINK_TULIP_BOUQUET.get());
        basicItem(CouplesItems.OXEYE_DAISY_BOUQUET.get());
        basicItem(CouplesItems.CORNFLOWER_BOUQUET.get());
        basicItem(CouplesItems.LILY_OF_THE_VALLEY_BOUQUET.get());
        basicItem(CouplesItems.WITHER_ROSE_BOUQUET.get());
        basicItem(CouplesItems.SUNFLOWER_BOUQUET.get());
        basicItem(CouplesItems.LILAC_BOUQUET.get());
        basicItem(CouplesItems.ROSE_BUSH_BOUQUET.get());
        basicItem(CouplesItems.ANNIVERSARY_JOURNAL.get());
        
        // Custom icon item for advancements
        basicItem(CouplesItems.HEART_ICON.get());
    }
}

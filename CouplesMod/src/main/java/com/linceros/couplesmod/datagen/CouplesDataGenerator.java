package com.linceros.couplesmod.datagen;

import com.linceros.couplesmod.CouplesMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = CouplesMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CouplesDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new CouplesRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new CouplesAdvancementProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new CouplesItemModelProvider(packOutput, event.getExistingFileHelper()));
    }
}

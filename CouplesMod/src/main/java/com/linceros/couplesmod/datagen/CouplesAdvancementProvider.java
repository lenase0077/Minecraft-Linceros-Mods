package com.linceros.couplesmod.datagen;

import com.linceros.couplesmod.init.CouplesItems;
import com.linceros.couplesmod.advancements.CouplesTriggers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CouplesAdvancementProvider extends AdvancementProvider {
    public CouplesAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, existingFileHelper, List.of(new CouplesAdvancementGenerator()));
    }

    private static final class CouplesAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
            
            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(
                            new ItemStack(CouplesItems.ROSE_BUSH_BOUQUET.get()),
                            Component.translatable("advancements.couplesmod.root.title"),
                            Component.translatable("advancements.couplesmod.root.description"),
                            ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("has_bouquet", InventoryChangeTrigger.TriggerInstance.hasItems(
                            net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(
                                    CouplesItems.DANDELION_BOUQUET.get(),
                                    CouplesItems.POPPY_BOUQUET.get(),
                                    CouplesItems.BLUE_ORCHID_BOUQUET.get(),
                                    CouplesItems.ALLIUM_BOUQUET.get(),
                                    CouplesItems.RED_TULIP_BOUQUET.get(),
                                    CouplesItems.ORANGE_TULIP_BOUQUET.get(),
                                    CouplesItems.WHITE_TULIP_BOUQUET.get(),
                                    CouplesItems.PINK_TULIP_BOUQUET.get(),
                                    CouplesItems.OXEYE_DAISY_BOUQUET.get(),
                                    CouplesItems.CORNFLOWER_BOUQUET.get(),
                                    CouplesItems.LILY_OF_THE_VALLEY_BOUQUET.get(),
                                    CouplesItems.WITHER_ROSE_BOUQUET.get(),
                                    CouplesItems.SUNFLOWER_BOUQUET.get(),
                                    CouplesItems.LILAC_BOUQUET.get(),
                                    CouplesItems.ROSE_BUSH_BOUQUET.get()
                            ).build()
                    ))
                    .save(saver, ResourceLocation.fromNamespaceAndPath("couplesmod", "couplesmod/root"), existingFileHelper);

            AdvancementHolder engagementRing = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            new ItemStack(CouplesItems.ENGAGEMENT_RING.get()),
                            Component.translatable("advancements.couplesmod.engagement_ring.title"),
                            Component.translatable("advancements.couplesmod.engagement_ring.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("has_engagement_ring", InventoryChangeTrigger.TriggerInstance.hasItems(
                            net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(
                                    CouplesItems.ENGAGEMENT_RING.get(),
                                    CouplesItems.IRON_ENGAGEMENT_RING.get(),
                                    CouplesItems.GOLD_ENGAGEMENT_RING.get(),
                                    CouplesItems.EMERALD_ENGAGEMENT_RING.get()
                            ).build()
                    ))
                    .save(saver, ResourceLocation.fromNamespaceAndPath("couplesmod", "couplesmod/engagement_ring"), existingFileHelper);

            AdvancementHolder anniversaryJournal = Advancement.Builder.advancement()
                    .parent(engagementRing)
                    .display(
                            new ItemStack(CouplesItems.ANNIVERSARY_JOURNAL.get()),
                            Component.translatable("advancements.couplesmod.anniversary_journal.title"),
                            Component.translatable("advancements.couplesmod.anniversary_journal.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("has_anniversary_journal", InventoryChangeTrigger.TriggerInstance.hasItems(CouplesItems.ANNIVERSARY_JOURNAL.get()))
                    .save(saver, ResourceLocation.fromNamespaceAndPath("couplesmod", "couplesmod/anniversary_journal"), existingFileHelper);

            AdvancementHolder firstKiss = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            new ItemStack(CouplesItems.HEART_ICON.get()),
                            Component.translatable("advancements.couplesmod.first_kiss.title"),
                            Component.translatable("advancements.couplesmod.first_kiss.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("kissed_partner", CouplesTriggers.KISS_PARTNER.get().createCriterion(new com.linceros.couplesmod.advancements.ActionTrigger.Instance(java.util.Optional.empty())))
                    .save(saver, ResourceLocation.fromNamespaceAndPath("couplesmod", "couplesmod/first_kiss"), existingFileHelper);

            AdvancementHolder tyingTheKnot = Advancement.Builder.advancement()
                    .parent(engagementRing)
                    .display(
                            new ItemStack(CouplesItems.GOLD_ENGAGEMENT_RING.get()),
                            Component.translatable("advancements.couplesmod.tying_the_knot.title"),
                            Component.translatable("advancements.couplesmod.tying_the_knot.description"),
                            null,
                            AdvancementType.GOAL,
                            true,
                            true,
                            false
                    )
                    .addCriterion("got_married", CouplesTriggers.GET_MARRIED.get().createCriterion(new com.linceros.couplesmod.advancements.ActionTrigger.Instance(java.util.Optional.empty())))
                    .save(saver, ResourceLocation.fromNamespaceAndPath("couplesmod", "couplesmod/tying_the_knot"), existingFileHelper);
        }
    }
}

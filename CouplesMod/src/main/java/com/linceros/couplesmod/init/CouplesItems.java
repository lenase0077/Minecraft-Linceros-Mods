package com.linceros.couplesmod.init;

import com.linceros.couplesmod.CouplesMod;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import java.util.function.Supplier;

public class CouplesItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, CouplesMod.MODID);

    public static final Supplier<Item> ENGAGEMENT_RING = ITEMS.register("engagement_ring", () -> new com.linceros.couplesmod.item.EngagementRingItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> IRON_ENGAGEMENT_RING = ITEMS.register("iron_engagement_ring", () -> new com.linceros.couplesmod.item.EngagementRingItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> GOLD_ENGAGEMENT_RING = ITEMS.register("gold_engagement_ring", () -> new com.linceros.couplesmod.item.EngagementRingItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> EMERALD_ENGAGEMENT_RING = ITEMS.register("emerald_engagement_ring", () -> new com.linceros.couplesmod.item.EngagementRingItem(new Item.Properties().stacksTo(1)));

    // Bouquets
    public static final Supplier<Item> DANDELION_BOUQUET = ITEMS.register("dandelion_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> POPPY_BOUQUET = ITEMS.register("poppy_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> BLUE_ORCHID_BOUQUET = ITEMS.register("blue_orchid_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> ALLIUM_BOUQUET = ITEMS.register("allium_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> RED_TULIP_BOUQUET = ITEMS.register("red_tulip_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> ORANGE_TULIP_BOUQUET = ITEMS.register("orange_tulip_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> WHITE_TULIP_BOUQUET = ITEMS.register("white_tulip_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> PINK_TULIP_BOUQUET = ITEMS.register("pink_tulip_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> OXEYE_DAISY_BOUQUET = ITEMS.register("oxeye_daisy_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> CORNFLOWER_BOUQUET = ITEMS.register("cornflower_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> LILY_OF_THE_VALLEY_BOUQUET = ITEMS.register("lily_of_the_valley_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> WITHER_ROSE_BOUQUET = ITEMS.register("wither_rose_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> SUNFLOWER_BOUQUET = ITEMS.register("sunflower_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> LILAC_BOUQUET = ITEMS.register("lilac_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> ROSE_BUSH_BOUQUET = ITEMS.register("rose_bush_bouquet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> ANNIVERSARY_JOURNAL = ITEMS.register("anniversary_journal", () -> new com.linceros.couplesmod.item.AnniversaryJournalItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> HEART_ICON = ITEMS.register("heart_icon", () -> new Item(new Item.Properties()));
}

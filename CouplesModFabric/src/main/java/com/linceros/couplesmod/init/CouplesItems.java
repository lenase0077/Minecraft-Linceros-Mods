package com.linceros.couplesmod.init;

import com.linceros.couplesmod.CouplesMod;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class CouplesItems {
    public static Item register(String id, java.util.function.Function<Item.Properties, Item> factory) {
        net.minecraft.resources.ResourceKey<Item> key = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, Identifier.fromNamespaceAndPath(CouplesMod.MODID, id));
        Item item = factory.apply(new Item.Properties().setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static final Item ENGAGEMENT_RING = register("engagement_ring", p -> new com.linceros.couplesmod.item.EngagementRingItem(p.stacksTo(1)));
    public static final Item IRON_ENGAGEMENT_RING = register("iron_engagement_ring", p -> new com.linceros.couplesmod.item.EngagementRingItem(p.stacksTo(1)));
    public static final Item GOLD_ENGAGEMENT_RING = register("gold_engagement_ring", p -> new com.linceros.couplesmod.item.EngagementRingItem(p.stacksTo(1)));
    public static final Item EMERALD_ENGAGEMENT_RING = register("emerald_engagement_ring", p -> new com.linceros.couplesmod.item.EngagementRingItem(p.stacksTo(1)));

    // Bouquets
    public static final Item DANDELION_BOUQUET = register("dandelion_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item POPPY_BOUQUET = register("poppy_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item BLUE_ORCHID_BOUQUET = register("blue_orchid_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item ALLIUM_BOUQUET = register("allium_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item RED_TULIP_BOUQUET = register("red_tulip_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item ORANGE_TULIP_BOUQUET = register("orange_tulip_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item WHITE_TULIP_BOUQUET = register("white_tulip_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item PINK_TULIP_BOUQUET = register("pink_tulip_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item OXEYE_DAISY_BOUQUET = register("oxeye_daisy_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item CORNFLOWER_BOUQUET = register("cornflower_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item LILY_OF_THE_VALLEY_BOUQUET = register("lily_of_the_valley_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item WITHER_ROSE_BOUQUET = register("wither_rose_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item SUNFLOWER_BOUQUET = register("sunflower_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item LILAC_BOUQUET = register("lilac_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item ROSE_BUSH_BOUQUET = register("rose_bush_bouquet", p -> new Item(p.stacksTo(1)));
    public static final Item ANNIVERSARY_JOURNAL = register("anniversary_journal", p -> new com.linceros.couplesmod.item.AnniversaryJournalItem(p.stacksTo(1)));
    public static final Item HEART_ICON = register("heart_icon", p -> new Item(p));

    public static void initialize() {}
}

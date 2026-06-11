package com.linceros.couplesmod.datagen;

import com.linceros.couplesmod.init.CouplesItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CouplesRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public CouplesRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Original Ring (Fallback)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CouplesItems.ENGAGEMENT_RING.get())
                .pattern(" D ")
                .pattern("G G")
                .pattern(" G ")
                .define('G', Items.GOLD_BLOCK)
                .define('D', Items.DIAMOND)
                .unlockedBy("has_gold", has(Items.GOLD_BLOCK))
                .save(recipeOutput);

        // New Iron Ring
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CouplesItems.IRON_ENGAGEMENT_RING.get())
                .pattern(" I ")
                .pattern("I I")
                .pattern(" I ")
                .define('I', Items.IRON_BLOCK)
                .unlockedBy("has_iron", has(Items.IRON_BLOCK))
                .save(recipeOutput);

        // New Gold Ring
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CouplesItems.GOLD_ENGAGEMENT_RING.get())
                .pattern(" G ")
                .pattern("G G")
                .pattern(" G ")
                .define('G', Items.GOLD_BLOCK)
                .unlockedBy("has_gold", has(Items.GOLD_BLOCK))
                .save(recipeOutput);

        // New Emerald Ring
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CouplesItems.EMERALD_ENGAGEMENT_RING.get())
                .pattern(" E ")
                .pattern("G G")
                .pattern(" G ")
                .define('G', Items.GOLD_BLOCK)
                .define('E', Items.EMERALD)
                .unlockedBy("has_gold", has(Items.GOLD_BLOCK))
                .save(recipeOutput);

        // Anniversary Journal Recipes
        buildJournal(recipeOutput, Items.DANDELION, "dandelion");
        buildJournal(recipeOutput, Items.POPPY, "poppy");
        buildJournal(recipeOutput, Items.BLUE_ORCHID, "blue_orchid");
        buildJournal(recipeOutput, Items.ALLIUM, "allium");
        buildJournal(recipeOutput, Items.RED_TULIP, "red_tulip");
        buildJournal(recipeOutput, Items.ORANGE_TULIP, "orange_tulip");
        buildJournal(recipeOutput, Items.WHITE_TULIP, "white_tulip");
        buildJournal(recipeOutput, Items.PINK_TULIP, "pink_tulip");
        buildJournal(recipeOutput, Items.OXEYE_DAISY, "oxeye_daisy");
        buildJournal(recipeOutput, Items.CORNFLOWER, "cornflower");
        buildJournal(recipeOutput, Items.LILY_OF_THE_VALLEY, "lily_of_the_valley");
        buildJournal(recipeOutput, Items.WITHER_ROSE, "wither_rose");
        buildJournal(recipeOutput, Items.SUNFLOWER, "sunflower");
        buildJournal(recipeOutput, Items.LILAC, "lilac");
        buildJournal(recipeOutput, Items.ROSE_BUSH, "rose_bush");

        // Bouquets
        buildBouquet(recipeOutput, Items.DANDELION, CouplesItems.DANDELION_BOUQUET);
        buildBouquet(recipeOutput, Items.POPPY, CouplesItems.POPPY_BOUQUET);
        buildBouquet(recipeOutput, Items.BLUE_ORCHID, CouplesItems.BLUE_ORCHID_BOUQUET);
        buildBouquet(recipeOutput, Items.ALLIUM, CouplesItems.ALLIUM_BOUQUET);
        buildBouquet(recipeOutput, Items.RED_TULIP, CouplesItems.RED_TULIP_BOUQUET);
        buildBouquet(recipeOutput, Items.ORANGE_TULIP, CouplesItems.ORANGE_TULIP_BOUQUET);
        buildBouquet(recipeOutput, Items.WHITE_TULIP, CouplesItems.WHITE_TULIP_BOUQUET);
        buildBouquet(recipeOutput, Items.PINK_TULIP, CouplesItems.PINK_TULIP_BOUQUET);
        buildBouquet(recipeOutput, Items.OXEYE_DAISY, CouplesItems.OXEYE_DAISY_BOUQUET);
        buildBouquet(recipeOutput, Items.CORNFLOWER, CouplesItems.CORNFLOWER_BOUQUET);
        buildBouquet(recipeOutput, Items.LILY_OF_THE_VALLEY, CouplesItems.LILY_OF_THE_VALLEY_BOUQUET);
        buildBouquet(recipeOutput, Items.WITHER_ROSE, CouplesItems.WITHER_ROSE_BOUQUET);
        buildBouquet(recipeOutput, Items.SUNFLOWER, CouplesItems.SUNFLOWER_BOUQUET);
        buildBouquet(recipeOutput, Items.LILAC, CouplesItems.LILAC_BOUQUET);
        buildBouquet(recipeOutput, Items.ROSE_BUSH, CouplesItems.ROSE_BUSH_BOUQUET);
    }

    private void buildBouquet(RecipeOutput recipeOutput, Item flower, Supplier<Item> bouquet) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, bouquet.get())
                .pattern("FFF")
                .pattern("FFF")
                .pattern("FFF")
                .define('F', flower)
                .unlockedBy("has_flower", has(flower))
                .save(recipeOutput);
    }

    private void buildJournal(RecipeOutput recipeOutput, Item flower, String flowerName) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CouplesItems.ANNIVERSARY_JOURNAL.get())
                .pattern("FFF")
                .pattern("FBF")
                .pattern("FFF")
                .define('F', flower)
                .define('B', Items.BOOK)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(recipeOutput, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(com.linceros.couplesmod.CouplesMod.MODID, "anniversary_journal_from_" + flowerName));
    }
}

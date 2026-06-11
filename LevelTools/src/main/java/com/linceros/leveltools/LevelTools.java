package com.linceros.leveltools;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.world.item.Items;
import com.linceros.leveltools.api.LevelToolsAPI;

@Mod(LevelTools.MODID)
public class LevelTools {
    public static final String MODID = "leveltools";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LevelTools(IEventBus modEventBus, ModContainer modContainer) {
        // Register Data Components
        LevelToolsComponents.DATA_COMPONENTS.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modEventBus.addListener(this::commonSetup);

        // Events like LevelingSystem and BonusSystem are automatically registered via @EventBusSubscriber
        // But ClientSetup is also registered via @EventBusSubscriber(value = Dist.CLIENT)
        
        LOGGER.info("Linceros LevelTools initialized for NeoForge 1.21.1!");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Pickaxes
            LevelToolsAPI.registerEvolution(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE);
            LevelToolsAPI.registerEvolution(Items.STONE_PICKAXE, Items.IRON_PICKAXE);
            LevelToolsAPI.registerEvolution(Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE);
            LevelToolsAPI.registerEvolution(Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);

            // Axes
            LevelToolsAPI.registerEvolution(Items.WOODEN_AXE, Items.STONE_AXE);
            LevelToolsAPI.registerEvolution(Items.STONE_AXE, Items.IRON_AXE);
            LevelToolsAPI.registerEvolution(Items.IRON_AXE, Items.DIAMOND_AXE);
            LevelToolsAPI.registerEvolution(Items.DIAMOND_AXE, Items.NETHERITE_AXE);

            // Swords
            LevelToolsAPI.registerEvolution(Items.WOODEN_SWORD, Items.STONE_SWORD);
            LevelToolsAPI.registerEvolution(Items.STONE_SWORD, Items.IRON_SWORD);
            LevelToolsAPI.registerEvolution(Items.IRON_SWORD, Items.DIAMOND_SWORD);
            LevelToolsAPI.registerEvolution(Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);

            // Shovels
            LevelToolsAPI.registerEvolution(Items.WOODEN_SHOVEL, Items.STONE_SHOVEL);
            LevelToolsAPI.registerEvolution(Items.STONE_SHOVEL, Items.IRON_SHOVEL);
            LevelToolsAPI.registerEvolution(Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL);
            LevelToolsAPI.registerEvolution(Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);

            // Hoes
            LevelToolsAPI.registerEvolution(Items.WOODEN_HOE, Items.STONE_HOE);
            LevelToolsAPI.registerEvolution(Items.STONE_HOE, Items.IRON_HOE);
            LevelToolsAPI.registerEvolution(Items.IRON_HOE, Items.DIAMOND_HOE);
            LevelToolsAPI.registerEvolution(Items.DIAMOND_HOE, Items.NETHERITE_HOE);

            // Helmets
            LevelToolsAPI.registerEvolution(Items.LEATHER_HELMET, Items.CHAINMAIL_HELMET);
            LevelToolsAPI.registerEvolution(Items.CHAINMAIL_HELMET, Items.IRON_HELMET);
            LevelToolsAPI.registerEvolution(Items.IRON_HELMET, Items.DIAMOND_HELMET);
            LevelToolsAPI.registerEvolution(Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);

            // Chestplates
            LevelToolsAPI.registerEvolution(Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE);
            LevelToolsAPI.registerEvolution(Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE);
            LevelToolsAPI.registerEvolution(Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE);
            LevelToolsAPI.registerEvolution(Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);

            // Leggings
            LevelToolsAPI.registerEvolution(Items.LEATHER_LEGGINGS, Items.CHAINMAIL_LEGGINGS);
            LevelToolsAPI.registerEvolution(Items.CHAINMAIL_LEGGINGS, Items.IRON_LEGGINGS);
            LevelToolsAPI.registerEvolution(Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS);
            LevelToolsAPI.registerEvolution(Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);

            // Boots
            LevelToolsAPI.registerEvolution(Items.LEATHER_BOOTS, Items.CHAINMAIL_BOOTS);
            LevelToolsAPI.registerEvolution(Items.CHAINMAIL_BOOTS, Items.IRON_BOOTS);
            LevelToolsAPI.registerEvolution(Items.IRON_BOOTS, Items.DIAMOND_BOOTS);
            LevelToolsAPI.registerEvolution(Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
        });
    }
}

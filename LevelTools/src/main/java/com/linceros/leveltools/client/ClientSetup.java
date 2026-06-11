package com.linceros.leveltools.client;

import com.linceros.leveltools.Config;
import com.linceros.leveltools.LevelToolsComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = "leveltools", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    private static float lastKnownXp = -1f;
    private static ItemStack lastKnownStack = ItemStack.EMPTY;
    private static long showHudUntil = 0;

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath("leveltools", "hud"), (guiGraphics, deltaTracker) -> {
            if (!Config.ENABLED.get() || !Config.SHOW_HUD.get()) return;

            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;

            ItemStack stack = player.getMainHandItem();
            if (stack.isEmpty() || (!stack.has(LevelToolsComponents.TOOL_LEVEL.get()) && !stack.has(LevelToolsComponents.TOOL_XP.get()))) return;

            int level = stack.getOrDefault(LevelToolsComponents.TOOL_LEVEL.get(), 0);
            float xp = stack.getOrDefault(LevelToolsComponents.TOOL_XP.get(), 0f);
            float baseRequiredXp = Config.DURABILITY_LOSS_TO_LEVEL.get().floatValue();
            float multiplier = Config.LEVEL_XP_MULTIPLIER.get().floatValue();
            float requiredXp = (float) (baseRequiredXp * Math.pow(level + 1, multiplier));

            // Track changes to show HUD
            if (!ItemStack.isSameItemSameComponents(stack, lastKnownStack) || xp != lastKnownXp) {
                showHudUntil = Util.getMillis() + 3000; // Show for 3 seconds
                lastKnownStack = stack.copy();
                lastKnownXp = xp;
            }

            // Hide HUD if time has passed
            if (Util.getMillis() > showHudUntil) {
                return;
            }

            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();

            // Setup dimensions
            String text = "Tool Lvl: " + level + " | XP: " + String.format("%.0f", xp) + "/" + String.format("%.0f", requiredXp);
            int textWidth = mc.font.width(text);
            int barWidth = 100;
            int barHeight = 4;
            int totalWidth = Math.max(textWidth, barWidth);
            
            int x = 0;
            int y = 0;

            switch (Config.HUD_POSITION.get()) {
                case TOP_LEFT:
                    x = 5;
                    y = 5;
                    break;
                case TOP_RIGHT:
                    x = screenWidth - totalWidth - 5;
                    y = 5;
                    break;
                case BOTTOM_LEFT:
                    x = 5;
                    y = screenHeight - 25;
                    break;
                case BOTTOM_RIGHT:
                    x = screenWidth - totalWidth - 5;
                    y = screenHeight - 25;
                    break;
                case CENTER_ABOVE_HOTBAR:
                default:
                    x = (screenWidth - totalWidth) / 2;
                    y = screenHeight - 75; // Raised to avoid overlapping with item name
                    break;
            }

            // Draw text
            int textX = x + (totalWidth - textWidth) / 2;
            guiGraphics.drawString(mc.font, text, textX, y, 0xFFD700);
            
            // Draw progress bar under the text
            int barX = x + (totalWidth - barWidth) / 2;
            int barY = y + 12; // 12 pixels below the text
            
            guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xAA000000); // background
            int progressWidth = (int) ((xp / requiredXp) * barWidth);
            guiGraphics.fill(barX, barY, barX + progressWidth, barY + barHeight, 0xFF00AA00); // foreground
        });
    }
}

@EventBusSubscriber(modid = "leveltools", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
class ClientGameEvents {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (!Config.ENABLED.get()) return;

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        if (stack.has(LevelToolsComponents.TOOL_LEVEL.get()) || stack.has(LevelToolsComponents.TOOL_XP.get())) {
            int level = stack.getOrDefault(LevelToolsComponents.TOOL_LEVEL.get(), 0);
            float xp = stack.getOrDefault(LevelToolsComponents.TOOL_XP.get(), 0f);
            float baseRequiredXp = Config.DURABILITY_LOSS_TO_LEVEL.get().floatValue();
            float multiplier = Config.LEVEL_XP_MULTIPLIER.get().floatValue();
            float requiredXp = (float) (baseRequiredXp * Math.pow(level + 1, multiplier));

            event.getToolTip().add(Component.literal("§6Level: §e" + level));
            event.getToolTip().add(Component.literal("§6XP: §e" + String.format("%.1f", xp) + " / " + String.format("%.1f", requiredXp)));

            if (level > 0) {
                if (stack.getItem() instanceof ArmorItem) {
                    float reductionBonus = level * Config.ARMOR_DAMAGE_REDUCTION_PERCENT.get().floatValue();
                    event.getToolTip().add(Component.literal("§2Bonuses: §a+" + reductionBonus + "% Damage Reduction"));
                } else {
                    float speedBonus = level * Config.SPEED_BONUS_PERCENT.get().floatValue();
                    float dmgBonus = level * Config.DAMAGE_BONUS_PERCENT.get().floatValue();
                    event.getToolTip().add(Component.literal("§2Bonuses: §a+" + speedBonus + "% Speed, +" + dmgBonus + "% Damage"));
                }
            }
        }
    }
}

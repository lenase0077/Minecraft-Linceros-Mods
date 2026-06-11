package com.linceros.leveltools.system;

import com.linceros.leveltools.Config;
import com.linceros.leveltools.LevelToolsComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = "leveltools")
public class BonusSystem {

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!Config.ENABLED.get()) return;

        Player player = event.getEntity();
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.isEmpty()) return;

        int level = mainHand.getOrDefault(LevelToolsComponents.TOOL_LEVEL.get(), 0);
        if (level > 0) {
            float speedBonusPercent = Config.SPEED_BONUS_PERCENT.get().floatValue();
            float multiplier = 1.0f + (level * speedBonusPercent / 100.0f);
            event.setNewSpeed(event.getOriginalSpeed() * multiplier);
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (!Config.ENABLED.get()) return;

        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.isEmpty()) return;

            int level = mainHand.getOrDefault(LevelToolsComponents.TOOL_LEVEL.get(), 0);
            if (level > 0) {
                float damageBonusPercent = Config.DAMAGE_BONUS_PERCENT.get().floatValue();
                float multiplier = 1.0f + (level * damageBonusPercent / 100.0f);
                event.setNewDamage(event.getOriginalDamage() * multiplier);
            }
        }

        // Armor damage reduction
        if (event.getEntity() instanceof Player victim) {
            int totalArmorLevels = 0;
            for (ItemStack armor : victim.getArmorSlots()) {
                if (!armor.isEmpty()) {
                    totalArmorLevels += armor.getOrDefault(LevelToolsComponents.TOOL_LEVEL.get(), 0);
                }
            }

            if (totalArmorLevels > 0) {
                float reductionPercent = Config.ARMOR_DAMAGE_REDUCTION_PERCENT.get().floatValue();
                float maxReduction = 0.8f; // Cap at 80% to prevent total invincibility
                float reduction = Math.min((totalArmorLevels * reductionPercent) / 100.0f, maxReduction);
                
                float newDamage = event.getNewDamage() * (1.0f - reduction);
                event.setNewDamage(newDamage);
            }
        }
    }
}

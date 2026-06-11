package com.linceros.leveltools.system;

import com.linceros.leveltools.Config;
import com.linceros.leveltools.LevelToolsComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import com.linceros.leveltools.api.LevelToolsAPI;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = "leveltools")
public class LevelingSystem {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!Config.ENABLED.get() || event.isCanceled()) return;

        Player player = event.getPlayer();
        if (player == null || player.level().isClientSide) return;

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.isEmpty()) return;

        // Check if the item is a tool by checking if it has tool data component
        if (mainHand.has(DataComponents.TOOL)) {
            grantXP(player, EquipmentSlot.MAINHAND, mainHand, Config.TOOL_XP_MULTIPLIER.get().floatValue());
        }
    }

    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent.Post event) {
        if (!Config.ENABLED.get()) return;

        if (event.getSource().getEntity() instanceof Player player) {
            if (player.level().isClientSide) return;

            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.isEmpty()) return;

            // Simple check to determine if it is a weapon (e.g. sword or axe).
            boolean isWeapon = mainHand.getItem() instanceof SwordItem || 
                               mainHand.getItem() instanceof AxeItem || 
                               mainHand.getItem() instanceof TridentItem;

            if (isWeapon || mainHand.has(DataComponents.TOOL)) {
                // If it is an axe, it's a tool, but it's used as a weapon here, so we apply the weapon multiplier.
                grantXP(player, EquipmentSlot.MAINHAND, mainHand, Config.WEAPON_XP_MULTIPLIER.get().floatValue());
            }
        }

        // Check if a player received damage to level up armors
        if (event.getEntity() instanceof Player victim) {
            float damage = event.getNewDamage();
            if (damage > 0) {
                float xpToGrant = damage * Config.ARMOR_XP_GAIN_MULTIPLIER.get().floatValue();
                for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                    ItemStack armor = victim.getItemBySlot(slot);
                    if (!armor.isEmpty() && armor.has(DataComponents.MAX_DAMAGE)) {
                        grantXP(victim, slot, armor, xpToGrant);
                    }
                }
            }
        }
    }

    public static void grantXP(Player player, EquipmentSlot slot, ItemStack stack, float amount) {
        if (amount <= 0) return;

        // Ensure item can be damaged (it has a base max damage)
        Integer baseMaxDamage = stack.getItem().components().get(DataComponents.MAX_DAMAGE);
        if (baseMaxDamage == null || baseMaxDamage <= 0) return;

        int currentLevel = stack.getOrDefault(LevelToolsComponents.TOOL_LEVEL.get(), 0);
        float currentXp = stack.getOrDefault(LevelToolsComponents.TOOL_XP.get(), 0f);

        float baseRequiredXp = Config.DURABILITY_LOSS_TO_LEVEL.get().floatValue();
        float multiplier = Config.LEVEL_XP_MULTIPLIER.get().floatValue();
        // Polynomial scaling: 100 * (1)^1.5 = 100, 100 * (2)^1.5 = 282, 100 * (20)^1.5 = 8944
        float requiredXp = (float) (baseRequiredXp * Math.pow(currentLevel + 1, multiplier));

        float newXp = currentXp + amount;
        
        if (newXp >= requiredXp) {
            // Level up!
            newXp -= requiredXp; // carry over
            int newLevel = currentLevel + 1;
            
            stack.set(LevelToolsComponents.TOOL_LEVEL.get(), newLevel);
            stack.set(LevelToolsComponents.TOOL_XP.get(), newXp);

            // Apply Max Durability Bonus
            float durabilityBonus = Config.DURABILITY_BONUS_PERCENT.get().floatValue();
            int newMaxDamage = (int) (baseMaxDamage * (1.0f + (newLevel * durabilityBonus / 100.0f)));
            stack.set(DataComponents.MAX_DAMAGE, newMaxDamage);

            // Check for Evolution
            boolean evolved = false;
            if (Config.ENABLE_EVOLUTIONS.get() && newLevel % Config.EVOLUTION_LEVEL_INTERVAL.get() == 0) {
                Item nextTier = LevelToolsAPI.getEvolution(stack.getItem());
                if (nextTier != null) {
                    // Create new stack with same data components
                    ItemStack newStack = stack.transmuteCopy(nextTier, stack.getCount());
                    
                    // Recalculate Max Damage for the new tier item
                    Integer newTierBaseMaxDamage = nextTier.components().get(DataComponents.MAX_DAMAGE);
                    if (newTierBaseMaxDamage != null && newTierBaseMaxDamage > 0) {
                        int newTierMaxDamage = (int) (newTierBaseMaxDamage * (1.0f + (newLevel * durabilityBonus / 100.0f)));
                        newStack.set(DataComponents.MAX_DAMAGE, newTierMaxDamage);
                    }

                    // Repair the new item if configured
                    if (Config.LEVEL_UP_FULL_REPAIR.get()) {
                        newStack.set(DataComponents.DAMAGE, 0);
                    }

                    player.setItemSlot(slot, newStack);
                    stack = newStack; // Update local reference
                    evolved = true;
                }
            }

            // Repair original item if configured (and didn't evolve)
            if (!evolved && Config.LEVEL_UP_FULL_REPAIR.get()) {
                stack.set(DataComponents.DAMAGE, 0);
            }

            // Send message, particles, and sound
            if (player.level() instanceof ServerLevel serverLevel) {
                String itemName = stack.getHoverName().getString();
                if (evolved) {
                    // Epic evolution sound
                    serverLevel.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

                    if (Config.SHOW_LEVEL_UP_MESSAGE.get()) {
                        player.sendSystemMessage(Component.literal("§d✨ EVOLUTION! §eYour gear evolved into a §b" + itemName + " §7(Level " + newLevel + ")!"));
                    }
                    // Spectacular evolution particles (Wider spread, more speed)
                    serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, 
                        player.getX(), player.getY() + 1.0, player.getZ(), 
                        80, 1.5, 1.2, 1.5, 0.7);
                    serverLevel.sendParticles(ParticleTypes.END_ROD, 
                        player.getX(), player.getY() + 1.0, player.getZ(), 
                        50, 1.5, 1.2, 1.5, 0.3);
                } else {
                    // Normal level up sound
                    serverLevel.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5f, 1.0f);

                    if (Config.SHOW_LEVEL_UP_MESSAGE.get()) {
                        player.sendSystemMessage(Component.literal("§a⬆ LEVEL UP! §e" + itemName + " §fis now §bLevel " + newLevel + "§7!"));
                    }
                    // Normal level up particles (Wider spread)
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, 
                        player.getX(), player.getY() + 1.0, player.getZ(), 
                        25, 1.2, 1.0, 1.2, 0.1);
                }
            }
            
        } else {
            // Just update XP
            stack.set(LevelToolsComponents.TOOL_XP.get(), newXp);
        }
    }
}

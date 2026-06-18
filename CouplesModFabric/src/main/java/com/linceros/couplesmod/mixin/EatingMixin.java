package com.linceros.couplesmod.mixin;

import com.linceros.couplesmod.init.CouplesComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class EatingMixin {
    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void couplesmod$onEat(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            ItemStack stack = player.getUseItem();
            if (stack.has(CouplesComponents.GIFTED_FOOD)) {
                net.minecraft.world.food.FoodProperties food = stack.get(net.minecraft.core.component.DataComponents.FOOD);
                if (food != null) {
                    float extraSaturation = food.nutrition() * food.saturation() * 2.0f * 0.5f;
                    player.getFoodData().setSaturation(Math.min(player.getFoodData().getSaturationLevel() + extraSaturation, player.getFoodData().getFoodLevel()));
                    
                    if (!player.level().isClientSide()) {
                        player.sendSystemMessage(Component.literal("§d💝 You ate a delicious gift! 💝"));
                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
                    }          
                }
            }
        }
    }
}

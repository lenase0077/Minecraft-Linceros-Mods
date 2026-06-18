package com.linceros.couplesmod.client.mixin;

import com.linceros.couplesmod.client.RelationshipDataCache;
import com.linceros.couplesmod.attachment.RelationshipData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends net.minecraft.world.entity.Entity> {
    
    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void couplesmod$onExtractRenderState(T entity, net.minecraft.client.renderer.entity.state.EntityRenderState state, float partialTick, CallbackInfo ci) {
        if (entity instanceof Player targetPlayer) {
            if (RelationshipDataCache.status != RelationshipData.Status.NONE && RelationshipDataCache.partnerUuid != null) {
                if (targetPlayer.getUUID().equals(RelationshipDataCache.partnerUuid)) {
                    if (state.nameTag != null) {
                        state.nameTag = Component.literal("§d💕 ").append(state.nameTag).append(" §d💕");
                    } else {
                        state.nameTag = Component.literal("§d💕");
                    }
                }
            }
        }
    }
}

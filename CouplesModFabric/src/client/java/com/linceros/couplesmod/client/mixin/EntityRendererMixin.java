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
    
    @Inject(method = "renderNameTag", at = @At("RETURN"))
    private void couplesmod$onRenderNameTag(T entity, Component displayName, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTick, CallbackInfo ci) {
        if (entity instanceof Player targetPlayer) {
            if (RelationshipDataCache.status != RelationshipData.Status.NONE && RelationshipDataCache.partnerUuid != null) {
                if (targetPlayer.getUUID().equals(RelationshipDataCache.partnerUuid)) {
                    poseStack.pushPose();
                    poseStack.translate(0.0D, targetPlayer.getBbHeight() + 0.8D, 0.0D);
                    poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
                    poseStack.scale(-0.025F, -0.025F, 0.025F);
                    
                    Component text = Component.literal("§d💕");
                    float offset = (float)(-Minecraft.getInstance().font.width(text) / 2);
                    
                    Minecraft.getInstance().font.drawInBatch(
                        text, offset, 0f, 0xFFFFFF, false, poseStack.last().pose(), buffer,
                        net.minecraft.client.gui.Font.DisplayMode.SEE_THROUGH, 0, packedLight
                    );
                    
                    poseStack.popPose();
                }
            }
        }
    }
}

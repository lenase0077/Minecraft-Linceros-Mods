package com.linceros.couplesmod.client;

import com.linceros.couplesmod.CouplesMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import com.linceros.couplesmod.attachment.RelationshipData;
import com.linceros.couplesmod.network.PushGlobalDataPacket;
import com.linceros.couplesmod.init.CouplesParticles;
import com.linceros.couplesmod.client.particle.HeartParticle;

@EventBusSubscriber(modid = CouplesMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static final KeyMapping OPEN_RELATIONSHIP_GUI = new KeyMapping(
            "key.couplesmod.open_gui",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_G,
            "key.categories.couplesmod"
    );

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_RELATIONSHIP_GUI);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(CouplesParticles.HEART_PARTICLE.get(), HeartParticle.Provider::new);
    }
}

@EventBusSubscriber(modid = CouplesMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
class ClientGameEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        while (ClientSetup.OPEN_RELATIONSHIP_GUI.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            boolean lookingAtPartner = false;
            
            if (mc.hitResult != null && mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.ENTITY) {
                net.minecraft.world.phys.EntityHitResult entityHit = (net.minecraft.world.phys.EntityHitResult) mc.hitResult;
                if (entityHit.getEntity() instanceof net.minecraft.world.entity.player.Player targetPlayer) {
                    if (RelationshipDataCache.partnerUuid != null && targetPlayer.getUUID().equals(RelationshipDataCache.partnerUuid)) {
                        lookingAtPartner = true;
                    }
                }
            }
            mc.setScreen(new RelationshipScreen(lookingAtPartner));
        }
    }

    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        RelationshipData globalData = GlobalRelationshipManager.loadGlobalData();
        if (globalData.status() != RelationshipData.Status.NONE) {
            PacketDistributor.sendToServer(new PushGlobalDataPacket(
                    globalData.status(), globalData.partnerUuid(), globalData.level(), globalData.xp(), globalData.lastGiftTime(), globalData.lastKissTime(), globalData.startDate(), globalData.kisses(), globalData.gifts(), globalData.sharedBeds()
            ));
        }
    }

    @SubscribeEvent
    public static void onRenderNameTag(net.neoforged.neoforge.client.event.RenderNameTagEvent event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.player.Player targetPlayer) {
            if (RelationshipDataCache.status != RelationshipData.Status.NONE && RelationshipDataCache.partnerUuid != null) {
                if (targetPlayer.getUUID().equals(RelationshipDataCache.partnerUuid)) {
                    // Render a see-through heart above the partner's head
                    net.minecraft.client.renderer.MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                    com.mojang.blaze3d.vertex.PoseStack poseStack = event.getPoseStack();
                    poseStack.pushPose();
                    poseStack.translate(0.0D, targetPlayer.getBbHeight() + 0.8D, 0.0D);
                    poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
                    poseStack.scale(-0.025F, -0.025F, 0.025F);
                    
                    net.minecraft.network.chat.Component text = net.minecraft.network.chat.Component.literal("§d💕");
                    float offset = (float)(-Minecraft.getInstance().font.width(text) / 2);
                    
                    Minecraft.getInstance().font.drawInBatch(
                        text, offset, 0f, 0xFFFFFF, false, poseStack.last().pose(), event.getMultiBufferSource(),
                        net.minecraft.client.gui.Font.DisplayMode.SEE_THROUGH, 0, event.getPackedLight()
                    );
                    
                    poseStack.popPose();
                }
            }
        }
    }
}

package com.linceros.couplesmod.client;

import com.linceros.couplesmod.client.network.ClientPacketHandlers;
import com.linceros.couplesmod.client.particle.HeartParticle;
import com.linceros.couplesmod.init.CouplesParticles;
import com.linceros.couplesmod.init.CouplesItems;
import com.linceros.couplesmod.network.SyncRelationshipPacket;
import com.linceros.couplesmod.attachment.RelationshipData;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class CouplesModClient implements ClientModInitializer {
    public static KeyMapping openRelationshipGui;

    @Override
    public void onInitializeClient() {
        openRelationshipGui = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.couplesmod.open_gui",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openRelationshipGui.consumeClick()) {
                boolean lookingAtPartner = false;
                if (client.hitResult != null && client.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.ENTITY) {
                    net.minecraft.world.phys.EntityHitResult entityHit = (net.minecraft.world.phys.EntityHitResult) client.hitResult;
                    if (entityHit.getEntity() instanceof net.minecraft.world.entity.player.Player targetPlayer) {
                        if (RelationshipDataCache.partnerUuid != null && targetPlayer.getUUID().equals(RelationshipDataCache.partnerUuid)) {
                            lookingAtPartner = true;
                        }
                    }
                }
                client.setScreen(new RelationshipScreen(lookingAtPartner));
            }
        });

        ParticleProviderRegistry.getInstance().register(CouplesParticles.HEART_PARTICLE, HeartParticle.Provider::new);

        ClientPlayNetworking.registerGlobalReceiver(SyncRelationshipPacket.TYPE, ClientPacketHandlers::handleSync);

        UseItemCallback.EVENT.register((player, level, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (level.isClientSide() && stack.getItem() == CouplesItems.ANNIVERSARY_JOURNAL) {
                RelationshipData data = RelationshipDataCache.status != RelationshipData.Status.NONE 
                    ? new RelationshipData(
                        RelationshipDataCache.status, 
                        java.util.Optional.ofNullable(RelationshipDataCache.partnerUuid), 
                        RelationshipDataCache.level, 
                        RelationshipDataCache.xp, 
                        RelationshipDataCache.lastGiftTime, 
                        RelationshipDataCache.lastKissTime, 
                        RelationshipDataCache.startDate, 
                        RelationshipDataCache.kisses, 
                        RelationshipDataCache.gifts, 
                        RelationshipDataCache.sharedBeds) 
                    : RelationshipData.EMPTY;
                
                if (data.status() != RelationshipData.Status.NONE) {
                    Minecraft.getInstance().setScreen(new JournalScreen(data));
                }
                if (RelationshipDataCache.status == RelationshipData.Status.NONE) {
                    player.sendSystemMessage(Component.literal("§cYou are not in a relationship yet!"));
                } else {
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        });
    }
}

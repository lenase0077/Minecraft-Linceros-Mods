package com.linceros.couplesmod.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class CouplesNetworking {
    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(SyncRelationshipPacket.TYPE, SyncRelationshipPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SendGiftPacket.TYPE, SendGiftPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SendGiftPacket.TYPE, PacketHandlers::handleGift);
    }
}

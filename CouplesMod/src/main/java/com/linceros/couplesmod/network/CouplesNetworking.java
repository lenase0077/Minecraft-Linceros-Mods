package com.linceros.couplesmod.network;

import com.linceros.couplesmod.CouplesMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CouplesMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CouplesNetworking {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(CouplesMod.MODID).versioned("1.0.0");

        registrar.playToClient(
                SyncRelationshipPacket.TYPE,
                SyncRelationshipPacket.STREAM_CODEC,
                PacketHandlers::handleSync
        );

        registrar.playToServer(
                SendGiftPacket.TYPE,
                SendGiftPacket.STREAM_CODEC,
                PacketHandlers::handleGift
        );
    }
}

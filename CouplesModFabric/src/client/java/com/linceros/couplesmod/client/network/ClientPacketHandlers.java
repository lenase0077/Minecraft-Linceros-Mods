package com.linceros.couplesmod.client.network;

import com.linceros.couplesmod.client.RelationshipDataCache;
import com.linceros.couplesmod.network.SyncRelationshipPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientPacketHandlers {
    public static void handleSync(SyncRelationshipPacket payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            RelationshipDataCache.status = payload.status();
            RelationshipDataCache.partnerUuid = payload.partnerUuid().orElse(null);
            RelationshipDataCache.level = payload.level();
            RelationshipDataCache.xp = payload.xp();
            RelationshipDataCache.startDate = payload.startDate();
            RelationshipDataCache.kisses = payload.kisses();
            RelationshipDataCache.gifts = payload.gifts();
            RelationshipDataCache.sharedBeds = payload.sharedBeds();
        });
    }
}

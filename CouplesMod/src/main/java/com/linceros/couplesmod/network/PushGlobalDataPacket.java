package com.linceros.couplesmod.network;

import com.linceros.couplesmod.CouplesMod;
import com.linceros.couplesmod.attachment.RelationshipData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.UUID;

public record PushGlobalDataPacket(RelationshipData.Status status, Optional<UUID> partnerUuid, int level, int xp, long lastGiftTime, long lastKissTime, long startDate, int kisses, int gifts, int sharedBeds) implements CustomPacketPayload {
    public static final Type<PushGlobalDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CouplesMod.MODID, "push_global_data"));

    public static final StreamCodec<FriendlyByteBuf, PushGlobalDataPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> packet.write(buf),
            PushGlobalDataPacket::new
    );

    public PushGlobalDataPacket(FriendlyByteBuf buf) {
        this(buf.readEnum(RelationshipData.Status.class),
             buf.readBoolean() ? Optional.of(buf.readUUID()) : Optional.empty(),
             buf.readInt(), buf.readInt(), buf.readLong(), buf.readLong(), buf.readLong(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(status);
        buf.writeBoolean(partnerUuid.isPresent());
        partnerUuid.ifPresent(buf::writeUUID);
        buf.writeInt(level);
        buf.writeInt(xp);
        buf.writeLong(lastGiftTime);
        buf.writeLong(lastKissTime);
        buf.writeLong(startDate);
        buf.writeInt(kisses);
        buf.writeInt(gifts);
        buf.writeInt(sharedBeds);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

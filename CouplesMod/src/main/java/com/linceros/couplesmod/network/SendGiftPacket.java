package com.linceros.couplesmod.network;

import com.linceros.couplesmod.CouplesMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SendGiftPacket() implements CustomPacketPayload {
    public static final Type<SendGiftPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CouplesMod.MODID, "send_gift"));

    public static final StreamCodec<FriendlyByteBuf, SendGiftPacket> STREAM_CODEC = StreamCodec.unit(new SendGiftPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

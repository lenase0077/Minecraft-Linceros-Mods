package com.linceros.couplesmod.init;

import com.linceros.couplesmod.CouplesMod;
import com.linceros.couplesmod.attachment.RelationshipData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;

public class CouplesAttachments {

    public static final AttachmentType<RelationshipData> RELATIONSHIP = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(CouplesMod.MODID, "relationship"),
            builder -> builder.initializer(() -> RelationshipData.EMPTY)
                    .persistent(RelationshipData.CODEC)
                    .copyOnDeath()
    );

    public static void initialize() {}
}

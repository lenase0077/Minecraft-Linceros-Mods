package com.linceros.couplesmod.init;

import com.linceros.couplesmod.CouplesMod;
import com.linceros.couplesmod.attachment.RelationshipData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

public class CouplesAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, CouplesMod.MODID);

    public static final Supplier<AttachmentType<RelationshipData>> RELATIONSHIP = ATTACHMENT_TYPES.register(
            "relationship",
            () -> AttachmentType.builder(() -> RelationshipData.EMPTY)
                    .serialize(RelationshipData.CODEC)
                    .copyOnDeath() // Persist relationship when the player dies
                    .build()
    );
}

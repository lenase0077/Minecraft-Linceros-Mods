package com.linceros.couplesmod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;
import com.linceros.couplesmod.attachment.RelationshipData;
import com.linceros.couplesmod.init.CouplesAttachments;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class EngagementRingItem extends Item implements ICurioItem {

    public EngagementRingItem(Properties properties) {
        super(properties);
    }

}

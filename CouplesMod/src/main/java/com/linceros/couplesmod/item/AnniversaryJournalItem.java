package com.linceros.couplesmod.item;

import com.linceros.couplesmod.attachment.RelationshipData;
import com.linceros.couplesmod.client.RelationshipDataCache;
import com.linceros.couplesmod.client.JournalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.Optional;

public class AnniversaryJournalItem extends Item {
    public AnniversaryJournalItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide()) {
            RelationshipData data = RelationshipDataCache.status != RelationshipData.Status.NONE 
                ? new RelationshipData(
                    RelationshipDataCache.status, 
                    Optional.ofNullable(RelationshipDataCache.partnerUuid), 
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
            } else {
                player.displayClientMessage(Component.literal("§cYou are not in a relationship yet!"), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
    }
}

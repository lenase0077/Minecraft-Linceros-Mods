package com.linceros.couplesmod.network;

import com.linceros.couplesmod.attachment.RelationshipData;
import com.linceros.couplesmod.client.RelationshipDataCache;
import com.linceros.couplesmod.init.CouplesAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketHandlers {
    public static void handleSync(SyncRelationshipPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
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



    public static void handleGift(SendGiftPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sender) {
                RelationshipData data = sender.getData(CouplesAttachments.RELATIONSHIP);
                if (data.status() != RelationshipData.Status.NONE && data.partnerUuid().isPresent()) {
                    ServerPlayer partner = sender.server.getPlayerList().getPlayer(data.partnerUuid().get());
                    if (partner != null) {
                        ItemStack gift = sender.getMainHandItem();
                        if (gift.isEmpty()) {
                            sender.sendSystemMessage(Component.literal("§cYou must hold the item you want to gift in your main hand!"));
                            return;
                        }
                        
                        // Check if it's food and add the component
                        ItemStack giftStack = gift.copy();
                        if (giftStack.has(net.minecraft.core.component.DataComponents.FOOD)) {
                            giftStack.set(com.linceros.couplesmod.init.CouplesComponents.GIFTED_FOOD.get(), true);
                            
                            net.minecraft.world.item.component.ItemLore lore = giftStack.getOrDefault(net.minecraft.core.component.DataComponents.LORE, net.minecraft.world.item.component.ItemLore.EMPTY);
                            java.util.List<Component> loreLines = new java.util.ArrayList<>(lore.lines());
                            loreLines.add(Component.literal("§d💝 A gift from your love 💝"));
                            giftStack.set(net.minecraft.core.component.DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(loreLines));
                        }

                        // Try to give to partner
                        if (partner.getInventory().add(giftStack)) {
                            int baseRarityXp = switch (gift.getRarity()) {
                                case COMMON -> 25;
                                case UNCOMMON -> 50;
                                case RARE -> 100;
                                case EPIC -> 200;
                            };
                            int xpGain = baseRarityXp + gift.getCount();
                            gift.shrink(gift.getCount());
                            sender.sendSystemMessage(Component.literal("§aGift successfully sent to " + partner.getName().getString() + "!"));
                            partner.sendSystemMessage(Component.literal("§d🎁 " + sender.getName().getString() + " sent you a gift!"));
                            
                            // Check cooldown and grant XP
                            long currentTime = System.currentTimeMillis();
                            boolean xpBoost = (currentTime - data.lastGiftTime() >= 1200000L); // 20 minutes (1 MC Day)
                            
                            int newXp = xpBoost ? data.xp() + xpGain : data.xp();
                            int newLevel = data.level();
                            boolean leveledUp = false;
                            
                            if (xpBoost) {
                                while (newXp >= newLevel * 100) {
                                    newXp -= newLevel * 100;
                                    newLevel++;
                                    leveledUp = true;
                                }
                            }
                            
                            long newGiftTime = xpBoost ? currentTime : data.lastGiftTime();
                            
                            RelationshipData updatedData = data.withXp(newLevel, newXp).withGiftTime(newGiftTime).withGifts(data.gifts() + 1);
                            sender.setData(CouplesAttachments.RELATIONSHIP, updatedData);
                            PacketDistributor.sendToPlayer(sender, new SyncRelationshipPacket(updatedData.status(), updatedData.partnerUuid(), updatedData.level(), updatedData.xp(), updatedData.lastGiftTime(), updatedData.lastKissTime(), updatedData.startDate(), updatedData.kisses(), updatedData.gifts(), updatedData.sharedBeds()));
                            
                            RelationshipData partnerData = partner.getData(CouplesAttachments.RELATIONSHIP);
                            RelationshipData updatedPartnerData = partnerData.withXp(newLevel, newXp).withGiftTime(newGiftTime).withGifts(partnerData.gifts() + 1);
                            partner.setData(CouplesAttachments.RELATIONSHIP, updatedPartnerData);
                            PacketDistributor.sendToPlayer(partner, new SyncRelationshipPacket(updatedPartnerData.status(), updatedPartnerData.partnerUuid(), updatedPartnerData.level(), updatedPartnerData.xp(), updatedPartnerData.lastGiftTime(), updatedPartnerData.lastKissTime(), updatedPartnerData.startDate(), updatedPartnerData.kisses(), updatedPartnerData.gifts(), updatedPartnerData.sharedBeds()));
                            
                            if (leveledUp) {
                                sender.sendSystemMessage(Component.literal("§d💕 Your relationship leveled up to " + newLevel + "! 💕"));
                                partner.sendSystemMessage(Component.literal("§d💕 Your relationship leveled up to " + newLevel + "! 💕"));
                                sender.serverLevel().playSound(null, sender.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
                                partner.serverLevel().playSound(null, partner.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
                            }
                        } else {
                            sender.sendSystemMessage(Component.literal("§c" + partner.getName().getString() + "'s inventory is full! Could not send gift."));
                        }
                    } else {
                        sender.sendSystemMessage(Component.literal("§cYour partner is currently offline."));
                    }
                }
            }
        });
    }
}

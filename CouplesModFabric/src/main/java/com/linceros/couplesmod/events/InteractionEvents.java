package com.linceros.couplesmod.events;

import com.linceros.couplesmod.attachment.RelationshipData;
import com.linceros.couplesmod.init.CouplesAttachments;
import com.linceros.couplesmod.init.CouplesItems;
import com.linceros.couplesmod.init.CouplesParticles;
import com.linceros.couplesmod.advancements.CouplesTriggers;
import com.linceros.couplesmod.network.SyncRelationshipPacket;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class InteractionEvents {

    public record PendingProposal(java.util.UUID proposer, long timestamp, boolean isMarriage) {}
    public static final java.util.Map<java.util.UUID, PendingProposal> PENDING_PROPOSALS = new java.util.concurrent.ConcurrentHashMap<>();

    public static void initialize() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof Player targetPlayer) {
                return onEntityInteract(player, targetPlayer, hand);
            }
            return InteractionResult.PASS;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (net.minecraft.server.level.ServerPlayer player : server.getPlayerList().getPlayers()) {
                onPlayerTick(player);
            }
        });

        EntitySleepEvents.STOP_SLEEPING.register((entity, blockPos) -> {
            if (entity instanceof net.minecraft.server.level.ServerPlayer player) {
                onPlayerWakeUp(player);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            onPlayerLogin(handler.player);
        });
    }

    private static InteractionResult onEntityInteract(Player player, Player targetPlayer, InteractionHand hand) {
        if (player.level().isClientSide()) return InteractionResult.PASS; // Only process on server

        ServerLevel serverLevel = (ServerLevel) player.level();
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        RelationshipData playerRel = player.getAttachedOrCreate(CouplesAttachments.RELATIONSHIP);
        RelationshipData targetRel = targetPlayer.getAttachedOrCreate(CouplesAttachments.RELATIONSHIP);
        long currentTime = System.currentTimeMillis();

        // 1. Shift + Right Click (Kiss mechanic & Proposal Accept)
        if (player.isShiftKeyDown() && itemStack.isEmpty()) {
            // Check if accepting proposal
            PendingProposal pending = PENDING_PROPOSALS.get(player.getUUID());
            if (pending != null && pending.proposer().equals(targetPlayer.getUUID())) {
                if (System.currentTimeMillis() - pending.timestamp() > 60000L) {
                    PENDING_PROPOSALS.remove(player.getUUID());
                    player.sendSystemMessage(Component.literal("§cThe proposal has expired."));
                } else {
                    // Check if proposer still has the item
                    boolean hasItem = false;
                    for (int i = 0; i < targetPlayer.getInventory().getContainerSize(); i++) {
                        ItemStack invStack = targetPlayer.getInventory().getItem(i);
                        if (pending.isMarriage() ? invStack.getItem() == CouplesItems.ENGAGEMENT_RING : isBouquet(invStack.getItem())) {
                            if (pending.isMarriage()) {
                                // For marriage, don't shrink! The proposer keeps their ring.
                                hasItem = true;
                            } else {
                                // For dating, consume the bouquet
                                invStack.shrink(1);
                                hasItem = true;
                            }
                            break;
                        }
                    }
                    
                    if (!hasItem) {
                        player.sendSystemMessage(Component.literal("§c" + targetPlayer.getName().getString() + " no longer has the proposal item!"));
                        PENDING_PROPOSALS.remove(player.getUUID());
                        return InteractionResult.FAIL;
                    }

                    // Process acceptance
                    long startDate = System.currentTimeMillis();
                    if (pending.isMarriage()) {
                        player.setAttached(CouplesAttachments.RELATIONSHIP, playerRel.withStatus(RelationshipData.Status.MARRIED));
                        targetPlayer.setAttached(CouplesAttachments.RELATIONSHIP, targetRel.withStatus(RelationshipData.Status.MARRIED));
                        syncRelationship(player);
                        syncRelationship(targetPlayer);
                        
                        ItemStack partnerRing = new ItemStack(CouplesItems.ENGAGEMENT_RING);
                        if (!player.getInventory().add(partnerRing)) {
                            player.drop(partnerRing, false);
                        }
                        
                        targetPlayer.sendSystemMessage(Component.literal("§b✨ " + player.getName().getString() + " accepted your marriage proposal! ✨"));
                        player.sendSystemMessage(Component.literal("§b✨ You are now married to " + targetPlayer.getName().getString() + "! ✨"));
                        spawnHearts(serverLevel, player, targetPlayer, 100);
                        serverLevel.playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
                        CouplesTriggers.GET_MARRIED.trigger((net.minecraft.server.level.ServerPlayer) player);
                        CouplesTriggers.GET_MARRIED.trigger((net.minecraft.server.level.ServerPlayer) targetPlayer);
                    } else {
                        player.setAttached(CouplesAttachments.RELATIONSHIP, playerRel.withStatus(RelationshipData.Status.DATING).withPartner(targetPlayer.getUUID()).withStartDate(startDate));
                        targetPlayer.setAttached(CouplesAttachments.RELATIONSHIP, targetRel.withStatus(RelationshipData.Status.DATING).withPartner(player.getUUID()).withStartDate(startDate));
                        syncRelationship(player);
                        syncRelationship(targetPlayer);
                        
                        targetPlayer.sendSystemMessage(Component.literal("§d" + player.getName().getString() + " accepted your dating proposal!"));
                        player.sendSystemMessage(Component.literal("§dYou are now dating " + targetPlayer.getName().getString() + "!"));
                        spawnHearts(serverLevel, player, targetPlayer, 30);
                        serverLevel.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
                    }
                    PENDING_PROPOSALS.remove(player.getUUID());
                }
                return InteractionResult.SUCCESS;
            }

            // Normal Kiss mechanic
            if (isPartner(playerRel, targetPlayer)) {
                if (currentTime - playerRel.lastKissTime() < 500L) {
                    return InteractionResult.SUCCESS; // Anti-spam for double packets
                }

                boolean giveXp = (currentTime - playerRel.lastKissTime() >= 3000L); // 3s anti-spam cooldown for XP
                int newXp = playerRel.xp();
                int newLevel = playerRel.level();
                boolean leveledUp = false;

                if (giveXp) {
                    newXp += 5;
                    if (newXp >= newLevel * 100) {
                        newLevel++;
                        newXp = 0;
                        leveledUp = true;
                    }
                }
                
                RelationshipData updatedData = playerRel.withXp(newLevel, newXp).withKissTime(currentTime).withKisses(playerRel.kisses() + 1);
                player.setAttached(CouplesAttachments.RELATIONSHIP, updatedData);
                syncRelationship(player);

                RelationshipData targetUpdatedData = targetRel.withXp(newLevel, newXp).withKissTime(currentTime).withKisses(targetRel.kisses() + 1);
                targetPlayer.setAttached(CouplesAttachments.RELATIONSHIP, targetUpdatedData);
                syncRelationship(targetPlayer);

                if (leveledUp) {
                    player.sendSystemMessage(Component.literal("§d💕 Your relationship leveled up to " + newLevel + "! 💕"));
                    targetPlayer.sendSystemMessage(Component.literal("§d💕 Your relationship leveled up to " + newLevel + "! 💕"));
                    serverLevel.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
                }

                // Kiss!
                spawnHearts(serverLevel, player, targetPlayer, 3);
                serverLevel.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 1.0f);
                CouplesTriggers.KISS_PARTNER.trigger((net.minecraft.server.level.ServerPlayer) player);
                CouplesTriggers.KISS_PARTNER.trigger((net.minecraft.server.level.ServerPlayer) targetPlayer);
                return InteractionResult.SUCCESS;
            }
        }

        // 2. Bouquet (Proposal to Date)
        if (isBouquet(itemStack.getItem())) {
            PendingProposal existing = PENDING_PROPOSALS.get(targetPlayer.getUUID());
            if (existing != null && existing.proposer().equals(player.getUUID()) && (currentTime - existing.timestamp() < 1000L)) {
                return InteractionResult.SUCCESS; // Anti-spam
            }

            if (playerRel.status() == RelationshipData.Status.NONE && targetRel.status() == RelationshipData.Status.NONE) {
                PENDING_PROPOSALS.put(targetPlayer.getUUID(), new PendingProposal(player.getUUID(), currentTime, false));
                
                player.sendSystemMessage(Component.literal("§dYou proposed to " + targetPlayer.getName().getString() + "! Waiting for their response..."));
                targetPlayer.sendSystemMessage(Component.literal("§d" + player.getName().getString() + " asked you out! Shift + Right Click them with an empty hand to accept!"));
                
                spawnHearts(serverLevel, player, targetPlayer, 5);
                return InteractionResult.SUCCESS;
            } else if (playerRel.status() != RelationshipData.Status.NONE) {
                player.sendSystemMessage(Component.literal("§cYou are already in a relationship!"));
                return InteractionResult.FAIL;
            } else {
                player.sendSystemMessage(Component.literal("§cThis person is already in a relationship!"));
                return InteractionResult.FAIL;
            }
        }

        // 3. Ring (Marriage Proposal)
        if (itemStack.getItem() == CouplesItems.ENGAGEMENT_RING) {
            PendingProposal existing = PENDING_PROPOSALS.get(targetPlayer.getUUID());
            if (existing != null && existing.proposer().equals(player.getUUID()) && (currentTime - existing.timestamp() < 1000L)) {
                return InteractionResult.SUCCESS; // Anti-spam
            }

            if (playerRel.status() == RelationshipData.Status.DATING && isPartner(playerRel, targetPlayer)) {
                if (playerRel.level() < 10) {
                    player.sendSystemMessage(Component.literal("§cYour relationship must be at least Level 10 to propose!"));
                    return InteractionResult.FAIL;
                }
                
                PENDING_PROPOSALS.put(targetPlayer.getUUID(), new PendingProposal(player.getUUID(), currentTime, true));
                
                player.sendSystemMessage(Component.literal("§b✨ You proposed to " + targetPlayer.getName().getString() + "! Waiting for their response..."));
                targetPlayer.sendSystemMessage(Component.literal("§b✨ " + player.getName().getString() + " proposed marriage! Shift + Right Click them with an empty hand to accept!"));
                
                spawnHearts(serverLevel, player, targetPlayer, 10);
                return InteractionResult.SUCCESS;
            } else {
                player.sendSystemMessage(Component.literal("§cYou must be dating this person to propose!"));
                return InteractionResult.FAIL;
            }
        }
        
        return InteractionResult.PASS;
    }

    private static void onPlayerTick(net.minecraft.server.level.ServerPlayer player) {
        if (player.tickCount % 20 != 0) return;

        RelationshipData rel = player.getAttachedOrCreate(CouplesAttachments.RELATIONSHIP);
        if (rel.status() == RelationshipData.Status.MARRIED && rel.partnerUuid().isPresent()) {
            java.util.UUID partnerUuid = rel.partnerUuid().get();
            // Check for partner within 32 blocks
            java.util.List<net.minecraft.server.level.ServerPlayer> players = ((net.minecraft.server.level.ServerLevel)player.level()).getPlayers(p -> 
                p.getUUID().equals(partnerUuid) && p.distanceTo(player) <= 32.0D
            );
            if (!players.isEmpty()) {
                // Apply effects for 15 seconds (300 ticks)
                player.addEffect(new MobEffectInstance(MobEffects.HASTE, 300, 1, false, false, true)); // Haste 2
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 0, false, false, true)); // Regen 1
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, 0, false, false, true)); // Speed 1
                player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 300, 1, false, false, true)); // Health Boost 2
                player.addEffect(new MobEffectInstance(MobEffects.LUCK, 300, 0, false, false, true)); // Luck 1
            }
        }
    }

    private static void onPlayerWakeUp(net.minecraft.server.level.ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        // Check if they woke up successfully (morning time)
        if (level.getOverworldClockTime() % 24000L < 12000L) {
            RelationshipData rel = player.getAttachedOrCreate(CouplesAttachments.RELATIONSHIP);
            if (rel.status() != RelationshipData.Status.NONE && rel.partnerUuid().isPresent()) {
                java.util.UUID partnerUuid = rel.partnerUuid().get();
                net.minecraft.server.level.ServerPlayer partner = level.getServer().getPlayerList().getPlayer(partnerUuid);
                if (partner != null && partner.level() == level) {
                    // Check if partner is also waking up / recently slept and is within 10 blocks
                    if (partner.distanceTo(player) <= 10.0D) {
                        // Apply buff to the player who triggered the event (the partner's event will also fire)
                        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 12000, 0, false, false, true)); // 10 minutes
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 12000, 0, false, false, true)); // 10 minutes
                        
                        if (player.getUUID().compareTo(partnerUuid) > 0) {
                            player.setAttached(CouplesAttachments.RELATIONSHIP, rel.withSharedBeds(rel.sharedBeds() + 1));
                            syncRelationship(player);
                            RelationshipData partnerRel = partner.getAttachedOrCreate(CouplesAttachments.RELATIONSHIP);
                            partner.setAttached(CouplesAttachments.RELATIONSHIP, partnerRel.withSharedBeds(partnerRel.sharedBeds() + 1));
                            syncRelationship(partner);
                        }
                        
                        player.sendSystemMessage(Component.literal("§d💕 You feel well rested after sleeping next to your partner! 💕"));
                    }
                }
            }
        }
    }

    private static boolean isPartner(RelationshipData data, Player target) {
        return data.partnerUuid().isPresent() && data.partnerUuid().get().equals(target.getUUID());
    }

    private static boolean isBouquet(Item item) {
        return item == CouplesItems.DANDELION_BOUQUET || item == CouplesItems.POPPY_BOUQUET || 
               item == CouplesItems.BLUE_ORCHID_BOUQUET || item == CouplesItems.ALLIUM_BOUQUET || 
               item == CouplesItems.RED_TULIP_BOUQUET || item == CouplesItems.ORANGE_TULIP_BOUQUET || 
               item == CouplesItems.WHITE_TULIP_BOUQUET || item == CouplesItems.PINK_TULIP_BOUQUET || 
               item == CouplesItems.OXEYE_DAISY_BOUQUET || item == CouplesItems.CORNFLOWER_BOUQUET || 
               item == CouplesItems.LILY_OF_THE_VALLEY_BOUQUET || item == CouplesItems.WITHER_ROSE_BOUQUET || 
               item == CouplesItems.SUNFLOWER_BOUQUET || item == CouplesItems.LILAC_BOUQUET || item == CouplesItems.ROSE_BUSH_BOUQUET;
    }

    private static void spawnHearts(ServerLevel level, Player p1, Player p2, int count) {
        double x = (p1.getX() + p2.getX()) / 2.0;
        double y = (p1.getY() + p2.getY()) / 2.0 + 1.0;
        double z = (p1.getZ() + p2.getZ()) / 2.0;
        level.sendParticles(CouplesParticles.HEART_PARTICLE, x, y, z, count, 0.5, 0.5, 0.5, 0.1);
    }

    private static void onPlayerLogin(net.minecraft.server.level.ServerPlayer player) {
        syncRelationship(player);
    }

    private static void syncRelationship(Player player) {
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            RelationshipData data = sp.getAttachedOrCreate(CouplesAttachments.RELATIONSHIP);
            ServerPlayNetworking.send(sp, new SyncRelationshipPacket(
                data.status(), data.partnerUuid(), data.level(), data.xp(), data.lastGiftTime(), data.lastKissTime(), data.startDate(), data.kisses(), data.gifts(), data.sharedBeds()
            ));
        }
    }
}

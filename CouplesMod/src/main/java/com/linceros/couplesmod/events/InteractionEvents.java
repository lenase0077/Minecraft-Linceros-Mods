package com.linceros.couplesmod.events;

import com.linceros.couplesmod.CouplesMod;
import com.linceros.couplesmod.attachment.RelationshipData;
import com.linceros.couplesmod.init.CouplesAttachments;
import com.linceros.couplesmod.init.CouplesItems;
import com.linceros.couplesmod.init.CouplesParticles;
import com.linceros.couplesmod.advancements.CouplesTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import com.linceros.couplesmod.init.CouplesComponents;
import com.linceros.couplesmod.network.SyncRelationshipPacket;

@EventBusSubscriber(modid = CouplesMod.MODID)
public class InteractionEvents {

    public record PendingProposal(java.util.UUID proposer, long timestamp, boolean isMarriage) {}
    public static final java.util.Map<java.util.UUID, PendingProposal> PENDING_PROPOSALS = new java.util.concurrent.ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Player targetPlayer)) return;
        
        Player player = event.getEntity();
        if (player.level().isClientSide()) return; // Only process on server

        ServerLevel serverLevel = (ServerLevel) player.level();
        ItemStack itemStack = event.getItemStack();
        InteractionHand hand = event.getHand();
        
        if (hand != InteractionHand.MAIN_HAND) return;

        RelationshipData playerRel = player.getData(CouplesAttachments.RELATIONSHIP);
        RelationshipData targetRel = targetPlayer.getData(CouplesAttachments.RELATIONSHIP);

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
                        if (pending.isMarriage() ? invStack.getItem() == CouplesItems.ENGAGEMENT_RING.get() : isBouquet(invStack.getItem())) {
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
                        event.setCancellationResult(InteractionResult.FAIL);
                        event.setCanceled(true);
                        return;
                    }

                    // Process acceptance
                    long startDate = System.currentTimeMillis();
                    if (pending.isMarriage()) {
                        player.setData(CouplesAttachments.RELATIONSHIP, playerRel.withStatus(RelationshipData.Status.MARRIED));
                        targetPlayer.setData(CouplesAttachments.RELATIONSHIP, targetRel.withStatus(RelationshipData.Status.MARRIED));
                        syncRelationship(player);
                        syncRelationship(targetPlayer);
                        
                        ItemStack partnerRing = new ItemStack(CouplesItems.ENGAGEMENT_RING.get());
                        if (!player.getInventory().add(partnerRing)) {
                            player.drop(partnerRing, false);
                        }
                        
                        targetPlayer.sendSystemMessage(Component.literal("§b✨ " + player.getName().getString() + " accepted your marriage proposal! ✨"));
                        player.sendSystemMessage(Component.literal("§b✨ You are now married to " + targetPlayer.getName().getString() + "! ✨"));
                        spawnHearts(serverLevel, player, targetPlayer, 100);
                        serverLevel.playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
                        CouplesTriggers.GET_MARRIED.get().trigger((net.minecraft.server.level.ServerPlayer) player);
                        CouplesTriggers.GET_MARRIED.get().trigger((net.minecraft.server.level.ServerPlayer) targetPlayer);
                    } else {
                        player.setData(CouplesAttachments.RELATIONSHIP, playerRel.withStatus(RelationshipData.Status.DATING).withPartner(targetPlayer.getUUID()).withStartDate(startDate));
                        targetPlayer.setData(CouplesAttachments.RELATIONSHIP, targetRel.withStatus(RelationshipData.Status.DATING).withPartner(player.getUUID()).withStartDate(startDate));
                        syncRelationship(player);
                        syncRelationship(targetPlayer);
                        
                        targetPlayer.sendSystemMessage(Component.literal("§d" + player.getName().getString() + " accepted your dating proposal!"));
                        player.sendSystemMessage(Component.literal("§dYou are now dating " + targetPlayer.getName().getString() + "!"));
                        spawnHearts(serverLevel, player, targetPlayer, 30);
                        serverLevel.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
                    }
                    PENDING_PROPOSALS.remove(player.getUUID());
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            // Normal Kiss mechanic
            if (isPartner(playerRel, targetPlayer)) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - playerRel.lastKissTime() >= 3000L) { // 3s anti-spam cooldown for XP
                    int newXp = playerRel.xp() + 5;
                    int newLevel = playerRel.level();
                    boolean leveledUp = false;
                    if (newXp >= newLevel * 100) {
                        newLevel++;
                        newXp = 0;
                        leveledUp = true;
                    }
                    RelationshipData updatedData = playerRel.withXp(newLevel, newXp).withKissTime(currentTime);
                    player.setData(CouplesAttachments.RELATIONSHIP, updatedData);
                    syncRelationship(player);

                    RelationshipData targetUpdatedData = targetRel.withXp(newLevel, newXp).withKissTime(currentTime);
                    targetPlayer.setData(CouplesAttachments.RELATIONSHIP, targetUpdatedData);
                    syncRelationship(targetPlayer);

                    if (leveledUp) {
                        player.sendSystemMessage(Component.literal("§d💕 Your relationship leveled up to " + newLevel + "! 💕"));
                        targetPlayer.sendSystemMessage(Component.literal("§d💕 Your relationship leveled up to " + newLevel + "! 💕"));
                        serverLevel.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
                    }
                }
                
                // Increment kisses stat and sync unconditionally
                RelationshipData latestData = player.getData(CouplesAttachments.RELATIONSHIP);
                RelationshipData targetLatestData = targetPlayer.getData(CouplesAttachments.RELATIONSHIP);
                player.setData(CouplesAttachments.RELATIONSHIP, latestData.withKisses(latestData.kisses() + 1));
                targetPlayer.setData(CouplesAttachments.RELATIONSHIP, targetLatestData.withKisses(targetLatestData.kisses() + 1));
                syncRelationship(player);
                syncRelationship(targetPlayer);

                // Kiss!
                spawnHearts(serverLevel, player, targetPlayer, 3);
                serverLevel.playSound(null, player.blockPosition(), SoundEvents.CAT_PURREOW, SoundSource.PLAYERS, 1.0f, 1.0f);
                CouplesTriggers.KISS_PARTNER.get().trigger((net.minecraft.server.level.ServerPlayer) player);
                CouplesTriggers.KISS_PARTNER.get().trigger((net.minecraft.server.level.ServerPlayer) targetPlayer);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }

        // 2. Bouquet (Proposal to Date)
        if (isBouquet(itemStack.getItem())) {
            if (playerRel.status() == RelationshipData.Status.NONE && targetRel.status() == RelationshipData.Status.NONE) {
                PENDING_PROPOSALS.put(targetPlayer.getUUID(), new PendingProposal(player.getUUID(), System.currentTimeMillis(), false));
                
                player.sendSystemMessage(Component.literal("§dYou proposed to " + targetPlayer.getName().getString() + "! Waiting for their response..."));
                targetPlayer.sendSystemMessage(Component.literal("§d" + player.getName().getString() + " asked you out! Shift + Right Click them with an empty hand to accept!"));
                
                spawnHearts(serverLevel, player, targetPlayer, 5);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            } else if (playerRel.status() != RelationshipData.Status.NONE) {
                player.sendSystemMessage(Component.literal("§cYou are already in a relationship!"));
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            } else {
                player.sendSystemMessage(Component.literal("§cThis person is already in a relationship!"));
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
        }

        // 3. Ring (Marriage Proposal)
        if (itemStack.getItem() == CouplesItems.ENGAGEMENT_RING.get()) {
            if (playerRel.status() == RelationshipData.Status.DATING && isPartner(playerRel, targetPlayer)) {
                if (playerRel.level() < 10) {
                    player.sendSystemMessage(Component.literal("§cYour relationship must be at least Level 10 to propose!"));
                    event.setCancellationResult(InteractionResult.FAIL);
                    event.setCanceled(true);
                    return;
                }
                
                PENDING_PROPOSALS.put(targetPlayer.getUUID(), new PendingProposal(player.getUUID(), System.currentTimeMillis(), true));
                
                player.sendSystemMessage(Component.literal("§b✨ You proposed to " + targetPlayer.getName().getString() + "! Waiting for their response..."));
                targetPlayer.sendSystemMessage(Component.literal("§b✨ " + player.getName().getString() + " proposed marriage! Shift + Right Click them with an empty hand to accept!"));
                
                spawnHearts(serverLevel, player, targetPlayer, 10);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            } else {
                player.sendSystemMessage(Component.literal("§cYou must be dating this person to propose!"));
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
        net.minecraft.world.entity.player.Player playerEntity = event.getEntity();
        if (playerEntity.level().isClientSide || playerEntity.tickCount % 20 != 0) return;

        net.minecraft.server.level.ServerPlayer player = (net.minecraft.server.level.ServerPlayer) playerEntity;
        RelationshipData rel = player.getData(CouplesAttachments.RELATIONSHIP);
        if (rel.status() == RelationshipData.Status.MARRIED && rel.partnerUuid().isPresent()) {
            java.util.UUID partnerUuid = rel.partnerUuid().get();
            // Check for partner within 32 blocks
            java.util.List<net.minecraft.server.level.ServerPlayer> players = player.serverLevel().getPlayers(p -> 
                p.getUUID().equals(partnerUuid) && p.distanceTo(player) <= 32.0D
            );
            if (!players.isEmpty()) {
                // Apply effects for 15 seconds (300 ticks)
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 300, 1, false, false, true)); // Haste 2
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 0, false, false, true)); // Regen 1
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 0, false, false, true)); // Speed 1
                player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 300, 1, false, false, true)); // Health Boost 2
                player.addEffect(new MobEffectInstance(MobEffects.LUCK, 300, 0, false, false, true)); // Luck 1
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            // Check if they woke up successfully (morning time)
            if (level.isDay()) {
                RelationshipData rel = player.getData(CouplesAttachments.RELATIONSHIP);
                if (rel.status() != RelationshipData.Status.NONE && rel.partnerUuid().isPresent()) {
                    java.util.UUID partnerUuid = rel.partnerUuid().get();
                    net.minecraft.server.level.ServerPlayer partner = level.getServer().getPlayerList().getPlayer(partnerUuid);
                    if (partner != null && partner.level() == level) {
                        // Check if partner is also waking up / recently slept and is within 10 blocks
                        if (partner.distanceTo(player) <= 10.0D) {
                            // Apply buff to the player who triggered the event (the partner's event will also fire)
                            player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 12000, 0, false, false, true)); // 10 minutes
                            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 12000, 0, false, false, true)); // 10 minutes
                            
                            // Only increment sharedBeds once per night, but since both wake up, 
                            // we'll let each increment their own stat. Wait, they share stats? 
                            // Yes, they should have the same stat, but we increment it on both.
                            // To prevent double counting, maybe we only increment if player's UUID > partner's UUID.
                            if (player.getUUID().compareTo(partnerUuid) > 0) {
                                player.setData(CouplesAttachments.RELATIONSHIP, rel.withSharedBeds(rel.sharedBeds() + 1));
                                syncRelationship(player);
                                RelationshipData partnerRel = partner.getData(CouplesAttachments.RELATIONSHIP);
                                partner.setData(CouplesAttachments.RELATIONSHIP, partnerRel.withSharedBeds(partnerRel.sharedBeds() + 1));
                                syncRelationship(partner);
                            }
                            
                            player.sendSystemMessage(Component.literal("§d💕 You feel well rested after sleeping next to your partner! 💕"));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            ItemStack stack = event.getItem();
            if (stack.has(CouplesComponents.GIFTED_FOOD.get())) {
                net.minecraft.world.food.FoodProperties food = stack.get(net.minecraft.core.component.DataComponents.FOOD);
                if (food != null) {
                    float extraSaturation = food.nutrition() * food.saturation() * 2.0f * 0.5f;
                    player.getFoodData().setSaturation(Math.min(player.getFoodData().getSaturationLevel() + extraSaturation, player.getFoodData().getFoodLevel()));
                    
                    if (!player.level().isClientSide) {
                        player.sendSystemMessage(Component.literal("§d💝 You ate a delicious gift! 💝"));
                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
                    }          
                }
            }
        }
    }

    private static boolean isPartner(RelationshipData data, Player target) {
        return data.partnerUuid().isPresent() && data.partnerUuid().get().equals(target.getUUID());
    }

    private static boolean isBouquet(Item item) {
        return item == CouplesItems.DANDELION_BOUQUET.get() || item == CouplesItems.POPPY_BOUQUET.get() || 
               item == CouplesItems.BLUE_ORCHID_BOUQUET.get() || item == CouplesItems.ALLIUM_BOUQUET.get() || 
               item == CouplesItems.RED_TULIP_BOUQUET.get() || item == CouplesItems.ORANGE_TULIP_BOUQUET.get() || 
               item == CouplesItems.WHITE_TULIP_BOUQUET.get() || item == CouplesItems.PINK_TULIP_BOUQUET.get() || 
               item == CouplesItems.OXEYE_DAISY_BOUQUET.get() || item == CouplesItems.CORNFLOWER_BOUQUET.get() || 
               item == CouplesItems.LILY_OF_THE_VALLEY_BOUQUET.get() || item == CouplesItems.WITHER_ROSE_BOUQUET.get() || 
               item == CouplesItems.SUNFLOWER_BOUQUET.get() || item == CouplesItems.LILAC_BOUQUET.get() || item == CouplesItems.ROSE_BUSH_BOUQUET.get();
    }

    private static void spawnHearts(ServerLevel level, Player p1, Player p2, int count) {
        double x = (p1.getX() + p2.getX()) / 2.0;
        double y = (p1.getY() + p2.getY()) / 2.0 + 1.0;
        double z = (p1.getZ() + p2.getZ()) / 2.0;
        level.sendParticles(CouplesParticles.HEART_PARTICLE.get(), x, y, z, count, 0.5, 0.5, 0.5, 0.1);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            syncRelationship(event.getEntity());
        }
    }

    private static void syncRelationship(Player player) {
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            RelationshipData data = sp.getData(CouplesAttachments.RELATIONSHIP);
            PacketDistributor.sendToPlayer(sp, new SyncRelationshipPacket(
                data.status(), data.partnerUuid(), data.level(), data.xp(), data.lastGiftTime(), data.lastKissTime(), data.startDate(), data.kisses(), data.gifts(), data.sharedBeds()
            ));
        }
    }
}

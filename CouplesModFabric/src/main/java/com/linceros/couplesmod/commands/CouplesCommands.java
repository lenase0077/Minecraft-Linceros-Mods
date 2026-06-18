package com.linceros.couplesmod.commands;

import com.linceros.couplesmod.CouplesMod;
import com.linceros.couplesmod.attachment.RelationshipData;
import com.linceros.couplesmod.init.CouplesAttachments;
import com.linceros.couplesmod.network.SyncRelationshipPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CouplesCommands {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("couples")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("setlevel")
                        .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                .executes(ctx -> setLevel(ctx, IntegerArgumentType.getInteger(ctx, "level"), ctx.getSource().getPlayerOrException()))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(ctx -> setLevel(ctx, IntegerArgumentType.getInteger(ctx, "level"), EntityArgument.getPlayer(ctx, "target")))
                                )
                        )
                )
                .then(Commands.literal("addxp")
                        .then(Commands.argument("xp", IntegerArgumentType.integer(1))
                                .executes(ctx -> addXp(ctx, IntegerArgumentType.getInteger(ctx, "xp"), ctx.getSource().getPlayerOrException()))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(ctx -> addXp(ctx, IntegerArgumentType.getInteger(ctx, "xp"), EntityArgument.getPlayer(ctx, "target")))
                                )
                        )
                )
                .then(Commands.literal("resetcooldowns")
                        .executes(ctx -> resetCooldowns(ctx, ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> resetCooldowns(ctx, EntityArgument.getPlayer(ctx, "target")))
                        )
                )
                .then(Commands.literal("divorce")
                        .executes(ctx -> divorce(ctx, ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> divorce(ctx, EntityArgument.getPlayer(ctx, "target")))
                        )
                )
            );
        });
    }

    private static int setLevel(CommandContext<CommandSourceStack> ctx, int level, ServerPlayer target) {
        RelationshipData data = target.getAttachedOrCreate(CouplesAttachments.RELATIONSHIP);
        RelationshipData updated = data.withXp(level, data.xp());
        target.setAttached(CouplesAttachments.RELATIONSHIP, updated);
        sync(target, updated);
        ctx.getSource().sendSuccess(() -> Component.literal("Set relationship level of " + target.getName().getString() + " to " + level), true);
        return 1;
    }

    private static int addXp(CommandContext<CommandSourceStack> ctx, int xp, ServerPlayer target) {
        RelationshipData data = target.getAttachedOrCreate(CouplesAttachments.RELATIONSHIP);
        int newXp = data.xp() + xp;
        int newLevel = data.level();
        while (newXp >= newLevel * 100) {
            newXp -= newLevel * 100;
            newLevel++;
        }
        RelationshipData updated = data.withXp(newLevel, newXp);
        target.setAttached(CouplesAttachments.RELATIONSHIP, updated);
        sync(target, updated);
        ctx.getSource().sendSuccess(() -> Component.literal("Added " + xp + " XP to " + target.getName().getString()), true);
        return 1;
    }

    private static int resetCooldowns(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        RelationshipData data = target.getAttachedOrCreate(CouplesAttachments.RELATIONSHIP);
        RelationshipData updated = new RelationshipData(data.status(), data.partnerUuid(), data.level(), data.xp(), 0L, 0L, data.startDate(), data.kisses(), data.gifts(), data.sharedBeds());
        target.setAttached(CouplesAttachments.RELATIONSHIP, updated);
        sync(target, updated);
        ctx.getSource().sendSuccess(() -> Component.literal("Reset cooldowns for " + target.getName().getString()), true);
        return 1;
    }

    private static int divorce(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        RelationshipData data = target.getAttachedOrCreate(CouplesAttachments.RELATIONSHIP);
        if (data.partnerUuid().isPresent()) {
            java.util.UUID partnerUuid = data.partnerUuid().get();
            ServerPlayer partner = (ServerPlayer) target.serverLevel().getPlayerByUUID(partnerUuid);
            if (partner != null) {
                partner.setAttached(CouplesAttachments.RELATIONSHIP, RelationshipData.EMPTY);
                sync(partner, RelationshipData.EMPTY);
            }
        }
        target.setAttached(CouplesAttachments.RELATIONSHIP, RelationshipData.EMPTY);
        sync(target, RelationshipData.EMPTY);
        ctx.getSource().sendSuccess(() -> Component.literal("Divorced " + target.getName().getString()), true);
        return 1;
    }

    private static void sync(ServerPlayer target, RelationshipData data) {
        ServerPlayNetworking.send(target, new SyncRelationshipPacket(data.status(), data.partnerUuid(), data.level(), data.xp(), data.lastGiftTime(), data.lastKissTime(), data.startDate(), data.kisses(), data.gifts(), data.sharedBeds()));
    }
}

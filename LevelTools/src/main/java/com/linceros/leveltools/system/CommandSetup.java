package com.linceros.leveltools.system;

import com.linceros.leveltools.Config;
import com.linceros.leveltools.LevelToolsComponents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = "leveltools")
public class CommandSetup {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("leveltools")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("setlevel")
                .then(Commands.argument("level", IntegerArgumentType.integer(0))
                    .executes(context -> {
                        CommandSourceStack source = context.getSource();
                        ServerPlayer player = source.getPlayerOrException();
                        int newLevel = IntegerArgumentType.getInteger(context, "level");
                        
                        ItemStack mainHand = player.getMainHandItem();
                        if (mainHand.isEmpty()) {
                            source.sendFailure(Component.literal("You must hold an item in your main hand!"));
                            return 0;
                        }
                        
                        Integer baseMaxDamage = mainHand.getItem().components().get(DataComponents.MAX_DAMAGE);
                        if (baseMaxDamage == null || baseMaxDamage <= 0) {
                            source.sendFailure(Component.literal("This item does not have durability!"));
                            return 0;
                        }

                        mainHand.set(LevelToolsComponents.TOOL_LEVEL.get(), newLevel);
                        
                        float durabilityBonus = Config.DURABILITY_BONUS_PERCENT.get().floatValue();
                        int newMaxDamage = (int) (baseMaxDamage * (1.0f + (newLevel * durabilityBonus / 100.0f)));
                        mainHand.set(DataComponents.MAX_DAMAGE, newMaxDamage);

                        source.sendSuccess(() -> Component.literal("Set tool level to " + newLevel), false);
                        return 1;
                    })))
            .then(Commands.literal("addxp")
                .then(Commands.argument("amount", FloatArgumentType.floatArg(0))
                    .executes(context -> {
                        CommandSourceStack source = context.getSource();
                        ServerPlayer player = source.getPlayerOrException();
                        float amount = FloatArgumentType.getFloat(context, "amount");
                        
                        ItemStack mainHand = player.getMainHandItem();
                        if (mainHand.isEmpty()) {
                            source.sendFailure(Component.literal("You must hold an item in your main hand!"));
                            return 0;
                        }

                        LevelingSystem.grantXP(player, net.minecraft.world.entity.EquipmentSlot.MAINHAND, mainHand, amount);
                        
                        source.sendSuccess(() -> Component.literal("Granted " + amount + " XP to tool."), false);
                        return 1;
                    })))
        );
    }
}

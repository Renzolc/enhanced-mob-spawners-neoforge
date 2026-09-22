package com.branders.spawnermod.command;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.config.ModConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class SpawnerModCommands {

    /**
     * Register /ems config commands. Fabric only registered on integrated
     * (singleplayer); match that here.
     */
    public static void register(RegisterCommandsEvent event) {
        if (event.getCommandSelection() == Commands.CommandSelection.DEDICATED) {
            return;
        }

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        newCommand(dispatcher, "default_spawner_range", IntegerArgumentType.integer(0));
        newCommand(dispatcher, "default_spawner_range_enabled", IntegerArgumentType.integer(0, 1));
        newCommand(dispatcher, "disable_count", IntegerArgumentType.integer(0, 1));
        newCommand(dispatcher, "disable_egg_removal_from_spawner", IntegerArgumentType.integer(0, 1));
        newCommand(dispatcher, "disable_range", IntegerArgumentType.integer(0, 1));
        newCommand(dispatcher, "disable_silk_touch", IntegerArgumentType.integer(0, 1));
        newCommand(dispatcher, "disable_spawner_config", IntegerArgumentType.integer(0, 1));
        newCommand(dispatcher, "disable_speed", IntegerArgumentType.integer(0, 1));
        newCommand(dispatcher, "display_item_id_from_right_click_in_log", IntegerArgumentType.integer(0, 1));
        newCommand(dispatcher, "limited_spawns_amount", IntegerArgumentType.integer(0));
        newCommand(dispatcher, "limited_spawns_enabled", IntegerArgumentType.integer(0, 1));
        newCommand(dispatcher, "monster_egg_drop_chance", IntegerArgumentType.integer(0, 100));
        newCommand(dispatcher, "monster_egg_only_drop_when_killed_by_player", IntegerArgumentType.integer(0, 1));
        newCommand(dispatcher, "spawner_hardness", IntegerArgumentType.integer(0));

        ConfigValues.getSpawnEggEntities().forEachRemaining(e -> {
            newCommand(dispatcher, e, IntegerArgumentType.integer(0, 1));
        });

        dispatcher.register(literal("ems").then(literal("reset").executes(ctx -> {
            ConfigValues.setDefaultConfigValues();
            ctx.getSource().sendSuccess(() -> Component.literal("[EMS]: Config reset to default"), false);
            return 1;
        })));
    }

    private static void newCommand(CommandDispatcher<CommandSourceStack> dispatcher, String name,
            IntegerArgumentType type) {
        // Brigadier literals cannot contain ':'; map "minecraft:pig" -> "minecraft_pig"
        String literalName = name.replace(':', '_');
        dispatcher.register(literal("ems").then(literal(literalName).executes(ctx -> {
            final int value = ConfigValues.get(name);
            ctx.getSource().sendSuccess(
                    () -> Component.literal("[EMS]: %s is currently set to %s".formatted(name, value)), false);
            return 1;
        }).then(argument("value", type).requires(source -> source.hasPermission(2)).executes(ctx -> {
            final int value = IntegerArgumentType.getInteger(ctx, "value");
            ConfigValues.put(name, value);
            ctx.getSource().sendSuccess(
                    () -> Component.literal("[EMS]: %s updated to %s".formatted(name, value)), false);
            ModConfigManager.saveConfigToFile();
            return 1;
        }))));
    }
}

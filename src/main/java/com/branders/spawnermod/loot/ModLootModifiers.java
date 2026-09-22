package com.branders.spawnermod.loot;

import com.branders.spawnermod.SpawnerMod;
import com.mojang.serialization.MapCodec;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModLootModifiers {

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SpawnerMod.MOD_ID);

    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> SPAWNER_SILK_TOUCH =
            LOOT_MODIFIER_SERIALIZERS.register("spawner_silk_touch", () -> SpawnerSilkTouchLootModifier.CODEC);

    public static void register(IEventBus bus) {
        LOOT_MODIFIER_SERIALIZERS.register(bus);
    }
}

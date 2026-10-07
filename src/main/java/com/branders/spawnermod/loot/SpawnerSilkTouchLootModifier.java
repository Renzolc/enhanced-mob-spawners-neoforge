package com.branders.spawnermod.loot;

import com.branders.spawnermod.config.ConfigValues;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * Adds a spawner block drop when silk touch is used, unless disabled in config.
 */
public class SpawnerSilkTouchLootModifier extends LootModifier {

    public static final MapCodec<SpawnerSilkTouchLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).apply(inst, SpawnerSilkTouchLootModifier::new));

    public SpawnerSilkTouchLootModifier(LootItemCondition[] conditionsIn, int priority) {
        super(conditionsIn, priority);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (ConfigValues.get("disable_silk_touch") == 1) {
            return generatedLoot;
        }
        generatedLoot.add(new ItemStack(Blocks.SPAWNER));
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}

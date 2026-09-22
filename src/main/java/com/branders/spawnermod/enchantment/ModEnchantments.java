package com.branders.spawnermod.enchantment;

import com.branders.spawnermod.SpawnerMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Resource keys for data-driven enchantments defined under
 * {@code data/spawnermod/enchantment/}.
 */
public final class ModEnchantments {

    public static final ResourceKey<Enchantment> SPAWN_HARVEST = key("spawn_harvest");

    private ModEnchantments() {
    }

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath(SpawnerMod.MOD_ID, name));
    }
}

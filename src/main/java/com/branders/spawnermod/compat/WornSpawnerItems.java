package com.branders.spawnermod.compat;

import com.branders.spawnermod.registry.ModRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/** Hotbar and optional Curios-belt checks. Curios types are not touched unless that mod is loaded. */
public final class WornSpawnerItems {

    private WornSpawnerItems() {
    }

    public static boolean hasBeltKey(Player player) {
        if (!ModList.get().isLoaded("curios")) {
            return false;
        }
        return CuriosBeltItems.contains(player, stack -> stack.getItem() instanceof com.branders.spawnermod.item.SpawnerKey);
    }

    public static void damageBeltKey(ServerPlayer player, ServerLevel level) {
        if (!ModList.get().isLoaded("curios")) {
            return;
        }
        CuriosBeltItems.damageFirst(player, level,
                stack -> stack.getItem() instanceof com.branders.spawnermod.item.SpawnerKey);
    }

    /** Hotbar (selected or not) or a Curios belt slot. */
    public static boolean hasActiveCompass(Player player) {
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize() && slot < 9; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(ModRegistry.SPAWNER_COMPASS.get())) {
                return true;
            }
        }
        if (!ModList.get().isLoaded("curios")) {
            return false;
        }
        return CuriosBeltItems.contains(player, stack -> stack.is(ModRegistry.SPAWNER_COMPASS.get()));
    }
}

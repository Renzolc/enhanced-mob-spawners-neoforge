package com.branders.spawnermod.compat;

import com.branders.spawnermod.item.SpawnerCompassItem;
import com.branders.spawnermod.registry.ModRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/** Hotbar and optional Curios-belt checks. Curios types are not touched unless that mod is loaded. */
public final class WornSpawnerItems {

    private static final int HOTBAR_SLOTS = 9;

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

    /** Any Spawner Compass in the hotbar, offhand, or Curios belt, on or off. */
    public static boolean hasAnyCompass(Player player) {
        if (inventoryHasCompass(player, false)) {
            return true;
        }
        return beltCompassState(player) != null;
    }

    /** An enabled compass in the hotbar, offhand, or a Curios belt slot. */
    public static boolean hasActiveCompass(Player player) {
        if (inventoryHasCompass(player, true)) {
            return true;
        }
        return Boolean.TRUE.equals(beltCompassState(player));
    }

    /**
     * Turns every carried Spawner Compass to the same state. Hotbar, offhand, and belt all count.
     * If any of them is on, they all turn off. If all are off, they all turn on.
     *
     * @return the new state, or null when the player is not carrying a compass
     */
    public static Boolean toggleCarriedCompasses(ServerPlayer player) {
        boolean any = false;
        boolean anyOn = false;
        var inventory = player.getInventory();
        for (int slot = 0; slot < HOTBAR_SLOTS; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!isCompass(stack)) {
                continue;
            }
            any = true;
            anyOn |= SpawnerCompassItem.isEnabled(stack);
        }
        ItemStack offhand = player.getOffhandItem();
        if (isCompass(offhand)) {
            any = true;
            anyOn |= SpawnerCompassItem.isEnabled(offhand);
        }
        Boolean belt = beltCompassState(player);
        if (belt != null) {
            any = true;
            anyOn |= belt;
        }
        if (!any) {
            return null;
        }

        boolean enabled = !anyOn;
        for (int slot = 0; slot < HOTBAR_SLOTS; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (isCompass(stack)) {
                SpawnerCompassItem.setEnabled(stack, enabled);
            }
        }
        if (isCompass(offhand)) {
            SpawnerCompassItem.setEnabled(offhand, enabled);
        }
        if (ModList.get().isLoaded("curios")) {
            CuriosBeltItems.setCompassesEnabled(player, enabled);
        }
        return enabled;
    }

    private static boolean inventoryHasCompass(Player player, boolean enabledOnly) {
        var inventory = player.getInventory();
        for (int slot = 0; slot < HOTBAR_SLOTS; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (matches(stack, enabledOnly)) {
                return true;
            }
        }
        return matches(player.getOffhandItem(), enabledOnly);
    }

    /** Null when Curios is absent or the belt has no compass. */
    private static Boolean beltCompassState(Player player) {
        if (!ModList.get().isLoaded("curios")) {
            return null;
        }
        return CuriosBeltItems.compassEnabled(player);
    }

    private static boolean matches(ItemStack stack, boolean enabledOnly) {
        return enabledOnly ? isEnabledCompass(stack) : isCompass(stack);
    }

    private static boolean isCompass(ItemStack stack) {
        return stack.is(ModRegistry.SPAWNER_COMPASS.get());
    }

    private static boolean isEnabledCompass(ItemStack stack) {
        return isCompass(stack) && SpawnerCompassItem.isEnabled(stack);
    }
}

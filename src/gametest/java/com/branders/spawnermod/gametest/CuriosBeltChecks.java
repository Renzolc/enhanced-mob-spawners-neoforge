package com.branders.spawnermod.gametest;

import com.branders.spawnermod.compat.BeltSlotGrants;
import com.branders.spawnermod.compat.CuriosBeltSlots;
import com.branders.spawnermod.compat.WornSpawnerItems;
import com.branders.spawnermod.networking.SpawnerModNetworking;
import com.branders.spawnermod.networking.packet.SyncSpawnerPacket;
import com.branders.spawnermod.registry.ModRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

/** Curios-only checks. Loaded only when Curios is present. */
final class CuriosBeltChecks {

    private CuriosBeltChecks() {
    }

    static void run(GameTestHelper helper, ServerPlayer player, BlockPos spawnerPos) {
        var inventory = CuriosApi.getCuriosInventory(player).orElse(null);
        Check.isTrue(helper, inventory != null, "player has no Curios inventory");
        var belt = inventory.getStacksHandler("belt").orElse(null);
        Check.isTrue(helper, belt != null, "player has no Curios belt slot");
        IDynamicStackHandler stacks = belt.getStacks();
        Check.isTrue(helper, stacks.getSlots() >= 1, "belt has no slots");

        // Compass on the belt counts as active and is not a key.
        stacks.setStackInSlot(0, new ItemStack(ModRegistry.SPAWNER_COMPASS.get()));
        Check.isTrue(helper, WornSpawnerItems.hasActiveCompass(player), "belt compass is not active");
        Check.isFalse(helper, WornSpawnerItems.hasBeltKey(player), "belt compass was taken for a key");

        // Key on the belt, empty hands: applying settings damages the belt key.
        stacks.setStackInSlot(0, new ItemStack(ModRegistry.SPAWNER_KEY.get()));
        Check.isTrue(helper, WornSpawnerItems.hasBeltKey(player), "belt key not found");
        Check.isTrue(helper, player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty(),
                "hands are not empty");
        Check.isTrue(helper, SpawnerModNetworking.applySpawnerSettings(player.serverLevel(),
                new SyncSpawnerPacket(spawnerPos, 20, 4, 32, 6, 200, 800), player), "settings not applied");
        ItemStack key = stacks.getStackInSlot(0);
        Check.isTrue(helper, key.is(ModRegistry.SPAWNER_KEY.get()), "belt key disappeared");
        int remaining = key.getMaxDamage() - key.getDamageValue();
        Check.isTrue(helper, remaining == 15, "belt key durability expected 15 but was " + remaining);
        stacks.setStackInSlot(0, ItemStack.EMPTY);

        // The Supplementaries extra-slot grant raises the belt to at least four slots.
        Check.isTrue(helper, CuriosBeltSlots.applyShortfall(player), "belt shortfall not applied");
        belt = inventory.getStacksHandler("belt").orElseThrow();
        belt.update();
        Check.isTrue(helper, belt.getSlots() >= BeltSlotGrants.MIN_BELT_SLOTS,
                "belt has " + belt.getSlots() + " slots after the grant");
        // Applying again does not stack the modifier.
        int slots = belt.getSlots();
        CuriosBeltSlots.applyShortfall(player);
        belt.update();
        Check.isTrue(helper, belt.getSlots() == slots, "belt grew again to " + belt.getSlots());
    }
}

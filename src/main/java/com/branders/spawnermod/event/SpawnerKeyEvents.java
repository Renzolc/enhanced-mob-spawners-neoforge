package com.branders.spawnermod.event;

import com.branders.spawnermod.compat.WornSpawnerItems;
import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.item.SpawnerKey;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Opens the spawner screen from a Curios belt key when the key is not the item
 * being used in either hand. Spawn eggs are left alone so they can still set
 * or replace the mob inside the spawner.
 */
public final class SpawnerKeyEvents {

    private SpawnerKeyEvents() {
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (ConfigValues.get("disable_spawner_config") != 0) {
            return;
        }

        ItemStack used = event.getItemStack();
        if (used.getItem() instanceof SpawnerKey || isSpawnEgg(used)) {
            return;
        }

        Level level = event.getLevel();
        var pos = event.getPos();
        if (level.getBlockState(pos).getBlock() != Blocks.SPAWNER) {
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner)) {
            return;
        }

        Player player = event.getEntity();
        if (!WornSpawnerItems.hasBeltKey(player)) {
            return;
        }

        // Empty main hand should not swallow an egg in the offhand.
        if (used.isEmpty() && isSpawnEgg(player.getItemInHand(otherHand(event.getHand())))) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (level.isClientSide() && event.getHand() == InteractionHand.MAIN_HAND) {
            SpawnerKey.openScreen(spawner.getSpawner(), pos);
        }
    }

    /**
     * Vanilla and NeoForge eggs, plus ids this mod already treats as eggs
     * ({@code namespace:mob_spawn_egg} and {@code namespace:spawn_egg_mob}).
     */
    private static boolean isSpawnEgg(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() instanceof SpawnEggItem) {
            return true;
        }
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().contains("spawn_egg");
    }

    private static InteractionHand otherHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }
}

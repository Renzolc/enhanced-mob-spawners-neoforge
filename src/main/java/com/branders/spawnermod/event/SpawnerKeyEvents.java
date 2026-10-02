package com.branders.spawnermod.event;

import com.branders.spawnermod.compat.WornSpawnerItems;
import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.item.SpawnerKey;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Belt-slot fallback for the Spawner Key. The screen opens only when the hand
 * being used is empty. Anything in that hand, including a spawn egg, keeps its
 * normal use. A key held in the hand still uses {@link SpawnerKey#useOn}.
 */
public final class SpawnerKeyEvents {

    private SpawnerKeyEvents() {
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (ConfigValues.get("disable_spawner_config") != 0) {
            return;
        }
        if (!event.getItemStack().isEmpty()) {
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

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!level.isClientSide()) {
            return;
        }
        // Main hand already opened the screen when it was empty. An empty offhand
        // after a held main-hand item is its own click.
        if (event.getHand() == InteractionHand.OFF_HAND && player.getMainHandItem().isEmpty()) {
            return;
        }
        SpawnerKey.openScreen(spawner.getSpawner(), pos);
    }
}

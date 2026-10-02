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
 * Opens the spawner screen from a Curios belt key when the key is not the item
 * being used in either hand.
 */
public final class SpawnerKeyEvents {

    private SpawnerKeyEvents() {
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (ConfigValues.get("disable_spawner_config") != 0) {
            return;
        }
        if (event.getItemStack().getItem() instanceof SpawnerKey) {
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
        if (level.isClientSide() && event.getHand() == InteractionHand.MAIN_HAND) {
            SpawnerKey.openScreen(spawner.getSpawner(), pos);
        }
    }
}

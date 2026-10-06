package com.branders.spawnermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.branders.spawnermod.event.EventHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;

@Mixin(BlockBehaviour.class)
public class UpdateNeighborMixin {

    @Inject(at = @At("HEAD"), method = "neighborChanged")
    private void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock,
            Orientation orientation, boolean movedByPiston, CallbackInfo ci) {
        // Since 1.21.2 the source position is no longer passed. A spawner next to a
        // changed block is itself notified, so react when the notified block is a spawner.
        if (state.getBlock() instanceof SpawnerBlock) {
            EventHandler.updateNeighbor(pos, world);
        }
    }
}

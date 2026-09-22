package com.branders.spawnermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.branders.spawnermod.event.EventHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockBehaviour.class)
public class UpdateNeighborMixin {

    @Inject(at = @At("HEAD"), method = "neighborChanged")
    private void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos,
            boolean notify, CallbackInfo ci) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction dir : Direction.values()) {
            mutable.setWithOffset(sourcePos, dir);
            if (world.getBlockState(mutable).getBlock() instanceof SpawnerBlock) {
                EventHandler.updateNeighbor(mutable, world);
            }
        }
    }
}

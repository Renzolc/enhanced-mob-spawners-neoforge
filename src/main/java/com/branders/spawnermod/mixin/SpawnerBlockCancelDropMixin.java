package com.branders.spawnermod.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.branders.spawnermod.config.ConfigValues;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Block.class)
public class SpawnerBlockCancelDropMixin {

    @Inject(at = @At("HEAD"), method = "playerDestroy", cancellable = true)
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state,
            @Nullable BlockEntity blockEntity, ItemStack tool, CallbackInfo info) {
        if (state.getBlock() instanceof SpawnerBlock) {
            if (ConfigValues.get("disable_silk_touch") == 1)
                info.cancel();
        }
    }
}

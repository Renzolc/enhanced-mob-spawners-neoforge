package com.branders.spawnermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.branders.spawnermod.spawner.CompassTrackingAccess;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;

/**
 * Persists compass tracking on the spawner block entity NBT. The flag lives in
 * the same compound {@link BaseSpawner#save} already writes (Delay, SpawnData,
 * and so on), which the spawner block entity stores and syncs.
 */
@Mixin(BaseSpawner.class)
public class CompassTrackingMixin implements CompassTrackingAccess {

    @Unique
    private boolean spawnermod$compassTracking = true;

    @Override
    public boolean spawnermod$isCompassTracking() {
        return this.spawnermod$compassTracking;
    }

    @Override
    public void spawnermod$setCompassTracking(boolean tracking) {
        this.spawnermod$compassTracking = tracking;
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void spawnermod$readTracking(Level level, BlockPos pos, CompoundTag tag, CallbackInfo ci) {
        if (tag.contains(NBT_KEY)) {
            this.spawnermod$compassTracking = tag.getBoolean(NBT_KEY);
        }
    }

    @Inject(method = "save", at = @At("RETURN"))
    private void spawnermod$writeTracking(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag written = cir.getReturnValue();
        if (written != null) {
            written.putBoolean(NBT_KEY, this.spawnermod$compassTracking);
        }
    }
}

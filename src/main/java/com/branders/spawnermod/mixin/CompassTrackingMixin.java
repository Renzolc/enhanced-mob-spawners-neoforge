package com.branders.spawnermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.branders.spawnermod.spawner.CompassTrackingAccess;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    private void spawnermod$readTracking(Level level, BlockPos pos, ValueInput input, CallbackInfo ci) {
        // Absent flag (older spawners) keeps the current value, as before.
        this.spawnermod$compassTracking = input.getBooleanOr(NBT_KEY, this.spawnermod$compassTracking);
    }

    @Inject(method = "save", at = @At("RETURN"))
    private void spawnermod$writeTracking(ValueOutput output, CallbackInfo ci) {
        output.putBoolean(NBT_KEY, this.spawnermod$compassTracking);
    }
}

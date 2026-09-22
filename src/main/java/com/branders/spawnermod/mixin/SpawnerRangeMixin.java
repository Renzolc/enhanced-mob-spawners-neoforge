package com.branders.spawnermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.branders.spawnermod.config.ConfigValues;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;

@Mixin(BaseSpawner.class)
public class SpawnerRangeMixin {

    private boolean rangeSet = false;

    @Inject(at = @At("HEAD"), method = "isNearPlayer", cancellable = true)
    private void isNearPlayer(Level level, BlockPos spawner, CallbackInfoReturnable<Boolean> cir) {
        if (ConfigValues.get("default_spawner_range_enabled") == 1) {
            if (rangeSet)
                return;

            int range = ConfigValues.get("default_spawner_range");
            rangeSet = true;

            BaseSpawner logic = (BaseSpawner) (Object) this;
            CompoundTag nbt = logic.save(new CompoundTag());
            nbt.putShort("RequiredPlayerRange", (short) range);
            logic.load(level, spawner, nbt);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getShort(Ljava/lang/String;)S"), method = "load")
    private void readNbt(Level world, BlockPos pos, CompoundTag nbt, CallbackInfo info) {
        if (ConfigValues.get("default_spawner_range_enabled") == 0)
            return;
        rangeSet = nbt.getBoolean("RangeSet");
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putShort(Ljava/lang/String;S)V"), method = "save")
    private void writeNbt(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> info) {
        if (ConfigValues.get("default_spawner_range_enabled") == 0)
            return;
        nbt.putBoolean("RangeSet", rangeSet);
    }
}

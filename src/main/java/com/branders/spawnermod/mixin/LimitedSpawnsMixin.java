package com.branders.spawnermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.spawner.SpawnerNbt;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;

@Mixin(BaseSpawner.class)
public class LimitedSpawnsMixin {

    @Unique
    private short spawns = 0;

    /** Counts each spawned entity (the spawn particles event follows every successful spawn). */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;levelEvent(ILnet/minecraft/core/BlockPos;I)V"), method = "serverTick")
    private void entitySpawn(ServerLevel world, BlockPos pos, CallbackInfo ci) {
        if (ConfigValues.get("limited_spawns_enabled") == 0)
            return;

        String entity = SpawnerNbt.entityId(SpawnerNbt.save((BaseSpawner) (Object) this));
        if (entity != null && entity.contains("area_effect_cloud"))
            return;

        spawns++;
    }

    /** Before each spawn attempt: switch the spawner off once the limit is reached. */
    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/EntityType;by(Lnet/minecraft/nbt/CompoundTag;)Ljava/util/Optional;"), method = "serverTick", cancellable = true)
    public void cancel(ServerLevel world, BlockPos pos, CallbackInfo ci) {
        if (ConfigValues.get("limited_spawns_enabled") == 0)
            return;

        world.getBlockEntity(pos).setChanged();
        world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);

        if (spawns >= ConfigValues.get("limited_spawns_amount")) {
            BaseSpawner self = (BaseSpawner) (Object) this;
            CompoundTag nbt = SpawnerNbt.save(self);
            nbt.putShort("RequiredPlayerRange", (short) 0);
            SpawnerNbt.load(self, world, pos, nbt);
            world.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "load")
    private void readNbt(Level world, BlockPos pos, CompoundTag nbt, CallbackInfo info) {
        if (ConfigValues.get("limited_spawns_enabled") == 0)
            return;
        spawns = SpawnerNbt.getShort(nbt, "spawns");
    }

    @Inject(at = @At("RETURN"), method = "save")
    private void writeNbt(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> info) {
        if (ConfigValues.get("limited_spawns_enabled") == 0)
            return;
        info.getReturnValue().putShort("spawns", spawns);
    }
}

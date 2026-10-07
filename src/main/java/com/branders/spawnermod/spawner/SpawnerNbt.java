package com.branders.spawnermod.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;

/**
 * Reads and writes the spawner settings ({@code Delay}, {@code SpawnCount}, {@code SpawnData}
 * and so on) as one compound tag. All spawner edits go through here so version differences in
 * {@link BaseSpawner#save}/{@link BaseSpawner#load} and in {@link CompoundTag} getters stay in
 * one place.
 */
public final class SpawnerNbt {

    private SpawnerNbt() {
    }

    public static CompoundTag save(BaseSpawner logic) {
        return logic.save(new CompoundTag());
    }

    public static void load(BaseSpawner logic, Level level, BlockPos pos, CompoundTag tag) {
        logic.load(level, pos, tag);
    }

    public static short getShort(CompoundTag tag, String key) {
        return tag.getShortOr(key, (short) 0);
    }

    public static boolean getBoolean(CompoundTag tag, String key, boolean defaultValue) {
        return tag.getBooleanOr(key, defaultValue);
    }

    /** Entity id in {@code SpawnData}, for example {@code minecraft:pig}, or null when there is none. */
    public static String entityId(CompoundTag spawnerTag) {
        return spawnerTag.getCompound("SpawnData")
                .flatMap(data -> data.getCompound("entity"))
                .flatMap(entity -> entity.getString("id"))
                .orElse(null);
    }
}

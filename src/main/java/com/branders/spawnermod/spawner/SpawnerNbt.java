package com.branders.spawnermod.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

/**
 * Reads and writes the spawner settings ({@code Delay}, {@code SpawnCount}, {@code SpawnData}
 * and so on) as one compound tag. All spawner edits go through here so version differences in
 * {@link BaseSpawner#save}/{@link BaseSpawner#load} and in {@link CompoundTag} getters stay in
 * one place.
 *
 * <p>Since 1.21.6 {@link BaseSpawner} reads and writes through {@code ValueInput}/{@code ValueOutput};
 * the tag based {@code TagValueInput}/{@code TagValueOutput} wrap a plain {@link CompoundTag}.
 */
public final class SpawnerNbt {

    private SpawnerNbt() {
    }

    public static CompoundTag save(BaseSpawner logic) {
        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        logic.save(output);
        return output.buildResult();
    }

    public static void load(BaseSpawner logic, Level level, BlockPos pos, CompoundTag tag) {
        RegistryAccess registries = level != null ? level.registryAccess() : RegistryAccess.EMPTY;
        logic.load(level, pos, TagValueInput.create(ProblemReporter.DISCARDING, registries, tag));
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

package com.branders.spawnermod.client;

import com.branders.spawnermod.spawner.CompassTrackingAccess;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Client target lookup for the spawner compass. The needle angle is computed by
 * {@link SpawnerCompassAngle} (item model property {@code spawnermod:spawner_compass}):
 * a real target points, and a null target spins the same way a compass does with
 * nowhere to point.
 * The needle follows the living entity the item is rendered for. Curios draws
 * belt-slot items with the local player, so a compass worn on the belt keeps
 * pointing and does not have to be in either hand.
 */
public final class SpawnerCompassClient {

    /** Horizontal search radius, in blocks. */
    public static final int SEARCH_RADIUS = 128;

    /** How long a scan result is reused, in ticks. The needle still updates every frame. */
    private static final int CACHE_TICKS = 20;

    private static boolean cacheValid;
    private static long cachedTick = Long.MIN_VALUE;
    private static int cachedEntityId = Integer.MIN_VALUE;
    private static ResourceKey<Level> cachedDimension;
    private static BlockPos cachedTarget;

    private SpawnerCompassClient() {
    }

    static GlobalPos target(ClientLevel level, ItemStack stack, Entity entity) {
        if (entity == null || level == null) {
            return null;
        }
        // Hotbar icons are drawn for every slot, selected or not, with the local player.
        BlockPos pos = findNearestTracked(level, entity);
        if (pos == null) {
            return null;
        }
        return GlobalPos.of(level.dimension(), pos);
    }

    /** Nearest tracked spawner inside the horizontal radius, or null. Cached for a short interval. */
    public static BlockPos findNearestTracked(ClientLevel level, Entity entity) {
        long tick = level.getGameTime();
        if (cacheValid && cachedEntityId == entity.getId() && cachedDimension == level.dimension()
                && tick >= cachedTick && tick - cachedTick < CACHE_TICKS) {
            return cachedTarget;
        }

        BlockPos found = scan(level, entity);
        cacheValid = true;
        cachedTick = tick;
        cachedEntityId = entity.getId();
        cachedDimension = level.dimension();
        cachedTarget = found;
        return found;
    }

    private static BlockPos scan(ClientLevel level, Entity entity) {
        BlockPos origin = entity.blockPosition();
        int radiusSqr = SEARCH_RADIUS * SEARCH_RADIUS;
        int chunkRadius = (SEARCH_RADIUS >> 4) + 1;
        int centerX = origin.getX() >> 4;
        int centerZ = origin.getZ() >> 4;

        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;

        for (int cx = centerX - chunkRadius; cx <= centerX + chunkRadius; cx++) {
            for (int cz = centerZ - chunkRadius; cz <= centerZ + chunkRadius; cz++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(cx, cz);
                if (chunk == null) {
                    continue;
                }
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof SpawnerBlockEntity spawner)) {
                        continue;
                    }
                    BlockPos pos = blockEntity.getBlockPos();
                    int dx = pos.getX() - origin.getX();
                    int dz = pos.getZ() - origin.getZ();
                    if (dx * dx + dz * dz > radiusSqr) {
                        continue;
                    }
                    if (!isTracked(spawner)) {
                        continue;
                    }
                    double distance = entity.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        best = pos;
                    }
                }
            }
        }
        return best;
    }

    private static boolean isTracked(SpawnerBlockEntity spawner) {
        if (spawner.getSpawner() instanceof CompassTrackingAccess access) {
            return access.spawnermod$isCompassTracking();
        }
        return true;
    }

}

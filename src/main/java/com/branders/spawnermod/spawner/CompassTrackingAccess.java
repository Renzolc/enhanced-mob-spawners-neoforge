package com.branders.spawnermod.spawner;

/**
 * Extra per-spawner state stored on {@link net.minecraft.world.level.BaseSpawner}.
 * Missing NBT means tracking is on, so spawners that predate this flag stay findable.
 */
public interface CompassTrackingAccess {

    String NBT_KEY = "spawnermod_compass_tracking";

    boolean spawnermod$isCompassTracking();

    void spawnermod$setCompassTracking(boolean tracking);
}

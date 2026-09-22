package com.branders.spawnermod;

import net.neoforged.bus.api.IEventBus;

/**
 * Client-side initialization (reserved for future client-only hooks).
 * Payload handlers for playToClient are registered on the common bus in
 * {@link SpawnerModNetworking} (NeoForge 21.1 API).
 *
 * @author Anders &lt;Branders&gt; Blomqvist
 */
public final class SpawnerModClient {

    private SpawnerModClient() {
    }

    public static void init(IEventBus modEventBus) {
        // Client payload handling is registered via PayloadRegistrar.playToClient
        // in SpawnerModNetworking on NeoForge 1.21.1.
    }
}

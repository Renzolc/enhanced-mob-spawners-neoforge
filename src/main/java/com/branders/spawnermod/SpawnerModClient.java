package com.branders.spawnermod;

import com.branders.spawnermod.client.SpawnerCompassClient;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
/**
 * Client-side initialization.
 * Payload handlers for playToClient are registered on the common bus in
 * {@link com.branders.spawnermod.networking.SpawnerModNetworking} (NeoForge 21.1 API).
 *
 * @author Anders &lt;Branders&gt; Blomqvist
 */
public final class SpawnerModClient {

    private SpawnerModClient() {
    }

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(SpawnerModClient::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(SpawnerCompassClient::register);
    }
}

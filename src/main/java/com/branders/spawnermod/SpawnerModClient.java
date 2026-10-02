package com.branders.spawnermod;

import com.branders.spawnermod.client.SpawnerCompassClient;
import com.branders.spawnermod.client.SpawnerCompassHud;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
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
        modEventBus.addListener(SpawnerModClient::onRegisterGuiLayers);
    }

    private static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(SpawnerMod.MOD_ID, "spawner_compass_hud"),
                SpawnerCompassHud::render);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(SpawnerCompassClient::register);
    }
}

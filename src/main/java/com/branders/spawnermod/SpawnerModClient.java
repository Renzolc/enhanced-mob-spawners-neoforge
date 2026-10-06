package com.branders.spawnermod;

import com.branders.spawnermod.client.SpawnerCompassAngle;
import com.branders.spawnermod.client.SpawnerCompassHud;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
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
        modEventBus.addListener(SpawnerModClient::onRegisterItemModelProperties);
        modEventBus.addListener(SpawnerModClient::onRegisterGuiLayers);
    }

    private static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(SpawnerMod.MOD_ID, "spawner_compass_hud"),
                SpawnerCompassHud::render);
    }

    private static void onRegisterItemModelProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(SpawnerMod.MOD_ID, "spawner_compass"),
                SpawnerCompassAngle.MAP_CODEC);
    }
}

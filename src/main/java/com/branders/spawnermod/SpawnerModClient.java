package com.branders.spawnermod;

import com.branders.spawnermod.client.CompassTrackerScreen;
import com.branders.spawnermod.client.SpawnerCompassAngle;
import com.branders.spawnermod.client.SpawnerCompassClient;
import com.branders.spawnermod.client.SpawnerCompassHud;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

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
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> (container, parent) -> new CompassTrackerScreen(parent));
        modEventBus.addListener(SpawnerModClient::onRegisterItemModelProperties);
        modEventBus.addListener(SpawnerModClient::onRegisterGuiLayers);
        modEventBus.addListener(SpawnerModClient::onRegisterKeys);
        NeoForge.EVENT_BUS.addListener(SpawnerCompassClient::onClientTick);
    }

    private static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.registerCategory(SpawnerCompassClient.CATEGORY);
        event.register(SpawnerCompassClient.TOGGLE_COMPASS);
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

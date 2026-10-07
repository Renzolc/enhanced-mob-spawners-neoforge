package com.branders.spawnermod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.branders.spawnermod.compat.BeltSlotGrants;
import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.config.ModConfigManager;
import com.branders.spawnermod.event.EventHandler;
import com.branders.spawnermod.event.SpawnerKeyEvents;
import com.branders.spawnermod.loot.ModLootModifiers;
import com.branders.spawnermod.networking.SpawnerModNetworking;
import com.branders.spawnermod.networking.packet.SyncConfigPacket;
import com.branders.spawnermod.registry.ModRegistry;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * Enhanced Mob Spawners — NeoForge port.
 *
 * @author Anders &lt;Branders&gt; Blomqvist
 */
@Mod(SpawnerMod.MOD_ID)
public class SpawnerMod {

    public static final String MOD_ID = "spawnermod";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final EventHandler eventHandler = new EventHandler();

    public SpawnerMod(IEventBus modEventBus) {
        ModConfigManager.initConfig(MOD_ID);

        ModRegistry.register(modEventBus);
        ModLootModifiers.register(modEventBus);

        modEventBus.addListener(this::registerPayloads);

        NeoForge.EVENT_BUS.register(eventHandler);
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);
        NeoForge.EVENT_BUS.addListener(this::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(this::onPlayerChangeDimension);
        NeoForge.EVENT_BUS.addListener(SpawnerKeyEvents::onRightClickBlock);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            SpawnerModClient.init(modEventBus);
        }
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        SpawnerModNetworking.register(event);
    }

    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // Refresh local config (singleplayer after leaving a server)
        ModConfigManager.initConfig(MOD_ID);

        PacketDistributor.sendToPlayer(player,
                new SyncConfigPacket(
                        ConfigValues.get("disable_spawner_config"),
                        ConfigValues.get("disable_count"),
                        ConfigValues.get("disable_range"),
                        ConfigValues.get("disable_speed"),
                        ConfigValues.get("limited_spawns_enabled"),
                        ConfigValues.get("limited_spawns_amount"),
                        ConfigValues.get("default_spawner_range_enabled"),
                        ConfigValues.get("default_spawner_range")));
        BeltSlotGrants.schedule(player);
    }

    private void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BeltSlotGrants.schedule(player);
        }
    }

    private void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BeltSlotGrants.schedule(player);
        }
    }
}

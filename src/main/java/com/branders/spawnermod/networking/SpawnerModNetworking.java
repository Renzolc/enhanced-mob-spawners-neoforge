package com.branders.spawnermod.networking;

import com.branders.spawnermod.SpawnerMod;
import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.item.SpawnerKey;
import com.branders.spawnermod.networking.packet.SyncConfigPacket;
import com.branders.spawnermod.networking.packet.SyncSpawnerPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class SpawnerModNetworking {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(SyncSpawnerPacket.TYPE, SyncSpawnerPacket.STREAM_CODEC,
                SpawnerModNetworking::handleSyncSpawner);

        registrar.playToClient(SyncConfigPacket.TYPE, SyncConfigPacket.STREAM_CODEC,
                SpawnerModNetworking::handleSyncConfig);
    }

    private static void handleSyncSpawner(SyncSpawnerPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player().level() instanceof ServerLevel world)) {
                SpawnerMod.LOGGER.warn("Server world is NULL, cannot sync spawner.");
                return;
            }

            BlockPos pos = payload.pos();
            short requiredPlayerRange = (short) payload.requiredPlayerRange();
            short delay = (short) payload.delay();
            short spawnCount = (short) payload.spawnCount();
            short maxNearbyEntities = (short) payload.maxNearbyEntities();
            short minSpawnDelay = (short) payload.minSpawnDelay();
            short maxSpawnDelay = (short) payload.maxSpawnDelay();

            if (!(world.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner)) {
                return;
            }

            BaseSpawner logic = spawner.getSpawner();
            BlockState blockstate = world.getBlockState(pos);
            CompoundTag nbt = logic.save(new CompoundTag());

            if (requiredPlayerRange == 0)
                nbt.putShort("SpawnRange", nbt.getShort("RequiredPlayerRange"));
            else
                nbt.putShort("SpawnRange", (short) 4);

            nbt.putShort("Delay", delay);
            nbt.putShort("SpawnCount", spawnCount);
            nbt.putShort("RequiredPlayerRange", requiredPlayerRange);
            nbt.putShort("MaxNearbyEntities", maxNearbyEntities);
            nbt.putShort("MinSpawnDelay", minSpawnDelay);
            nbt.putShort("MaxSpawnDelay", maxSpawnDelay);

            logic.load(world, pos, nbt);
            spawner.setChanged();
            world.sendBlockUpdated(pos, blockstate, blockstate, Block.UPDATE_ALL);

            ItemStack stack = context.player().getMainHandItem();
            if (stack.getItem() instanceof SpawnerKey) {
                stack.hurtAndBreak(1, context.player(), EquipmentSlot.MAINHAND);
            }

            world.levelEvent(LevelEvent.PARTICLES_WAX_OFF, pos, 0);
        });
    }

    private static void handleSyncConfig(SyncConfigPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ConfigValues.put("disable_spawner_config", payload.config());
            ConfigValues.put("disable_count", payload.count());
            ConfigValues.put("disable_range", payload.range());
            ConfigValues.put("disable_speed", payload.speed());
            ConfigValues.put("limited_spawns_enabled", payload.limitedSpawns());
            ConfigValues.put("limited_spawns_amount", payload.limitedSpawnsAmount());
            ConfigValues.put("default_spawner_range_enabled", payload.isCustomRange());
            ConfigValues.put("default_spawner_range", payload.customRange());
        });
    }
}

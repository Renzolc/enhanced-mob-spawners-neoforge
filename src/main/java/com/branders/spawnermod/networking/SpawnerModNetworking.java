package com.branders.spawnermod.networking;

import com.branders.spawnermod.SpawnerMod;
import com.branders.spawnermod.compat.WornSpawnerItems;
import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.item.SpawnerKey;
import com.branders.spawnermod.networking.packet.SyncConfigPacket;
import com.branders.spawnermod.networking.packet.SyncSpawnerPacket;
import com.branders.spawnermod.networking.packet.SyncSpawnerTrackingPacket;
import com.branders.spawnermod.networking.packet.UpdateSpawnerTrackingPacket;
import com.branders.spawnermod.spawner.CompassTrackingAccess;
import com.branders.spawnermod.spawner.SpawnerNbt;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
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

        registrar.playToServer(UpdateSpawnerTrackingPacket.TYPE, UpdateSpawnerTrackingPacket.STREAM_CODEC,
                SpawnerModNetworking::handleUpdateTracking);

        registrar.playToClient(SyncSpawnerTrackingPacket.TYPE, SyncSpawnerTrackingPacket.STREAM_CODEC,
                SpawnerModNetworking::handleSyncTracking);
    }

    private static void handleUpdateTracking(UpdateSpawnerTrackingPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!(player.level() instanceof ServerLevel world)) {
                return;
            }

            BlockPos pos = payload.pos();
            if (!player.blockPosition().closerThan(pos, 16.0D)) {
                return;
            }
            if (!applyTracking(world, pos, payload.tracking())) {
                return;
            }

            SyncSpawnerTrackingPacket sync = new SyncSpawnerTrackingPacket(pos, payload.tracking());
            PacketDistributor.sendToPlayersTrackingChunk(world, new ChunkPos(pos), sync);
            PacketDistributor.sendToPlayer(player, sync);

            player.displayClientMessage(Component.translatable(payload.tracking()
                    ? "message.spawnermod.compass_tracking.on"
                    : "message.spawnermod.compass_tracking.off"), true);
        });
    }

    /**
     * Sets the compass tracking (ignore toggle) flag on a spawner. Server side only.
     *
     * @return false when there is no spawner at that position
     */
    public static boolean applyTracking(ServerLevel world, BlockPos pos, boolean tracking) {
        if (!(world.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner)) {
            return false;
        }
        if (!(spawner.getSpawner() instanceof CompassTrackingAccess access)) {
            return false;
        }

        access.spawnermod$setCompassTracking(tracking);
        spawner.setChanged();
        BlockState state = world.getBlockState(pos);
        world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        return true;
    }

    private static void handleSyncTracking(SyncSpawnerTrackingPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player().level().getBlockEntity(payload.pos()) instanceof SpawnerBlockEntity spawner)) {
                return;
            }
            if (spawner.getSpawner() instanceof CompassTrackingAccess access) {
                access.spawnermod$setCompassTracking(payload.tracking());
            }
        });
    }

    private static void handleSyncSpawner(SyncSpawnerPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player().level() instanceof ServerLevel world)) {
                SpawnerMod.LOGGER.warn("Server world is NULL, cannot sync spawner.");
                return;
            }
            applySpawnerSettings(world, payload,
                    context.player() instanceof ServerPlayer serverPlayer ? serverPlayer : null);
        });
    }

    /**
     * Applies Spawner Key GUI settings to the spawner at {@code payload.pos()} and
     * damages the key that was used. Server side only.
     *
     * @return false when there is no spawner at that position
     */
    public static boolean applySpawnerSettings(ServerLevel world, SyncSpawnerPacket payload, ServerPlayer player) {
        BlockPos pos = payload.pos();
        short requiredPlayerRange = (short) payload.requiredPlayerRange();
        short delay = (short) payload.delay();
        short spawnCount = (short) payload.spawnCount();
        short maxNearbyEntities = (short) payload.maxNearbyEntities();
        short minSpawnDelay = (short) payload.minSpawnDelay();
        short maxSpawnDelay = (short) payload.maxSpawnDelay();

        if (!(world.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner)) {
            return false;
        }

        BaseSpawner logic = spawner.getSpawner();
        BlockState blockstate = world.getBlockState(pos);
        CompoundTag nbt = SpawnerNbt.save(logic);

        if (requiredPlayerRange == 0)
            nbt.putShort("SpawnRange", SpawnerNbt.getShort(nbt, "RequiredPlayerRange"));
        else
            nbt.putShort("SpawnRange", (short) 4);

        nbt.putShort("Delay", delay);
        nbt.putShort("SpawnCount", spawnCount);
        nbt.putShort("RequiredPlayerRange", requiredPlayerRange);
        nbt.putShort("MaxNearbyEntities", maxNearbyEntities);
        nbt.putShort("MinSpawnDelay", minSpawnDelay);
        nbt.putShort("MaxSpawnDelay", maxSpawnDelay);

        SpawnerNbt.load(logic, world, pos, nbt);
        spawner.setChanged();
        world.sendBlockUpdated(pos, blockstate, blockstate, Block.UPDATE_ALL);

        damageUsedKey(player, world);

        world.levelEvent(LevelEvent.PARTICLES_WAX_OFF, pos, 0);
        return true;
    }

    private static void damageUsedKey(ServerPlayer player, ServerLevel world) {
        if (player == null) {
            return;
        }
        ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof SpawnerKey) {
            main.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            return;
        }
        ItemStack off = player.getOffhandItem();
        if (off.getItem() instanceof SpawnerKey) {
            off.hurtAndBreak(1, player, EquipmentSlot.OFFHAND);
            return;
        }
        WornSpawnerItems.damageBeltKey(player, world);
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

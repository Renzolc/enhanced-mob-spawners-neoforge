package com.branders.spawnermod.networking.packet;

import com.branders.spawnermod.SpawnerMod;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SyncSpawnerPacket(BlockPos pos, int delay, int spawnCount, int requiredPlayerRange, int maxNearbyEntities,
        int minSpawnDelay, int maxSpawnDelay) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncSpawnerPacket> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(SpawnerMod.MOD_ID, "packet.sync_spawner_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSpawnerPacket> STREAM_CODEC = StreamCodec.of(
            SyncSpawnerPacket::encode, SyncSpawnerPacket::decode);

    private static void encode(RegistryFriendlyByteBuf buf, SyncSpawnerPacket packet) {
        buf.writeBlockPos(packet.pos);
        buf.writeVarInt(packet.delay);
        buf.writeVarInt(packet.spawnCount);
        buf.writeVarInt(packet.requiredPlayerRange);
        buf.writeVarInt(packet.maxNearbyEntities);
        buf.writeVarInt(packet.minSpawnDelay);
        buf.writeVarInt(packet.maxSpawnDelay);
    }

    private static SyncSpawnerPacket decode(RegistryFriendlyByteBuf buf) {
        return new SyncSpawnerPacket(buf.readBlockPos(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

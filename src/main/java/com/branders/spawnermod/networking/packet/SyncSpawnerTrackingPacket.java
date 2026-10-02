package com.branders.spawnermod.networking.packet;

import com.branders.spawnermod.SpawnerMod;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Server to client: a spawner's compass-tracking flag changed. */
public record SyncSpawnerTrackingPacket(BlockPos pos, boolean tracking) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncSpawnerTrackingPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(SpawnerMod.MOD_ID, "packet.sync_spawner_tracking"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSpawnerTrackingPacket> STREAM_CODEC = StreamCodec.of(
            SyncSpawnerTrackingPacket::encode, SyncSpawnerTrackingPacket::decode);

    private static void encode(RegistryFriendlyByteBuf buf, SyncSpawnerTrackingPacket packet) {
        buf.writeBlockPos(packet.pos);
        buf.writeBoolean(packet.tracking);
    }

    private static SyncSpawnerTrackingPacket decode(RegistryFriendlyByteBuf buf) {
        return new SyncSpawnerTrackingPacket(buf.readBlockPos(), buf.readBoolean());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

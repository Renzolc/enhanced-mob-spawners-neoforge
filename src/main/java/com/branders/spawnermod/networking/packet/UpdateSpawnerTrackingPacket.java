package com.branders.spawnermod.networking.packet;

import com.branders.spawnermod.SpawnerMod;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Client to server: the Spawner Key GUI toggled compass tracking for one spawner. */
public record UpdateSpawnerTrackingPacket(BlockPos pos, boolean tracking) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateSpawnerTrackingPacket> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(SpawnerMod.MOD_ID, "packet.update_spawner_tracking"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSpawnerTrackingPacket> STREAM_CODEC = StreamCodec.of(
            UpdateSpawnerTrackingPacket::encode, UpdateSpawnerTrackingPacket::decode);

    private static void encode(RegistryFriendlyByteBuf buf, UpdateSpawnerTrackingPacket packet) {
        buf.writeBlockPos(packet.pos);
        buf.writeBoolean(packet.tracking);
    }

    private static UpdateSpawnerTrackingPacket decode(RegistryFriendlyByteBuf buf) {
        return new UpdateSpawnerTrackingPacket(buf.readBlockPos(), buf.readBoolean());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

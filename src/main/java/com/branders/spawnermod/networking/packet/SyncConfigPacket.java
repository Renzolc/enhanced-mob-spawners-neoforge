package com.branders.spawnermod.networking.packet;

import com.branders.spawnermod.SpawnerMod;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SyncConfigPacket(int config, int count, int range, int speed, int limitedSpawns, int limitedSpawnsAmount,
        int isCustomRange, int customRange) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncConfigPacket> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(SpawnerMod.MOD_ID, "packet.sync_config_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncConfigPacket> STREAM_CODEC = StreamCodec.of(
            SyncConfigPacket::encode, SyncConfigPacket::decode);

    private static void encode(RegistryFriendlyByteBuf buf, SyncConfigPacket packet) {
        buf.writeVarInt(packet.config);
        buf.writeVarInt(packet.count);
        buf.writeVarInt(packet.range);
        buf.writeVarInt(packet.speed);
        buf.writeVarInt(packet.limitedSpawns);
        buf.writeVarInt(packet.limitedSpawnsAmount);
        buf.writeVarInt(packet.isCustomRange);
        buf.writeVarInt(packet.customRange);
    }

    private static SyncConfigPacket decode(RegistryFriendlyByteBuf buf) {
        return new SyncConfigPacket(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

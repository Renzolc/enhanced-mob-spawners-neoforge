package com.branders.spawnermod.networking.packet;

import com.branders.spawnermod.SpawnerMod;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Client to server: the Toggle Spawner Compass key was pressed. */
public record ToggleCompassPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ToggleCompassPacket> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(SpawnerMod.MOD_ID, "packet.toggle_compass"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleCompassPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
            }, buf -> new ToggleCompassPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package com.branders.spawnermod.client;

import com.branders.spawnermod.gui.SpawnerConfigGui;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BaseSpawner;

/**
 * Opens the Spawner Key config screen. Client only: kept out of {@code SpawnerKey} because
 * NeoForge 21.6+ no longer strips {@code @OnlyIn} members, so a common class must not reference
 * screen classes directly.
 */
public final class SpawnerKeyScreen {

    private SpawnerKeyScreen() {
    }

    public static void open(BaseSpawner logic, BlockPos pos) {
        Minecraft.getInstance().setScreen(new SpawnerConfigGui(Component.translatable(""), logic, pos));
    }
}

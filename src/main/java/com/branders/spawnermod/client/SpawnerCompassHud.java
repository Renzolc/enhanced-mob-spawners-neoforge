package com.branders.spawnermod.client;

import com.branders.spawnermod.compat.WornSpawnerItems;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

/**
 * Top-center HUD while a spawner compass is in the hotbar or on the belt.
 * Distance is Euclidean blocks from the player to the spawner block center,
 * rounded to the nearest integer. The search itself stays the 128-block
 * horizontal scan.
 */
public final class SpawnerCompassHud {

    private SpawnerCompassHud() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || minecraft.options.hideGui) {
            return;
        }
        if (!WornSpawnerItems.hasActiveCompass(minecraft.player)) {
            return;
        }

        BlockPos target = SpawnerCompassClient.findNearestTracked(minecraft.level, minecraft.player);
        if (target == null) {
            return;
        }

        double distance = Math.sqrt(minecraft.player.distanceToSqr(target.getX() + 0.5D, target.getY() + 0.5D,
                target.getZ() + 0.5D));
        int blocks = (int) Math.round(distance);
        Component text = Component.translatable("hud.spawnermod.spawner_nearby", blocks);
        int x = (graphics.guiWidth() - minecraft.font.width(text)) / 2;
        graphics.drawString(minecraft.font, text, x, 8, 0xFFFFFFFF, true);
    }
}

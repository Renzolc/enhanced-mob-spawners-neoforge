package com.branders.spawnermod.client;

import java.util.List;

import com.branders.spawnermod.compat.WornSpawnerItems;
import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.registry.ModRegistry;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * HUD while a spawner compass is in the hotbar, offhand, or on the belt.
 * Tracking on: the in-game compass icon, with the distance in blocks under it.
 * Tracking off: one line, "A spawner has been detected nearby", and no distance.
 * Position comes from the mod config screen. Jade's tooltip pushes it aside.
 * The search itself stays the 128-block horizontal scan.
 */
public final class SpawnerCompassHud {

    private static final ItemStack ICON = new ItemStack(ModRegistry.SPAWNER_COMPASS.get());
    private static final int ICON_SIZE = 16;
    private static final int TEXT_GAP = 2;

    private SpawnerCompassHud() {
    }

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || minecraft.options.hideGui) {
            return;
        }
        if (!WornSpawnerItems.hasAnyCompass(minecraft.player)) {
            return;
        }

        BlockPos target = SpawnerCompassClient.findNearestTracked(minecraft.level, minecraft.player);
        if (target == null) {
            return;
        }

        if (WornSpawnerItems.hasActiveCompass(minecraft.player)) {
            renderTracking(graphics, minecraft, target);
        } else {
            renderDetected(graphics, minecraft);
        }
    }

    private static void renderTracking(GuiGraphicsExtractor graphics, Minecraft minecraft, BlockPos target) {
        double distance = Math.sqrt(minecraft.player.distanceToSqr(target.getX() + 0.5D, target.getY() + 0.5D,
                target.getZ() + 0.5D));
        int blocks = (int) Math.round(distance);
        Component text = Component.translatable("hud.spawnermod.spawner_distance", blocks);
        Font font = minecraft.font;
        int width = Math.max(ICON_SIZE, font.width(text));
        int height = ICON_SIZE + TEXT_GAP + font.lineHeight;
        int[] origin = place(graphics, width, height);

        int iconX = origin[0] + (width - ICON_SIZE) / 2;
        graphics.item(minecraft.player, ICON, iconX, origin[1], 0);
        int textX = origin[0] + (width - font.width(text)) / 2;
        graphics.text(font, text, textX, origin[1] + ICON_SIZE + TEXT_GAP, 0xFFFFFFFF, true);
    }

    private static void renderDetected(GuiGraphicsExtractor graphics, Minecraft minecraft) {
        Component text = Component.translatable("hud.spawnermod.spawner_detected");
        Font font = minecraft.font;
        int[] origin = place(graphics, font.width(text), font.lineHeight);
        graphics.text(font, text, origin[0], origin[1], 0xFFFFFFFF, true);
    }

    private static int[] place(GuiGraphicsExtractor graphics, int width, int height) {
        CompassHudAnchor anchor = CompassHudAnchor.byId(ConfigValues.get("compass_hud_anchor"));
        CompassHudPlacement.Box jade = JadeHudBounds.current();
        List<CompassHudPlacement.Box> obstacles = jade == null ? List.of() : List.of(jade);
        return CompassHudPlacement.place(anchor, graphics.guiWidth(), graphics.guiHeight(), width, height,
                ConfigValues.get("compass_hud_offset_x"), ConfigValues.get("compass_hud_offset_y"), obstacles);
    }
}

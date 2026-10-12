package com.branders.spawnermod.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.branders.spawnermod.SpawnerMod;

import net.neoforged.fml.ModList;

/** Jade's current tooltip box, in GUI pixels. Absent when Jade is hidden or not installed. */
public final class JadeHudBounds {

    private static boolean unavailable;
    private static Field shownField;
    private static Field alphaField;
    private static Field tooltipRectField;
    private static Field innerRectField;
    private static Method getX;
    private static Method getY;
    private static Method getWidth;
    private static Method getHeight;

    private JadeHudBounds() {
    }

    public static CompassHudPlacement.Box current() {
        if (unavailable || !ModList.get().isLoaded("jade")) {
            return null;
        }
        try {
            if (shownField == null) {
                Class<?> renderer = Class.forName("snownee.jade.overlay.OverlayRenderer");
                shownField = renderer.getField("shown");
                alphaField = renderer.getField("alpha");
                tooltipRectField = renderer.getField("rect");
                innerRectField = tooltipRectField.getType().getField("rect");
                Class<?> rectType = innerRectField.getType();
                getX = rectType.getMethod("getX");
                getY = rectType.getMethod("getY");
                getWidth = rectType.getMethod("getWidth");
                getHeight = rectType.getMethod("getHeight");
            }
            if (!shownField.getBoolean(null) || alphaField.getFloat(null) < 0.05F) {
                return null;
            }
            Object inner = innerRectField.get(tooltipRectField.get(null));
            int width = (Integer) getWidth.invoke(inner);
            int height = (Integer) getHeight.invoke(inner);
            if (width <= 0 || height <= 0) {
                return null;
            }
            return new CompassHudPlacement.Box((Integer) getX.invoke(inner), (Integer) getY.invoke(inner), width, height);
        } catch (ReflectiveOperationException e) {
            unavailable = true;
            SpawnerMod.LOGGER.warn("Could not read Jade's overlay position. The compass tracker will not move for it.");
            return null;
        }
    }
}

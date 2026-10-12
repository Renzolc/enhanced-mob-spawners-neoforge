package com.branders.spawnermod.client;

import net.minecraft.network.chat.Component;

/** Where the compass tracker sits before the manual pixel offset is applied. */
public enum CompassHudAnchor {
    TOP_CENTER("top_center"),
    TOP_LEFT("top_left"),
    TOP_RIGHT("top_right"),
    BOTTOM_LEFT("bottom_left"),
    BOTTOM_RIGHT("bottom_right"),
    BOTTOM_CENTER("bottom_center");

    private final String id;

    CompassHudAnchor(String id) {
        this.id = id;
    }

    public Component label() {
        return Component.translatable("options.spawnermod.compass_hud.anchor." + id);
    }

    public CompassHudAnchor next() {
        CompassHudAnchor[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public boolean top() {
        return this == TOP_CENTER || this == TOP_LEFT || this == TOP_RIGHT;
    }

    public static CompassHudAnchor byId(int id) {
        CompassHudAnchor[] values = values();
        if (id < 0 || id >= values.length) {
            return TOP_CENTER;
        }
        return values[id];
    }
}

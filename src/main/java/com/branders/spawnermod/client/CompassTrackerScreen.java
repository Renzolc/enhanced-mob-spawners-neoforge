package com.branders.spawnermod.client;

import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.config.ModConfigManager;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/** Pause menu → Mods → Enhanced Mob Spawners config. Placement of the compass tracker. */
public class CompassTrackerScreen extends Screen {

    private static final int OFFSET_LIMIT = 10000;

    private final Screen parent;
    private CompassHudAnchor anchor = CompassHudAnchor.TOP_CENTER;
    private int offsetX;
    private int offsetY;
    private boolean loaded;
    private EditBox xBox;
    private EditBox yBox;
    private Button anchorButton;

    public CompassTrackerScreen(Screen parent) {
        super(Component.translatable("options.spawnermod.compass_hud"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (xBox != null) {
            offsetX = parse(xBox.getValue());
            offsetY = parse(yBox.getValue());
        } else if (!loaded) {
            anchor = CompassHudAnchor.byId(ConfigValues.get("compass_hud_anchor"));
            offsetX = ConfigValues.get("compass_hud_offset_x");
            offsetY = ConfigValues.get("compass_hud_offset_y");
            loaded = true;
        }
        super.init();

        int center = this.width / 2;
        int top = this.height / 2 - 52;
        anchorButton = Button.builder(anchorLabel(), button -> {
            anchor = anchor.next();
            button.setMessage(anchorLabel());
        }).bounds(center - 100, top, 200, 20).build();
        addRenderableWidget(anchorButton);

        xBox = new EditBox(this.font, center - 100, top + 44, 96, 20, Component.translatable("options.spawnermod.compass_hud.x"));
        yBox = new EditBox(this.font, center + 4, top + 44, 96, 20, Component.translatable("options.spawnermod.compass_hud.y"));
        xBox.setMaxLength(6);
        yBox.setMaxLength(6);
        xBox.setFilter(CompassTrackerScreen::isInteger);
        yBox.setFilter(CompassTrackerScreen::isInteger);
        xBox.setValue(Integer.toString(offsetX));
        yBox.setValue(Integer.toString(offsetY));
        addRenderableWidget(xBox);
        addRenderableWidget(yBox);

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> save())
                .bounds(center - 100, top + 96, 200, 20).build());
    }

    private Component anchorLabel() {
        return Component.translatable("options.spawnermod.compass_hud.anchor", anchor.label());
    }

    private void save() {
        ConfigValues.put("compass_hud_anchor", anchor.ordinal());
        ConfigValues.put("compass_hud_offset_x", parse(xBox.getValue()));
        ConfigValues.put("compass_hud_offset_y", parse(yBox.getValue()));
        ModConfigManager.saveConfigToFile();
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 74, 0xFFFFFFFF);
        int center = this.width / 2;
        int top = this.height / 2 - 52;
        graphics.drawString(this.font, Component.translatable("options.spawnermod.compass_hud.x"), center - 100, top + 32,
                0xFFA0A0A0, false);
        graphics.drawString(this.font, Component.translatable("options.spawnermod.compass_hud.y"), center + 4, top + 32,
                0xFFA0A0A0, false);
        graphics.drawCenteredString(this.font, Component.translatable("options.spawnermod.compass_hud.hint"),
                this.width / 2, top + 72, 0xFFA0A0A0);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    private static boolean isInteger(String text) {
        if (text.isEmpty() || text.equals("-")) {
            return true;
        }
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int parse(String text) {
        try {
            return Math.max(-OFFSET_LIMIT, Math.min(OFFSET_LIMIT, Integer.parseInt(text)));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

package com.branders.spawnermod.gui;

import com.branders.spawnermod.SpawnerMod;
import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.networking.packet.SyncSpawnerPacket;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BaseSpawner;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Spawner GUI config screen.
 *
 * @author Anders &lt;Branders&gt; Blomqvist
 */
@OnlyIn(Dist.CLIENT)
public class SpawnerConfigGui extends Screen {

    private static class Data {
        short LOW, DEFAULT, HIGH, HIGHEST;

        public Data(int i, int j, int k, int l) {
            LOW = (short) i;
            DEFAULT = (short) j;
            HIGH = (short) k;
            HIGHEST = (short) l;
        }
    }

    private static final Data DELAY = new Data(30, 20, 10, 5);
    private static final Data MIN_SPAWN_DELAY = new Data(300, 200, 100, 50);
    private static final Data MAX_SPAWN_DELAY = new Data(900, 800, 400, 100);
    private static final Data SPAWN_COUNT = new Data(2, 4, 6, 12);
    private static final Data MAX_NEARBY_ENTITIES = new Data(6, 6, 12, 24);
    private static final Data REQUIRED_PLAYER_RANGE = new Data(16, 32, 64, 128);

    private static final Component TITLE_TEXT = Component.translatable("gui.spawnermod.spawner_config_screen_title");

    private static final ResourceLocation SPAWNER_CONFIG_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            SpawnerMod.MOD_ID, "textures/gui/spawner_config_screen.png");
    private static final ResourceLocation SPAWNS_ICON_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            SpawnerMod.MOD_ID, "textures/gui/spawner_config_screen_icon_spawns.png");
    private static final int SPAWNER_CONFIG_TEXTURE_WIDTH = 178;
    private static final int SPAWNER_CONFIG_TEXTURE_HEIGHT = 177;

    private Button countButton;
    private Button speedButton;
    private Button rangeButton;
    private Button disableButton;

    private int countOptionValue;
    private int speedOptionValue;
    private int rangeOptionValue;

    private short delay;
    private short minSpawnDelay;
    private short maxSpawnDelay;
    private short spawnCount;
    private short maxNearbyEntities;
    private short requiredPlayerRange;
    private boolean disabled;
    private short spawns;

    private final boolean cachedDisabled;
    private final boolean limitedSpawns;

    private boolean isCustomRange;
    private short customRange;

    private final BlockPos pos;

    public SpawnerConfigGui(Component title, BaseSpawner logic, BlockPos pos) {
        super(title);
        this.pos = pos;

        if (ConfigValues.get("default_spawner_range_enabled") == 1) {
            isCustomRange = true;
            customRange = (short) ConfigValues.get("default_spawner_range");
        }

        CompoundTag nbt = logic.save(new CompoundTag());
        delay = nbt.getShort("Delay");
        minSpawnDelay = nbt.getShort("MinSpawnDelay");
        maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
        spawnCount = nbt.getShort("SpawnCount");
        maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
        requiredPlayerRange = nbt.getShort("RequiredPlayerRange");

        short spawnRange = nbt.getShort("SpawnRange");
        if (spawnRange > 4) {
            disabled = true;
            cachedDisabled = true;
            requiredPlayerRange = spawnRange;
        } else {
            disabled = false;
            cachedDisabled = false;
        }

        countOptionValue = loadOptionState(spawnCount, SPAWN_COUNT);
        speedOptionValue = loadOptionState(minSpawnDelay, MIN_SPAWN_DELAY);
        rangeOptionValue = loadOptionState(requiredPlayerRange, REQUIRED_PLAYER_RANGE);

        if (ConfigValues.get("limited_spawns_enabled") != 0) {
            limitedSpawns = true;
            if (nbt.contains("spawns")) {
                spawns = nbt.getShort("spawns");
                if (ConfigValues.get("limited_spawns_amount") - spawns == 0) {
                    disabled = true;
                }
            }
        } else {
            limitedSpawns = false;
        }
    }

    @Override
    protected void init() {
        countButton = addRenderableWidget(Button.builder(
                Component.translatable("button.count." + getButtonText(countOptionValue)), button -> {
                    switch (countOptionValue) {
                    case 0 -> {
                        countOptionValue = 1;
                        spawnCount = SPAWN_COUNT.DEFAULT;
                        maxNearbyEntities = MAX_NEARBY_ENTITIES.DEFAULT;
                    }
                    case 1 -> {
                        countOptionValue = 2;
                        spawnCount = SPAWN_COUNT.HIGH;
                        maxNearbyEntities = MAX_NEARBY_ENTITIES.HIGH;
                    }
                    case 2 -> {
                        countOptionValue = 3;
                        spawnCount = SPAWN_COUNT.HIGHEST;
                        maxNearbyEntities = MAX_NEARBY_ENTITIES.HIGHEST;
                    }
                    case 3 -> {
                        countOptionValue = 0;
                        spawnCount = SPAWN_COUNT.LOW;
                        maxNearbyEntities = MAX_NEARBY_ENTITIES.LOW;
                    }
                    }
                    countButton.setMessage(Component.translatable("button.count." + getButtonText(countOptionValue)));
                }).bounds(width / 2 - 48, 55, 108, 20).build());

        speedButton = addRenderableWidget(Button.builder(
                Component.translatable("button.speed." + getButtonText(speedOptionValue)), button -> {
                    switch (speedOptionValue) {
                    case 0 -> {
                        speedOptionValue = 1;
                        delay = DELAY.DEFAULT;
                        minSpawnDelay = MIN_SPAWN_DELAY.DEFAULT;
                        maxSpawnDelay = MAX_SPAWN_DELAY.DEFAULT;
                    }
                    case 1 -> {
                        speedOptionValue = 2;
                        delay = DELAY.HIGH;
                        minSpawnDelay = MIN_SPAWN_DELAY.HIGH;
                        maxSpawnDelay = MAX_SPAWN_DELAY.HIGH;
                    }
                    case 2 -> {
                        speedOptionValue = 3;
                        delay = DELAY.HIGHEST;
                        minSpawnDelay = MIN_SPAWN_DELAY.HIGHEST;
                        maxSpawnDelay = MAX_SPAWN_DELAY.HIGHEST;
                    }
                    case 3 -> {
                        speedOptionValue = 0;
                        delay = DELAY.LOW;
                        minSpawnDelay = MIN_SPAWN_DELAY.LOW;
                        maxSpawnDelay = MAX_SPAWN_DELAY.LOW;
                    }
                    }
                    speedButton.setMessage(Component.translatable("button.speed." + getButtonText(speedOptionValue)));
                }).bounds(width / 2 - 48, 80, 108, 20).build());

        rangeButton = addRenderableWidget(Button.builder(
                Component.translatable("button.range." + getButtonText(rangeOptionValue))
                        .append(" " + requiredPlayerRange),
                button -> {
                    switch (rangeOptionValue) {
                    case 0 -> {
                        rangeOptionValue = 1;
                        requiredPlayerRange = REQUIRED_PLAYER_RANGE.DEFAULT;
                    }
                    case 1 -> {
                        rangeOptionValue = 2;
                        requiredPlayerRange = REQUIRED_PLAYER_RANGE.HIGH;
                    }
                    case 2 -> {
                        rangeOptionValue = 3;
                        requiredPlayerRange = REQUIRED_PLAYER_RANGE.HIGHEST;
                    }
                    case 3 -> {
                        if (isCustomRange) {
                            rangeOptionValue = 4;
                            requiredPlayerRange = customRange;
                        } else {
                            rangeOptionValue = 0;
                            requiredPlayerRange = REQUIRED_PLAYER_RANGE.LOW;
                        }
                    }
                    case 4 -> {
                        rangeOptionValue = 0;
                        requiredPlayerRange = REQUIRED_PLAYER_RANGE.LOW;
                    }
                    }
                    rangeButton.setMessage(Component.translatable("button.range." + getButtonText(rangeOptionValue))
                            .append(" " + requiredPlayerRange));
                }).bounds(width / 2 - 48, 105, 108, 20).build());

        disableButton = addRenderableWidget(Button.builder(
                Component.translatable("button.toggle." + getButtonText(disabled)), button -> {
                    if (disabled) {
                        disabled = false;
                        toggleButtons(true);
                        switch (rangeOptionValue) {
                        case 0 -> requiredPlayerRange = REQUIRED_PLAYER_RANGE.LOW;
                        case 1 -> requiredPlayerRange = REQUIRED_PLAYER_RANGE.DEFAULT;
                        case 2 -> requiredPlayerRange = REQUIRED_PLAYER_RANGE.HIGH;
                        case 3 -> requiredPlayerRange = REQUIRED_PLAYER_RANGE.HIGHEST;
                        }
                    } else {
                        disabled = true;
                        toggleButtons(false);
                        requiredPlayerRange = 0;
                    }
                    disableButton.setMessage(Component.translatable("button.toggle." + getButtonText(disabled)));
                }).bounds(width / 2 - 48, 130, 108, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("button.save"), button -> {
            configureSpawner();
            this.onClose();
        }).bounds(width / 2 - 89, 180 + 10, 178, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("button.cancel"), button -> {
            this.onClose();
        }).bounds(width / 2 - 89, 180 + 35, 178, 20).build());

        toggleButtons(!disabled);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);

        graphics.blit(SPAWNER_CONFIG_TEXTURE, width / 2 - SPAWNER_CONFIG_TEXTURE_WIDTH / 2, 5, 0, 0,
                SPAWNER_CONFIG_TEXTURE_WIDTH, SPAWNER_CONFIG_TEXTURE_HEIGHT, SPAWNER_CONFIG_TEXTURE_WIDTH,
                SPAWNER_CONFIG_TEXTURE_HEIGHT);

        int length = TITLE_TEXT.getString().length() * 2;
        graphics.drawString(font, TITLE_TEXT, width / 2 - length - 3, 33, 0xFFD964);

        if (limitedSpawns) {
            graphics.blit(SPAWNS_ICON_TEXTURE, width / 2 - 7 + 101, 23, 0, 0, 14, 14, 14, 14);
            graphics.drawString(font,
                    Component.literal("" + (ConfigValues.get("limited_spawns_amount") - spawns)), width / 2 + 114, 27,
                    0xFFFFFF);
        }
    }

    private void configureSpawner() {
        if (cachedDisabled && disabled)
            return;

        PacketDistributor.sendToServer(new SyncSpawnerPacket(pos, delay, spawnCount, requiredPlayerRange,
                maxNearbyEntities, minSpawnDelay, maxSpawnDelay));
    }

    private String getButtonText(int optionValue) {
        return switch (optionValue) {
        case 0 -> "low";
        case 1 -> "default";
        case 2 -> "high";
        case 3 -> "very_high";
        case 4 -> "custom";
        default -> "default";
        };
    }

    private String getButtonText(boolean disabled) {
        return disabled ? "disabled" : "enabled";
    }

    private int loadOptionState(short current, Data reference) {
        if (isCustomRange && current == customRange)
            return 4;
        if (current == reference.LOW)
            return 0;
        else if (current == reference.DEFAULT)
            return 1;
        else if (current == reference.HIGH)
            return 2;
        else if (current == reference.HIGHEST)
            return 3;
        else
            return 0;
    }

    private void toggleButtons(boolean state) {
        if (ConfigValues.get("disable_count") != 0) {
            countButton.active = false;
            countButton.setMessage(Component.translatable("button.count.disabled"));
        } else
            countButton.active = state;

        if (ConfigValues.get("disable_speed") != 0) {
            speedButton.active = false;
            speedButton.setMessage(Component.translatable("button.speed.disabled"));
        } else
            speedButton.active = state;

        if (ConfigValues.get("disable_range") != 0) {
            rangeButton.active = false;
            rangeButton.setMessage(Component.translatable("button.range.disabled"));
        } else
            rangeButton.active = state;
    }
}

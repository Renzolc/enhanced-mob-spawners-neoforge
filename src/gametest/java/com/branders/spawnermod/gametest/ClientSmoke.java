package com.branders.spawnermod.gametest;

import java.util.UUID;

import com.branders.spawnermod.SpawnerMod;
import com.branders.spawnermod.item.SpawnerKey;
import com.branders.spawnermod.registry.ModRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Dev-only client smoke test, enabled with {@code -Dspawnermod.smoke=true} (see the
 * {@code smokeWorld} Gradle property). After a world loads it places a spawner next to the
 * player, puts a compass and a key in the hotbar, screenshots the compass HUD, opens the
 * Spawner Key screen, screenshots it and quits.
 */
@EventBusSubscriber(modid = SpawnerMod.MOD_ID, value = Dist.CLIENT)
public final class ClientSmoke {

    private static final boolean ENABLED = Boolean.getBoolean("spawnermod.smoke");

    private static int ticks = -1;
    private static volatile BlockPos spawnerPos;

    private ClientSmoke() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!ENABLED) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        ticks++;
        if (ticks == 40) {
            IntegratedServer server = mc.getSingleplayerServer();
            UUID id = mc.player.getUUID();
            server.execute(() -> {
                ServerPlayer player = server.getPlayerList().getPlayer(id);
                ServerLevel level = (ServerLevel) player.level();
                BlockPos pos = player.blockPosition().offset(3, 0, 0);
                level.setBlockAndUpdate(pos, Blocks.SPAWNER.defaultBlockState());
                if (level.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner) {
                    spawner.setEntityId(EntityTypes.PIG, level.getRandom());
                }
                player.getInventory().setItem(0, new ItemStack(ModRegistry.SPAWNER_COMPASS.get()));
                player.getInventory().setItem(1, new ItemStack(ModRegistry.SPAWNER_KEY.get()));
                spawnerPos = pos;
                SpawnerMod.LOGGER.info("EMS_SMOKE placed spawner at {}", pos);
            });
        } else if (ticks == 100) {
            screenshot(mc, "ems-smoke-hud.png");
        } else if (ticks == 120) {
            BlockPos pos = spawnerPos;
            if (pos != null && mc.level.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner) {
                SpawnerKey.openScreen(spawner.getSpawner(), pos);
                SpawnerMod.LOGGER.info("EMS_SMOKE opened screen {}", mc.gui.screen());
            } else {
                SpawnerMod.LOGGER.error("EMS_SMOKE no client spawner at {}", pos);
            }
        } else if (ticks == 160) {
            screenshot(mc, "ems-smoke-gui.png");
        } else if (ticks == 180) {
            SpawnerMod.LOGGER.info("EMS_SMOKE_DONE screen={}", mc.gui.screen());
            mc.stop();
        }
    }

    private static void screenshot(Minecraft mc, String name) {
        Screenshot.grab(mc.gameDirectory, name, mc.gameRenderer.mainRenderTarget(), 1,
                message -> SpawnerMod.LOGGER.info("EMS_SMOKE screenshot {}", message.getString()));
    }
}

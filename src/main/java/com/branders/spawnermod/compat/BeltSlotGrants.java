package com.branders.spawnermod.compat;

import com.branders.spawnermod.SpawnerMod;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;

/**
 * Extra Curios belt slots are applied only when both Curios and Supplementaries
 * are loaded. Curios classes stay in {@link CuriosBeltSlots} so this mod still
 * loads when either mod is absent.
 */
public final class BeltSlotGrants {

    /** Spawner key, spawner compass, Supplementaries key, Supplementaries quiver. */
    public static final int MIN_BELT_SLOTS = 4;

    private static final int MAX_ATTEMPTS = 20;

    private BeltSlotGrants() {
    }

    public static void schedule(ServerPlayer player) {
        if (!ModList.get().isLoaded("curios") || !ModList.get().isLoaded("supplementaries")) {
            return;
        }
        enqueue(player, 0);
    }

    private static void enqueue(ServerPlayer player, int attempt) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        server.tell(new TickTask(server.getTickCount() + 1, () -> {
            if (player.hasDisconnected()) {
                return;
            }
            boolean applied = CuriosBeltSlots.applyShortfall(player);
            if (!applied && attempt + 1 < MAX_ATTEMPTS) {
                enqueue(player, attempt + 1);
            } else if (!applied) {
                SpawnerMod.LOGGER.debug("Curios belt slot was not ready; left belt size unchanged.");
            }
        }));
    }
}

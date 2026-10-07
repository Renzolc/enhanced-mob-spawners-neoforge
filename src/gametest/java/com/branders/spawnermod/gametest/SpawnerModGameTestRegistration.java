package com.branders.spawnermod.gametest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.branders.spawnermod.SpawnerMod;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.gametest.GameTestHooks;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registers the GameTests with the data-driven test system (Minecraft 1.21.5+): each test body is
 * a {@code test_function} registry entry, and each test is a {@link FunctionGameTestInstance}
 * using the {@code spawnermod:empty} structure. Tests in different environments run in separate
 * batches, so the limited-spawns and Curios tests each get their own environment.
 */
@EventBusSubscriber(modid = SpawnerMod.MOD_ID)
public final class SpawnerModGameTestRegistration {

    private static final ResourceLocation STRUCTURE = id("empty");
    private static final int DEFAULT_TICKS = 100;

    private record Test(String env, int maxTicks, Consumer<GameTestHelper> body) {
    }

    private static final Map<String, Test> TESTS = new LinkedHashMap<>();

    static {
        TESTS.put("key_settings_applied_server_side",
                new Test("default", DEFAULT_TICKS, SpawnerModGameTests::keySettingsAppliedServerSide));
        TESTS.put("key_durability_16_to_15",
                new Test("default", DEFAULT_TICKS, SpawnerModGameTests::keyDurability16To15));
        TESTS.put("silk_touch_drops_spawner",
                new Test("default", DEFAULT_TICKS, SpawnerModGameTests::silkTouchDropsSpawner));
        TESTS.put("spawn_harvest_kill_drops_egg",
                new Test("default", DEFAULT_TICKS, SpawnerModGameTests::spawnHarvestKillDropsEgg));
        TESTS.put("redstone_toggles_spawner",
                new Test("default", DEFAULT_TICKS, SpawnerModGameTests::redstoneTogglesSpawner));
        TESTS.put("compass_flag_survives_save_and_load",
                new Test("default", DEFAULT_TICKS, SpawnerModGameTests::compassFlagSurvivesSaveAndLoad));
        TESTS.put("limited_spawns_counter",
                new Test("limited_spawns", 200, SpawnerModGameTests::limitedSpawnsCounter));
        TESTS.put("curios_belt_slot",
                new Test("curios", DEFAULT_TICKS, SpawnerModGameTests::curiosBeltSlot));
    }

    private SpawnerModGameTestRegistration() {
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(SpawnerMod.MOD_ID, path);
    }

    @SubscribeEvent
    public static void registerFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> TESTS.forEach((name, test) -> helper.register(id(name), test.body())));
    }

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        // The dev runServer/runClient runs also load this source set. NeoForge fires this event on
        // a dedicated server only after the test registries are frozen, so register in the
        // GameTest server only.
        if (!GameTestHooks.isGametestServer()) {
            return;
        }
        Map<String, Holder<TestEnvironmentDefinition>> environments = new LinkedHashMap<>();
        TESTS.forEach((name, test) -> {
            Holder<TestEnvironmentDefinition> env = environments.computeIfAbsent(test.env(),
                    e -> event.registerEnvironment(id(e)));
            event.registerTest(id(name), new FunctionGameTestInstance(
                    ResourceKey.create(Registries.TEST_FUNCTION, id(name)),
                    new TestData<>(env, STRUCTURE, test.maxTicks(), 0, true)));
        });
    }
}

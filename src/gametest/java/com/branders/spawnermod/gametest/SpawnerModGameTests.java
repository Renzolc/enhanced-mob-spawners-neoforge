package com.branders.spawnermod.gametest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.branders.spawnermod.SpawnerMod;
import com.branders.spawnermod.compat.WornSpawnerItems;
import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.enchantment.ModEnchantments;
import com.branders.spawnermod.networking.SpawnerModNetworking;
import com.branders.spawnermod.networking.packet.SyncSpawnerPacket;
import com.branders.spawnermod.registry.ModRegistry;
import com.branders.spawnermod.spawner.CompassTrackingAccess;
import com.mojang.authlib.GameProfile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the server-side behaviour of Enhanced Mob Spawners.
 * Run with {@code ./gradlew runGameTestServer}.
 */
@GameTestHolder(SpawnerMod.MOD_ID)
@PrefixGameTestTemplate(false)
public class SpawnerModGameTests {

    private static final String EMPTY = "empty";
    private static final BlockPos SPAWNER = new BlockPos(3, 1, 3);

    // ---------------------------------------------------------------- helpers

    private static SpawnerBlockEntity placeSpawner(GameTestHelper helper, EntityType<?> type) {
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                helper.setBlock(x, 0, z, Blocks.STONE);
            }
        }
        helper.setBlock(SPAWNER, Blocks.SPAWNER);
        BlockEntity be = helper.getBlockEntity(SPAWNER);
        if (!(be instanceof SpawnerBlockEntity spawner)) {
            throw new IllegalStateException("No spawner block entity at " + SPAWNER);
        }
        spawner.setEntityId(type, helper.getLevel().getRandom());
        return spawner;
    }

    private static FakePlayer fakePlayer(GameTestHelper helper, String name) {
        return FakePlayerFactory.get(helper.getLevel(),
                new GameProfile(UUID.nameUUIDFromBytes(("spawnermod_" + name).getBytes()), "ems_" + name));
    }

    private static Holder<Enchantment> enchantment(ServerLevel level, ResourceKey<Enchantment> key) {
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key);
    }

    private static CompoundTag spawnerNbt(SpawnerBlockEntity spawner) {
        return spawner.getSpawner().save(new CompoundTag());
    }

    /**
     * A fake player placed in the level so {@code BaseSpawner#isNearPlayer} sees it. It has no
     * network connection, so nothing is sent to it, and no login events fire.
     */
    private static FakePlayer addWatcher(GameTestHelper helper, BlockPos absolutePos) {
        ServerLevel level = helper.getLevel();
        FakePlayer watcher = new FakePlayer(level, new GameProfile(UUID.randomUUID(), "ems_watcher"));
        watcher.moveTo(absolutePos.getX() + 0.5D, absolutePos.getY() + 2.0D, absolutePos.getZ() + 0.5D);
        level.addNewPlayer(watcher);
        return watcher;
    }

    private static void removeWatcher(GameTestHelper helper, FakePlayer watcher) {
        helper.getLevel().removePlayerImmediately(watcher, Entity.RemovalReason.DISCARDED);
    }

    private static void assertShort(GameTestHelper helper, CompoundTag nbt, String key, int expected) {
        int actual = nbt.getShort(key);
        helper.assertTrue(actual == expected, key + " expected " + expected + " but was " + actual);
    }

    // ------------------------------------------------------------------ tests

    /** Settings sent by the Spawner Key GUI are written to the spawner on the server. */
    @GameTest(template = EMPTY)
    public static void keySettingsAppliedServerSide(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        SpawnerBlockEntity spawner = placeSpawner(helper, EntityType.PIG);
        BlockPos pos = helper.absolutePos(SPAWNER);
        FakePlayer player = fakePlayer(helper, "settings");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModRegistry.SPAWNER_KEY.get()));
        try {
            helper.assertTrue(SpawnerModNetworking.applySpawnerSettings(level,
                    new SyncSpawnerPacket(pos, 10, 6, 64, 12, 100, 400), player), "settings were not applied");
            CompoundTag nbt = spawnerNbt(spawner);
            assertShort(helper, nbt, "Delay", 10);
            assertShort(helper, nbt, "SpawnCount", 6);
            assertShort(helper, nbt, "RequiredPlayerRange", 64);
            assertShort(helper, nbt, "MaxNearbyEntities", 12);
            assertShort(helper, nbt, "MinSpawnDelay", 100);
            assertShort(helper, nbt, "MaxSpawnDelay", 400);
            assertShort(helper, nbt, "SpawnRange", 4);

            // Disable toggle: range 0 parks the old range in SpawnRange.
            SpawnerModNetworking.applySpawnerSettings(level, new SyncSpawnerPacket(pos, 10, 6, 0, 12, 100, 400),
                    player);
            nbt = spawnerNbt(spawner);
            assertShort(helper, nbt, "RequiredPlayerRange", 0);
            assertShort(helper, nbt, "SpawnRange", 64);
        } finally {
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        helper.succeed();
    }

    /** The key has 16 durability and each applied save costs one. */
    @GameTest(template = EMPTY)
    public static void keyDurability16To15(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        placeSpawner(helper, EntityType.ZOMBIE);
        BlockPos pos = helper.absolutePos(SPAWNER);
        ItemStack key = new ItemStack(ModRegistry.SPAWNER_KEY.get());
        helper.assertTrue(key.getMaxDamage() == 16, "key max durability is " + key.getMaxDamage());
        helper.assertTrue(key.getDamageValue() == 0, "new key is already damaged");

        FakePlayer player = fakePlayer(helper, "durability");
        player.setItemInHand(InteractionHand.MAIN_HAND, key);
        try {
            SpawnerModNetworking.applySpawnerSettings(level, new SyncSpawnerPacket(pos, 20, 4, 32, 6, 200, 800),
                    player);
            ItemStack after = player.getMainHandItem();
            helper.assertTrue(after.is(ModRegistry.SPAWNER_KEY.get()), "key disappeared");
            int remaining = after.getMaxDamage() - after.getDamageValue();
            helper.assertTrue(remaining == 15, "remaining durability expected 15 but was " + remaining);
        } finally {
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        helper.succeed();
    }

    /** Silk touch makes a spawner drop itself; a plain pickaxe does not. */
    @GameTest(template = EMPTY)
    public static void silkTouchDropsSpawner(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        SpawnerBlockEntity spawner = placeSpawner(helper, EntityType.SKELETON);
        BlockPos pos = helper.absolutePos(SPAWNER);
        BlockState state = level.getBlockState(pos);

        ItemStack silkPick = new ItemStack(Items.DIAMOND_PICKAXE);
        silkPick.enchant(enchantment(level, Enchantments.SILK_TOUCH), 1);
        List<ItemStack> silkDrops = state.getDrops(new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, silkPick)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, spawner));
        helper.assertTrue(silkDrops.stream().anyMatch(s -> s.is(Items.SPAWNER)),
                "silk touch drops were " + silkDrops);

        List<ItemStack> plainDrops = state.getDrops(new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE))
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, spawner));
        helper.assertFalse(plainDrops.stream().anyMatch(s -> s.is(Items.SPAWNER)),
                "plain pickaxe dropped a spawner: " + plainDrops);
        helper.succeed();
    }

    /** A kill with a Spawn Harvest weapon drops the mob's spawn egg; a plain weapon does not. */
    @GameTest(template = EMPTY)
    public static void spawnHarvestKillDropsEgg(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                helper.setBlock(x, 0, z, Blocks.STONE);
            }
        }
        BlockPos harvestPos = new BlockPos(1, 1, 1);
        BlockPos plainPos = new BlockPos(5, 1, 5);
        Pig harvested = helper.spawnWithNoFreeWill(EntityType.PIG, harvestPos);
        Pig plain = helper.spawnWithNoFreeWill(EntityType.PIG, plainPos);

        FakePlayer player = fakePlayer(helper, "harvest");
        try {
            ItemStack sword = new ItemStack(Items.IRON_SWORD);
            sword.enchant(enchantment(level, ModEnchantments.SPAWN_HARVEST), 1);
            player.setItemInHand(InteractionHand.MAIN_HAND, sword);
            harvested.hurt(level.damageSources().playerAttack(player), Float.MAX_VALUE);

            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
            plain.hurt(level.damageSources().playerAttack(player), Float.MAX_VALUE);
        } finally {
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }

        helper.succeedWhen(() -> {
            helper.assertTrue(harvested.isDeadOrDying() && plain.isDeadOrDying(), "pigs are still alive");
            helper.assertItemEntityPresent(Items.PIG_SPAWN_EGG, harvestPos, 2.0D);
            helper.assertItemEntityNotPresent(Items.PIG_SPAWN_EGG, plainPos, 2.0D);
        });
    }

    /** With limited spawns on, a spawner counts its spawns and shuts itself off at the limit. */
    @GameTest(template = EMPTY, batch = "spawnermod_limited_spawns", timeoutTicks = 200)
    public static void limitedSpawnsCounter(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        SpawnerBlockEntity spawner = placeSpawner(helper, EntityType.PIG);
        BlockPos pos = helper.absolutePos(SPAWNER);

        // A player in the level is needed for BaseSpawner#isNearPlayer.
        FakePlayer watcher = addWatcher(helper, pos);

        int oldEnabled = ConfigValues.get("limited_spawns_enabled");
        int oldAmount = ConfigValues.get("limited_spawns_amount");
        ConfigValues.put("limited_spawns_enabled", 1);
        ConfigValues.put("limited_spawns_amount", 2);

        CompoundTag entity = new CompoundTag();
        entity.putString("id", "minecraft:pig");
        entity.putBoolean("NoAI", true);
        SpawnData data = new SpawnData(entity,
                Optional.of(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15), new InclusiveRange<>(0, 15))),
                Optional.empty());

        CompoundTag nbt = spawnerNbt(spawner);
        nbt.put("SpawnData", SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, data).getOrThrow());
        nbt.remove("SpawnPotentials");
        nbt.putShort("Delay", (short) 0);
        nbt.putShort("MinSpawnDelay", (short) 2);
        nbt.putShort("MaxSpawnDelay", (short) 2);
        nbt.putShort("SpawnCount", (short) 1);
        nbt.putShort("MaxNearbyEntities", (short) 20);
        nbt.putShort("RequiredPlayerRange", (short) 16);
        nbt.putShort("SpawnRange", (short) 4);
        nbt.putShort("spawns", (short) 0);
        spawner.getSpawner().load(level, pos, nbt);

        helper.runAfterDelay(60, () -> {
            CompoundTag after = spawnerNbt(spawner);
            int pigs = level.getEntitiesOfClass(Pig.class, new AABB(pos).inflate(6.0D)).size();
            BlockPos watcherPos = watcher.blockPosition();
            removeWatcher(helper, watcher);
            ConfigValues.put("limited_spawns_enabled", oldEnabled);
            ConfigValues.put("limited_spawns_amount", oldAmount);

            helper.assertTrue(after.getShort("spawns") == 2, "spawn counter is " + after.getShort("spawns")
                    + " (pigs=" + pigs + ", watcher at " + watcherPos + ", spawner at " + pos + ")");
            helper.assertTrue(after.getShort("RequiredPlayerRange") == 0,
                    "spawner was not shut off, RequiredPlayerRange=" + after.getShort("RequiredPlayerRange"));
            helper.assertTrue(pigs == 2, "expected 2 spawned pigs but found " + pigs);
            helper.succeed();
        });
    }

    /**
     * With Curios installed, a key or compass on the belt works without holding it.
     * Run with {@code ./gradlew runGameTestServer -PwithCurios}; without Curios this test only
     * checks that the mod reports no belt items.
     */
    @GameTest(template = EMPTY, batch = "spawnermod_curios")
    public static void curiosBeltSlot(GameTestHelper helper) {
        placeSpawner(helper, EntityType.PIG);
        BlockPos pos = helper.absolutePos(SPAWNER);
        FakePlayer player = fakePlayer(helper, "curios");
        try {
            if (ModList.get().isLoaded("curios")) {
                CuriosBeltChecks.run(helper, player, pos);
                SpawnerMod.LOGGER.info("curiosBeltSlot: checked with Curios loaded");
            } else {
                helper.assertFalse(WornSpawnerItems.hasBeltKey(player), "belt key reported without Curios");
                SpawnerMod.LOGGER.info("curiosBeltSlot: Curios not loaded, belt checks skipped");
            }
        } finally {
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        helper.succeed();
    }

    /** A redstone signal next to a spawner switches it off; removing the signal switches it back on. */
    @GameTest(template = EMPTY)
    public static void redstoneTogglesSpawner(GameTestHelper helper) {
        SpawnerBlockEntity spawner = placeSpawner(helper, EntityType.PIG);
        int range = spawnerNbt(spawner).getShort("RequiredPlayerRange");
        helper.assertTrue(range > 4, "unexpected default range " + range);

        helper.setBlock(SPAWNER.east(), Blocks.REDSTONE_BLOCK);
        CompoundTag powered = spawnerNbt(spawner);
        assertShort(helper, powered, "RequiredPlayerRange", 0);
        assertShort(helper, powered, "SpawnRange", range);

        helper.setBlock(SPAWNER.east(), Blocks.AIR);
        CompoundTag unpowered = spawnerNbt(spawner);
        assertShort(helper, unpowered, "RequiredPlayerRange", range);
        assertShort(helper, unpowered, "SpawnRange", 4);
        helper.succeed();
    }

    /** The compass ignore toggle is saved with the spawner and read back on load. */
    @GameTest(template = EMPTY)
    public static void compassFlagSurvivesSaveAndLoad(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        SpawnerBlockEntity spawner = placeSpawner(helper, EntityType.SPIDER);
        BlockPos pos = helper.absolutePos(SPAWNER);
        BlockState state = level.getBlockState(pos);

        helper.assertTrue(((CompassTrackingAccess) spawner.getSpawner()).spawnermod$isCompassTracking(),
                "new spawner should be tracked");
        helper.assertTrue(SpawnerModNetworking.applyTracking(level, pos, false), "toggle was not applied");

        CompoundTag saved = spawner.saveWithFullMetadata(level.registryAccess());
        helper.assertTrue(saved.contains(CompassTrackingAccess.NBT_KEY), "flag missing from saved NBT");

        BlockEntity loaded = BlockEntity.loadStatic(pos, state, saved, level.registryAccess());
        helper.assertTrue(loaded instanceof SpawnerBlockEntity, "reloaded block entity is " + loaded);
        helper.assertFalse(((CompassTrackingAccess) ((SpawnerBlockEntity) loaded).getSpawner())
                .spawnermod$isCompassTracking(), "ignored spawner became tracked after reload");

        // Spawners saved before the flag existed stay tracked.
        saved.remove(CompassTrackingAccess.NBT_KEY);
        BlockEntity legacy = BlockEntity.loadStatic(pos, state, saved, level.registryAccess());
        helper.assertTrue(((CompassTrackingAccess) ((SpawnerBlockEntity) legacy).getSpawner())
                .spawnermod$isCompassTracking(), "legacy spawner should be tracked");

        // And back on again.
        SpawnerModNetworking.applyTracking(level, pos, true);
        BlockEntity again = BlockEntity.loadStatic(pos, state,
                spawner.saveWithFullMetadata(level.registryAccess()), level.registryAccess());
        helper.assertTrue(((CompassTrackingAccess) ((SpawnerBlockEntity) again).getSpawner())
                .spawnermod$isCompassTracking(), "re-enabled spawner is not tracked after reload");
        helper.succeed();
    }
}

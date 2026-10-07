# Port notes: Enhanced Mob Spawners Fabric → NeoForge 1.21.1

## Versions
- Minecraft 1.21.1
- NeoForge 21.1.251
- ModDevGradle 2.0.147
- Mod version 1.2.8 (same as Fabric)
- Package / mod id unchanged: `com.branders.spawnermod` / `spawnermod`
- License CC0-1.0, attribution Branders / Anders Blomqvist

## Fabric → NeoForge remapping

| Fabric | NeoForge |
|--------|----------|
| `ModInitializer` / `ClientModInitializer` | `@Mod` + `IEventBus` / client Dist init |
| Yarn names | Official/Mojmap (+ Parchment) |
| `UseBlockCallback` | `PlayerInteractEvent.RightClickBlock` |
| `PlayerBlockBreakEvents.BEFORE` | `BlockEvent.BreakEvent` |
| `LootTableEvents.MODIFY` | Global Loot Modifier (`spawner_silk_touch`) |
| Fabric networking `PayloadTypeRegistry` | `RegisterPayloadHandlersEvent` + `PayloadRegistrar` |
| `ServerPlayNetworking` / `ClientPlayNetworking` | `PacketDistributor` / `ClientPacketDistributor` |
| `ItemGroupEvents` | `BuildCreativeModeTabContentsEvent` |
| `CommandRegistrationCallback` | `RegisterCommandsEvent` |
| `FabricLoader.getConfigDir()` | `FMLPaths.CONFIGDIR` |
| `MobSpawnerLogic` | `BaseSpawner` |
| Mixins | Kept; targets rewritten to Mojmap |

## Mixins (all ported)
LimitedSpawnsMixin, SpawnerBlockExpMixin, MobEntityDropsMixin, SpawnerBlockCancelDropMixin, UpdateNeighborMixin, SpawnerRangeMixin, BlockHardnessMixin

## Enhancement: Spawner Key enchantability
- Durability raised from Fabric **10** → **64** (tool-like; each GUI save still costs 1 durability).
- `isEnchantable` = true, `getEnchantmentValue` = 14 (iron-tool-like).
- `supportsEnchantment` explicitly allows **Mending** and **Unbreaking**.
- Added to `#minecraft:enchantable/durability` so enchanting table / anvil vanilla rules apply.
- Still has the cosmetic enchanted glint (`isFoil`).

## Known gaps / notes
- `/ems` entity egg toggles use sanitized literals (`minecraft_pig` instead of `minecraft:pig`) because Brigadier literals cannot contain `:`.
- Commands registered for INTEGRATED (and ALL) selection, not DEDICATED — matches Fabric integrated-only.
- Carrier mod sneak-bypass check uses `ModList.isLoaded("carrier")`.
- Runtime mixin inject targets may need tweaking if Mojmap method bodies differ slightly from Yarn; compile does not verify inject hits.

## NeoForge 21.1 networking note
On NeoForge 21.1.x, `playToClient` takes the handler directly on `PayloadRegistrar`
(there is no `RegisterClientPayloadHandlersEvent` / `ClientPacketDistributor` —
those appear in newer NeoForge docs). Client→server uses `PacketDistributor.sendToServer`.
StreamCodec.composite is limited to 6 fields; packets with more use `StreamCodec.of`.

## Feature: Spawn Harvest enchantment
- Data-driven enchantment: `data/spawnermod/enchantment/spawn_harvest.json` (max level 1, weight 2 = rare).
- Supported items tag: `#spawnermod:enchantable/spawn_harvest` (swords, axes via sharp_weapon, bows, crossbows; optional FD knives / `c:tools/knife`).
- Listed in `#minecraft:non_treasure` so it appears in the enchanting table.
- Kill egg drops: **only** when the killing weapon has Spawn Harvest (100%). `DamageSource#getWeaponItem()` covers melee main-hand and bow/crossbow projectiles.
- Config `monster_egg_drop_chance` is ignored for drops (random chance removed).

## Port to Minecraft 1.21.4 (NeoForge 21.4.158, branch `mc/1.21.4`)
- `Item.Properties` IDs are set by `DeferredRegister.Items#registerItem`; enchantability is now the
  `enchantable(14)` property (the `isEnchantable`/`getEnchantmentValue` overrides are gone).
- Item models: `assets/spawnermod/items/*.json`. The compass needle uses a custom
  `range_dispatch` property `spawnermod:spawner_compass` (`SpawnerCompassAngle`, registered with
  `RegisterRangeSelectItemModelPropertyEvent`), replacing `ItemProperties.register`.
- Recipes use the 1.21.2+ ingredient format (`"minecraft:diamond"` instead of `{"item": ...}`).
- `GuiGraphics#blit` takes `RenderType::guiTextured`.
- `BlockBehaviour#neighborChanged` no longer passes the source position; the redstone toggle
  now reacts when the notified block is the spawner itself.
- `Mob#dropFromLootTable` gained a `ServerLevel` parameter. `MinecraftServer#tell` is `schedule`.
- `Registry#get(ResourceLocation)` returns an `Optional`; lookups use `getValue`.

## Port to Minecraft 1.21.5 (NeoForge 21.5.98, branch `mc/1.21.5`)
- `CompoundTag` getters return `Optional`. All spawner NBT access goes through
  `spawner/SpawnerNbt` (`getShortOr`, `getBooleanOr`, entity id from `SpawnData.entity.id`).
- `BaseSpawner#load` no longer calls `CompoundTag#getShort`, so the limited-spawns and range
  mixins read their fields at `HEAD` of `load` and write them into the returned tag at `RETURN`
  of `save`.
- `Tag#getAsString` is gone; the egg drop and limited-spawns checks read the entity id from the
  `SpawnData` compound instead of parsing SNBT.
- `Item#appendHoverText` takes `TooltipDisplay` and a `Consumer<Component>`.
- GameTests use the data-driven system: test bodies are `minecraft:test_function` entries and
  `SpawnerModGameTestRegistration` registers `FunctionGameTestInstance`s with
  `RegisterGameTestsEvent`. The limited-spawns and Curios tests have their own environments, so
  they run in separate batches. Tests are only registered in the GameTest server (NeoForge fires
  the event after the registries are frozen on a dev dedicated server).
- `Entity#moveTo` is `snapTo`; `GameTestHelper#assertTrue` takes a `Component`.

## Port to Minecraft 1.21.8 (NeoForge 21.8.54, branch `mc/1.21.8`)
- `BaseSpawner#load`/`save` take `ValueInput`/`ValueOutput` (1.21.6). `SpawnerNbt` wraps them with
  `TagValueOutput`/`TagValueInput`, so the rest of the code still edits a plain `CompoundTag`. The
  limited-spawns, range and compass-tracking mixins read from the `ValueInput` at `HEAD`/`RETURN` of
  `load` and write into the `ValueOutput` at `RETURN` of `save`. The spawn-limit check now targets
  `EntityType#by(ValueInput)`.
- NeoForge 21.6+ no longer strips `@OnlyIn` members. The annotations are gone and the screen opening
  moved to the client-only `client/SpawnerKeyScreen`, so `SpawnerKey` loads on a dedicated server.
- GUI rendering: `GuiGraphics#blit` takes `RenderPipelines.GUI_TEXTURED`, and text colours need an
  alpha channel (`0xFFFFFFFF`); text with alpha 0 is no longer drawn.
- Client to server packets use `ClientPacketDistributor.sendToServer`.
- `ServerPlayer#serverLevel()` is `level()`.
- Curios 12 fills a player's slots when the player joins the level, so the Curios GameTest calls
  `ICuriosItemHandler#loadDatapacks` for its fake player.

## Port to Minecraft 1.21.10 (NeoForge 21.10.64, branch `mc/1.21.10`)
- Item model properties get an `ItemOwner` instead of a `LivingEntity`. `SpawnerCompassAngle` uses
  the entity behind the owner (or spins when there is none).
- `Level#isClientSide` is a method, `Entity#getServer` is gone (`level().getServer()`), and
  `FMLEnvironment.dist` is `FMLEnvironment.getDist()`.
- `GameTestHooks#isGametestServer` is gone; the GameTest registration checks
  `ServerModLoader#isGameTestServer`.

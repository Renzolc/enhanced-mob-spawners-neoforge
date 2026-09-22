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

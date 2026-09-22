# Enhanced Mob Spawners (NeoForge)

NeoForge port of **[Enhanced Mob Spawners](https://github.com/andersblomqvist/enhanced-mob-spawners)** by **Branders (Anders Blomqvist)**.

| | |
|---|---|
| **Mod id** | `spawnermod` |
| **Version** | 1.0.0 |
| **Minecraft** | 1.21.1 |
| **Loader** | NeoForge |
| **License** | [CC0-1.0](LICENSE) (same as upstream) |

## Credits

This project is based on the original **Enhanced Mob Spawners** Fabric/Forge mod by **Branders (Anders Blomqvist)**:

- Source: https://github.com/andersblomqvist/enhanced-mob-spawners
- CurseForge: https://www.curseforge.com/minecraft/mc-mods/enhanced-mob-spawners
- Modrinth: https://modrinth.com/mod/enhanced-mob-spawners

All credit for the original design, features, assets, and implementation goes to Branders. This NeoForge build is a community port with the changes listed below.

## What's new in this NeoForge fork (1.0.0)

- **NeoForge 1.21.1 port** of the Fabric 1.2.8 / Minecraft 1.21.1 build (APIs, networking, events, mixins remapped to Mojmap).
- **Spawner Key** is enchantable with **Mending** and **Unbreaking** (durability raised to 64 so those enchantments matter).
- New **treasure** enchantment **Spawn Harvest** (level 1 only; not on the enchanting table, very rare in loot):
  - Applies to swords, axes, bows, crossbows, and knives (including Farmer's Delight when present).
  - Killing a mob with a Spawn Harvest weapon **always** drops that mob's spawn egg.
  - The old **random** spawn-egg drop chance from kills is **removed** — eggs from kills only drop with Spawn Harvest.

## Original features (unchanged intent)

- Silk Touch harvestable spawners
- Spawner Key + in-game config GUI (count / speed / range / on-off)
- Egg removal from spawners, limited spawns, hardness / default range options
- Redstone toggle, JSON config, `/ems` commands (integrated)

## Build

```bash
./gradlew build
```

Jar: `build/libs/spawnermod-1.0.0.jar`

## Port notes

See [`PORT_NOTES.md`](PORT_NOTES.md) for Fabric → NeoForge API mapping details.

# Enhanced Mob Spawners (NeoForge)

NeoForge port of **[Enhanced Mob Spawners](https://github.com/andersblomqvist/enhanced-mob-spawners)** by **Branders (Anders Blomqvist)**.

| | |
|---|---|
| **Mod id** | `spawnermod` |
| **Version** | 1.2.13 |
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
- **Spawner Key** is enchantable with **Mending** and **Unbreaking** (durability 16 so those enchantments matter).
- New **treasure** enchantment **Spawn Harvest** (level 1 only; not on the enchanting table, very rare in loot):
  - Applies to swords, axes, bows, crossbows, and knives (including Farmer's Delight when present).
  - Killing a mob with a Spawn Harvest weapon **always** drops that mob's spawn egg.
  - The old **random** spawn-egg drop chance from kills is **removed** — eggs from kills only drop with Spawn Harvest.

## Original features (unchanged intent)

- Silk Touch harvestable spawners
- Spawner Key + in-game config GUI (count / speed / range / on-off)
- Egg removal from spawners, limited spawns, hardness / default range options
- Redstone toggle, JSON config, `/ems` commands (integrated)


## Spawner Compass

Craft a **Spawner Compass** with one compass and one mob spawner (shapeless). Hold it in either hand and it points at the nearest loaded mob spawner within 128 blocks, the same way a compass points at a lodestone. If nothing in range can be tracked, the needle spins.

The Spawner Key config screen has a **Compass tracking** button on the bottom row. Click it to turn tracking off or on for that spawner. The choice is saved on the spawner, and spawners you have not changed stay tracked.

If Curios is installed, the Spawner Key and Spawner Compass can be worn in the belt slot. Right-click a mob spawner with an empty hand while the key is on the belt and the config screen opens. Anything held in that hand, including a spawn egg, is left alone. A key in the hand still opens the screen as before. The compass still points from the belt and from any hotbar slot. While it is in the hotbar or on the belt and a tracked spawner is within 128 blocks, a line at the top of the screen shows that a spawner was detected and the distance in blocks. If Supplementaries is installed as well, this mod adds only the missing belt slots so there are at least four (this key, this compass, Supplementaries' key, and its quiver). A belt that is already that large is not grown again.

## Build

```bash
./gradlew build
```

Jar: `build/libs/spawnermod-1.2.13.jar`

## Port notes

See [`PORT_NOTES.md`](PORT_NOTES.md) for Fabric → NeoForge API mapping details.

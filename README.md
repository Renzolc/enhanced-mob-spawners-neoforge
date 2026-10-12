# Enhanced Mob Spawners (NeoForge)

NeoForge port of **[Enhanced Mob Spawners](https://github.com/andersblomqvist/enhanced-mob-spawners)** by **Branders (Anders Blomqvist)**.

| | |
|---|---|
| **Mod id** | `spawnermod` |
| **Version** | 1.2.15+mc1.21.5 |
| **Minecraft** | 1.21.5 (this branch, `mc/1.21.5`) |
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

If Curios is installed, the Spawner Key and Spawner Compass can be worn in the belt slot. Right-click a mob spawner with an empty hand while the key is on the belt and the config screen opens. Anything held in that hand, including a spawn egg, is left alone. A key in the hand still opens the screen as before. The compass still points from the belt and from any hotbar slot. Right-click it, or press **Toggle Spawner Compass** (Options, Controls, Enhanced Mob Spawners; unbound until you set a key), to turn tracking off or on for every compass in the hotbar, offhand, or belt. With tracking off and a spawner within 128 blocks, the screen says a spawner was detected. With tracking on, that line is replaced by the compass icon and the distance in blocks under it. By default that readout sits at the top center. Pause, then Mods, then Enhanced Mob Spawners, then Config chooses another corner, bottom center, or a pixel offset from that spot. Bottom center sits just above the hotbar. If Jade's tooltip covers the readout, it steps aside. If Supplementaries is installed as well, this mod adds only the missing belt slots so there are at least four (this key, this compass, Supplementaries' key, and its quiver). A belt that is already that large is not grown again.

## Supported versions

Each Minecraft version has its own branch. Spawner Key screen with the compass ignore toggle,
Spawner Compass, optional Curios belt slots, silk-touch spawners, Spawn Harvest, limited spawns,
and key durability 16 are kept on every version. The compass tracking toggle, the compass
readout, and its placement screen are on `main` (1.2.15) until the other branches are updated.

| Minecraft | NeoForge | Java | Branch | Jar | Curios (optional) |
|---|---|---|---|---|---|
| 1.21.1 | 21.1.251+ | 21 | `main` | `spawnermod-1.2.15.jar` | 9.5.1 |
| 1.21.4 | 21.4.158+ | 21 | `mc/1.21.4` | `spawnermod-1.2.13-mc1.21.4.jar` | 10.0.1 |
| 1.21.5 | 21.5.98+ | 21 | `mc/1.21.5` | `spawnermod-1.2.15-mc1.21.5.jar` | 11.0.1 |
| 1.21.8 | 21.8.54+ | 21 | `mc/1.21.8` | `spawnermod-1.2.13-mc1.21.8.jar` | 12.0.0 |
| 1.21.10 | 21.10.64+ | 21 | `mc/1.21.10` | `spawnermod-1.2.13-mc1.21.10.jar` | 13.0.0 |
| 1.21.11 | 21.11.45+ | 21 | `mc/1.21.11` | `spawnermod-1.2.13-mc1.21.11.jar` | 14.0.0 |
| 26.1.2 | 26.1.2.114+ | 25 | `mc/26.1.2` | `spawnermod-1.2.13-mc26.1.2.jar` | 15.0.0 |
| 26.2 | 26.2.0.88+ | 25 | `mc/26.2` | `spawnermod-1.2.13-mc26.2.jar` | 16.0.0 |

Supplementaries is only released for 1.21.1, so the extra belt slots it triggers only apply there.
On other versions the check simply finds no Supplementaries and does nothing.

## Build

```bash
./gradlew build
```

Jar: `build/libs/spawnermod-1.2.15-mc1.21.5.jar`

## Tests

```bash
./gradlew runGameTestServer               # GameTests (Curios not installed)
./gradlew runGameTestServer -PwithCurios  # GameTests with Curios on the belt
./gradlew runClient -PsmokeWorld=<save>   # dev client smoke test: HUD and key screen screenshots
```

The GameTests cover the Spawner Key settings applied on the server, key durability (16 to 15),
the silk-touch spawner drop, Spawn Harvest egg drops, the limited-spawns counter, the redstone
toggle, the compass ignore flag surviving save and load, the compass tracker placement, and the Curios belt slot.

## Port notes

See [`PORT_NOTES.md`](PORT_NOTES.md) for Fabric → NeoForge API mapping details.

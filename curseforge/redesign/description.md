![Enhanced Mob Spawners NeoForge by Renzo](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/header-enhanced-mob-spawners-neoforge.png)

![divider](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/divider-enhanced-mob-spawners-neoforge.png)

[![GitHub](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/badge-github-enhanced-mob-spawners-neoforge.png)](https://github.com/Renzolc/enhanced-mob-spawners-neoforge) [![CurseForge](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/badge-curseforge-enhanced-mob-spawners-neoforge.png)](https://www.curseforge.com/minecraft/mc-mods/enhanced-mob-spawners-neoforge) [![Issues](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/badge-issues-enhanced-mob-spawners-neoforge.png)](https://github.com/Renzolc/enhanced-mob-spawners-neoforge/issues)

![divider](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/divider-enhanced-mob-spawners-neoforge.png)

## 📖 ABOUT

**Enhanced Mob Spawners NeoForge** is a NeoForge 1.21.1 port of **[Enhanced Mob Spawners](https://www.curseforge.com/minecraft/mc-mods/enhanced-mob-spawners) by Branders (Anders Blomqvist)**. Mine spawners with Silk Touch, take out or swap their spawn eggs, and tune them with the Spawner Key. The original design, features and assets are Branders' work. 🏆

This fork adds a few things of its own: the Spawner Key takes Mending and Unbreaking, the new **Spawn Harvest** enchantment replaces random egg drops, and the **Spawner Compass** finds spawners near you. Both the key and the compass fit in a Curios belt slot. 🧭

Released under CC0, the same license as [Branders' source](https://github.com/andersblomqvist/enhanced-mob-spawners). Branders' original description is kept at the bottom of this page. 📜

![divider](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/divider-enhanced-mob-spawners-neoforge.png)

## 📚 FEATURES

- ⛏️ **Silk Touch spawners:** Mine a mob spawner with Silk Touch and place it wherever you like. *(Branders)*
- 🥚 **Egg retrieval:** Right-click a spawner to get its spawn egg back, or use a different egg to change the mob. *(Branders)*
- 🔑 **Spawner Key:** Opens a config screen for count, speed, range up to 128 blocks, and on/off. Redstone can switch a spawner off too. *(Branders)*
- 📏 **Limits and defaults:** Limited spawns per spawner, a default range for every spawner, and spawner hardness, all in the config file. *(Branders)*
- ✨ **Enchantable key:** The Spawner Key has 16 durability, each save costs 1, and it takes Mending and Unbreaking.
- 🗡️ **Spawn Harvest:** A treasure enchantment for swords, axes, bows, crossbows and knives. A kill with it always drops that mob's spawn egg. It is never offered in the enchanting table.
- 🚫 **No random egg drops:** Branders' random egg drop chance is removed. Eggs from kills only come from Spawn Harvest.
- 🧭 **Spawner Compass:** Shapeless recipe from a compass and a mob spawner. It points to the nearest tracked spawner within 128 blocks and shows the distance on screen.
- 📡 **Compass tracking:** A button on the Spawner Key screen hides a spawner from the compass. Spawners you haven't changed stay tracked.
- 🎽 **Belt slots:** With Curios, the key and the compass can go in a belt slot. With Supplementaries too, missing belt slots are added so there are at least four.

![divider](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/divider-enhanced-mob-spawners-neoforge.png)

## 📷 MEDIA

![The Spawner Key config screen](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/media-0-spawner-config.png)

![The Spawner Compass detecting a spawner and in a belt slot](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/media-1-spawner-compass.png)

![The Spawn Harvest enchantment](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/media-2-spawn-harvest.png)

![Recipes for the Spawner Key and the Spawner Compass](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/media-3-recipes.png)

![divider](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/divider-enhanced-mob-spawners-neoforge.png)

## 📦 INSTALLATION

**⏩ INSTALL ON BOTH CLIENT AND SERVER (NEOFORGE 1.21.1)**

**⏩ NO REQUIRED DEPENDENCIES**

**⏩ OPTIONAL: [CURIOS API](https://www.curseforge.com/minecraft/mc-mods/curios) 5 OR NEWER, FOR THE BELT SLOT**

**⏩ OPTIONAL: [SUPPLEMENTARIES](https://www.curseforge.com/minecraft/mc-mods/supplementaries), FOR EXTRA BELT SLOTS**

Don't install this next to Branders' own Forge or Fabric version of Enhanced Mob Spawners: this port uses the same mod id, `spawnermod`.

![divider](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/divider-enhanced-mob-spawners-neoforge.png)

## ⚙️ CONFIGURATION

Settings are in `.minecraft/config/spawnermod.json`. On/off values use 1 for true and 0 for false.

- `disable_silk_touch`, `disable_spawner_config`, `disable_count`, `disable_range`, `disable_speed`, `disable_egg_removal_from_spawner`: turn individual features off.
- `limited_spawns_enabled`, `limited_spawns_amount` (32): let spawners stop after a number of spawns.
- `default_spawner_range_enabled`, `default_spawner_range` (52): a default player range for every spawner.
- `spawner_hardness` (5): how hard spawners are to mine.
- One entry per spawn egg to turn eggs off for specific mobs.

`monster_egg_drop_chance` is still in the file but has no effect in this fork, because eggs from kills only come from Spawn Harvest.

![divider](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/divider-enhanced-mob-spawners-neoforge.png)

Renzo is actively working on Enhanced Mob Spawners NeoForge and welcomes feedback, bug reports and ideas in the comments. 💬

| [![Disassembly Delight](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/card-disassembly-delight.png)](https://www.curseforge.com/minecraft/mc-mods/disassembly-delight) | [![Farmer's Delight Tweaks](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/card-farmers-delight-tweaks.png)](https://www.curseforge.com/minecraft/mc-mods/farmers-delight-tweaks) |
|:---:|:---:|
| [![Sophisticated Advanced Crafting](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/card-sophisticated-advanced-crafting.png)](https://www.curseforge.com/minecraft/mc-mods/sophisticated-advanced-crafting) | [![Create Cart Loot](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/card-create-cart-loot.png)](https://www.curseforge.com/minecraft/mc-mods/create-cart-loot) |
| [![Mo' Enchantments Loot Compat](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/card-mo-enchantments-loot-compat.png)](https://www.curseforge.com/minecraft/mc-mods/mo-enchantments-loot-compat) |   |

![divider](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/divider-enhanced-mob-spawners-neoforge.png)

[![Made by Renzo](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/footer-renzo.png)](https://github.com/Renzolc)

![divider](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/divider-enhanced-mob-spawners-neoforge.png)

© 2026 Renzo (NeoForge port and fork changes). Original mod by Branders (Anders Blomqvist). Released under CC0 1.0 Universal (public domain dedication), the same as the original source.

![divider](https://raw.githubusercontent.com/Renzolc/enhanced-mob-spawners-neoforge/main/curseforge/redesign/divider-enhanced-mob-spawners-neoforge.png)

## 📜 ORIGINAL DESCRIPTION BY BRANDERS

*Imported from Branders' original project description: feature overview and configuration. Author-update notes omitted. Some details differ in this fork; for example, the random egg drop is replaced by Spawn Harvest (see above).*

Adds ability to Silk Touch spawner and retrieve Monster Egg from it + custom configuration.

- Make Mob Spawners minable with silk touch
- Right-click on a mob spawner and it will drop its monster egg corresponding to what type of entity inside.
- Adds a 4% chance for all entities to drop its monster egg (can be changed in config)
- Adds Spawner Block in Creative Tabs (decorations)
- Modify Mob Spawner stats such as count, speed and range. Example: make spawner active for a maximun of 128 blocks! Vanilla is 16
- Customize what you want inside mod configuation file
- Toggle spawner ON/OFF with ingame config menu or Redstone
- Option to turn on limited spawns, via config file! Control number of entities a spawner can spawn before it dies. (not supported in Forge 1.18)
- Spawner Key item - used to change spawner block behaviour.
- Change default range of all spawners (through config)
- Change spawner block hardness (through config)

### Configuration

Config found at `.minecraft/config/spawnermod.json`.

true/false values are represented as 1 = true, 0 = false

drop rate is between 0 - 100 (whole numbers)

If you have an older version of the mod: config file is a spawnermod.toml file.

Goal of this mod is to add more functionality to the original minecraft spawner. This mod makes the player able to move mob spawners ability to fully control what type of entity inside by making it possible to retrieve the Monster Egg.

Original mod: [Enhanced Mob Spawners on CurseForge](https://www.curseforge.com/minecraft/mc-mods/enhanced-mob-spawners) · Source: [github.com/andersblomqvist/enhanced-mob-spawners](https://github.com/andersblomqvist/enhanced-mob-spawners)


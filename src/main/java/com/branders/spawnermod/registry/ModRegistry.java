package com.branders.spawnermod.registry;

import com.branders.spawnermod.SpawnerMod;
import com.branders.spawnermod.command.SpawnerModCommands;
import com.branders.spawnermod.item.SpawnerCompassItem;
import com.branders.spawnermod.item.SpawnerKey;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRegistry {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpawnerMod.MOD_ID);

    public static final DeferredItem<Item> SPAWNER_KEY = ITEMS.registerItem("spawner_key",
            SpawnerKey::new, new Item.Properties().durability(16).enchantable(SpawnerKey.ENCHANTMENT_VALUE).rarity(Rarity.RARE));

    public static final DeferredItem<Item> SPAWNER_COMPASS = ITEMS.registerItem("spawner_compass",
            SpawnerCompassItem::new, new Item.Properties().rarity(Rarity.RARE));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(ModRegistry::addCreative);
        NeoForge.EVENT_BUS.addListener(ModRegistry::onRegisterCommands);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(SPAWNER_KEY);
            event.accept(SPAWNER_COMPASS);
        }
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        SpawnerModCommands.register(event);
    }

    /**
     * Given the entity, get its spawn egg registry name.
     *
     * @param entityString e.g. minecraft:pig
     * @return modid:entity_spawn_egg or whackmod:spawn_egg_entity
     */
    public static String getSpawnEggRegistryName(String entityString) {
        Item egg = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(entityString + "_spawn_egg"));

        if (egg == null || egg == net.minecraft.world.item.Items.AIR) {
            String[] split = entityString.split(":");
            assert (split.length == 2);
            String id = split[0];
            String e = "spawn_egg_" + split[1];
            return id + ":" + e;
        }

        return entityString + "_spawn_egg";
    }
}

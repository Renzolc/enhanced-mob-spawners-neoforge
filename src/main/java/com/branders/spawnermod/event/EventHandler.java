package com.branders.spawnermod.event;

import java.util.stream.Collectors;

import com.branders.spawnermod.SpawnerMod;
import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.item.SpawnerKey;
import com.branders.spawnermod.mixin.UpdateNeighborMixin;
import com.branders.spawnermod.registry.ModRegistry;
import com.branders.spawnermod.spawner.SpawnerNbt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Handles events regarding the mob spawner.
 *
 * @author Anders &lt;Branders&gt; Blomqvist
 */
public class EventHandler {

    /**
     * Called when player breaks a block. If silk touch was used we drop the
     * monster egg; otherwise drop EXP.
     */
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        Level world = event.getPlayer().level();
        if (world.isClientSide())
            return;

        Player player = event.getPlayer();
        if (player.isCreative())
            return;

        BlockPos pos = event.getPos();
        if (!(world.getBlockState(pos).getBlock() instanceof SpawnerBlock))
            return;

        ItemStack stack = player.getMainHandItem();

        if (checkSilkTouch(stack) && ConfigValues.get("disable_silk_touch") == 0) {
            if (ConfigValues.get("disable_egg_removal_from_spawner") == 0)
                dropMonsterEgg(pos, world);
        } else {
            int size = 15 + world.random.nextInt(15) + world.random.nextInt(15);
            ExperienceOrb.award((ServerLevel) world, Vec3.atCenterOf(pos), size);
        }
    }

    /**
     * Right-click a spawner to retrieve the egg inside.
     */
    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level world = event.getLevel();
        InteractionHand hand = event.getHand();

        if (player.isShiftKeyDown() && ModList.get().isLoaded("carrier"))
            return;

        if (world.isClientSide())
            return;

        if (world.getBlockState(event.getPos()).getBlock() != Blocks.SPAWNER)
            return;

        if (hand == InteractionHand.OFF_HAND)
            return;

        if (ConfigValues.get("disable_egg_removal_from_spawner") != 0)
            return;

        Item item = player.getMainHandItem().getItem();

        if (item instanceof BlockItem || item instanceof SpawnEggItem || item instanceof SpawnerKey)
            return;

        String registryName = BuiltInRegistries.ITEM.getKey(item).toString();

        if (ConfigValues.get("display_item_id_from_right_click_in_log") == 1)
            SpawnerMod.LOGGER.info("Right clicked with item id: " + registryName);

        if (ConfigValues.isItemIdBlacklisted(registryName))
            return;

        InteractionResult result = dropMonsterEgg(event.getPos(), world);
        if (result.consumesAction()) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private InteractionResult dropMonsterEgg(BlockPos pos, Level world) {
        BlockState blockstate = world.getBlockState(pos);
        if (!(world.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner))
            return InteractionResult.PASS;

        BaseSpawner logic = spawner.getSpawner();

        String entityString = SpawnerNbt.entityId(SpawnerNbt.save(logic));
        if (entityString == null)
            return InteractionResult.PASS;

        if (entityString.contains("area_effect_cloud"))
            return InteractionResult.PASS;

        String eggId = ModRegistry.getSpawnEggRegistryName(entityString);
        Item egg = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(eggId));
        if (egg == null || egg == Items.AIR) {
            SpawnerMod.LOGGER.info("Could not find spawn egg for: " + entityString);
            return InteractionResult.PASS;
        }
        ItemStack itemStack = new ItemStack(egg);

        double d0 = (double) (world.getRandom().nextFloat() * 0.7F) + (double) 0.15F;
        double d1 = (double) (world.getRandom().nextFloat() * 0.7F) + (double) 0.06F + 0.6D;
        double d2 = (double) (world.getRandom().nextFloat() * 0.7F) + (double) 0.15F;

        ItemEntity entityItem = new ItemEntity(world, (double) pos.getX() + d0, (double) pos.getY() + d1,
                (double) pos.getZ() + d2, itemStack);
        entityItem.setDefaultPickUpDelay();
        world.addFreshEntity(entityItem);

        logic.setEntityId(EntityType.AREA_EFFECT_CLOUD, world, world.random, pos);
        spawner.setChanged();
        world.sendBlockUpdated(pos, blockstate, blockstate, 3);

        return InteractionResult.SUCCESS;
    }

    public static Item getSpawnEgg(String entityString) {
        Item egg = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(entityString + "_spawn_egg"));

        if (egg == Items.AIR) {
            String[] split = entityString.split(":");
            assert (split.length == 2);
            String id = split[0];
            String e = "spawn_egg_" + split[1];
            egg = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(id + ":" + e));
        }

        return egg;
    }

    /**
     * Redstone power toggles spawner range. Called from {@link UpdateNeighborMixin}.
     */
    public static void updateNeighbor(BlockPos spawnerPos, Level world) {
        BlockState blockstate = world.getBlockState(spawnerPos);

        if (!(world.getBlockEntity(spawnerPos) instanceof SpawnerBlockEntity spawner))
            return;

        BaseSpawner logic = spawner.getSpawner();
        CompoundTag nbt = SpawnerNbt.save(logic);

        if (world.hasNeighborSignal(spawnerPos)) {
            short value = SpawnerNbt.getShort(nbt, "RequiredPlayerRange");

            if (SpawnerNbt.getShort(nbt, "SpawnRange") > 4)
                return;

            nbt.putShort("SpawnRange", value);
            nbt.putShort("RequiredPlayerRange", (short) 0);
        } else {
            short pr = SpawnerNbt.getShort(nbt, "SpawnRange");

            if (pr <= 4)
                return;

            nbt.putShort("RequiredPlayerRange", pr);
            nbt.putShort("SpawnRange", (short) 4);
        }

        SpawnerNbt.load(logic, world, spawnerPos, nbt);
        spawner.setChanged();
        world.sendBlockUpdated(spawnerPos, blockstate, blockstate, Block.UPDATE_ALL);
    }

    private boolean checkSilkTouch(ItemStack stack) {
        var silkTouch = stack.getEnchantments().entrySet().stream().filter(entry -> {
            int level = EnchantmentHelper.getItemEnchantmentLevel(entry.getKey(), stack);
            if (entry.getKey().is(Enchantments.SILK_TOUCH) && level >= 1) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        return silkTouch.size() >= 1;
    }
}

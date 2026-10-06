package com.branders.spawnermod.item;

import java.util.List;

import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.gui.SpawnerConfigGui;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.BaseSpawner;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Spawner Key — opens the spawner config GUI. Damageable so Unbreaking /
 * Mending are meaningful (durability 16, similar to a low-tier tool; each GUI
 * save damages the key by 1 as in the Fabric mod).
 *
 * @author Anders &lt;Branders&gt; Blomqvist
 */
public class SpawnerKey extends Item {

    /** Enchantment value comparable to iron tools so the enchanting table can apply durability enchants. */
    public static final int ENCHANTMENT_VALUE = 14;

    private static final Component TOOL_TIP = Component.translatable("tooltip.spawnermod.spawner_key_disabled")
            .setStyle(Style.EMPTY.withColor(0xff0000));

    public SpawnerKey(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    /**
     * Allow Mending and Unbreaking via anvil / enchanting table. Other
     * enchantments fall back to the default tag-based rules.
     */
    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.MENDING) || enchantment.is(Enchantments.UNBREAKING)) {
            return true;
        }
        return super.supportsEnchantment(stack, enchantment);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
            TooltipFlag flag) {
        if (ConfigValues.get("disable_spawner_config") != 0) {
            tooltip.add(TOOL_TIP);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (ConfigValues.get("disable_spawner_config") != 0)
            return InteractionResult.FAIL;

        Level world = context.getLevel();

        if (!world.isClientSide)
            return InteractionResult.FAIL;

        BlockPos pos = context.getClickedPos();
        if (world.getBlockState(pos).getBlock() != Blocks.SPAWNER)
            return InteractionResult.FAIL;

        if (!(world.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner))
            return InteractionResult.FAIL;

        BaseSpawner logic = spawner.getSpawner();
        openScreen(logic, pos);

        return InteractionResult.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    public static void openScreen(BaseSpawner logic, BlockPos pos) {
        Minecraft.getInstance().setScreen(new SpawnerConfigGui(Component.translatable(""), logic, pos));
    }
}

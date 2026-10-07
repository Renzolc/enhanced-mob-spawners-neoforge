package com.branders.spawnermod.item;

import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Points at the nearest loaded mob spawner. The needle is client-side; see
 * {@link com.branders.spawnermod.client.SpawnerCompassClient}.
 */
public class SpawnerCompassItem extends Item {

    private static final Component TOOLTIP = Component.translatable("tooltip.spawnermod.spawner_compass");

    public SpawnerCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(TOOLTIP);
    }
}

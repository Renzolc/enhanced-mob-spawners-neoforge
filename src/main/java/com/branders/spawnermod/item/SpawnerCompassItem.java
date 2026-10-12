package com.branders.spawnermod.item;

import java.util.List;

import com.branders.spawnermod.compat.WornSpawnerItems;
import com.branders.spawnermod.registry.ModRegistry;

import net.minecraft.server.level.ServerPlayer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Points at the nearest loaded mob spawner while enabled. The needle is
 * client-side; see {@link com.branders.spawnermod.client.SpawnerCompassClient}.
 * Right-click, or the Toggle Spawner Compass key, turns tracking on or off for every
 * compass in the hotbar, offhand, or Curios belt.
 */
public class SpawnerCompassItem extends Item {

    private static final Component TOOLTIP = Component.translatable("tooltip.spawnermod.spawner_compass");

    public SpawnerCompassItem(Properties properties) {
        super(properties);
    }

    /** A compass with no component is on, so older stacks keep working. */
    public static boolean isEnabled(ItemStack stack) {
        return stack.getOrDefault(ModRegistry.COMPASS_ENABLED.get(), Boolean.TRUE);
    }

    public static void setEnabled(ItemStack stack, boolean enabled) {
        stack.set(ModRegistry.COMPASS_ENABLED.get(), enabled);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            Boolean enabled = WornSpawnerItems.toggleCarriedCompasses(serverPlayer);
            if (enabled != null) {
                announce(player, level, enabled);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static void announce(Player player, Level level, boolean enabled) {
        player.displayClientMessage(Component.translatable(enabled
                ? "message.spawnermod.compass.on"
                : "message.spawnermod.compass.off"), true);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK,
                SoundSource.PLAYERS, 0.4F, enabled ? 1.15F : 0.85F);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(TOOLTIP);
        boolean enabled = isEnabled(stack);
        tooltip.add(Component.translatable(enabled
                ? "tooltip.spawnermod.spawner_compass.on"
                : "tooltip.spawnermod.spawner_compass.off")
                .withStyle(enabled ? ChatFormatting.GRAY : ChatFormatting.RED));
    }
}

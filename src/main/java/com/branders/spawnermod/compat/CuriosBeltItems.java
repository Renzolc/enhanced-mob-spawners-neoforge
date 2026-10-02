package com.branders.spawnermod.compat;

import java.util.function.Predicate;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

/** Reads and damages items in the Curios belt slot. Only loaded when Curios is present. */
public final class CuriosBeltItems {

    private CuriosBeltItems() {
    }

    public static boolean contains(LivingEntity entity, Predicate<ItemStack> test) {
        IDynamicStackHandler stacks = belt(entity);
        if (stacks == null) {
            return false;
        }
        for (int i = 0; i < stacks.getSlots(); i++) {
            if (test.test(stacks.getStackInSlot(i))) {
                return true;
            }
        }
        return false;
    }

    public static void damageFirst(ServerPlayer player, ServerLevel level, Predicate<ItemStack> test) {
        IDynamicStackHandler stacks = belt(player);
        if (stacks == null) {
            return;
        }
        for (int i = 0; i < stacks.getSlots(); i++) {
            ItemStack stack = stacks.getStackInSlot(i);
            if (!test.test(stack)) {
                continue;
            }
            stack.hurtAndBreak(1, level, player, item -> {
            });
            stacks.setStackInSlot(i, stack);
            return;
        }
    }

    private static IDynamicStackHandler belt(LivingEntity entity) {
        var inventory = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (inventory == null) {
            return null;
        }
        var belt = inventory.getStacksHandler(CuriosBeltSlots.BELT).orElse(null);
        return belt == null ? null : belt.getStacks();
    }
}

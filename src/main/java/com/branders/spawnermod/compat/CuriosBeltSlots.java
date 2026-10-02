package com.branders.spawnermod.compat;

import com.branders.spawnermod.SpawnerMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

/**
 * Raises the belt slot count to {@link BeltSlotGrants#MIN_BELT_SLOTS} using one
 * permanent Curios slot modifier. The same id is replaced rather than stacked,
 * and an existing larger belt is left alone.
 */
public final class CuriosBeltSlots {

    static final String BELT = "belt";
    private static final ResourceLocation MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(SpawnerMod.MOD_ID,
            "belt_shortfall");

    private CuriosBeltSlots() {
    }

    /** @return false when the belt inventory is not available yet */
    public static boolean applyShortfall(ServerPlayer player) {
        ICuriosItemHandler inventory = CuriosApi.getCuriosInventory(player).orElse(null);
        if (inventory == null) {
            return false;
        }
        ICurioStacksHandler belt = inventory.getStacksHandler(BELT).orElse(null);
        if (belt == null) {
            return false;
        }

        double ours = 0.0D;
        for (AttributeModifier modifier : belt.getPermanentModifiers()) {
            if (MODIFIER_ID.equals(modifier.id())
                    && modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                ours += modifier.amount();
            }
        }

        int occupied = occupied(belt.getStacks());
        int base = Math.max(0, belt.getSlots() - (int) Math.round(ours));
        int target = Math.max(BeltSlotGrants.MIN_BELT_SLOTS, occupied);
        int desired = Math.max(0, target - base);
        if ((int) Math.round(ours) == desired) {
            return true;
        }
        // Growing is always safe. Shrinking is skipped if it would drop worn items.
        if (desired < ours && base + desired < occupied) {
            return true;
        }

        inventory.removeSlotModifier(BELT, MODIFIER_ID);
        if (desired > 0) {
            inventory.addPermanentSlotModifier(BELT, MODIFIER_ID, desired, AttributeModifier.Operation.ADD_VALUE);
        }
        return true;
    }

    private static int occupied(IDynamicStackHandler stacks) {
        int count = 0;
        for (int i = 0; i < stacks.getSlots(); i++) {
            ItemStack stack = stacks.getStackInSlot(i);
            if (!stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }
}

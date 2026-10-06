package com.branders.spawnermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.branders.spawnermod.config.ConfigValues;
import com.branders.spawnermod.enchantment.ModEnchantments;
import com.branders.spawnermod.event.EventHandler;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Spawn egg drops on kill require the {@link ModEnchantments#SPAWN_HARVEST}
 * enchantment on the killing weapon (main-hand melee or bow/crossbow that fired
 * the projectile). Random egg drop chance from the mod is disabled.
 */
@Mixin(Mob.class)
public class MobEntityDropsMixin {

    @Inject(at = @At("HEAD"), method = "dropFromLootTable", cancellable = true)
    private void dropFromLootTable(ServerLevel level, DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        ItemStack weapon = source.getWeaponItem();
        if (weapon == null || weapon.isEmpty() || !hasSpawnHarvest(weapon)) {
            return;
        }

        if (ConfigValues.get("monster_egg_only_drop_when_killed_by_player") == 1 && !causedByPlayer) {
            return;
        }

        Mob entity = (Mob) (Object) this;
        if (!(entity.level() instanceof ServerLevel world)) {
            return;
        }

        EntityType<?> entityType = entity.getType();
        String entityString = EntityType.getKey(entityType).toString();

        if (ConfigValues.isEggDisabled(entityString)) {
            return;
        }

        Item egg = EventHandler.getSpawnEgg(entityString);
        if (egg != null && egg != Items.AIR) {
            world.addFreshEntity(new ItemEntity(world, entity.xo, entity.yo, entity.zo, new ItemStack(egg)));
        }
    }

    private static boolean hasSpawnHarvest(ItemStack stack) {
        return stack.getEnchantments().keySet().stream()
                .anyMatch(holder -> holder.is(ModEnchantments.SPAWN_HARVEST));
    }
}

package com.branders.spawnermod.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.NeedleDirectionHelper;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Item model property {@code spawnermod:spawner_compass}: the needle angle (0..1) towards the
 * nearest tracked spawner, using the same wobble and spin as the vanilla compass.
 */
public final class SpawnerCompassAngle implements RangeSelectItemModelProperty {

    public static final MapCodec<SpawnerCompassAngle> MAP_CODEC = Codec.BOOL.optionalFieldOf("wobble", true)
            .xmap(SpawnerCompassAngle::new, angle -> angle.state.wobbleEnabled());

    private final State state;

    public SpawnerCompassAngle(boolean wobble) {
        this.state = new State(wobble);
    }

    @Override
    public float get(ItemStack stack, ClientLevel level, ItemOwner owner, int seed) {
        return this.state.get(stack, level, owner, seed);
    }

    @Override
    public MapCodec<SpawnerCompassAngle> type() {
        return MAP_CODEC;
    }

    private static final class State extends NeedleDirectionHelper {

        private final NeedleDirectionHelper.Wobbler wobbler;
        private final NeedleDirectionHelper.Wobbler noTargetWobbler;
        private final RandomSource random = RandomSource.create();

        State(boolean wobble) {
            super(wobble);
            this.wobbler = this.newWobbler(0.8F);
            this.noTargetWobbler = this.newWobbler(0.8F);
        }

        boolean wobbleEnabled() {
            return this.wobble();
        }

        @Override
        protected float calculate(ItemStack stack, ClientLevel level, int seed, ItemOwner owner) {
            // Since 1.21.9 the holder is an ItemOwner; held, hotbar and item frame stacks still
            // have an entity behind it.
            Entity entity = owner instanceof Entity e ? e : owner != null ? owner.asLivingEntity() : null;
            if (entity == null) {
                return this.spinning(seed, level.getGameTime());
            }
            GlobalPos target = SpawnerCompassClient.target(level, stack, entity);
            long gameTime = level.getGameTime();
            return !isValidTarget(entity, target)
                    ? this.spinning(seed, gameTime)
                    : this.towards(entity, gameTime, target.pos());
        }

        private float spinning(int seed, long gameTime) {
            if (this.noTargetWobbler.shouldUpdate(gameTime)) {
                this.noTargetWobbler.update(gameTime, this.random.nextFloat());
            }
            float rotation = this.noTargetWobbler.rotation() + (float) (seed * 1327217883) / 2.1474836E9F;
            return Mth.positiveModulo(rotation, 1.0F);
        }

        private float towards(Entity entity, long gameTime, BlockPos target) {
            Vec3 center = Vec3.atCenterOf(target);
            float angle = (float) (Math.atan2(center.z() - entity.getZ(), center.x() - entity.getX())
                    / (float) (Math.PI * 2));
            float yaw = Mth.positiveModulo(entity.getVisualRotationYInDegrees() / 360.0F, 1.0F);
            if (entity instanceof Player player && player.isLocalPlayer()
                    && player.level().tickRateManager().runsNormally()) {
                if (this.wobbler.shouldUpdate(gameTime)) {
                    this.wobbler.update(gameTime, 0.5F - (yaw - 0.25F));
                }
                return Mth.positiveModulo(angle + this.wobbler.rotation(), 1.0F);
            }
            return Mth.positiveModulo(0.5F - (yaw - 0.25F - angle), 1.0F);
        }

        private static boolean isValidTarget(Entity entity, GlobalPos pos) {
            return pos != null && pos.dimension() == entity.level().dimension()
                    && !(pos.pos().distToCenterSqr(entity.position()) < 1.0E-5F);
        }
    }
}

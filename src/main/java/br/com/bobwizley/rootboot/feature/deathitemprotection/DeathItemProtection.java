package br.com.bobwizley.rootboot.feature.deathitemprotection;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SpellParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;

public final class DeathItemProtection {

    private static final SpellParticleOption GLOW =
            SpellParticleOption.create(ParticleTypes.EFFECT, -1, 1.0F);

    private static boolean enabled;

    private DeathItemProtection() {
    }

    static void enable() {
        enabled = true;
    }

    static void disable() {
        enabled = false;
    }

    public static void protect(ItemEntity item) {
        if (!enabled) {
            return;
        }

        ((ProtectedDeathItem) item).rootboot$setProtectedDeathItem(true);
        item.setUnlimitedLifetime();
    }

    public static void applyCurrentPolicy(ItemEntity item) {
        ProtectedDeathItem protectedItem = (ProtectedDeathItem) item;
        if (!protectedItem.rootboot$isProtectedDeathItem()) {
            return;
        }

        if (enabled) {
            item.setUnlimitedLifetime();
            showGlow(item);
            return;
        }

        protectedItem.rootboot$setProtectedDeathItem(false);
        protectedItem.rootboot$resetDespawnAge();
    }

    private static void showGlow(ItemEntity item) {
        if (item.tickCount % 2 != 0
                || !(item.level() instanceof ServerLevel level)
                || level.getNearestPlayer(item, 32.0) == null
                || item.getRandom().nextFloat() >= 0.05F) {
            return;
        }

        level.sendParticles(
                GLOW,
                item.getX(),
                item.getY() + 0.2,
                item.getZ(),
                1,
                0.0,
                0.0,
                0.0,
                0.0);
    }
}

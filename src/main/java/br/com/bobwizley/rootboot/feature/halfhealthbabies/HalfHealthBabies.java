package br.com.bobwizley.rootboot.feature.halfhealthbabies;

import br.com.bobwizley.rootboot.RootBoot;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Half-health Babies: while a mob is a baby it keeps exactly half of the max health it would
 * otherwise have in its current environment.
 *
 * <p>The reduction is a multiplicative modifier so that it composes with, instead of replacing,
 * modifiers contributed by other mods, equipment or the environment; its fixed id keeps repeated
 * applications from stacking. It is permanent so that it is saved with the entity — a baby loaded
 * while the feature is disabled must be recognizable as reduced in order to be reverted.
 */
public final class HalfHealthBabies {

    public static final Identifier MODIFIER_ID =
            Identifier.fromNamespaceAndPath(RootBoot.MOD_ID, "baby_half_health");

    private static final AttributeModifier REDUCTION = new AttributeModifier(
            MODIFIER_ID, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    private static boolean enabled;

    private HalfHealthBabies() {
    }

    static void enable() {
        enabled = true;
    }

    static void disable() {
        enabled = false;
    }

    public static void applyCurrentPolicy(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return;
        }

        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }

        boolean reduced = maxHealth.hasModifier(MODIFIER_ID);
        if (reduced == (enabled && entity.isBaby())) {
            return;
        }

        float previousMaxHealth = entity.getMaxHealth();
        float health = entity.getHealth();
        if (reduced) {
            maxHealth.removeModifier(MODIFIER_ID);
        } else {
            maxHealth.addPermanentModifier(REDUCTION);
        }
        entity.setHealth(health * entity.getMaxHealth() / previousMaxHealth);
    }
}

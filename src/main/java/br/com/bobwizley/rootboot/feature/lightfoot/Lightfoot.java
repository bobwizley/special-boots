package br.com.bobwizley.rootboot.feature.lightfoot;

import br.com.bobwizley.rootboot.enchantment.RootBootEnchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public final class Lightfoot {

    private static boolean enabled;

    private Lightfoot() {
    }

    static void enable() {
        enabled = true;
    }

    static void disable() {
        enabled = false;
    }

    /**
     * Slot eligibility is delegated to {@link EnchantmentHelper}, which only reports levels from
     * slots the enchantment definition accepts — feet, for Lightfoot.
     */
    public static boolean preventsTrampling(Level level, Entity entity) {
        if (!enabled || !(entity instanceof LivingEntity living)) {
            return false;
        }

        return level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(RootBootEnchantments.LIGHTFOOT)
                .map(lightfoot -> EnchantmentHelper.getEnchantmentLevel(lightfoot, living) > 0)
                .orElse(false);
    }
}

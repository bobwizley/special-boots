package br.com.bobwizley.rootboot.enchantment;

import br.com.bobwizley.rootboot.RootBoot;
import br.com.bobwizley.rootboot.feature.heavyfoot.HeavyfootEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;

/**
 * Effect types referenced by the RootBoot enchantment definitions. Registration is
 * unconditional: an enchantment JSON naming an unregistered effect type fails to load and the
 * enchantment itself disappears from the registry, which would break boots that already carry
 * it. The feature toggles suspend the behavior instead of the registration.
 */
public final class RootBootEnchantmentEffects {

    private RootBootEnchantmentEffects() {
    }

    public static void register() {
        register("heavyfoot", HeavyfootEffect.CODEC);
    }

    private static <T extends EnchantmentEntityEffect> void register(String name, MapCodec<T> codec) {
        Registry.register(
                BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE,
                Identifier.fromNamespaceAndPath(RootBoot.MOD_ID, name),
                codec);
    }
}

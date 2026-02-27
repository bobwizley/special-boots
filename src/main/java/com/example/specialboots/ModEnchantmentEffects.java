package com.example.specialboots;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;

public class ModEnchantmentEffects {

    public static final ResourceKey<Enchantment> HEAVYFOOT =
            ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("specialboots", "heavyfoot"));

    public static final MapCodec<HeavyfootEffect> HEAVYFOOT_EFFECT =
            register("heavyfoot", HeavyfootEffect.CODEC);

    private static <T extends EnchantmentEntityEffect> MapCodec<T> register(String id, MapCodec<T> codec) {
        return Registry.register(
                BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE,
                Identifier.fromNamespaceAndPath("specialboots", id),
                codec
        );
    }

    public static void register() {
        SpecialBoots.LOGGER.info("Registering enchantment effects for specialboots");
    }
}

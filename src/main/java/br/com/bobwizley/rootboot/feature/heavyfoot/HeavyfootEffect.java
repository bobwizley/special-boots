package br.com.bobwizley.rootboot.feature.heavyfoot;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * The {@code minecraft:tick} effect carried by the Heavyfoot enchantment. It takes no
 * serialized parameters because the affected area is owned by the feature configuration,
 * which the server reads at initialization — not by the enchantment definition.
 */
public record HeavyfootEffect() implements EnchantmentEntityEffect {

    public static final MapCodec<HeavyfootEffect> CODEC = MapCodec.unit(HeavyfootEffect::new);

    @Override
    public void apply(
            ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 pos) {
        Heavyfoot.trample(level, entity);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}

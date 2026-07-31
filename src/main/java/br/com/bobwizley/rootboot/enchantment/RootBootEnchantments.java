package br.com.bobwizley.rootboot.enchantment;

import br.com.bobwizley.rootboot.RootBoot;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public final class RootBootEnchantments {
    public static final ResourceKey<Enchantment> HEAVYFOOT = of("heavyfoot");
    public static final ResourceKey<Enchantment> LIGHTFOOT = of("lightfoot");
    public static final TagKey<Enchantment> EXCLUSIVE_SPECIAL_BOOTS = TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(RootBoot.MOD_ID, "exclusive_set/special_boots"));

    private static ResourceKey<Enchantment> of(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(RootBoot.MOD_ID, name));
    }
}

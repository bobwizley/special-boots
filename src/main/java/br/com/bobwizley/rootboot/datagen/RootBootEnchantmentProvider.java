package br.com.bobwizley.rootboot.datagen;

import br.com.bobwizley.rootboot.enchantment.RootBootEnchantments;
import br.com.bobwizley.rootboot.feature.heavyfoot.HeavyfootEffect;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;

public final class RootBootEnchantmentProvider extends FabricDynamicRegistryProvider {

    public RootBootEnchantmentProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);

        entries.add(
                RootBootEnchantments.HEAVYFOOT,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemLookup.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                                5, // weight
                                1, // max level
                                Enchantment.dynamicCost(15, 0), // min cost
                                Enchantment.dynamicCost(45, 0), // max cost
                                2, // anvil cost
                                net.minecraft.world.entity.EquipmentSlotGroup.FEET
                        )
                ).exclusiveWith(
                        registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(RootBootEnchantments.EXCLUSIVE_SPECIAL_BOOTS)
                ).withEffect(
                        EnchantmentEffectComponents.TICK,
                        new HeavyfootEffect()
                ).build(net.minecraft.resources.Identifier.fromNamespaceAndPath(br.com.bobwizley.rootboot.RootBoot.MOD_ID, "heavyfoot"))
        );

        entries.add(
                RootBootEnchantments.LIGHTFOOT,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemLookup.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                                5, // weight
                                1, // max level
                                Enchantment.dynamicCost(15, 0), // min cost
                                Enchantment.dynamicCost(45, 0), // max cost
                                2, // anvil cost
                                net.minecraft.world.entity.EquipmentSlotGroup.FEET
                        )
                ).exclusiveWith(
                        registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(RootBootEnchantments.EXCLUSIVE_SPECIAL_BOOTS)
                ).build(net.minecraft.resources.Identifier.fromNamespaceAndPath(br.com.bobwizley.rootboot.RootBoot.MOD_ID, "lightfoot"))
        );
    }

    @Override
    public String getName() {
        return "RootBoot Enchantments";
    }
}

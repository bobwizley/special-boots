package br.com.bobwizley.rootboot.datagen;

import br.com.bobwizley.rootboot.enchantment.RootBootEnchantments;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;

public final class RootBootEnchantmentTagProvider extends EnchantmentTagsProvider {

    public RootBootEnchantmentTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(RootBootEnchantments.EXCLUSIVE_SPECIAL_BOOTS)
                .addOptional(RootBootEnchantments.HEAVYFOOT)
                .addOptional(RootBootEnchantments.LIGHTFOOT);

        tag(EnchantmentTags.IN_ENCHANTING_TABLE)
                .addOptional(RootBootEnchantments.HEAVYFOOT)
                .addOptional(RootBootEnchantments.LIGHTFOOT);

        tag(EnchantmentTags.NON_TREASURE)
                .addOptional(RootBootEnchantments.HEAVYFOOT)
                .addOptional(RootBootEnchantments.LIGHTFOOT);
    }
}

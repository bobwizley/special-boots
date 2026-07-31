package br.com.bobwizley.rootboot.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public final class RootBootDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(RootBootRecipeProvider::new);
        pack.addProvider(
                (output, registries) ->
                        new BetterTreesConfiguredFeatureProvider(output, registries));
        pack.addProvider(RootBootEnchantmentProvider::new);
        pack.addProvider(RootBootEnchantmentTagProvider::new);
    }
}

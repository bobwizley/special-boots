package br.com.bobwizley.rootboot.datagen;

import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

public final class BetterTreesConfiguredFeatureProvider implements DataProvider {

    private final PackOutput.PathProvider paths;

    public BetterTreesConfiguredFeatureProvider(
            FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.paths = output.createRegistryElementsPathProvider(Registries.CONFIGURED_FEATURE);
    }

    BetterTreesConfiguredFeatureProvider(Path output) {
        this.paths =
                new PackOutput(output)
                        .createRegistryElementsPathProvider(Registries.CONFIGURED_FEATURE);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<Identifier, JsonObject> configurations = configuredFeatures();
        return DataProvider.saveAll(output, json -> json, paths::json, configurations);
    }

    static Map<Identifier, JsonObject> configuredFeatures() {
        return BetterTreesConfigurations.create().entrySet().stream()
                .collect(
                        java.util.stream.Collectors.toMap(
                                entry ->
                                        Identifier.fromNamespaceAndPath(
                                                "minecraft", entry.getKey()),
                                Map.Entry::getValue));
    }

    @Override
    public String getName() {
        return "RootBoot Better Trees Configured Features";
    }
}

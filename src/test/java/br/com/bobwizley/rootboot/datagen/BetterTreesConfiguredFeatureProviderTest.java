package br.com.bobwizley.rootboot.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.CachedOutput;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BetterTreesConfiguredFeatureProviderTest {

    private static final Set<String> EXPECTED_IDS =
            Set.of(
                    "acacia",
                    "azalea_tree",
                    "birch",
                    "birch_bees_0002",
                    "birch_bees_0002_leaf_litter",
                    "birch_bees_002",
                    "birch_bees_005",
                    "birch_leaf_litter",
                    "cherry",
                    "cherry_bees_005",
                    "dark_oak",
                    "dark_oak_leaf_litter",
                    "fancy_oak",
                    "fancy_oak_bees",
                    "fancy_oak_bees_0002",
                    "fancy_oak_bees_0002_leaf_litter",
                    "fancy_oak_bees_002",
                    "fancy_oak_bees_005",
                    "fancy_oak_leaf_litter",
                    "jungle_bush",
                    "jungle_tree",
                    "jungle_tree_no_vine",
                    "mangrove",
                    "mega_jungle_tree",
                    "mega_pine",
                    "mega_spruce",
                    "oak",
                    "oak_bees_0002",
                    "oak_bees_0002_leaf_litter",
                    "oak_bees_002",
                    "oak_bees_005",
                    "oak_leaf_litter",
                    "pale_oak",
                    "pale_oak_bonemeal",
                    "pale_oak_creaking",
                    "pine",
                    "spruce",
                    "super_birch_bees",
                    "super_birch_bees_0002",
                    "super_birch_bees_05",
                    "swamp_oak",
                    "tall_mangrove");

    private static Map<String, JsonObject> configurations;

    @BeforeAll
    static void generateConfigurations() {
        configurations =
                BetterTreesConfiguredFeatureProvider.configuredFeatures().entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        entry -> entry.getKey().getPath(), Map.Entry::getValue));
    }

    @Test
    void providerBuildsTheExactVanillaIdSet() {
        assertEquals(EXPECTED_IDS, configurations.keySet());
        assertEquals(42, configurations.size());
    }

    @Test
    void providerWritesEveryConfiguredFeatureToItsVanillaPath(@TempDir Path output)
            throws IOException {
        new BetterTreesConfiguredFeatureProvider(output).run(CachedOutput.NO_CACHE).join();
        Path directory = output.resolve("data/minecraft/worldgen/configured_feature");

        try (Stream<Path> files = Files.list(directory)) {
            Set<String> ids =
                    files.map(path -> path.getFileName().toString().replaceFirst("\\.json$", ""))
                            .collect(Collectors.toSet());
            assertEquals(EXPECTED_IDS, ids);
        }
        JsonObject paleOak =
                JsonParser.parseString(Files.readString(directory.resolve("pale_oak_creaking.json")))
                        .getAsJsonObject();
        assertTrue(
                hasDecorator(
                        config(paleOak).getAsJsonArray("decorators"),
                        "minecraft:creaking_heart"));
    }

    @Test
    void oakBirchCherryAndBeeVariantsKeepTheirStructuralDifferences() {
        JsonObject oak = json("oak");
        assertEquals("minecraft:fancy_trunk_placer", type(oak, "trunk_placer"));
        assertEquals(10, config(oak).getAsJsonObject("trunk_placer").get("base_height").getAsInt());

        JsonObject birch = json("birch");
        assertEquals("minecraft:forking_trunk_placer", type(birch, "trunk_placer"));
        assertEquals("minecraft:fancy_foliage_placer", type(birch, "foliage_placer"));

        JsonObject cherry = json("cherry");
        assertEquals("minecraft:fancy_trunk_placer", type(cherry, "trunk_placer"));
        assertEquals("minecraft:cherry_foliage_placer", type(cherry, "foliage_placer"));

        assertEquals(0.002, beehiveProbability("oak_bees_0002"));
        assertEquals(0.02, beehiveProbability("oak_bees_002"));
        assertEquals(0.05, beehiveProbability("oak_bees_005"));
    }

    @Test
    void mangroveMegaSprucePaleOakAndLeafLitterVariantsArePresent() {
        assertEquals("minecraft:mangrove_root_placer", type(json("mangrove"), "root_placer"));
        assertEquals(
                12,
                config(json("tall_mangrove"))
                        .getAsJsonObject("root_placer")
                        .getAsJsonObject("mangrove_root_placement")
                        .get("max_root_width")
                        .getAsInt());
        assertEquals(
                "minecraft:giant_trunk_placer",
                type(json("mega_spruce"), "trunk_placer"));
        assertEquals(
                26,
                config(json("mega_spruce"))
                        .getAsJsonObject("trunk_placer")
                        .get("base_height")
                        .getAsInt());

        JsonArray paleOakDecorators = config(json("pale_oak_creaking")).getAsJsonArray("decorators");
        assertTrue(hasDecorator(paleOakDecorators, "minecraft:creaking_heart"));
        assertTrue(hasDecorator(paleOakDecorators, "minecraft:pale_moss"));
        assertEquals(
                "minecraft:mangrove_root_placer",
                type(json("pale_oak_bonemeal"), "root_placer"));

        JsonArray litterDecorators =
                config(json("oak_bees_0002_leaf_litter")).getAsJsonArray("decorators");
        assertTrue(hasDecorator(litterDecorators, "minecraft:beehive"));
        assertTrue(hasDecorator(litterDecorators, "minecraft:place_on_ground"));
    }

    @Test
    void everyEntryIsAValidTreeConfiguredFeature() {
        configurations.forEach(
                (id, configuredFeature) ->
                        assertEquals("minecraft:tree", json(id).get("type").getAsString(), id));
    }

    private static JsonObject json(String id) {
        return configurations.get(id);
    }

    private static JsonObject config(JsonObject configuredFeature) {
        return configuredFeature.getAsJsonObject("config");
    }

    private static String type(JsonObject configuredFeature, String field) {
        return config(configuredFeature).getAsJsonObject(field).get("type").getAsString();
    }

    private static double beehiveProbability(String id) {
        JsonArray decorators = config(json(id)).getAsJsonArray("decorators");
        for (JsonElement element : decorators) {
            JsonObject decorator = element.getAsJsonObject();
            if (decorator.get("type").getAsString().equals("minecraft:beehive")) {
                return decorator.get("probability").getAsDouble();
            }
        }
        throw new AssertionError("missing beehive decorator in " + id);
    }

    private static boolean hasDecorator(JsonArray decorators, String expectedType) {
        for (JsonElement element : decorators) {
            if (element.getAsJsonObject().get("type").getAsString().equals(expectedType)) {
                return true;
            }
        }
        return false;
    }
}

package br.com.bobwizley.rootboot.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.bobwizley.rootboot.recipe.RecipeSpec;
import br.com.bobwizley.rootboot.recipe.RootBootRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import net.minecraft.world.item.crafting.Recipe;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Confronts every additive RootBoot recipe with the real 26.2 data read out of the Minecraft jar. Deciding
 * what a slice may leave out is a comparison against this data, and such a comparison is worthless unless it
 * runs again on every build: a Minecraft update can turn one of these recipes into a duplicate of a vanilla
 * one, which no id collision reveals because the two live in different namespaces.
 *
 * <p>{@link VanillaCookingConflictTest} covers the complementary case, the recipes that deliberately claim
 * a vanilla id in order to replace it. That an id exists at all is proven by
 * {@link RootBootRecipeProviderTest}, which resolves every one of them against the real registry.
 */
class VanillaRecipeConflictTest {

    private static final String RECIPES = "data/minecraft/recipe/";

    private static Map<String, JsonObject> vanillaRecipes;

    @BeforeAll
    static void readVanillaRecipes() throws IOException {
        vanillaRecipes = loadVanillaRecipes();
    }

    @Test
    void noAdditiveStonecuttingRepeatsAConversionVanillaAlreadyOffers() {
        Set<String> vanillaConversions = vanillaStonecuttingConversions();

        for (RecipeSpec spec : additive()) {
            if (!(spec instanceof RecipeSpec.Stonecutting stonecutting)) {
                continue;
            }
            List<String> duplicated =
                    stonecutting.ingredients().stream()
                            .map(
                                    input ->
                                            conversion(
                                                    input.reference(),
                                                    stonecutting.result().item(),
                                                    stonecutting.result().count()))
                            .filter(vanillaConversions::contains)
                            .toList();

            assertEquals(
                    List.of(),
                    duplicated,
                    () -> id(spec) + " repeats a vanilla stonecutter conversion: " + duplicated);
        }
    }

    @Test
    void noAdditiveShapedRecipeRepeatsAVanillaOne() {
        Set<String> vanillaShapes = vanillaShapedRecipes();

        for (RecipeSpec spec : additive()) {
            if (!(spec instanceof RecipeSpec.Shaped shaped)) {
                continue;
            }
            String shape = shapeOf(shaped);
            assertTrue(
                    !vanillaShapes.contains(shape),
                    () -> id(spec) + " repeats a vanilla crafting recipe: " + shape);
        }
    }

    /** The recipes that add to vanilla; the {@code minecraft}-namespaced ones replace it on purpose. */
    private static List<RecipeSpec> additive() {
        return RootBootRecipes.all().stream()
                .filter(spec -> spec.namespace().equals("rootboot"))
                .toList();
    }

    /** Every input-to-output conversion the vanilla stonecutter performs, quantity included. */
    private static Set<String> vanillaStonecuttingConversions() {
        Set<String> conversions = new LinkedHashSet<>();
        for (JsonObject recipe : vanillaRecipes.values()) {
            if (!type(recipe).equals("minecraft:stonecutting")) {
                continue;
            }
            JsonObject result = recipe.getAsJsonObject("result");
            for (String input : ingredientList(recipe.get("ingredient"))) {
                conversions.add(conversion(input, result.get("id").getAsString(), count(result)));
            }
        }
        return conversions;
    }

    /** Every vanilla shaped recipe, reduced to the pattern, key and result that make it that recipe. */
    private static Set<String> vanillaShapedRecipes() {
        Set<String> shapes = new LinkedHashSet<>();
        for (JsonObject recipe : vanillaRecipes.values()) {
            if (!type(recipe).equals("minecraft:crafting_shaped")) {
                continue;
            }
            JsonObject result = recipe.getAsJsonObject("result");
            List<String> pattern =
                    recipe.getAsJsonArray("pattern").asList().stream().map(JsonElement::getAsString).toList();
            Map<String, List<String>> key = new LinkedHashMap<>();
            recipe
                    .getAsJsonObject("key")
                    .entrySet()
                    .forEach(entry -> key.put(entry.getKey(), ingredientList(entry.getValue())));

            shapes.add(shape(pattern, key, result.get("id").getAsString(), count(result)));
        }
        return shapes;
    }

    private static String shapeOf(RecipeSpec.Shaped spec) {
        Map<String, List<String>> key =
                spec.key().entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        entry -> String.valueOf(entry.getKey()),
                                        entry -> List.of(entry.getValue().reference()),
                                        (first, second) -> first,
                                        LinkedHashMap::new));
        return shape(spec.pattern(), key, spec.result().item(), spec.result().count());
    }

    private static String shape(
            List<String> pattern, Map<String, List<String>> key, String result, int count) {
        String inputs =
                key.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining(","));
        return pattern + "|" + inputs + "|" + result + "x" + count;
    }

    private static String conversion(String input, String result, int count) {
        return input + "->" + result + "x" + count;
    }

    /** A recipe ingredient is either one id or a list of equivalent ones. */
    private static List<String> ingredientList(JsonElement ingredient) {
        if (ingredient == null) {
            return List.of();
        }
        if (ingredient.isJsonArray()) {
            JsonArray array = ingredient.getAsJsonArray();
            return array.asList().stream().map(JsonElement::getAsString).toList();
        }
        return List.of(ingredient.getAsString());
    }

    private static int count(JsonObject result) {
        return result.has("count") ? result.get("count").getAsInt() : 1;
    }

    private static String type(JsonObject recipe) {
        return recipe.get("type").getAsString();
    }

    private static String id(RecipeSpec spec) {
        return spec.namespace() + ":" + spec.path();
    }

    /**
     * Every vanilla recipe, read from the jar the Minecraft classes came from: the generated datapack sits
     * earlier on the test classpath, so classpath lookups would find RootBoot's own output instead.
     */
    private static Map<String, JsonObject> loadVanillaRecipes() throws IOException {
        Map<String, JsonObject> recipes = new LinkedHashMap<>();
        try (JarFile jar = new JarFile(minecraftJar().toFile())) {
            for (JarEntry entry : jar.stream().toList()) {
                String name = entry.getName();
                if (!name.startsWith(RECIPES) || !name.endsWith(".json")) {
                    continue;
                }
                try (InputStream in = jar.getInputStream(entry);
                        InputStreamReader reader = new InputStreamReader(in)) {
                    recipes.put(
                            name.substring(RECIPES.length(), name.length() - ".json".length()),
                            JsonParser.parseReader(reader).getAsJsonObject());
                }
            }
        }
        assertTrue(!recipes.isEmpty(), "no vanilla recipes found in the Minecraft jar");
        return recipes;
    }

    private static Path minecraftJar() {
        try {
            return Path.of(Recipe.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new UncheckedIOException(new IOException("cannot locate the Minecraft jar", e));
        }
    }
}

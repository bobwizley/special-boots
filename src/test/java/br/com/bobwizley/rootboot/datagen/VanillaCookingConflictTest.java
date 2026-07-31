package br.com.bobwizley.rootboot.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.bobwizley.rootboot.recipe.RecipeSpec;
import br.com.bobwizley.rootboot.recipe.RootBootRecipes;
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
 * Reads the vanilla furnace recipes straight out of the Minecraft jar, so the Recycling overrides are
 * checked against the real 26.2 data rather than against an assumption about it. Two things can go wrong
 * and both are silent in game: an override aimed at an id vanilla does not have, and a piece of gear
 * vanilla still cooks into a nugget through a recipe RootBoot leaves untouched.
 */
class VanillaCookingConflictTest {

    private static final String RECIPES = "data/minecraft/recipe/";

    private static Map<String, JsonObject> vanillaRecipes;

    @BeforeAll
    static void readVanillaRecipes() throws IOException {
        vanillaRecipes = loadVanillaRecipes();
    }

    @Test
    void everyOverriddenIdIsAVanillaNuggetRecipeReplacedByAnIngotOne() {
        for (RecipeSpec.Cooking spec : overrides()) {
            JsonObject vanilla = vanillaRecipes.get(spec.path());
            assertTrue(vanilla != null, () -> "no vanilla recipe named " + spec.path() + " to override");

            String vanillaResult = vanilla.getAsJsonObject("result").get("id").getAsString();
            assertTrue(
                    vanillaResult.endsWith("_nugget"),
                    () -> spec.path() + " overrides " + vanillaResult + ", expected a nugget recipe");
            assertEquals(
                    Set.copyOf(ingredients(vanilla)),
                    Set.copyOf(spec.inputs()),
                    () -> "override " + spec.path() + " must accept exactly the gear vanilla accepted");
        }
    }

    @Test
    void noVanillaCookingRecipeStillClaimsGearRootBootRecycles() {
        Set<String> overriddenIds = overrides().stream().map(RecipeSpec::path).collect(Collectors.toSet());
        Set<String> recycledGear =
                cookingSpecs().stream()
                        .flatMap(spec -> spec.inputs().stream())
                        .collect(Collectors.toSet());

        for (Map.Entry<String, JsonObject> vanilla : vanillaRecipes.entrySet()) {
            if (overriddenIds.contains(vanilla.getKey()) || !isCooking(vanilla.getValue())) {
                continue;
            }
            List<String> claimed =
                    ingredients(vanilla.getValue()).stream().filter(recycledGear::contains).toList();
            assertEquals(
                    List.of(),
                    claimed,
                    () -> "vanilla:" + vanilla.getKey() + " still cooks gear RootBoot recycles: " + claimed);
        }
    }

    private static List<RecipeSpec.Cooking> cookingSpecs() {
        return RootBootRecipes.all().stream()
                .filter(RecipeSpec.Cooking.class::isInstance)
                .map(RecipeSpec.Cooking.class::cast)
                .toList();
    }

    private static List<RecipeSpec.Cooking> overrides() {
        return cookingSpecs().stream().filter(spec -> spec.namespace().equals("minecraft")).toList();
    }

    private static boolean isCooking(JsonObject recipe) {
        String type = recipe.get("type").getAsString();
        return type.equals("minecraft:smelting") || type.equals("minecraft:blasting");
    }

    /** The item ids a cooking recipe accepts; a non-cooking recipe has no {@code ingredient} at all. */
    private static List<String> ingredients(JsonObject recipe) {
        JsonElement ingredient = recipe.get("ingredient");
        if (ingredient == null) {
            return List.of();
        }
        return ingredient.isJsonArray()
                ? ingredient.getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList()
                : List.of(ingredient.getAsString());
    }

    /**
     * Every vanilla recipe, keyed by path the way a spec names its override target. Read from the jar the
     * Minecraft classes themselves came from: the generated datapack sits earlier on the test classpath,
     * so looking these up as classpath resources would find RootBoot's own overrides instead of vanilla.
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

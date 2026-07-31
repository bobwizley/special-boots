package br.com.bobwizley.rootboot.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.com.bobwizley.rootboot.recipe.RecipeSpec;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Ingredient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Asserts that a recipe JSON object (generated file or codec output) matches a {@link RecipeSpec}. */
final class RecipeJson {

    private RecipeJson() {
    }

    static void assertMatchesSpec(RecipeSpec spec, JsonObject json) {
        assertEquals(spec.result().item(), json.getAsJsonObject("result").get("id").getAsString());
        assertEquals(spec.result().count(), resultCount(json));
        assertEquals(
                spec.group(),
                Optional.ofNullable(json.get("group")).map(JsonElement::getAsString),
                () -> "recipe book group of " + spec.namespace() + ":" + spec.path());
        switch (spec) {
            case RecipeSpec.Cooking cooking -> assertCooking(cooking, json);
            case RecipeSpec.Shaped shaped -> assertShaped(shaped, json);
            case RecipeSpec.Shapeless shapeless -> assertShapeless(shapeless, json);
            case RecipeSpec.Stonecutting stonecutting -> assertStonecutting(stonecutting, json);
        }
    }

    /** The durations Minecraft leaves out of the JSON when a cooking recipe uses the plain furnace pace. */
    private static int defaultCookingTime(RecipeSpec.CookingMethod method) {
        return switch (method) {
            case SMELTING -> 200;
            case BLASTING -> 100;
        };
    }

    private static void assertCooking(RecipeSpec.Cooking spec, JsonObject json) {
        assertEquals(
                "minecraft:" + spec.method().name().toLowerCase(Locale.ROOT),
                json.get("type").getAsString());

        JsonElement ingredient = json.get("ingredient");
        List<String> inputs =
                ingredient.isJsonArray()
                        ? ingredient.getAsJsonArray().asList().stream()
                                .map(JsonElement::getAsString)
                                .collect(Collectors.toList())
                        : List.of(ingredient.getAsString());
        assertEquals(spec.inputs(), inputs);

        assertEquals(spec.experience(), json.get("experience").getAsFloat());
        assertEquals(
                spec.cookingTime(),
                Optional.ofNullable(json.get("cookingtime"))
                        .map(JsonElement::getAsInt)
                        .orElseGet(() -> defaultCookingTime(spec.method())));
    }

    private static void assertShaped(RecipeSpec.Shaped spec, JsonObject json) {
        assertEquals("minecraft:crafting_shaped", json.get("type").getAsString());

        List<String> pattern =
                json.getAsJsonArray("pattern").asList().stream()
                        .map(JsonElement::getAsString)
                        .collect(Collectors.toList());
        assertEquals(spec.pattern(), pattern);

        JsonObject key = json.getAsJsonObject("key");
        spec.key()
                .forEach(
                        (symbol, ingredient) ->
                                assertEquals(
                                        ingredient.reference(), key.get(String.valueOf(symbol)).getAsString()));
    }

    private static void assertShapeless(RecipeSpec.Shapeless spec, JsonObject json) {
        assertEquals("minecraft:crafting_shapeless", json.get("type").getAsString());

        List<String> actual =
                json.getAsJsonArray("ingredients").asList().stream()
                        .map(JsonElement::getAsString)
                        .collect(Collectors.toList());
        List<String> expected =
                spec.ingredients().stream().map(Ingredient::reference).collect(Collectors.toList());
        assertEquals(expected, actual);
    }

    private static void assertStonecutting(RecipeSpec.Stonecutting spec, JsonObject json) {
        assertEquals("minecraft:stonecutting", json.get("type").getAsString());

        JsonElement ingredient = json.get("ingredient");
        List<String> actual =
                ingredient.isJsonArray()
                        ? ingredient.getAsJsonArray().asList().stream()
                                .map(JsonElement::getAsString)
                                .collect(Collectors.toList())
                        : List.of(ingredient.getAsString());
        List<String> expected =
                spec.ingredients().stream().map(Ingredient::reference).collect(Collectors.toList());
        assertEquals(expected, actual, () -> "inputs of " + spec.namespace() + ":" + spec.path());
    }

    private static int resultCount(JsonObject json) {
        JsonObject result = json.getAsJsonObject("result");
        return result.has("count") ? result.get("count").getAsInt() : 1;
    }
}

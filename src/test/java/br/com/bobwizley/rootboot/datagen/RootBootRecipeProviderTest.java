package br.com.bobwizley.rootboot.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import br.com.bobwizley.rootboot.recipe.RecipeSpec;
import br.com.bobwizley.rootboot.recipe.RootBootRecipes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.crafting.Recipe;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Runs the datagen recipe provider in-process against a capturing output and serializes the recipes it
 * produces, so a regression in the spec-to-builder mapping (ids, format, ingredients, quantities) is
 * caught here rather than slipping past against stale committed JSON.
 */
class RootBootRecipeProviderTest {

    private static HolderLookup.Provider registries;

    @BeforeAll
    static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        registries = VanillaRegistries.createLookup();
    }

    @Test
    void providerEmitsEveryRecipeWithMatchingContent() {
        CapturingOutput output = new CapturingOutput();
        new RootBootRecipeProvider.Recipes(registries, output).buildRecipes();

        Set<String> expectedIds =
                RootBootRecipes.all().stream()
                        .map(spec -> spec.namespace() + ":" + spec.path())
                        .collect(java.util.stream.Collectors.toSet());
        assertEquals(expectedIds, output.recipes.keySet());

        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
        for (RecipeSpec spec : RootBootRecipes.all()) {
            Recipe<?> recipe = output.recipes.get(spec.namespace() + ":" + spec.path());
            assertNotNull(recipe, () -> "provider did not emit " + spec.path());
            JsonObject json = Recipe.CODEC.encodeStart(ops, recipe).getOrThrow().getAsJsonObject();
            RecipeJson.assertMatchesSpec(spec, json);
        }
    }

    // A cooking recipe accepts a whole family of gear, and the recipe book unlocks on an OR of the
    // advancement criteria: one criterion per eligible item is what makes owning any of them enough.
    @Test
    void everyEligibleItemUnlocksItsCookingRecipe() {
        CapturingOutput output = new CapturingOutput();
        new RootBootRecipeProvider.Recipes(registries, output).buildRecipes();

        for (RecipeSpec spec : RootBootRecipes.all()) {
            if (!(spec instanceof RecipeSpec.Cooking cooking)) {
                continue;
            }
            String id = spec.namespace() + ":" + spec.path();
            Advancement advancement = output.advancements.get(id).value();

            Set<String> expected = new LinkedHashSet<>();
            expected.add("has_the_recipe");
            cooking.inputs().forEach(input -> expected.add("has_" + input.split(":")[1]));
            assertEquals(expected, advancement.criteria().keySet(), () -> "unlock criteria of " + id);

            assertEquals(
                    List.of(List.copyOf(expected)),
                    advancement.requirements().requirements(),
                    () -> "any single eligible item must unlock " + id);
        }
    }

    private static final class CapturingOutput implements RecipeOutput {

        private final Map<String, Recipe<?>> recipes = new LinkedHashMap<>();
        private final Map<String, AdvancementHolder> advancements = new LinkedHashMap<>();

        @Override
        public void accept(
                ResourceKey<Recipe<?>> id, Recipe<?> recipe, AdvancementHolder advancement) {
            recipes.put(id.identifier().toString(), recipe);
            advancements.put(id.identifier().toString(), advancement);
        }

        @Override
        public Advancement.Builder advancement() {
            return Advancement.Builder.recipeAdvancement();
        }

        @Override
        public void includeRootAdvancement() {
        }
    }
}

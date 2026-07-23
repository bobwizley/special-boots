package br.com.bobwizley.rootboot.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import br.com.bobwizley.rootboot.recipe.RecipeSpec;
import br.com.bobwizley.rootboot.recipe.RootBootRecipes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.util.LinkedHashMap;
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

    private static final class CapturingOutput implements RecipeOutput {

        private final Map<String, Recipe<?>> recipes = new LinkedHashMap<>();

        @Override
        public void accept(
                ResourceKey<Recipe<?>> id, Recipe<?> recipe, AdvancementHolder advancement) {
            recipes.put(id.identifier().toString(), recipe);
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

package br.com.bobwizley.rootboot.datagen;

import br.com.bobwizley.rootboot.recipe.RecipeSpec;
import br.com.bobwizley.rootboot.recipe.RootBootRecipes;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Emits {@link RootBootRecipes} as datapack JSON via Fabric Data Generation. This is the datagen
 * boundary: the recipe content lives in the typed model, and this class only maps each spec onto the
 * matching Minecraft builder. The mapping is packaged as {@link Recipes} so tests can run it directly.
 */
public final class RootBootRecipeProvider extends FabricRecipeProvider {

    public RootBootRecipeProvider(
            FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected RecipeProvider createRecipeProvider(
            HolderLookup.Provider registries, RecipeOutput output) {
        return new Recipes(registries, output);
    }

    // Fabric forces the mod namespace onto recipe ids; overriding here keeps the spec's own namespace so
    // minecraft-namespaced recipes actually replace their vanilla counterparts.
    @Override
    protected Identifier getRecipeIdentifier(Identifier id) {
        return id;
    }

    @Override
    public String getName() {
        return "RootBoot Recipes";
    }

    /** Translates every {@link RootBootRecipes} spec into a Minecraft recipe builder. */
    static final class Recipes extends RecipeProvider {

        private final HolderGetter<Item> items;

        Recipes(HolderLookup.Provider registries, RecipeOutput output) {
            super(registries, output);
            this.items = registries.lookupOrThrow(Registries.ITEM);
        }

        @Override
        public void buildRecipes() {
            for (RecipeSpec spec : RootBootRecipes.all()) {
                switch (spec) {
                    case RecipeSpec.Cooking cooking -> emitCooking(cooking);
                    case RecipeSpec.Shaped shaped -> emitShaped(shaped);
                    case RecipeSpec.Shapeless shapeless -> emitShapeless(shapeless);
                    case RecipeSpec.Stonecutting stonecutting -> emitStonecutting(stonecutting);
                }
            }
        }

        private void emitCooking(RecipeSpec.Cooking spec) {
            Ingredient inputs =
                    Ingredient.of(spec.inputs().stream().map(this::item).toArray(Item[]::new));
            SimpleCookingRecipeBuilder builder =
                    switch (spec.method()) {
                        case SMELTING ->
                                SimpleCookingRecipeBuilder.smelting(
                                        inputs,
                                        category(spec.category()),
                                        CookingBookCategory.MISC,
                                        item(spec.result().item()),
                                        spec.experience(),
                                        spec.cookingTime());
                        case BLASTING ->
                                SimpleCookingRecipeBuilder.blasting(
                                        inputs,
                                        category(spec.category()),
                                        CookingBookCategory.MISC,
                                        item(spec.result().item()),
                                        spec.experience(),
                                        spec.cookingTime());
                    };
            spec.group().ifPresent(builder::group);
            // One criterion per input, the way vanilla does it: the recipe book unlocks on an OR of the
            // criteria, so a single one would hide the recipe from a player who owns any other eligible
            // piece of gear.
            for (String input : spec.inputs()) {
                builder.unlockedBy("has_" + identifier(input).getPath(), has(item(input)));
            }
            builder.save(output, recipeKey(spec));
        }

        private void emitShaped(RecipeSpec.Shaped spec) {
            ShapedRecipeBuilder builder =
                    ShapedRecipeBuilder.shaped(
                            items, category(spec.category()), item(spec.result().item()), spec.result().count());
            spec.pattern().forEach(builder::pattern);
            spec.key()
                    .forEach(
                            (symbol, ingredient) -> {
                                if (ingredient.isTag()) {
                                    builder.define(symbol, itemTag(ingredient.id()));
                                } else {
                                    builder.define(symbol, item(ingredient.id()));
                                }
                            });
            spec.group().ifPresent(builder::group);
            builder.unlockedBy("has_ingredient", criterion(firstIngredient(spec)));
            builder.save(output, recipeKey(spec));
        }

        private void emitShapeless(RecipeSpec.Shapeless spec) {
            ShapelessRecipeBuilder builder =
                    ShapelessRecipeBuilder.shapeless(
                            items, category(spec.category()), item(spec.result().item()), spec.result().count());
            for (RecipeSpec.Ingredient ingredient : spec.ingredients()) {
                if (ingredient.isTag()) {
                    builder.requires(itemTag(ingredient.id()));
                } else {
                    builder.requires(item(ingredient.id()));
                }
            }
            spec.group().ifPresent(builder::group);
            builder.unlockedBy("has_ingredient", criterion(spec.ingredients().getFirst()));
            builder.save(output, recipeKey(spec));
        }

        private void emitStonecutting(RecipeSpec.Stonecutting spec) {
            SingleItemRecipeBuilder builder =
                    SingleItemRecipeBuilder.stonecutting(
                            ingredient(spec.ingredient()),
                            category(spec.category()),
                            item(spec.result().item()),
                            spec.result().count());
            spec.group().ifPresent(builder::group);
            builder.unlockedBy("has_ingredient", criterion(spec.ingredient()));
            builder.save(output, recipeKey(spec));
        }

        private Ingredient ingredient(RecipeSpec.Ingredient ingredient) {
            return ingredient.isTag()
                    ? Ingredient.of(items.getOrThrow(itemTag(ingredient.id())))
                    : Ingredient.of(item(ingredient.id()));
        }

        private Criterion<?> criterion(RecipeSpec.Ingredient ingredient) {
            return ingredient.isTag() ? has(itemTag(ingredient.id())) : has(item(ingredient.id()));
        }

        private Item item(String id) {
            return items.getOrThrow(ResourceKey.create(Registries.ITEM, identifier(id))).value();
        }

        // Deterministic so the generated advancement is reproducible: the ingredient under the first
        // filled cell in reading order, not whatever the key map happens to iterate first.
        private static RecipeSpec.Ingredient firstIngredient(RecipeSpec.Shaped spec) {
            for (String row : spec.pattern()) {
                for (int i = 0; i < row.length(); i++) {
                    if (row.charAt(i) != ' ') {
                        return spec.key().get(row.charAt(i));
                    }
                }
            }
            throw new IllegalStateException("shaped recipe with an empty pattern: " + spec.path());
        }

        private static ResourceKey<Recipe<?>> recipeKey(RecipeSpec spec) {
            return ResourceKey.create(
                    Registries.RECIPE, Identifier.fromNamespaceAndPath(spec.namespace(), spec.path()));
        }

        private static TagKey<Item> itemTag(String id) {
            return TagKey.create(Registries.ITEM, identifier(id));
        }

        private static Identifier identifier(String id) {
            int colon = id.indexOf(':');
            return Identifier.fromNamespaceAndPath(id.substring(0, colon), id.substring(colon + 1));
        }

        private static RecipeCategory category(RecipeSpec.Category category) {
            return switch (category) {
                case TOOLS -> RecipeCategory.TOOLS;
                case DECORATIONS -> RecipeCategory.DECORATIONS;
                case REDSTONE -> RecipeCategory.REDSTONE;
                case MISC -> RecipeCategory.MISC;
            };
        }
    }
}

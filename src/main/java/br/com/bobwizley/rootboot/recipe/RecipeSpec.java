package br.com.bobwizley.rootboot.recipe;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Typed, Minecraft-agnostic source of truth for a recipe. The datagen provider translates each spec
 * into the corresponding Minecraft recipe builder, and tests assert directly against these values, so
 * the JSON packaged in the JAR and the Java definition never drift apart.
 *
 * <p>A spec's {@code namespace}/{@code path} pair is also its datapack location: the {@code minecraft}
 * namespace makes a recipe replace the vanilla one with the same id, while {@code rootboot} adds a new
 * recipe.
 */
public sealed interface RecipeSpec
        permits RecipeSpec.Shaped, RecipeSpec.Shapeless, RecipeSpec.Stonecutting {

    String namespace();

    String path();

    Category category();

    /**
     * The recipe book group, empty when the recipe stands alone. A spec that overrides a vanilla recipe
     * must repeat the vanilla group: the generated JSON replaces the original wholesale, so anything the
     * spec cannot express is erased rather than inherited.
     */
    Optional<String> group();

    Result result();

    /** Grouping used by the recipe book; irrelevant to gameplay but part of a real recipe. */
    enum Category {
        TOOLS,
        DECORATIONS,
        REDSTONE,
        MISC
    }

    /** A recipe input: either an item id ({@code isTag == false}) or an item tag id. */
    record Ingredient(String id, boolean isTag) {
        public static Ingredient item(String id) {
            return new Ingredient(id, false);
        }

        public static Ingredient tag(String id) {
            return new Ingredient(id, true);
        }

        /** How this ingredient is written in a datapack recipe: a tag is prefixed with {@code #}. */
        public String reference() {
            return isTag ? "#" + id : id;
        }
    }

    record Result(String item, int count) {}

    record Shaped(
            String namespace,
            String path,
            Category category,
            Optional<String> group,
            List<String> pattern,
            Map<Character, Ingredient> key,
            Result result)
            implements RecipeSpec {}

    record Shapeless(
            String namespace,
            String path,
            Category category,
            Optional<String> group,
            List<Ingredient> ingredients,
            Result result)
            implements RecipeSpec {}

    /** A stonecutter conversion: one input block cut into a stack of the result. */
    record Stonecutting(
            String namespace,
            String path,
            Category category,
            Optional<String> group,
            Ingredient ingredient,
            Result result)
            implements RecipeSpec {}
}

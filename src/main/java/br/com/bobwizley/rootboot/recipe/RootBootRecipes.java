package br.com.bobwizley.rootboot.recipe;

import br.com.bobwizley.rootboot.recipe.RecipeSpec.Category;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Ingredient;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Result;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shaped;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shapeless;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Every always-on recipe RootBoot ships, across features. These have no runtime toggle: they are baked
 * into the datapack at build time.
 */
public final class RootBootRecipes {

    private RootBootRecipes() {
    }

    public static List<RecipeSpec> all() {
        return Stream.of(simple(), MoreButtons.all(), Recycling.all(), OmniCutWood.all())
                .flatMap(List::stream)
                .toList();
    }

    /**
     * The four standalone recipes. Clock and compass keep their vanilla ids so the cheaper versions
     * replace the originals; bell and wool-to-string are additive.
     */
    static List<RecipeSpec> simple() {
        return List.of(cheaperClock(), cheaperCompass(), bell(), woolToString());
    }

    private static RecipeSpec cheaperClock() {
        return new Shaped(
                "minecraft",
                "clock",
                Category.TOOLS,
                Optional.empty(),
                List.of(" # ", "#X#", " # "),
                Map.of(
                        '#', Ingredient.item("minecraft:gold_nugget"),
                        'X', Ingredient.item("minecraft:redstone")),
                new Result("minecraft:clock", 1));
    }

    private static RecipeSpec cheaperCompass() {
        return new Shaped(
                "minecraft",
                "compass",
                Category.TOOLS,
                Optional.empty(),
                List.of(" # ", "#X#", " # "),
                Map.of(
                        '#', Ingredient.item("minecraft:iron_nugget"),
                        'X', Ingredient.item("minecraft:redstone")),
                new Result("minecraft:compass", 1));
    }

    private static RecipeSpec bell() {
        return new Shaped(
                "rootboot",
                "bell",
                Category.DECORATIONS,
                Optional.empty(),
                List.of("LLL", "SGS", "S S"),
                Map.of(
                        'L', Ingredient.tag("minecraft:logs"),
                        'G', Ingredient.item("minecraft:gold_block"),
                        'S', Ingredient.item("minecraft:stone")),
                new Result("minecraft:bell", 1));
    }

    private static RecipeSpec woolToString() {
        return new Shapeless(
                "rootboot",
                "wool_to_string",
                Category.MISC,
                Optional.empty(),
                List.of(Ingredient.tag("minecraft:wool")),
                new Result("minecraft:string", 4));
    }
}

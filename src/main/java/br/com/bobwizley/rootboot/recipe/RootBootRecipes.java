package br.com.bobwizley.rootboot.recipe;

import br.com.bobwizley.rootboot.recipe.RecipeSpec.Category;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Ingredient;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Result;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shaped;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shapeless;
import java.util.List;
import java.util.Map;

/**
 * The always-on simple recipes RootBoot ships. These have no runtime toggle: they are baked into the
 * datapack at build time and are the fixture the later declarative slices reuse.
 */
public final class RootBootRecipes {

    private RootBootRecipes() {
    }

    /** Clock and compass keep their vanilla ids so the cheaper versions replace the originals. */
    public static List<RecipeSpec> all() {
        return List.of(cheaperClock(), cheaperCompass(), bell(), woolToString());
    }

    private static RecipeSpec cheaperClock() {
        return new Shaped(
                "minecraft",
                "clock",
                Category.TOOLS,
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
                List.of(Ingredient.tag("minecraft:wool")),
                new Result("minecraft:string", 4));
    }
}

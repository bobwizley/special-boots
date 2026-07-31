package br.com.bobwizley.rootboot.recipe;

import br.com.bobwizley.rootboot.recipe.RecipeSpec.Category;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Ingredient;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Result;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shapeless;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Stonecutting;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * More-Buttons: buttons are deliberately generous. Crafting a block yields four buttons instead of
 * vanilla's one, and the stonecutter cuts six from the two stone families.
 *
 * <p>The stonecutter conversions for {@code stone_button} and {@code polished_blackstone_button} belong
 * to this feature by an explicit ownership decision (docs/FEATURES.md): OmniCut ceded them and must not
 * emit competing recipes.
 */
final class MoreButtons {

    private static final int CRAFTING_YIELD = 4;
    private static final int STONECUTTING_YIELD = 6;

    /** The recipe book group vanilla puts the twelve wooden button recipes in; the stone ones have none. */
    private static final Optional<String> WOODEN_BUTTON_GROUP = Optional.of("wooden_button");

    private static final List<String> WOODS =
            List.of(
                    "acacia",
                    "bamboo",
                    "birch",
                    "cherry",
                    "crimson",
                    "dark_oak",
                    "jungle",
                    "mangrove",
                    "oak",
                    "pale_oak",
                    "spruce",
                    "warped");

    private static final List<String> STONES = List.of("stone", "polished_blackstone");

    private MoreButtons() {
    }

    static List<RecipeSpec> all() {
        List<RecipeSpec> recipes = new ArrayList<>();
        for (ButtonFamily family : craftingFamilies()) {
            recipes.add(crafting(family));
        }
        for (ButtonFamily family : stoneFamilies()) {
            recipes.add(stonecutting(family));
        }
        return List.copyOf(recipes);
    }

    private static List<ButtonFamily> craftingFamilies() {
        List<ButtonFamily> families = new ArrayList<>();
        for (String wood : WOODS) {
            families.add(new ButtonFamily(wood + "_planks", wood + "_button", WOODEN_BUTTON_GROUP));
        }
        families.addAll(stoneFamilies());
        return families;
    }

    private static List<ButtonFamily> stoneFamilies() {
        return STONES.stream()
                .map(stone -> new ButtonFamily(stone, stone + "_button", Optional.<String>empty()))
                .toList();
    }

    // Reusing the vanilla recipe id is what makes the override replace the one-button yield; a rootboot
    // id would leave both recipes available.
    private static RecipeSpec crafting(ButtonFamily family) {
        return new Shapeless(
                "minecraft",
                family.button(),
                Category.REDSTONE,
                family.group(),
                List.of(Ingredient.item(family.blockId())),
                new Result(family.buttonId(), CRAFTING_YIELD));
    }

    // Additive, so no vanilla group to preserve: the stonecutter conversions are new recipes.
    private static RecipeSpec stonecutting(ButtonFamily family) {
        return new Stonecutting(
                "rootboot",
                family.button() + "_from_" + family.block() + "_stonecutting",
                Category.REDSTONE,
                Optional.empty(),
                List.of(Ingredient.item(family.blockId())),
                new Result(family.buttonId(), STONECUTTING_YIELD));
    }

    /**
     * A block and the button it turns into, named by their vanilla registry paths, plus the recipe book
     * group the vanilla crafting recipe used.
     */
    private record ButtonFamily(String block, String button, Optional<String> group) {

        String blockId() {
            return "minecraft:" + block;
        }

        String buttonId() {
            return "minecraft:" + button;
        }
    }
}

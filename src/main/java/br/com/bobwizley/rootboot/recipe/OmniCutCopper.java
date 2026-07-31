package br.com.bobwizley.rootboot.recipe;

import br.com.bobwizley.rootboot.recipe.RecipeSpec.Category;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Ingredient;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Result;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shaped;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Stonecutting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * OmniCut's copper slice: recycling only. The stonecutter cuts stairs, a chiseled block or a grate back
 * down into the cut copper they came from, and the crafting table puts two slabs back together.
 *
 * <p>Cutting copper forward is left to vanilla, which already cuts the whole family in the stonecutter
 * (docs/FEATURES.md). Only the recovery direction is missing there — vanilla's stonecutter never cuts a
 * product back up into its block — and that is exactly what this slice adds.
 *
 * <p>Every conversion gives back one unit, the reference's "vanilla price" figure: each of these products
 * costs one cut copper in the stonecutter, and a slab costs half of one, so a pair of them rebuilds a whole.
 */
final class OmniCutCopper {

    /**
     * The eight weathering states, as the prefix vanilla puts on every id in the family. The pristine state
     * has no prefix at all, which is why these are prefixes rather than names.
     */
    private static final List<String> WEATHERING =
            List.of(
                    "",
                    "exposed_",
                    "weathered_",
                    "oxidized_",
                    "waxed_",
                    "waxed_exposed_",
                    "waxed_weathered_",
                    "waxed_oxidized_");

    /** What recycles into cut copper. The slab is absent: it goes through the crafting table instead. */
    private static final List<String> PRODUCTS =
            List.of("cut_copper_stairs", "chiseled_copper", "copper_grate");

    private OmniCutCopper() {
    }

    static List<RecipeSpec> all() {
        List<RecipeSpec> recipes = new ArrayList<>();
        for (String weathering : WEATHERING) {
            recipes.add(uncut(weathering));
            recipes.add(unslab(weathering));
        }
        return List.copyOf(recipes);
    }

    private static RecipeSpec uncut(String weathering) {
        String block = weathering + "cut_copper";
        return new Stonecutting(
                "rootboot",
                "copper/uncut/" + block,
                Category.BUILDING_BLOCKS,
                Optional.empty(),
                PRODUCTS.stream().map(product -> Ingredient.item(item(weathering + product))).toList(),
                new Result(item(block), 1));
    }

    // Side by side rather than stacked, matching the wood and rock slices and the reference.
    private static RecipeSpec unslab(String weathering) {
        String block = weathering + "cut_copper";
        return new Shaped(
                "rootboot",
                "copper/unslab/" + block,
                Category.BUILDING_BLOCKS,
                Optional.empty(),
                List.of("##"),
                Map.of('#', Ingredient.item(item(block + "_slab"))),
                new Result(item(block), 1));
    }

    private static String item(String path) {
        return "minecraft:" + path;
    }
}

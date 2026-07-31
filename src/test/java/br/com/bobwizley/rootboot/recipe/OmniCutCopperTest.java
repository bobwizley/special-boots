package br.com.bobwizley.rootboot.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.bobwizley.rootboot.recipe.RecipeSpec.Ingredient;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shaped;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Stonecutting;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Exercises OmniCut's copper slice: the eight weathering states, recovery only, and no recipe going the
 * forward direction vanilla already covers.
 */
class OmniCutCopperTest {

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

    private RecipeSpec byPath(String path) {
        return OmniCutCopper.all().stream()
                .filter(spec -> spec.path().equals(path))
                .findFirst()
                .orElseThrow(() -> new AssertionError("missing recipe rootboot:" + path));
    }

    @Test
    void coversTheEightWeatheringStatesTwiceAndNothingElse() {
        List<String> expected =
                WEATHERING.stream()
                        .flatMap(
                                weathering ->
                                        List.of(
                                                        "copper/uncut/" + weathering + "cut_copper",
                                                        "copper/unslab/" + weathering + "cut_copper")
                                                .stream())
                        .toList();

        List<String> paths = OmniCutCopper.all().stream().map(RecipeSpec::path).toList();
        assertEquals(Set.copyOf(expected), Set.copyOf(paths));
        assertEquals(expected.size(), paths.size(), "no conversion may be emitted twice");
        assertTrue(
                OmniCutCopper.all().stream().allMatch(spec -> spec.namespace().equals("rootboot")),
                "the whole slice is additive, so nothing may claim a minecraft id");
    }

    @Test
    void everyStateRecyclesStairsChiseledAndGrateBackIntoOneCutCopper() {
        for (String weathering : WEATHERING) {
            String block = weathering + "cut_copper";
            Stonecutting recipe =
                    assertInstanceOf(Stonecutting.class, byPath("copper/uncut/" + block), block);

            assertEquals(
                    List.of(
                            Ingredient.item("minecraft:" + weathering + "cut_copper_stairs"),
                            Ingredient.item("minecraft:" + weathering + "chiseled_copper"),
                            Ingredient.item("minecraft:" + weathering + "copper_grate")),
                    recipe.ingredients(),
                    () -> "inputs of " + block);
            assertEquals("minecraft:" + block, recipe.result().item());
            assertEquals(1, recipe.result().count());
        }
    }

    @Test
    void everyStateReassemblesTwoSlabsIntoOneCutCopper() {
        for (String weathering : WEATHERING) {
            String block = weathering + "cut_copper";
            Shaped recipe = assertInstanceOf(Shaped.class, byPath("copper/unslab/" + block), block);

            assertEquals(List.of("##"), recipe.pattern(), "side by side, never stacked");
            assertEquals(Ingredient.item("minecraft:" + block + "_slab"), recipe.key().get('#'));
            assertEquals("minecraft:" + block, recipe.result().item());
            assertEquals(1, recipe.result().count());
        }
    }

    // Vanilla already cuts the whole copper family forward, so a cut recipe here would be a duplicate.
    @Test
    void nothingCutsCopperForward() {
        assertFalse(
                OmniCutCopper.all().stream().anyMatch(spec -> spec.path().contains("/cut/")),
                "the forward direction belongs to vanilla");
        assertTrue(
                OmniCutCopper.all().stream()
                        .allMatch(spec -> spec.result().item().endsWith("cut_copper")),
                "every recipe here recovers a cut copper block");
    }
}

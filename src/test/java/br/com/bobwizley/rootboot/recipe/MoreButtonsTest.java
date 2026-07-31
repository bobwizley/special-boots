package br.com.bobwizley.rootboot.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.bobwizley.rootboot.recipe.RecipeSpec.Ingredient;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shapeless;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Stonecutting;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Exercises the More-Buttons source of truth: the exact set of materials, recipe types and generous
 * quantities, plus the id discipline that makes the crafting recipes replace vanilla instead of
 * coexisting with it.
 */
class MoreButtonsTest {

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

    private RecipeSpec byId(String namespace, String path) {
        return MoreButtons.all().stream()
                .filter(spec -> spec.namespace().equals(namespace) && spec.path().equals(path))
                .findFirst()
                .orElseThrow(() -> new AssertionError("missing recipe " + namespace + ":" + path));
    }

    @Test
    void shipsExactlyFourteenCraftingOverridesAndTwoStonecuttingRecipes() {
        List<String> ids =
                MoreButtons.all().stream()
                        .map(spec -> spec.namespace() + ":" + spec.path())
                        .collect(Collectors.toList());

        List<String> expected =
                Stream.concat(
                                Stream.concat(WOODS.stream(), STONES.stream())
                                        .map(material -> "minecraft:" + material + "_button"),
                                STONES.stream()
                                        .map(
                                                stone ->
                                                        "rootboot:"
                                                                + stone
                                                                + "_button_from_"
                                                                + stone
                                                                + "_stonecutting"))
                        .collect(Collectors.toList());

        assertEquals(expected, ids);
    }

    @Test
    void everyPlanksBlockYieldsFourMatchingWoodenButtonsInCrafting() {
        for (String wood : WOODS) {
            Shapeless recipe =
                    assertInstanceOf(
                            Shapeless.class, byId("minecraft", wood + "_button"), wood + " button");

            assertEquals(
                    List.of(Ingredient.item("minecraft:" + wood + "_planks")), recipe.ingredients());
            assertEquals("minecraft:" + wood + "_button", recipe.result().item());
            assertEquals(4, recipe.result().count(), wood + " planks must yield four buttons");
            assertEquals(
                    Optional.of("wooden_button"),
                    recipe.group(),
                    () -> "the override must keep the vanilla recipe book group for " + wood);
        }
    }

    @Test
    void bothStoneBlocksYieldFourMatchingButtonsInCrafting() {
        for (String stone : STONES) {
            Shapeless recipe =
                    assertInstanceOf(
                            Shapeless.class, byId("minecraft", stone + "_button"), stone + " button");

            assertEquals(List.of(Ingredient.item("minecraft:" + stone)), recipe.ingredients());
            assertEquals("minecraft:" + stone + "_button", recipe.result().item());
            assertEquals(4, recipe.result().count(), stone + " must yield four buttons");
            assertEquals(
                    Optional.empty(),
                    recipe.group(),
                    () -> "vanilla gives " + stone + " button no group, so neither may the override");
        }
    }

    @Test
    void bothStoneBlocksYieldSixMatchingButtonsInTheStonecutter() {
        for (String stone : STONES) {
            Stonecutting recipe =
                    assertInstanceOf(
                            Stonecutting.class,
                            byId("rootboot", stone + "_button_from_" + stone + "_stonecutting"),
                            stone + " stonecutting");

            assertEquals(List.of(Ingredient.item("minecraft:" + stone)), recipe.ingredients());
            assertEquals("minecraft:" + stone + "_button", recipe.result().item());
            assertEquals(6, recipe.result().count(), stone + " must yield six buttons");
        }
    }

    @Test
    void craftingRecipesOverrideVanillaIdsInsteadOfAddingParallelOnes() {
        for (RecipeSpec spec : MoreButtons.all()) {
            if (spec instanceof Stonecutting) {
                continue;
            }
            assertEquals(
                    "minecraft",
                    spec.namespace(),
                    () -> "crafting recipe " + spec.path() + " must reuse the vanilla id to replace it");
        }
    }

    @Test
    void noTwoRecipesShareAnIdAcrossTheWholeMod() {
        List<String> ids =
                RootBootRecipes.all().stream()
                        .map(spec -> spec.namespace() + ":" + spec.path())
                        .collect(Collectors.toList());

        assertEquals(ids.size(), Set.copyOf(ids).size(), () -> "duplicate recipe id among " + ids);
    }

    // Guards the ownership decision recorded in docs/FEATURES.md: OmniCut ceded the two stone buttons to
    // More-Buttons. Scoped to those two results rather than to "no other stonecutter recipe exists", so
    // it still means something once OmniCut adds its own unrelated conversions instead of forcing whoever
    // adds them to weaken the test.
    @Test
    void moreButtonsIsTheSoleStonecutterSourceOfTheTwoCededButtons() {
        Set<String> moreButtonsIds =
                MoreButtons.all().stream().map(MoreButtonsTest::id).collect(Collectors.toSet());

        for (String button :
                List.of("minecraft:stone_button", "minecraft:polished_blackstone_button")) {
            List<RecipeSpec> sources =
                    RootBootRecipes.all().stream()
                            .filter(Stonecutting.class::isInstance)
                            .filter(spec -> spec.result().item().equals(button))
                            .collect(Collectors.toList());

            assertEquals(
                    1,
                    sources.size(),
                    () -> "exactly one stonecutter recipe may produce " + button + ", found " + sources);
            assertTrue(
                    moreButtonsIds.contains(id(sources.getFirst())),
                    () ->
                            button
                                    + " must be cut by More-Buttons, but "
                                    + id(sources.getFirst())
                                    + " claims it");
        }
    }

    // Four buttons from a planks block and six from a stone one only stay generous while the trip is
    // one-way: any recipe turning one of these buttons back into its material would multiply it without
    // limit. Scoped to the buttons this feature actually inflates — a recipe that merely consumes some
    // other button is not the problem.
    @Test
    void noRootBootRecipeTurnsAGenerouslyCraftedButtonBackIntoItsMaterial() {
        Set<String> generous =
                MoreButtons.all().stream().map(spec -> spec.result().item()).collect(Collectors.toSet());

        for (RecipeSpec spec : RootBootRecipes.all()) {
            List<String> recovered = inputs(spec).stream().filter(generous::contains).toList();

            assertEquals(
                    List.of(), recovered, () -> id(spec) + " consumes buttons More-Buttons makes generously");
        }
    }

    private static List<String> inputs(RecipeSpec spec) {
        return switch (spec) {
            case RecipeSpec.Cooking cooking -> cooking.inputs();
            case RecipeSpec.Shaped shaped -> shaped.key().values().stream().map(Ingredient::id).toList();
            case RecipeSpec.Shapeless shapeless ->
                    shapeless.ingredients().stream().map(Ingredient::id).toList();
            case Stonecutting stonecutting ->
                    stonecutting.ingredients().stream().map(Ingredient::id).toList();
        };
    }

    private static String id(RecipeSpec spec) {
        return spec.namespace() + ":" + spec.path();
    }
}

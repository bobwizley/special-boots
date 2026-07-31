package br.com.bobwizley.rootboot.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.bobwizley.rootboot.recipe.RecipeSpec.Ingredient;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shaped;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Stonecutting;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Exercises OmniCut's wood slice: exact coverage of the eleven families, the four input forms sharing one
 * recipe instead of duplicating it, and the reference's calibrated cutting and recycling quantities.
 */
class OmniCutWoodTest {

    /** The nether families, whose log and wood forms are stems and hyphae and which have no boat. */
    private static final Map<String, String[]> NETHER =
            Map.of("crimson", new String[] {"stem", "hyphae"}, "warped", new String[] {"stem", "hyphae"});

    private static final List<String> FAMILIES =
            List.of(
                    "acacia",
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

    /** Product path suffix to the number of them one log yields. */
    private static final Map<String, Integer> CUT_YIELDS =
            Map.of(
                    "planks", 4,
                    "stairs", 4,
                    "slab", 8,
                    "fence", 3,
                    "fence_gate", 1,
                    "door", 2,
                    "trapdoor", 2,
                    "pressure_plate", 2,
                    "sign", 2,
                    "button", 4);

    private static final List<String> TWO_PLANK_PRODUCTS =
            List.of("pressure_plate", "sign", "door", "trapdoor");

    /** Recycling recipe suffix to the products it accepts, in order. */
    private static final Map<String, List<String>> RECOVERY_INPUTS = recoveryInputs();

    private static Map<String, List<String>> recoveryInputs() {
        Map<String, List<String>> inputs = new LinkedHashMap<>();
        inputs.put("planks_1", List.of("stairs", "button"));
        inputs.put("planks_2", TWO_PLANK_PRODUCTS);
        inputs.put("planks_4", List.of("boat"));
        inputs.put("stairs_1", List.of("planks", "button"));
        inputs.put("stairs_2", TWO_PLANK_PRODUCTS);
        inputs.put("slab_2", List.of("stairs", "planks", "button"));
        inputs.put("slab_4", TWO_PLANK_PRODUCTS);
        inputs.put("stick_1", List.of("slab"));
        inputs.put("stick_2", List.of("stairs", "planks", "button"));
        inputs.put("stick_3", List.of("fence"));
        inputs.put("stick_4", TWO_PLANK_PRODUCTS);
        inputs.put("stick_8", List.of("fence_gate"));
        return Map.copyOf(inputs);
    }

    private static boolean hasBoat(String family) {
        return !NETHER.containsKey(family);
    }

    private static String logSuffix(String family) {
        return hasBoat(family) ? "log" : NETHER.get(family)[0];
    }

    private static String woodSuffix(String family) {
        return hasBoat(family) ? "wood" : NETHER.get(family)[1];
    }

    private static String item(String family, String product) {
        return "stick".equals(product) ? "minecraft:stick" : "minecraft:" + family + "_" + product;
    }

    private RecipeSpec byPath(String path) {
        return OmniCutWood.all().stream()
                .filter(spec -> spec.path().equals(path))
                .findFirst()
                .orElseThrow(() -> new AssertionError("missing recipe rootboot:" + path));
    }

    private Stonecutting cutting(String family, String product) {
        return assertInstanceOf(
                Stonecutting.class, byPath("wood/" + family + "/cut/" + product), family + " " + product);
    }

    @Test
    void coversExactlyTheElevenLogShapedFamiliesUnderTheRootBootNamespace() {
        Set<String> covered =
                OmniCutWood.all().stream().map(spec -> spec.path().split("/")[1]).collect(Collectors.toSet());

        assertEquals(Set.copyOf(FAMILIES), covered);
        assertTrue(
                OmniCutWood.all().stream().allMatch(spec -> spec.namespace().equals("rootboot")),
                "the whole slice is additive, so nothing may claim a minecraft id");
    }

    @Test
    void shipsOneRecipePerConversionWithNoDuplicateIds() {
        List<String> paths = OmniCutWood.all().stream().map(RecipeSpec::path).toList();

        List<String> expected = new ArrayList<>();
        for (String family : FAMILIES) {
            for (String product :
                    List.of(
                            "planks",
                            "stairs",
                            "slab",
                            "fence",
                            "fence_gate",
                            "door",
                            "trapdoor",
                            "pressure_plate",
                            "button",
                            "sign",
                            "boat",
                            "stick",
                            "wood",
                            "strip_log",
                            "strip_wood")) {
                if (hasBoat(family) || !product.equals("boat")) {
                    expected.add("wood/" + family + "/cut/" + product);
                }
            }
            for (String recovery : RECOVERY_INPUTS.keySet()) {
                if (hasBoat(family) || !recovery.equals("planks_4")) {
                    expected.add("wood/" + family + "/uncut/" + recovery);
                }
            }
            expected.add("wood/" + family + "/unslab/planks");
        }

        assertEquals(Set.copyOf(expected), Set.copyOf(paths));
        assertEquals(expected.size(), paths.size(), "no conversion may be emitted twice");
    }

    @Test
    void everyCuttingRecipeAcceptsTheFourFormsOfALogInASingleRecipe() {
        for (String family : FAMILIES) {
            List<Ingredient> forms =
                    List.of(
                            Ingredient.item("minecraft:" + family + "_" + logSuffix(family)),
                            Ingredient.item("minecraft:stripped_" + family + "_" + logSuffix(family)),
                            Ingredient.item("minecraft:" + family + "_" + woodSuffix(family)),
                            Ingredient.item("minecraft:stripped_" + family + "_" + woodSuffix(family)));

            List<String> products = new ArrayList<>(CUT_YIELDS.keySet());
            products.add("stick");
            if (hasBoat(family)) {
                products.add("boat");
            }
            for (String product : products) {
                assertEquals(
                        forms,
                        cutting(family, product).ingredients(),
                        () -> family + " " + product + " must accept log, stripped log, wood and stripped wood");
            }
        }
    }

    @Test
    void oneLogYieldsTheCalibratedQuantityOfEachProduct() {
        for (String family : FAMILIES) {
            CUT_YIELDS.forEach(
                    (product, count) -> {
                        Stonecutting recipe = cutting(family, product);
                        assertEquals(item(family, product), recipe.result().item());
                        assertEquals(count, recipe.result().count(), family + " " + product);
                    });

            Stonecutting sticks = cutting(family, "stick");
            assertEquals("minecraft:stick", sticks.result().item());
            assertEquals(8, sticks.result().count(), family + " sticks");

            if (hasBoat(family)) {
                Stonecutting boat = cutting(family, "boat");
                assertEquals("minecraft:" + family + "_boat", boat.result().item());
                assertEquals(1, boat.result().count(), family + " boat");
            }
        }
    }

    @Test
    void theShapeConversionsReadTheSingleFormTheyTransform() {
        for (String family : FAMILIES) {
            String log = "minecraft:" + family + "_" + logSuffix(family);
            String wood = "minecraft:" + family + "_" + woodSuffix(family);

            assertConversion(cutting(family, "wood"), log, wood);
            assertConversion(
                    cutting(family, "strip_log"),
                    log,
                    "minecraft:stripped_" + family + "_" + logSuffix(family));
            assertConversion(
                    cutting(family, "strip_wood"),
                    wood,
                    "minecraft:stripped_" + family + "_" + woodSuffix(family));
        }
    }

    @Test
    void recyclingGivesBackTheCalibratedPlanksAndSticksAndNeverALog() {
        for (String family : FAMILIES) {
            RECOVERY_INPUTS.forEach(
                    (recovery, products) -> {
                        if (!hasBoat(family) && recovery.equals("planks_4")) {
                            return;
                        }
                        Stonecutting recipe =
                                assertInstanceOf(
                                        Stonecutting.class,
                                        byPath("wood/" + family + "/uncut/" + recovery),
                                        family + " " + recovery);

                        String[] parts = recovery.split("_(?=\\d+$)");
                        assertEquals(item(family, parts[0]), recipe.result().item());
                        assertEquals(Integer.parseInt(parts[1]), recipe.result().count(), recovery);
                        assertEquals(
                                products.stream().map(product -> Ingredient.item(item(family, product))).toList(),
                                recipe.ingredients(),
                                () -> "inputs of " + family + " " + recovery);
                    });

            assertFalse(
                    OmniCutWood.all().stream()
                            .filter(spec -> spec.path().startsWith("wood/" + family + "/uncut/"))
                            .anyMatch(spec -> spec.result().item().contains(logSuffix(family))),
                    "recycling never gives back a log");
        }
    }

    @Test
    void twoSlabsSideBySideRebuildTheirPlanks() {
        for (String family : FAMILIES) {
            Shaped recipe =
                    assertInstanceOf(
                            Shaped.class, byPath("wood/" + family + "/unslab/planks"), family + " unslab");

            assertEquals(List.of("##"), recipe.pattern(), "side by side, never stacked");
            assertEquals(Ingredient.item("minecraft:" + family + "_slab"), recipe.key().get('#'));
            assertEquals("minecraft:" + family + "_planks", recipe.result().item());
            assertEquals(1, recipe.result().count());
        }
    }

    // The two wooden button paths must stay distinct: cutting a log here, crafting planks in More-Buttons.
    @Test
    void theCutWoodenButtonsCoexistWithTheMoreButtonsCraftingRecipes() {
        Set<String> moreButtons =
                MoreButtons.all().stream()
                        .map(spec -> spec.namespace() + ":" + spec.path())
                        .collect(Collectors.toSet());

        for (String family : FAMILIES) {
            Stonecutting cut = cutting(family, "button");
            assertFalse(
                    moreButtons.contains("rootboot:" + cut.path()),
                    () -> "id collision on the " + family + " button");
            assertFalse(
                    cut.ingredients().contains(Ingredient.item("minecraft:" + family + "_planks")),
                    () -> family + " buttons are cut from logs here; planks belong to More-Buttons");
        }
    }

    private static void assertConversion(Stonecutting recipe, String input, String output) {
        assertEquals(List.of(Ingredient.item(input)), recipe.ingredients());
        assertEquals(output, recipe.result().item());
        assertEquals(1, recipe.result().count());
    }
}

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
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Exercises OmniCut's rock slice: the recovery quantities, the exclusions that keep the set from
 * duplicating vanilla or RootBoot's own recipes, and the slab ids the reassembly recipes read.
 */
class OmniCutRockTest {

    /**
     * The reference's reassembly coverage. Only the blocks: the slab each one reads is spelled out in the
     * spec and checked against the real registry by {@code VanillaRecipeConflictTest}, so repeating the
     * pairing here would only assert that one copy of it matches another.
     */
    private static final List<String> REASSEMBLED_BLOCKS =
            List.of(
                    "andesite",
                    "blackstone",
                    "bricks",
                    "cinnabar",
                    "cinnabar_bricks",
                    "cobbled_deepslate",
                    "cobblestone",
                    "cut_red_sandstone",
                    "cut_sandstone",
                    "dark_prismarine",
                    "deepslate_bricks",
                    "deepslate_tiles",
                    "diorite",
                    "end_stone_bricks",
                    "granite",
                    "mossy_cobblestone",
                    "mossy_stone_bricks",
                    "mud_bricks",
                    "nether_bricks",
                    "polished_andesite",
                    "polished_blackstone",
                    "polished_blackstone_bricks",
                    "polished_cinnabar",
                    "polished_deepslate",
                    "polished_diorite",
                    "polished_granite",
                    "polished_sulfur",
                    "polished_tuff",
                    "prismarine",
                    "prismarine_bricks",
                    "quartz_block",
                    "red_nether_bricks",
                    "red_sandstone",
                    "sandstone",
                    "smooth_quartz",
                    "smooth_red_sandstone",
                    "smooth_sandstone",
                    "smooth_stone",
                    "stone",
                    "stone_bricks",
                    "sulfur",
                    "sulfur_bricks",
                    "tuff",
                    "tuff_bricks");

    private RecipeSpec byPath(String path) {
        return OmniCutRock.all().stream()
                .filter(spec -> spec.path().equals(path))
                .findFirst()
                .orElseThrow(() -> new AssertionError("missing recipe rootboot:" + path));
    }

    private Stonecutting uncut(String block, int count) {
        return assertInstanceOf(
                Stonecutting.class, byPath("rock/uncut/" + block + "_" + count), block + " x" + count);
    }

    private Shaped unslab(String block) {
        return assertInstanceOf(Shaped.class, byPath("rock/unslab/" + block), block + " unslab");
    }

    @Test
    void theWholeSliceIsAdditiveWithNoDuplicateIds() {
        List<String> paths = OmniCutRock.all().stream().map(RecipeSpec::path).toList();

        assertEquals(Set.copyOf(paths).size(), paths.size(), "no conversion may be emitted twice");
        assertTrue(
                OmniCutRock.all().stream().allMatch(spec -> spec.namespace().equals("rootboot")),
                "nothing here replaces a vanilla recipe, so nothing may claim a minecraft id");
        assertTrue(
                OmniCutRock.all().stream().allMatch(spec -> spec.path().matches("rock/(uncut|unslab)/.+")),
                "rock cutting is vanilla on 26.2; only uncut and unslab belong to this slice");
    }

    @Test
    void everyProductGivesBackOneBlockExceptThePressurePlatesWhichGiveTwo() {
        for (RecipeSpec spec : OmniCutRock.all()) {
            if (!(spec instanceof Stonecutting stonecutting)) {
                continue;
            }
            int expected =
                    stonecutting.ingredients().stream().allMatch(input -> input.id().endsWith("_pressure_plate"))
                            ? 2
                            : 1;
            assertEquals(
                    expected, stonecutting.result().count(), () -> "recovery quantity of " + spec.path());
        }
        assertEquals(2, uncut("stone", 2).result().count());
        assertEquals(
                List.of(Ingredient.item("minecraft:stone_pressure_plate")), uncut("stone", 2).ingredients());
        assertEquals(2, uncut("blackstone", 2).result().count());
    }

    @Test
    void smoothAndCrackedVariantsRecycleIntoTheirBaseBlock() {
        assertTrue(
                uncut("stone", 1).ingredients().containsAll(
                        List.of(
                                Ingredient.item("minecraft:smooth_stone"),
                                Ingredient.item("minecraft:cracked_stone_bricks"))),
                "smooth stone and cracked stone bricks give back stone");
        assertEquals("minecraft:stone", uncut("stone", 1).result().item());
        assertTrue(
                uncut("cobbled_deepslate", 1).ingredients().containsAll(
                        List.of(
                                Ingredient.item("minecraft:cracked_deepslate_bricks"),
                                Ingredient.item("minecraft:cracked_deepslate_tiles"))),
                "cracked deepslate bricks and tiles give back cobbled deepslate");
        assertEquals(
                Ingredient.item("minecraft:cracked_polished_blackstone_bricks"),
                uncut("blackstone", 1).ingredients().stream()
                        .filter(input -> input.id().contains("cracked"))
                        .findFirst()
                        .orElseThrow());
    }

    @Test
    void cinnabarAndSulfurRecycleAndReassembleAsTheOverlayDefines() {
        assertEquals(
                List.of(
                        "minecraft:polished_cinnabar",
                        "minecraft:chiseled_cinnabar",
                        "minecraft:cinnabar_bricks",
                        "minecraft:cinnabar_stairs",
                        "minecraft:polished_cinnabar_stairs",
                        "minecraft:cinnabar_brick_stairs",
                        "minecraft:cinnabar_wall",
                        "minecraft:polished_cinnabar_wall",
                        "minecraft:cinnabar_brick_wall"),
                uncut("cinnabar", 1).ingredients().stream().map(Ingredient::id).toList());
        assertEquals(
                List.of(
                        "minecraft:polished_sulfur",
                        "minecraft:chiseled_sulfur",
                        "minecraft:sulfur_bricks",
                        "minecraft:sulfur_stairs",
                        "minecraft:polished_sulfur_stairs",
                        "minecraft:sulfur_brick_stairs",
                        "minecraft:sulfur_wall",
                        "minecraft:polished_sulfur_wall",
                        "minecraft:sulfur_brick_wall"),
                uncut("sulfur", 1).ingredients().stream().map(Ingredient::id).toList());

        for (String block :
                List.of(
                        "cinnabar",
                        "cinnabar_bricks",
                        "polished_cinnabar",
                        "sulfur",
                        "sulfur_bricks",
                        "polished_sulfur")) {
            assertEquals("minecraft:" + block, unslab(block).result().item());
        }
    }

    @Test
    void twoSlabsSideBySideRebuildTheirBlock() {
        List<Shaped> reassemblies =
                OmniCutRock.all().stream()
                        .filter(spec -> spec.path().startsWith("rock/unslab/"))
                        .map(Shaped.class::cast)
                        .toList();

        assertEquals(
                Set.copyOf(REASSEMBLED_BLOCKS),
                reassemblies.stream()
                        .map(recipe -> recipe.path().substring("rock/unslab/".length()))
                        .collect(Collectors.toSet()),
                "the reference's reassembly set");
        for (Shaped recipe : reassemblies) {
            String block = recipe.path().substring("rock/unslab/".length());
            assertEquals(List.of("##"), recipe.pattern(), "side by side, never stacked");
            assertEquals("minecraft:" + block, recipe.result().item());
            assertEquals(1, recipe.result().count());

            Ingredient slab = recipe.key().get('#');
            assertTrue(slab.id().endsWith("_slab"), () -> block + " is rebuilt from " + slab.id());
        }
    }

    @Test
    void nothingVanillaAlreadyCutsOrThatMoreButtonsOwnsIsReintroduced() {
        Set<String> inputs =
                OmniCutRock.all().stream()
                        .filter(Stonecutting.class::isInstance)
                        .map(Stonecutting.class::cast)
                        .flatMap(spec -> spec.ingredients().stream())
                        .map(Ingredient::id)
                        .collect(Collectors.toSet());

        assertFalse(
                inputs.contains("minecraft:deepslate"),
                "vanilla already cuts deepslate into cobbled deepslate");
        assertFalse(
                inputs.stream().anyMatch(input -> input.endsWith("_button")),
                "More-Buttons cuts six buttons from a block; recycling one back would multiply material");
        assertFalse(
                OmniCutRock.all().stream().anyMatch(spec -> spec.result().item().endsWith("_button")),
                "the stone buttons belong to More-Buttons");
    }
}

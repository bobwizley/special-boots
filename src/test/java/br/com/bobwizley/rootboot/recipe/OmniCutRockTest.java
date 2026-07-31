package br.com.bobwizley.rootboot.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.bobwizley.rootboot.recipe.RecipeSpec.Ingredient;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shaped;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Stonecutting;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Exercises OmniCut's rock slice: the recovery quantities, the exclusions that keep the set from
 * duplicating vanilla or RootBoot's own recipes, and the slab ids the reassembly recipes read.
 */
class OmniCutRockTest {

    /**
     * The reference's reassembly set: every block a pair of slabs rebuilds, mapped to the slab id vanilla
     * actually named. Spelled out rather than derived, so the naming rule the spec applies is checked
     * against the registry instead of against itself.
     */
    private static final Map<String, String> SLABS =
            Map.ofEntries(
                    Map.entry("andesite", "andesite_slab"),
                    Map.entry("blackstone", "blackstone_slab"),
                    Map.entry("bricks", "brick_slab"),
                    Map.entry("cinnabar", "cinnabar_slab"),
                    Map.entry("cinnabar_bricks", "cinnabar_brick_slab"),
                    Map.entry("cobbled_deepslate", "cobbled_deepslate_slab"),
                    Map.entry("cobblestone", "cobblestone_slab"),
                    Map.entry("cut_red_sandstone", "cut_red_sandstone_slab"),
                    Map.entry("cut_sandstone", "cut_sandstone_slab"),
                    Map.entry("dark_prismarine", "dark_prismarine_slab"),
                    Map.entry("deepslate_bricks", "deepslate_brick_slab"),
                    Map.entry("deepslate_tiles", "deepslate_tile_slab"),
                    Map.entry("diorite", "diorite_slab"),
                    Map.entry("end_stone_bricks", "end_stone_brick_slab"),
                    Map.entry("granite", "granite_slab"),
                    Map.entry("mossy_cobblestone", "mossy_cobblestone_slab"),
                    Map.entry("mossy_stone_bricks", "mossy_stone_brick_slab"),
                    Map.entry("mud_bricks", "mud_brick_slab"),
                    Map.entry("nether_bricks", "nether_brick_slab"),
                    Map.entry("polished_andesite", "polished_andesite_slab"),
                    Map.entry("polished_blackstone", "polished_blackstone_slab"),
                    Map.entry("polished_blackstone_bricks", "polished_blackstone_brick_slab"),
                    Map.entry("polished_cinnabar", "polished_cinnabar_slab"),
                    Map.entry("polished_deepslate", "polished_deepslate_slab"),
                    Map.entry("polished_diorite", "polished_diorite_slab"),
                    Map.entry("polished_granite", "polished_granite_slab"),
                    Map.entry("polished_sulfur", "polished_sulfur_slab"),
                    Map.entry("polished_tuff", "polished_tuff_slab"),
                    Map.entry("prismarine", "prismarine_slab"),
                    Map.entry("prismarine_bricks", "prismarine_brick_slab"),
                    Map.entry("quartz_block", "quartz_slab"),
                    Map.entry("red_nether_bricks", "red_nether_brick_slab"),
                    Map.entry("red_sandstone", "red_sandstone_slab"),
                    Map.entry("sandstone", "sandstone_slab"),
                    Map.entry("smooth_quartz", "smooth_quartz_slab"),
                    Map.entry("smooth_red_sandstone", "smooth_red_sandstone_slab"),
                    Map.entry("smooth_sandstone", "smooth_sandstone_slab"),
                    Map.entry("smooth_stone", "smooth_stone_slab"),
                    Map.entry("stone", "stone_slab"),
                    Map.entry("stone_bricks", "stone_brick_slab"),
                    Map.entry("sulfur", "sulfur_slab"),
                    Map.entry("sulfur_bricks", "sulfur_brick_slab"),
                    Map.entry("tuff", "tuff_slab"),
                    Map.entry("tuff_bricks", "tuff_brick_slab"));

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
                SLABS.keySet(),
                reassemblies.stream()
                        .map(recipe -> recipe.path().substring("rock/unslab/".length()))
                        .collect(Collectors.toSet()),
                "the reference's reassembly set");
        for (Shaped recipe : reassemblies) {
            String block = recipe.path().substring("rock/unslab/".length());
            assertEquals(List.of("##"), recipe.pattern(), "side by side, never stacked");
            assertEquals("minecraft:" + block, recipe.result().item());
            assertEquals(1, recipe.result().count());
            assertEquals(
                    Ingredient.item("minecraft:" + SLABS.get(block)),
                    recipe.key().get('#'),
                    block + " slab");
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

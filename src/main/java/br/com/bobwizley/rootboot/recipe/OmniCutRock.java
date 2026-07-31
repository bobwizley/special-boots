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
 * OmniCut's rock slice: the stonecutter cuts a stone product back down into the block it came from, and
 * the crafting table puts two slabs back together into that block.
 *
 * <p>Cutting rock forward is absent on purpose. What the reference still cut on 26.2 has become vanilla —
 * the deepslate family and the polished blackstone button live in an overlay this version no longer reads —
 * and the two stone buttons belong to More-Buttons by an explicit ownership decision (docs/FEATURES.md).
 * Cinnabar and sulfur, which vanilla cuts but never uncuts, arrive here through the reference's own 26.2
 * overlay.
 *
 * <p>Recovery is one unit per product, the reference's "vanilla price" figure: every one of these products
 * costs a whole block in the stonecutter, so giving one back is neutral. The pressure plates are the
 * exception at two, because two blocks is what they cost.
 *
 * <p>No product that RootBoot itself makes generously is recoverable. Buttons are the only such product,
 * and they are absent from every list below: More-Buttons cuts six of them from one block, so recycling one
 * back into a whole block would turn the pair of recipes into a material multiplier. The reference has no
 * such conflict, since it cuts buttons one for one.
 */
final class OmniCutRock {

    /**
     * Every stone product that recycles, grouped by the block it gives back. Read against the 26.2
     * registry: {@code deepslate} is deliberately not among the products that give back
     * {@code cobbled_deepslate}, because vanilla already cuts that one.
     */
    private static final List<Recovery> RECOVERIES =
            List.of(
                    new Recovery(
                            "andesite",
                            1,
                            List.of(
                                    "polished_andesite",
                                    "andesite_stairs",
                                    "polished_andesite_stairs",
                                    "andesite_wall")),
                    new Recovery("basalt", 1, List.of("polished_basalt")),
                    new Recovery(
                            "blackstone",
                            1,
                            List.of(
                                    "polished_blackstone",
                                    "chiseled_polished_blackstone",
                                    "polished_blackstone_bricks",
                                    "cracked_polished_blackstone_bricks",
                                    "blackstone_stairs",
                                    "polished_blackstone_stairs",
                                    "polished_blackstone_brick_stairs",
                                    "blackstone_wall",
                                    "polished_blackstone_wall",
                                    "polished_blackstone_brick_wall")),
                    new Recovery("blackstone", 2, List.of("polished_blackstone_pressure_plate")),
                    new Recovery("bricks", 1, List.of("brick_stairs", "brick_wall")),
                    new Recovery(
                            "cinnabar",
                            1,
                            List.of(
                                    "polished_cinnabar",
                                    "chiseled_cinnabar",
                                    "cinnabar_bricks",
                                    "cinnabar_stairs",
                                    "polished_cinnabar_stairs",
                                    "cinnabar_brick_stairs",
                                    "cinnabar_wall",
                                    "polished_cinnabar_wall",
                                    "cinnabar_brick_wall")),
                    new Recovery(
                            "cobbled_deepslate",
                            1,
                            List.of(
                                    "polished_deepslate",
                                    "chiseled_deepslate",
                                    "deepslate_bricks",
                                    "cracked_deepslate_bricks",
                                    "deepslate_tiles",
                                    "cracked_deepslate_tiles",
                                    "cobbled_deepslate_stairs",
                                    "polished_deepslate_stairs",
                                    "deepslate_brick_stairs",
                                    "deepslate_tile_stairs",
                                    "cobbled_deepslate_wall",
                                    "polished_deepslate_wall",
                                    "deepslate_brick_wall",
                                    "deepslate_tile_wall")),
                    new Recovery(
                            "cobblestone",
                            1,
                            List.of("mossy_cobblestone", "cobblestone_stairs", "cobblestone_wall")),
                    new Recovery("dark_prismarine", 1, List.of("dark_prismarine_stairs")),
                    new Recovery(
                            "diorite",
                            1,
                            List.of(
                                    "polished_diorite",
                                    "diorite_stairs",
                                    "polished_diorite_stairs",
                                    "diorite_wall")),
                    new Recovery(
                            "end_stone",
                            1,
                            List.of("end_stone_bricks", "end_stone_brick_stairs", "end_stone_brick_wall")),
                    new Recovery(
                            "granite",
                            1,
                            List.of(
                                    "polished_granite",
                                    "granite_stairs",
                                    "polished_granite_stairs",
                                    "granite_wall")),
                    new Recovery(
                            "mossy_cobblestone",
                            1,
                            List.of("mossy_cobblestone_stairs", "mossy_cobblestone_wall")),
                    new Recovery(
                            "mossy_stone_bricks",
                            1,
                            List.of("mossy_stone_brick_stairs", "mossy_stone_brick_wall")),
                    new Recovery("mud_bricks", 1, List.of("packed_mud", "mud_brick_stairs", "mud_brick_wall")),
                    new Recovery(
                            "nether_bricks",
                            1,
                            List.of(
                                    "chiseled_nether_bricks",
                                    "cracked_nether_bricks",
                                    "nether_brick_stairs",
                                    "nether_brick_wall")),
                    new Recovery("prismarine", 1, List.of("prismarine_stairs", "prismarine_wall")),
                    new Recovery("prismarine_bricks", 1, List.of("prismarine_brick_stairs")),
                    new Recovery("purpur_block", 1, List.of("purpur_stairs")),
                    new Recovery(
                            "quartz_block",
                            1,
                            List.of(
                                    "smooth_quartz",
                                    "chiseled_quartz_block",
                                    "quartz_bricks",
                                    "quartz_pillar",
                                    "quartz_stairs")),
                    new Recovery(
                            "red_nether_bricks",
                            1,
                            List.of("red_nether_brick_stairs", "red_nether_brick_wall")),
                    new Recovery(
                            "red_sandstone",
                            1,
                            List.of(
                                    "smooth_red_sandstone",
                                    "chiseled_red_sandstone",
                                    "cut_red_sandstone",
                                    "red_sandstone_stairs",
                                    "red_sandstone_wall")),
                    new Recovery(
                            "sandstone",
                            1,
                            List.of(
                                    "smooth_sandstone",
                                    "chiseled_sandstone",
                                    "cut_sandstone",
                                    "sandstone_stairs",
                                    "sandstone_wall")),
                    new Recovery("smooth_quartz", 1, List.of("smooth_quartz_stairs")),
                    new Recovery("smooth_red_sandstone", 1, List.of("smooth_red_sandstone_stairs")),
                    new Recovery("smooth_sandstone", 1, List.of("smooth_sandstone_stairs")),
                    new Recovery(
                            "stone",
                            1,
                            List.of(
                                    "smooth_stone",
                                    "stone_bricks",
                                    "mossy_stone_bricks",
                                    "cracked_stone_bricks",
                                    "chiseled_stone_bricks",
                                    "stone_stairs",
                                    "stone_brick_stairs",
                                    "stone_brick_wall")),
                    new Recovery("stone", 2, List.of("stone_pressure_plate")),
                    new Recovery(
                            "sulfur",
                            1,
                            List.of(
                                    "polished_sulfur",
                                    "chiseled_sulfur",
                                    "sulfur_bricks",
                                    "sulfur_stairs",
                                    "polished_sulfur_stairs",
                                    "sulfur_brick_stairs",
                                    "sulfur_wall",
                                    "polished_sulfur_wall",
                                    "sulfur_brick_wall")),
                    new Recovery(
                            "tuff",
                            1,
                            List.of(
                                    "polished_tuff",
                                    "chiseled_tuff",
                                    "tuff_bricks",
                                    "chiseled_tuff_bricks",
                                    "tuff_stairs",
                                    "polished_tuff_stairs",
                                    "tuff_brick_stairs",
                                    "tuff_wall",
                                    "polished_tuff_wall",
                                    "tuff_brick_wall")));

    /**
     * The blocks a pair of slabs rebuilds, each paired with the slab vanilla actually named. The reference's
     * set, so a few blocks that do have a slab are deliberately missing — purpur among them.
     *
     * <p>Spelled out rather than derived from the block name. Vanilla mostly appends {@code _slab} to the
     * singular, but it does not promise to: {@code quartz_block} has a plain {@code quartz_slab}, and
     * singularizing an English plural is a rule about the language rather than about the registry. An
     * explicit pair cannot silently produce an id no version has.
     */
    private static final List<Reassembly> REASSEMBLIES =
            List.of(
                    new Reassembly("andesite", "andesite_slab"),
                    new Reassembly("blackstone", "blackstone_slab"),
                    new Reassembly("bricks", "brick_slab"),
                    new Reassembly("cinnabar", "cinnabar_slab"),
                    new Reassembly("cinnabar_bricks", "cinnabar_brick_slab"),
                    new Reassembly("cobbled_deepslate", "cobbled_deepslate_slab"),
                    new Reassembly("cobblestone", "cobblestone_slab"),
                    new Reassembly("cut_red_sandstone", "cut_red_sandstone_slab"),
                    new Reassembly("cut_sandstone", "cut_sandstone_slab"),
                    new Reassembly("dark_prismarine", "dark_prismarine_slab"),
                    new Reassembly("deepslate_bricks", "deepslate_brick_slab"),
                    new Reassembly("deepslate_tiles", "deepslate_tile_slab"),
                    new Reassembly("diorite", "diorite_slab"),
                    new Reassembly("end_stone_bricks", "end_stone_brick_slab"),
                    new Reassembly("granite", "granite_slab"),
                    new Reassembly("mossy_cobblestone", "mossy_cobblestone_slab"),
                    new Reassembly("mossy_stone_bricks", "mossy_stone_brick_slab"),
                    new Reassembly("mud_bricks", "mud_brick_slab"),
                    new Reassembly("nether_bricks", "nether_brick_slab"),
                    new Reassembly("polished_andesite", "polished_andesite_slab"),
                    new Reassembly("polished_blackstone", "polished_blackstone_slab"),
                    new Reassembly("polished_blackstone_bricks", "polished_blackstone_brick_slab"),
                    new Reassembly("polished_cinnabar", "polished_cinnabar_slab"),
                    new Reassembly("polished_deepslate", "polished_deepslate_slab"),
                    new Reassembly("polished_diorite", "polished_diorite_slab"),
                    new Reassembly("polished_granite", "polished_granite_slab"),
                    new Reassembly("polished_sulfur", "polished_sulfur_slab"),
                    new Reassembly("polished_tuff", "polished_tuff_slab"),
                    new Reassembly("prismarine", "prismarine_slab"),
                    new Reassembly("prismarine_bricks", "prismarine_brick_slab"),
                    new Reassembly("quartz_block", "quartz_slab"),
                    new Reassembly("red_nether_bricks", "red_nether_brick_slab"),
                    new Reassembly("red_sandstone", "red_sandstone_slab"),
                    new Reassembly("sandstone", "sandstone_slab"),
                    new Reassembly("smooth_quartz", "smooth_quartz_slab"),
                    new Reassembly("smooth_red_sandstone", "smooth_red_sandstone_slab"),
                    new Reassembly("smooth_sandstone", "smooth_sandstone_slab"),
                    new Reassembly("smooth_stone", "smooth_stone_slab"),
                    new Reassembly("stone", "stone_slab"),
                    new Reassembly("stone_bricks", "stone_brick_slab"),
                    new Reassembly("sulfur", "sulfur_slab"),
                    new Reassembly("sulfur_bricks", "sulfur_brick_slab"),
                    new Reassembly("tuff", "tuff_slab"),
                    new Reassembly("tuff_bricks", "tuff_brick_slab"));

    private OmniCutRock() {
    }

    static List<RecipeSpec> all() {
        List<RecipeSpec> recipes = new ArrayList<>();
        for (Recovery recovery : RECOVERIES) {
            recipes.add(uncut(recovery));
        }
        for (Reassembly reassembly : REASSEMBLIES) {
            recipes.add(unslab(reassembly));
        }
        return List.copyOf(recipes);
    }

    private static RecipeSpec uncut(Recovery recovery) {
        return new Stonecutting(
                "rootboot",
                "rock/uncut/" + recovery.result() + "_" + recovery.count(),
                Category.BUILDING_BLOCKS,
                Optional.empty(),
                recovery.products().stream().map(OmniCutRock::item).map(Ingredient::item).toList(),
                new Result(item(recovery.result()), recovery.count()));
    }

    // Side by side rather than stacked, matching the wood slice and the reference.
    private static RecipeSpec unslab(Reassembly reassembly) {
        return new Shaped(
                "rootboot",
                "rock/unslab/" + reassembly.block(),
                Category.BUILDING_BLOCKS,
                Optional.empty(),
                List.of("##"),
                Map.of('#', Ingredient.item(item(reassembly.slab()))),
                new Result(item(reassembly.block()), 1));
    }

    private static String item(String path) {
        return "minecraft:" + path;
    }

    /** A recycling conversion: the block that comes back, how many of it, and the products that give it. */
    private record Recovery(String result, int count, List<String> products) {}

    /** A slab pair and the block it rebuilds. */
    private record Reassembly(String block, String slab) {}
}

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
import java.util.stream.Stream;

/**
 * OmniCut's wood slice: the stonecutter cuts any form of a log into the wood products, cuts those products
 * back down into planks or sticks, and the crafting table puts two slabs back together into the block they
 * came from.
 *
 * <p>Every quantity here is the reference's pre-calibrated "vanilla price" figure, kept verbatim rather
 * than normalized into a single philosophy (docs/FEATURES.md): cutting is meant to cost what the crafting
 * table costs, and the only discounts are the ones the reference chose (stairs, wood, boat, sign, fence).
 * The recycling values are calibrated against those discounts, so no loop of recipes can multiply planks.
 *
 * <p>Each conversion is a single recipe accepting log, stripped log, wood and stripped wood, which is both
 * what the reference does and what keeps the four input forms from turning into four identical recipes. The
 * three shape conversions are the exception: stripping and log-to-wood necessarily read one specific form.
 *
 * <p>The wooden buttons cut here take a log and live under rootboot-namespaced {@code wood/...} ids, so
 * they coexist with the More-Buttons wooden buttons, which take planks on the crafting table under the
 * vanilla ids.
 */
final class OmniCutWood {

    /** The one product that is not named after its wood family. */
    private static final String STICK = "stick";

    private static final String BOAT = "boat";

    /**
     * The eleven log-shaped families. Bamboo is deliberately absent: it has no log, wood or stripped form,
     * and its mosaic and raft products are a different set from the one this slice defines.
     */
    private static final List<Family> FAMILIES =
            List.of(
                    overworld("acacia"),
                    overworld("birch"),
                    overworld("cherry"),
                    nether("crimson"),
                    overworld("dark_oak"),
                    overworld("jungle"),
                    overworld("mangrove"),
                    overworld("oak"),
                    overworld("pale_oak"),
                    overworld("spruce"),
                    nether("warped"));

    /** What one log cuts into, in the reference's calibrated quantities. */
    private static final List<Product> PRODUCTS =
            List.of(
                    new Product("planks", 4, Category.BUILDING_BLOCKS),
                    new Product("stairs", 4, Category.BUILDING_BLOCKS),
                    new Product("slab", 8, Category.BUILDING_BLOCKS),
                    new Product("fence", 3, Category.DECORATIONS),
                    new Product("fence_gate", 1, Category.REDSTONE),
                    new Product("door", 2, Category.REDSTONE),
                    new Product("trapdoor", 2, Category.REDSTONE),
                    new Product("pressure_plate", 2, Category.REDSTONE),
                    new Product("button", 4, Category.REDSTONE),
                    new Product("sign", 2, Category.DECORATIONS),
                    new Product(BOAT, 1, Category.TRANSPORTATION),
                    new Product(STICK, 8, Category.MISC));

    /** The four products that each cost two planks, so they all recycle at the same rate. */
    private static final List<String> TWO_PLANK_PRODUCTS =
            List.of("pressure_plate", "sign", "door", "trapdoor");

    /**
     * Recycling, in the reference's calibrated quantities. A product never gives back a log — the coarsest
     * form it recovers is planks — and fences only give back sticks, which is what stops sticks from being
     * reformed into planks.
     *
     * <p>Buttons are the one product the reference recycles and this slice does not. More-Buttons crafts
     * four of them from a single planks block, so recovering a whole planks from one button would turn the
     * pair of recipes into a material multiplier. The reference has no such conflict: it cuts buttons one
     * for one.
     */
    private static final List<Recovery> RECOVERIES =
            List.of(
                    new Recovery("planks", 1, List.of("stairs")),
                    new Recovery("planks", 2, TWO_PLANK_PRODUCTS),
                    new Recovery("planks", 4, List.of(BOAT)),
                    new Recovery("stairs", 1, List.of("planks")),
                    new Recovery("stairs", 2, TWO_PLANK_PRODUCTS),
                    new Recovery("slab", 2, List.of("stairs", "planks")),
                    new Recovery("slab", 4, TWO_PLANK_PRODUCTS),
                    new Recovery(STICK, 1, List.of("slab")),
                    new Recovery(STICK, 2, List.of("stairs", "planks")),
                    new Recovery(STICK, 3, List.of("fence")),
                    new Recovery(STICK, 4, TWO_PLANK_PRODUCTS),
                    new Recovery(STICK, 8, List.of("fence_gate")));

    private OmniCutWood() {
    }

    static List<RecipeSpec> all() {
        List<RecipeSpec> recipes = new ArrayList<>();
        for (Family family : FAMILIES) {
            recipes.addAll(cutting(family));
            recipes.addAll(recycling(family));
            recipes.add(unslab(family));
        }
        return List.copyOf(recipes);
    }

    private static List<RecipeSpec> cutting(Family family) {
        List<RecipeSpec> recipes = new ArrayList<>();
        for (Product product : PRODUCTS) {
            if (!family.offers(product.name())) {
                continue;
            }
            recipes.add(
                    new Stonecutting(
                            "rootboot",
                            family.path("cut", product.name()),
                            product.category(),
                            Optional.empty(),
                            family.cuttingInputs(),
                            new Result(family.item(product.name()), product.count())));
        }
        recipes.add(shapeConversion(family, "wood", family.log(), family.wood()));
        recipes.add(shapeConversion(family, "strip_log", family.log(), family.strippedLog()));
        recipes.add(shapeConversion(family, "strip_wood", family.wood(), family.strippedWood()));
        return recipes;
    }

    private static List<RecipeSpec> recycling(Family family) {
        List<RecipeSpec> recipes = new ArrayList<>();
        for (Recovery recovery : RECOVERIES) {
            List<Ingredient> inputs =
                    recovery.products().stream()
                            .filter(family::offers)
                            .map(product -> Ingredient.item(family.item(product)))
                            .toList();
            // Empty only for the boat recovery of the two nether families, which have no boat at all.
            if (inputs.isEmpty()) {
                continue;
            }
            recipes.add(
                    new Stonecutting(
                            "rootboot",
                            family.path("uncut", recovery.result() + "_" + recovery.count()),
                            category(recovery.result()),
                            Optional.empty(),
                            inputs,
                            new Result(family.item(recovery.result()), recovery.count())));
        }
        return recipes;
    }

    // Side by side rather than stacked: the reference chose the horizontal shape so that reassembling a
    // slab pair never competes with the vanilla shape that makes chiseled blocks.
    private static RecipeSpec unslab(Family family) {
        return new Shaped(
                "rootboot",
                family.path("unslab", "planks"),
                Category.BUILDING_BLOCKS,
                Optional.empty(),
                List.of("##"),
                Map.of('#', Ingredient.item(family.item("slab"))),
                new Result(family.item("planks"), 1));
    }

    private static RecipeSpec shapeConversion(Family family, String name, String input, String output) {
        return new Stonecutting(
                "rootboot",
                family.path("cut", name),
                Category.BUILDING_BLOCKS,
                Optional.empty(),
                List.of(Ingredient.item(input)),
                new Result(output, 1));
    }

    private static Category category(String product) {
        return PRODUCTS.stream()
                .filter(candidate -> candidate.name().equals(product))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown wood product: " + product))
                .category();
    }

    private static Family overworld(String name) {
        return new Family(name, "log", "wood", true);
    }

    /** Crimson and warped: stem and hyphae instead of log and wood, and no boat exists for either. */
    private static Family nether(String name) {
        return new Family(name, "stem", "hyphae", false);
    }

    /**
     * One wood family, named by the part its registry ids share, plus the suffixes its log and wood forms
     * use.
     */
    private record Family(String name, String logSuffix, String woodSuffix, boolean hasBoat) {

        String item(String product) {
            return STICK.equals(product) ? "minecraft:stick" : "minecraft:" + name + "_" + product;
        }

        String log() {
            return "minecraft:" + name + "_" + logSuffix;
        }

        String wood() {
            return "minecraft:" + name + "_" + woodSuffix;
        }

        String strippedLog() {
            return "minecraft:stripped_" + name + "_" + logSuffix;
        }

        String strippedWood() {
            return "minecraft:stripped_" + name + "_" + woodSuffix;
        }

        /** The four forms of a log, all equally valid input for a cutting recipe. */
        List<Ingredient> cuttingInputs() {
            return Stream.of(log(), strippedLog(), wood(), strippedWood()).map(Ingredient::item).toList();
        }

        boolean offers(String product) {
            return hasBoat || !BOAT.equals(product);
        }

        String path(String stage, String recipe) {
            return "wood/" + name + "/" + stage + "/" + recipe;
        }
    }

    /** A product cut from a log: its registry path suffix, how many one log yields, and its book tab. */
    private record Product(String name, int count, Category category) {}

    /** A recycling conversion: what comes back, how much of it, and the products that give it back. */
    private record Recovery(String result, int count, List<String> products) {}
}

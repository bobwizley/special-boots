package br.com.bobwizley.rootboot.recipe;

import br.com.bobwizley.rootboot.recipe.RecipeSpec.Category;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Cooking;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.CookingMethod;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Ingredient;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Result;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shapeless;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Recycling: worn gear gives back a single unit of the material it was made of. The output is always one
 * — recovery is deliberately partial, so no chain of recipes can multiply a material.
 *
 * <p>For copper, iron and gold this replaces vanilla's nugget-yielding recipes with whole ingots, an
 * intentional buff. Replacing means reusing vanilla's own recipe ids ({@code copper_nugget_from_smelting}
 * and friends): a datapack cannot delete a recipe, so leaving those ids alone would keep the nugget
 * result reachable alongside ours. That is also why chainmail rides in the iron recipe instead of getting
 * its own — vanilla smelts both families through a single id.
 *
 * <p>Every item id here was checked against the 26.2 registry, including the ones the feature notes flag
 * as uncertain ({@code copper_*}, {@code *_nautilus_armor}, {@code *_spear}); {@code RecyclingTest} and
 * the datagen tests keep that honest, since the provider only resolves ids the game really has.
 */
final class Recycling {

    private static final int RECOVERED = 1;

    /** Vanilla's gear-recycling values, kept so the overrides only change the result item. */
    private static final float EXPERIENCE = 0.1f;

    private static final int SMELTING_TIME = 200;
    private static final int BLASTING_TIME = 100;

    private static final List<String> ARMOR = List.of("helmet", "chestplate", "leggings", "boots");
    private static final List<String> TOOLS =
            List.of("sword", "pickaxe", "axe", "shovel", "hoe", "spear");
    private static final List<String> MOUNT_ARMOR = List.of("horse_armor", "nautilus_armor");

    private static final GearFamily COPPER = fullGear("copper");
    private static final GearFamily IRON = fullGear("iron");
    private static final GearFamily GOLDEN = fullGear("golden");
    private static final GearFamily DIAMOND = fullGear("diamond");
    private static final GearFamily NETHERITE = fullGear("netherite");

    /** Chainmail is armor only: the 26.2 registry has no chainmail tool, spear or mount armor. */
    private static final GearFamily CHAINMAIL = new GearFamily("chainmail", ARMOR);

    private Recycling() {
    }

    static List<RecipeSpec> all() {
        List<RecipeSpec> recipes = new ArrayList<>();
        for (ThermalGroup group : thermalGroups()) {
            recipes.add(cooking(group, CookingMethod.SMELTING, SMELTING_TIME));
            recipes.add(cooking(group, CookingMethod.BLASTING, BLASTING_TIME));
        }
        for (CraftedRecovery recovery : craftedRecoveries()) {
            recipes.add(crafting(recovery));
        }
        return List.copyOf(recipes);
    }

    private static List<ThermalGroup> thermalGroups() {
        return List.of(
                new ThermalGroup("minecraft", "copper_nugget", "copper_ingot", List.of(COPPER)),
                new ThermalGroup("minecraft", "iron_nugget", "iron_ingot", List.of(IRON, CHAINMAIL)),
                new ThermalGroup("minecraft", "gold_nugget", "gold_ingot", List.of(GOLDEN)),
                new ThermalGroup("rootboot", "diamond", "diamond", List.of(DIAMOND)),
                new ThermalGroup("rootboot", "netherite_scrap", "netherite_scrap", List.of(NETHERITE)));
    }

    /** The materials no furnace accepts, recovered on the crafting table instead. */
    private static List<CraftedRecovery> craftedRecoveries() {
        List<CraftedRecovery> recoveries = new ArrayList<>();
        for (String piece : ARMOR) {
            recoveries.add(new CraftedRecovery("leather_" + piece, "leather"));
        }
        recoveries.add(new CraftedRecovery("leather_horse_armor", "leather"));
        recoveries.add(new CraftedRecovery("turtle_helmet", "turtle_scute"));
        recoveries.add(new CraftedRecovery("wolf_armor", "armadillo_scute"));
        return recoveries;
    }

    private static RecipeSpec cooking(ThermalGroup group, CookingMethod method, int cookingTime) {
        return new Cooking(
                group.namespace(),
                group.path(method),
                Category.MISC,
                Optional.empty(),
                method,
                group.inputs(),
                new Result(id(group.recovered()), RECOVERED),
                EXPERIENCE,
                cookingTime);
    }

    private static RecipeSpec crafting(CraftedRecovery recovery) {
        return new Shapeless(
                "rootboot",
                recovery.recovered() + "_from_" + recovery.gear(),
                Category.MISC,
                Optional.empty(),
                List.of(Ingredient.item(id(recovery.gear()))),
                new Result(id(recovery.recovered()), RECOVERED));
    }

    private static GearFamily fullGear(String material) {
        List<String> pieces = new ArrayList<>(ARMOR);
        pieces.addAll(TOOLS);
        pieces.addAll(MOUNT_ARMOR);
        return new GearFamily(material, List.copyOf(pieces));
    }

    private static String id(String path) {
        return "minecraft:" + path;
    }

    /** The gear of one material, named by the registry path pieces share: {@code iron_helmet}. */
    private record GearFamily(String material, List<String> pieces) {

        List<String> itemIds() {
            return pieces.stream().map(piece -> id(material + "_" + piece)).toList();
        }
    }

    /**
     * Everything that cooks down to the same material. {@code recipePrefix} is the id the smelting and
     * blasting recipes are named after — vanilla's nugget recipe for the three families being overridden.
     */
    private record ThermalGroup(
            String namespace, String recipePrefix, String recovered, List<GearFamily> families) {

        String path(CookingMethod method) {
            return recipePrefix + "_from_" + method.name().toLowerCase(Locale.ROOT);
        }

        List<String> inputs() {
            return families.stream().flatMap(family -> family.itemIds().stream()).toList();
        }
    }

    /** A single piece of gear recovered on the crafting table, both named by their registry paths. */
    private record CraftedRecovery(String gear, String recovered) {}
}

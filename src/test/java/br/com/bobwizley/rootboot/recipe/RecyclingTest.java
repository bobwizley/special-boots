package br.com.bobwizley.rootboot.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.bobwizley.rootboot.recipe.RecipeSpec.Cooking;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.CookingMethod;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shapeless;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Exercises the Recycling source of truth: which gear is eligible per material, what each material gives
 * back, the single-unit rule that keeps recovery from becoming duplication, and the id discipline that
 * makes the copper/iron/gold recipes replace vanilla's nugget results instead of racing them.
 *
 * <p>The eligible piece lists are spelled out here rather than derived from the production code, so a
 * silent drop in coverage fails instead of updating both sides at once.
 */
class RecyclingTest {

    private static final List<String> FULL_GEAR =
            List.of(
                    "helmet",
                    "chestplate",
                    "leggings",
                    "boots",
                    "sword",
                    "pickaxe",
                    "axe",
                    "shovel",
                    "hoe",
                    "spear",
                    "horse_armor",
                    "nautilus_armor");

    private static final List<String> ARMOR_ONLY =
            List.of("helmet", "chestplate", "leggings", "boots");

    private static final Map<String, String> RECOVERED_MATERIAL =
            Map.of(
                    "minecraft:copper_nugget", "minecraft:copper_ingot",
                    "minecraft:iron_nugget", "minecraft:iron_ingot",
                    "minecraft:gold_nugget", "minecraft:gold_ingot",
                    "rootboot:diamond", "minecraft:diamond",
                    "rootboot:netherite_scrap", "minecraft:netherite_scrap");

    private static final Map<String, List<String>> ELIGIBLE_GEAR =
            Map.of(
                    "minecraft:copper_nugget", gear("copper", FULL_GEAR),
                    "minecraft:iron_nugget", concat(gear("iron", FULL_GEAR), gear("chainmail", ARMOR_ONLY)),
                    "minecraft:gold_nugget", gear("golden", FULL_GEAR),
                    "rootboot:diamond", gear("diamond", FULL_GEAR),
                    "rootboot:netherite_scrap", gear("netherite", FULL_GEAR));

    @Test
    void everyMaterialIsRecoverableInBothTheFurnaceAndTheBlastFurnace() {
        for (String prefix : RECOVERED_MATERIAL.keySet()) {
            for (CookingMethod method : CookingMethod.values()) {
                Cooking recipe = cooking(prefix, method);

                assertEquals(
                        ELIGIBLE_GEAR.get(prefix),
                        recipe.inputs(),
                        () -> "eligible gear of " + prefix + " " + method);
                assertEquals(RECOVERED_MATERIAL.get(prefix), recipe.result().item());
                assertEquals(
                        1,
                        recipe.result().count(),
                        () -> prefix + " must recover a single unit, never more");
            }
        }
    }

    @Test
    void ironGoldAndCopperReplaceTheVanillaNuggetRecipesTheyConflictWith() {
        for (String prefix :
                List.of("minecraft:copper_nugget", "minecraft:iron_nugget", "minecraft:gold_nugget")) {
            for (CookingMethod method : CookingMethod.values()) {
                Cooking recipe = cooking(prefix, method);

                assertEquals(
                        "minecraft",
                        recipe.namespace(),
                        () -> "the override must reuse the vanilla id " + prefix);
                assertTrue(
                        recipe.result().item().endsWith("_ingot"),
                        () -> prefix + " must give back a whole ingot instead of vanilla's nugget");
            }
        }
    }

    @Test
    void leatherTurtleAndWolfGearAreRecoveredOnTheCraftingTable() {
        Map<String, String> expected = new LinkedHashMap<>();
        for (String piece : ARMOR_ONLY) {
            expected.put("minecraft:leather_" + piece, "minecraft:leather");
        }
        expected.put("minecraft:leather_horse_armor", "minecraft:leather");
        expected.put("minecraft:turtle_helmet", "minecraft:turtle_scute");
        expected.put("minecraft:wolf_armor", "minecraft:armadillo_scute");

        assertEquals(expected, craftedRecoveries());
    }

    @Test
    void everyCraftedRecoveryIsOneItemInOneUnitOutAndAdditive() {
        for (Shapeless recipe : crafts()) {
            assertEquals(1, recipe.ingredients().size(), () -> recipe.path() + " recycles a single item");
            assertEquals(1, recipe.result().count(), () -> recipe.path() + " recovers a single unit");
            assertEquals(
                    "rootboot",
                    recipe.namespace(),
                    () -> recipe.path() + " is additive: vanilla has no recipe to replace");
        }
    }

    @Test
    void shipsOneSmeltingAndOneBlastingRecipePerMaterialPlusTheCraftedRecoveries() {
        Set<String> expected = new LinkedHashSet<>();
        for (String prefix : RECOVERED_MATERIAL.keySet()) {
            for (CookingMethod method : CookingMethod.values()) {
                expected.add(prefix + "_from_" + method.name().toLowerCase(Locale.ROOT));
            }
        }
        for (Map.Entry<String, String> recovery : craftedRecoveries().entrySet()) {
            String gear = recovery.getKey().substring("minecraft:".length());
            String recovered = recovery.getValue().substring("minecraft:".length());
            expected.add("rootboot:" + recovered + "_from_" + gear);
        }

        Set<String> actual =
                Recycling.all().stream()
                        .map(spec -> spec.namespace() + ":" + spec.path())
                        .collect(Collectors.toSet());
        assertEquals(expected, actual);
    }

    @Test
    void noGearIsRecycledByMoreThanOneRecipe() {
        List<String> smeltable = new ArrayList<>();
        for (String prefix : RECOVERED_MATERIAL.keySet()) {
            smeltable.addAll(cooking(prefix, CookingMethod.SMELTING).inputs());
        }
        smeltable.addAll(craftedRecoveries().keySet());

        assertEquals(
                smeltable.size(),
                Set.copyOf(smeltable).size(),
                () -> "gear recycled by more than one recipe: " + smeltable);
    }

    private static Map<String, String> craftedRecoveries() {
        return crafts().stream()
                .collect(
                        Collectors.toMap(
                                recipe -> recipe.ingredients().getFirst().id(),
                                recipe -> recipe.result().item(),
                                (first, second) -> first,
                                LinkedHashMap::new));
    }

    private static List<Shapeless> crafts() {
        return Recycling.all().stream()
                .filter(Shapeless.class::isInstance)
                .map(Shapeless.class::cast)
                .toList();
    }

    private static Cooking cooking(String prefix, CookingMethod method) {
        int colon = prefix.indexOf(':');
        String namespace = prefix.substring(0, colon);
        String path = prefix.substring(colon + 1) + "_from_" + method.name().toLowerCase(Locale.ROOT);
        RecipeSpec spec =
                Recycling.all().stream()
                        .filter(candidate -> candidate.namespace().equals(namespace))
                        .filter(candidate -> candidate.path().equals(path))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("missing recipe " + namespace + ":" + path));
        return assertInstanceOf(Cooking.class, spec, path);
    }

    private static List<String> gear(String material, List<String> pieces) {
        return pieces.stream().map(piece -> "minecraft:" + material + "_" + piece).toList();
    }

    private static List<String> concat(List<String> first, List<String> second) {
        List<String> all = new ArrayList<>(first);
        all.addAll(second);
        return List.copyOf(all);
    }
}

package br.com.bobwizley.rootboot.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.bobwizley.rootboot.recipe.RecipeSpec.Ingredient;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shaped;
import br.com.bobwizley.rootboot.recipe.RecipeSpec.Shapeless;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/** Exercises the typed recipe source of truth: ids, ingredients, formats and quantities. */
class RootBootRecipesTest {

    private RecipeSpec byId(String namespace, String path) {
        return RootBootRecipes.simple().stream()
                .filter(spec -> spec.namespace().equals(namespace) && spec.path().equals(path))
                .findFirst()
                .orElseThrow(() -> new AssertionError("missing recipe " + namespace + ":" + path));
    }

    @Test
    void shipsExactlyTheFourSimpleRecipes() {
        List<String> ids =
                RootBootRecipes.simple().stream()
                        .map(spec -> spec.namespace() + ":" + spec.path())
                        .collect(Collectors.toList());

        assertEquals(
                List.of(
                        "minecraft:clock",
                        "minecraft:compass",
                        "rootboot:bell",
                        "rootboot:wool_to_string"),
                ids);
    }

    // Two specs sharing an id would silently collapse into one file, so the loss is invisible downstream.
    @Test
    void noTwoRecipesInTheWholeSetShareAnId() {
        List<String> ids =
                RootBootRecipes.all().stream()
                        .map(spec -> spec.namespace() + ":" + spec.path())
                        .collect(Collectors.toList());

        List<String> duplicated =
                ids.stream().distinct().filter(id -> Collections.frequency(ids, id) > 1).toList();

        assertEquals(List.of(), duplicated, "every recipe must own its id");
    }

    @Test
    void cheaperClockReplacesVanillaWithFourGoldNuggetsAndNoIngot() {
        Shaped clock = assertInstanceOf(Shaped.class, byId("minecraft", "clock"));

        assertEquals(List.of(" # ", "#X#", " # "), clock.pattern());
        assertEquals(Ingredient.item("minecraft:gold_nugget"), clock.key().get('#'));
        assertEquals(Ingredient.item("minecraft:redstone"), clock.key().get('X'));
        assertEquals("minecraft:clock", clock.result().item());
        assertEquals(1, clock.result().count());
        assertFalse(usesItem(clock, "minecraft:gold_ingot"), "no gold ingot may participate");
        assertEquals(4, symbolCount(clock, '#'), "four gold nuggets on the arms");
    }

    @Test
    void cheaperCompassMirrorsClockWithFourIronNuggetsAndNoIngot() {
        Shaped compass = assertInstanceOf(Shaped.class, byId("minecraft", "compass"));

        assertEquals(List.of(" # ", "#X#", " # "), compass.pattern());
        assertEquals(Ingredient.item("minecraft:iron_nugget"), compass.key().get('#'));
        assertEquals(Ingredient.item("minecraft:redstone"), compass.key().get('X'));
        assertEquals("minecraft:compass", compass.result().item());
        assertEquals(1, compass.result().count());
        assertFalse(usesItem(compass, "minecraft:iron_ingot"), "no iron ingot may participate");
        assertEquals(4, symbolCount(compass, '#'), "four iron nuggets on the arms");
    }

    @Test
    void bellUsesThreeLogsGoldBlockAndFourStones() {
        Shaped bell = assertInstanceOf(Shaped.class, byId("rootboot", "bell"));

        assertEquals(List.of("LLL", "SGS", "S S"), bell.pattern());
        assertEquals(Ingredient.tag("minecraft:logs"), bell.key().get('L'));
        assertEquals(Ingredient.item("minecraft:gold_block"), bell.key().get('G'));
        assertEquals(Ingredient.item("minecraft:stone"), bell.key().get('S'));
        assertEquals(3, symbolCount(bell, 'L'), "three logs on the top row");
        assertEquals(1, symbolCount(bell, 'G'), "one gold block");
        assertEquals(4, symbolCount(bell, 'S'), "four stones");
        assertEquals("minecraft:bell", bell.result().item());
        assertEquals(1, bell.result().count());
    }

    @Test
    void woolToStringTakesOneWoolViaTagAndYieldsFourStrings() {
        Shapeless wool = assertInstanceOf(Shapeless.class, byId("rootboot", "wool_to_string"));

        assertEquals(List.of(Ingredient.tag("minecraft:wool")), wool.ingredients());
        assertEquals("minecraft:string", wool.result().item());
        assertEquals(4, wool.result().count());
    }

    private static boolean usesItem(Shaped recipe, String itemId) {
        return recipe.key().values().stream()
                .anyMatch(ingredient -> !ingredient.isTag() && ingredient.id().equals(itemId));
    }

    private static long symbolCount(Shaped recipe, char symbol) {
        return recipe.pattern().stream()
                .flatMapToInt(String::chars)
                .filter(c -> c == symbol)
                .count();
    }
}

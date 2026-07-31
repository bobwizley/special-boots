package br.com.bobwizley.rootboot.feature.specialboots;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.bobwizley.rootboot.config.RootBootConfig;
import br.com.bobwizley.rootboot.feature.FeatureRegistry;
import br.com.bobwizley.rootboot.feature.heavyfoot.HeavyfootFeature;
import br.com.bobwizley.rootboot.feature.lightfoot.LightfootFeature;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SpecialBootsTest {

    private static final Path DATA = Path.of("src", "main", "generated", "data");
    private static final Gson GSON = new Gson();

    @Test
    void featuresHaveCorrectIds() {
        assertEquals("heavyfoot", HeavyfootFeature.ID);
        assertEquals("lightfoot", LightfootFeature.ID);
    }

    @Test
    void featuresRegisterIndependentlyByToggle() {
        FeatureRegistry registry = new FeatureRegistry(List.of(
                new HeavyfootFeature(RootBootConfig.HEAVYFOOT_RADIUS_DEFAULT),
                new LightfootFeature()));

        RootBootConfig config = new RootBootConfig();
        config.setEnabled(HeavyfootFeature.ID, false);
        assertEquals(List.of(LightfootFeature.ID), registry.registerEnabled(config));

        config.setEnabled(HeavyfootFeature.ID, true);
        config.setEnabled(LightfootFeature.ID, false);
        assertEquals(List.of(HeavyfootFeature.ID), registry.registerEnabled(config));
    }

    @Test
    void enchantmentsHaveCorrectStatsAndExclusivity() throws IOException {
        for (String id : List.of("heavyfoot", "lightfoot")) {
            JsonObject json = enchantmentJson(id);

            assertEquals(5, json.get("weight").getAsInt());
            assertEquals(1, json.get("max_level").getAsInt());
            assertEquals(2, json.get("anvil_cost").getAsInt());
            assertEquals(15, json.get("min_cost").getAsJsonObject().get("base").getAsInt());
            assertEquals(45, json.get("max_cost").getAsJsonObject().get("base").getAsInt());

            JsonArray slots = json.getAsJsonArray("slots");
            assertEquals(1, slots.size());
            assertEquals("feet", slots.get(0).getAsString());

            assertEquals("#minecraft:enchantable/foot_armor", json.get("supported_items").getAsString());
            assertEquals("#rootboot:exclusive_set/special_boots", json.get("exclusive_set").getAsString());
        }
    }

    @Test
    void heavyfootCarriesItsTickEffectAndLightfootDoesNot() throws IOException {
        JsonObject heavyfoot = enchantmentJson("heavyfoot");
        JsonArray tickEffects = heavyfoot.getAsJsonObject("effects").getAsJsonArray("minecraft:tick");

        assertEquals(1, tickEffects.size());
        assertEquals(
                "rootboot:heavyfoot",
                tickEffects.get(0).getAsJsonObject().getAsJsonObject("effect").get("type").getAsString());
        assertFalse(enchantmentJson("lightfoot").has("effects"));
    }

    @Test
    void enchantmentsAreInCorrectTags() throws IOException {
        assertTagContains("minecraft/tags/enchantment/in_enchanting_table.json", "rootboot:heavyfoot", "rootboot:lightfoot");
        assertTagContains("minecraft/tags/enchantment/non_treasure.json", "rootboot:heavyfoot", "rootboot:lightfoot");
        assertTagContains("rootboot/tags/enchantment/exclusive_set/special_boots.json", "rootboot:heavyfoot", "rootboot:lightfoot");
    }

    @Test
    void enchantmentsAndOptionsHaveTranslations() throws IOException {
        Path langFile = Path.of("src", "main", "resources", "assets", "rootboot", "lang", "en_us.json");
        JsonObject langJson = GSON.fromJson(Files.readString(langFile), JsonObject.class);

        assertTrue(langJson.has("enchantment.rootboot.heavyfoot"));
        assertTrue(langJson.has("enchantment.rootboot.lightfoot"));
        assertTrue(langJson.has("option.rootboot.heavyfoot"));
        assertTrue(langJson.has("option.rootboot.heavyfoot.tooltip"));
        assertTrue(langJson.has("option.rootboot.heavyfoot_radius"));
        assertTrue(langJson.has("option.rootboot.heavyfoot_radius.tooltip"));
        assertTrue(langJson.has("option.rootboot.lightfoot"));
        assertTrue(langJson.has("option.rootboot.lightfoot.tooltip"));
    }

    private JsonObject enchantmentJson(String id) throws IOException {
        Path file = DATA.resolve("rootboot/enchantment/" + id + ".json");
        return GSON.fromJson(Files.readString(file), JsonObject.class);
    }

    private void assertTagContains(String relativePath, String... expectedValues) throws IOException {
        Path file = DATA.resolve(relativePath);
        JsonObject json = GSON.fromJson(Files.readString(file), JsonObject.class);
        JsonArray values = json.getAsJsonArray("values");

        List<String> actual = new ArrayList<>();
        values.forEach(e -> {
            if (e.isJsonObject()) {
                actual.add(e.getAsJsonObject().get("id").getAsString());
            } else {
                actual.add(e.getAsString());
            }
        });

        for (String expected : expectedValues) {
            assertTrue(actual.contains(expected), "Tag " + relativePath + " is missing " + expected);
        }
    }
}

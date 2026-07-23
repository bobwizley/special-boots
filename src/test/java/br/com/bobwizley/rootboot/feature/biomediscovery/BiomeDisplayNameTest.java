package br.com.bobwizley.rootboot.feature.biomediscovery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

class BiomeDisplayNameTest {

    @Test
    void vanillaBiomeUsesItsTranslationKeyWithAReadableFallback() {
        Identifier plains = Identifier.withDefaultNamespace("plains");

        assertEquals("biome.minecraft.plains", BiomeDisplayName.translationKey(plains));
        assertEquals("Plains", BiomeDisplayName.fallback(plains));
    }

    @Test
    void moddedBiomeUsesItsTranslationKeyWithAFormattedIdFallback() {
        Identifier crystalCaves = Identifier.fromNamespaceAndPath("example_mod", "crystal_caves");

        assertEquals("biome.example_mod.crystal_caves", BiomeDisplayName.translationKey(crystalCaves));
        assertEquals("Example Mod: Crystal Caves", BiomeDisplayName.fallback(crystalCaves));
    }
}

package br.com.bobwizley.rootboot.feature.biomediscovery;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import java.util.UUID;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

class BiomeDiscoveryStateTest {

    @Test
    void aBiomeIsDiscoveredOnlyOncePerPlayer() {
        BiomeDiscoveryState state = BiomeDiscoveryState.empty();
        UUID playerId = UUID.randomUUID();
        Identifier plains = Identifier.withDefaultNamespace("plains");

        assertTrue(state.discover(playerId, plains));
        assertFalse(state.discover(playerId, plains));
    }

    @Test
    void discoveriesAreIndependentBetweenPlayers() {
        BiomeDiscoveryState state = BiomeDiscoveryState.empty();
        Identifier plains = Identifier.withDefaultNamespace("plains");

        assertTrue(state.discover(UUID.randomUUID(), plains));
        assertTrue(state.discover(UUID.randomUUID(), plains));
    }

    @Test
    void movingIntoANewBiomeCreatesAnotherDiscovery() {
        BiomeDiscoveryState state = BiomeDiscoveryState.empty();
        UUID playerId = UUID.randomUUID();

        assertTrue(state.discover(playerId, Identifier.withDefaultNamespace("plains")));
        assertTrue(state.discover(playerId, Identifier.withDefaultNamespace("desert")));
    }

    @Test
    void vanillaAndModdedBiomeDiscoveriesSurvivePersistenceAndReconnect() {
        BiomeDiscoveryState original = BiomeDiscoveryState.empty();
        UUID playerId = UUID.randomUUID();
        Identifier plains = Identifier.withDefaultNamespace("plains");
        Identifier crystalCaves = Identifier.fromNamespaceAndPath("example", "crystal_caves");
        original.discover(playerId, plains);
        original.discover(playerId, crystalCaves);

        JsonElement encoded = BiomeDiscoveryState.TYPE.codec()
                .encodeStart(JsonOps.INSTANCE, original)
                .getOrThrow();
        BiomeDiscoveryState restored = BiomeDiscoveryState.TYPE.codec()
                .parse(JsonOps.INSTANCE, encoded)
                .getOrThrow();

        assertFalse(restored.discover(playerId, plains));
        assertFalse(restored.discover(playerId, crystalCaves));
    }
}

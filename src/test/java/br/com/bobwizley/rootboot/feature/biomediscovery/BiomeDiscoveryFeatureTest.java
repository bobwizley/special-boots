package br.com.bobwizley.rootboot.feature.biomediscovery;

import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.bobwizley.rootboot.config.RootBootConfig;
import br.com.bobwizley.rootboot.feature.FeatureRegistry;
import java.util.List;
import java.util.UUID;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

class BiomeDiscoveryFeatureTest {

    @Test
    void aBiomeVisitedWhileDisabledRemainsEligibleAfterReEnable() {
        RootBootConfig config = new RootBootConfig();
        config.setEnabled(BiomeDiscoveryFeature.ID, false);
        FeatureRegistry registry = new FeatureRegistry(List.of(new BiomeDiscoveryFeature()));
        BiomeDiscoveryState state = BiomeDiscoveryState.empty();
        UUID playerId = UUID.randomUUID();
        Identifier plains = Identifier.withDefaultNamespace("plains");

        assertTrue(registry.registerEnabled(config).isEmpty());
        assertTrue(state.discover(playerId, plains));
    }
}

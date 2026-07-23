package br.com.bobwizley.rootboot.feature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.bobwizley.rootboot.config.RootBootConfig;
import java.util.List;
import org.junit.jupiter.api.Test;

class FeatureRegistryTest {

    private static final class RecordingFeature implements Feature {
        private final String id;
        boolean registered = false;

        RecordingFeature(String id) {
            this.id = id;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public void register() {
            registered = true;
        }
    }

    @Test
    void registersEnabledFeatures() {
        RecordingFeature feature = new RecordingFeature("alpha");
        FeatureRegistry registry = new FeatureRegistry(List.of(feature));
        RootBootConfig config = new RootBootConfig();
        config.setEnabled("alpha", true);

        List<String> registered = registry.registerEnabled(config);

        assertTrue(feature.registered);
        assertEquals(List.of("alpha"), registered);
    }

    @Test
    void skipsDisabledFeatures() {
        RecordingFeature feature = new RecordingFeature("alpha");
        FeatureRegistry registry = new FeatureRegistry(List.of(feature));
        RootBootConfig config = new RootBootConfig();
        config.setEnabled("alpha", false);

        List<String> registered = registry.registerEnabled(config);

        assertFalse(feature.registered);
        assertTrue(registered.isEmpty());
    }

    @Test
    void featuresAreEnabledByDefault() {
        RecordingFeature feature = new RecordingFeature("alpha");
        FeatureRegistry registry = new FeatureRegistry(List.of(feature));

        List<String> registered = registry.registerEnabled(new RootBootConfig());

        assertTrue(feature.registered);
        assertEquals(List.of("alpha"), registered);
    }
}

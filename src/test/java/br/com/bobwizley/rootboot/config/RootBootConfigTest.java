package br.com.bobwizley.rootboot.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RootBootConfigTest {

    @Test
    void malformedJsonFallsBackToDefaults(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("rootboot.json");
        Files.writeString(file, "{ not valid json");

        RootBootConfig config = RootBootConfig.load(file);

        assertTrue(config.isEnabled("time_offset"));
    }

    @Test
    void ensureKeysSeedsMissingFeaturesAsEnabled(@TempDir Path dir) {
        RootBootConfig config = RootBootConfig.load(dir.resolve("rootboot.json"));

        assertTrue(config.ensureKeys(List.of("time_offset")));
        assertTrue(config.isEnabled("time_offset"));
        assertFalse(config.ensureKeys(List.of("time_offset")));
    }

    @Test
    void heavyfootRadiusDefaultsToOneAndStaysInRange(@TempDir Path dir) {
        RootBootConfig config = RootBootConfig.load(dir.resolve("rootboot.json"));

        assertEquals(1, config.heavyfootRadius());

        config.setHeavyfootRadius(2);
        assertEquals(2, config.heavyfootRadius());

        config.setHeavyfootRadius(7);
        assertEquals(2, config.heavyfootRadius());

        config.setHeavyfootRadius(-1);
        assertEquals(0, config.heavyfootRadius());
    }

    @Test
    void heavyfootRadiusOutOfRangeInFileIsClamped(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("rootboot.json");
        Files.writeString(file, "{ \"heavyfootRadius\": 9 }");

        assertEquals(2, RootBootConfig.load(file).heavyfootRadius());
    }

    @Test
    void roundTripsPersistedToggles(@TempDir Path dir) {
        Path file = dir.resolve("rootboot.json");
        RootBootConfig config = RootBootConfig.load(file);
        config.setEnabled("time_offset", false);
        config.save(file);

        assertFalse(RootBootConfig.load(file).isEnabled("time_offset"));
    }
}

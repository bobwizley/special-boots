package br.com.bobwizley.rootboot.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class GeneratedBetterTreesPackagingTest {

    private static final Path DATA = Path.of("src", "main", "generated", "data");

    @Test
    void generatedConfiguredFeaturesExactlyMatchTheSpecifiedVanillaIds() throws IOException {
        Path directory = DATA.resolve("minecraft/worldgen/configured_feature");
        assertTrue(Files.isDirectory(directory), "generated configured feature directory is missing");

        try (Stream<Path> files = Files.list(directory)) {
            Set<String> ids =
                    files.filter(path -> path.getFileName().toString().endsWith(".json"))
                            .map(path -> path.getFileName().toString().replaceFirst("\\.json$", ""))
                            .collect(Collectors.toSet());
            assertEquals(BetterTreesConfigurations.IDS, ids);
        }
    }

    @Test
    void generatedDataContainsNoOptionalPackOrClearTreesFunction() throws IOException {
        try (Stream<Path> files = Files.walk(Path.of("src", "main"))) {
            Set<String> paths =
                    files.map(path -> path.toString().replace('\\', '/'))
                            .collect(Collectors.toSet());
            assertFalse(paths.stream().anyMatch(path -> path.contains("cleartrees")));
            assertFalse(paths.stream().anyMatch(path -> path.contains("datapacks")));
        }
    }
}

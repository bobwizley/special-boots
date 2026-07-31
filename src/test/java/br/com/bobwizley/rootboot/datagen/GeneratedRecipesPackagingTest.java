package br.com.bobwizley.rootboot.datagen;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import br.com.bobwizley.rootboot.recipe.RecipeSpec;
import br.com.bobwizley.rootboot.recipe.RootBootRecipes;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Validates the JSON the datagen provider wrote under {@code src/main/generated} — the set that loom
 * packages into the JAR. Reading the committed output proves the generated set matches the typed
 * source of truth in ids, ingredients, formats, quantities and packaging location.
 */
class GeneratedRecipesPackagingTest {

    private static final Path DATA = Path.of("src", "main", "generated", "data");

    @Test
    void everySpecIsPackagedAtItsNamespacedLocationWithMatchingContent() {
        for (RecipeSpec spec : RootBootRecipes.all()) {
            Path file = recipePath(DATA, spec);
            assertTrue(Files.exists(file), () -> "generated recipe missing: " + file);
            RecipeJson.assertMatchesSpec(spec, read(file));
        }
    }

    @Test
    void theGeneratedRecipeSetIsExactlyTheSpecSetAcrossNamespaces() throws IOException {
        Set<String> expected =
                RootBootRecipes.all().stream()
                        .map(spec -> spec.namespace() + "/recipe/" + spec.path() + ".json")
                        .collect(Collectors.toSet());

        try (Stream<Path> walk = Files.walk(DATA)) {
            Set<String> generated =
                    walk.filter(Files::isRegularFile)
                            .map(p -> DATA.relativize(p).toString().replace('\\', '/'))
                            // A recipe id may nest, so the marker is the recipe directory of a namespace
                            // rather than the file's immediate parent.
                            .filter(p -> p.matches("[^/]+/recipe/.+\\.json"))
                            .collect(Collectors.toSet());
            assertTrue(
                    generated.equals(expected),
                    () -> "generated recipe set " + generated + " != spec set " + expected);
        }
    }

    @Test
    void theBuiltJarContainsEveryRecipe() throws IOException {
        Path jar = builtJar();
        assumeTrue(jar != null, "no built JAR present; run ./gradlew build");

        try (JarFile jarFile = new JarFile(jar.toFile())) {
            for (RecipeSpec spec : RootBootRecipes.all()) {
                String entry = "data/" + spec.namespace() + "/recipe/" + spec.path() + ".json";
                assertTrue(jarFile.getJarEntry(entry) != null, () -> "JAR missing entry: " + entry);
            }
        }
    }

    private static Path recipePath(Path root, RecipeSpec spec) {
        return root.resolve(spec.namespace()).resolve("recipe").resolve(spec.path() + ".json");
    }

    private static Path builtJar() throws IOException {
        Path libs = Path.of("build", "libs");
        if (!Files.isDirectory(libs)) {
            return null;
        }
        try (Stream<Path> jars = Files.list(libs)) {
            // The newest one: a version bump leaves the JARs of earlier versions behind, and checking a
            // stale JAR would fail on recipes that simply did not exist when it was built.
            return jars.filter(p -> p.getFileName().toString().matches("rootboot-.*(?<!-sources)\\.jar"))
                    .max(Comparator.comparing(GeneratedRecipesPackagingTest::lastModified))
                    .orElse(null);
        }
    }

    private static FileTime lastModified(Path file) {
        try {
            return Files.getLastModifiedTime(file);
        } catch (IOException e) {
            throw new AssertionError("could not read the timestamp of " + file, e);
        }
    }

    private static JsonObject read(Path file) {
        try {
            return JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        } catch (IOException e) {
            throw new AssertionError("could not read " + file, e);
        }
    }
}

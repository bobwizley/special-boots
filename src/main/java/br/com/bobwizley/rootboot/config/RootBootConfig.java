package br.com.bobwizley.rootboot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The single, unified RootBoot configuration file. Behavior features are keyed by their
 * stable id and enabled by default; the file is consulted during initialization to decide
 * which handlers get registered.
 */
public final class RootBootConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("rootboot-config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final int HEAVYFOOT_RADIUS_MIN = 0;
    public static final int HEAVYFOOT_RADIUS_MAX = 2;
    public static final int HEAVYFOOT_RADIUS_DEFAULT = 1;

    private Map<String, Boolean> features = new LinkedHashMap<>();
    private int heavyfootRadius = HEAVYFOOT_RADIUS_DEFAULT;

    public boolean isEnabled(String featureId) {
        return features.getOrDefault(featureId, true);
    }

    public void setEnabled(String featureId, boolean enabled) {
        features.put(featureId, enabled);
    }

    public int heavyfootRadius() {
        return heavyfootRadius;
    }

    public void setHeavyfootRadius(int radius) {
        heavyfootRadius = clampHeavyfootRadius(radius);
    }

    public boolean ensureKeys(Collection<String> featureIds) {
        boolean changed = false;
        for (String featureId : featureIds) {
            if (!features.containsKey(featureId)) {
                features.put(featureId, true);
                changed = true;
            }
        }
        return changed;
    }

    public static RootBootConfig load(Path path) {
        RootBootConfig config = null;
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                config = GSON.fromJson(reader, RootBootConfig.class);
            } catch (IOException | JsonParseException e) {
                LOGGER.error("Failed to read config at {}, using defaults", path, e);
            }
        }
        if (config == null) {
            config = new RootBootConfig();
        }
        if (config.features == null) {
            config.features = new LinkedHashMap<>();
        }
        config.heavyfootRadius = clampHeavyfootRadius(config.heavyfootRadius);
        return config;
    }

    private static int clampHeavyfootRadius(int radius) {
        return Math.max(HEAVYFOOT_RADIUS_MIN, Math.min(HEAVYFOOT_RADIUS_MAX, radius));
    }

    public void save(Path path) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save config at {}", path, e);
        }
    }
}

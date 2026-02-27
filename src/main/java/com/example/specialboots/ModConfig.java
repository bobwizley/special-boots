package com.example.specialboots;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("specialboots.json");
    private static ModConfig instance;

    private int radius = 0;

    public static ModConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public int getRadius() {
        return radius;
    }

    private static ModConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                ModConfig config = GSON.fromJson(reader, ModConfig.class);
                if (config != null) {
                    config.clamp();
                    return config;
                }
            } catch (IOException e) {
                SpecialBoots.LOGGER.error("Failed to read config, using defaults", e);
            }
        }

        ModConfig config = new ModConfig();
        config.save();
        return config;
    }

    private void clamp() {
        radius = Math.max(0, Math.min(5, radius));
    }

    private void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            SpecialBoots.LOGGER.error("Failed to save config", e);
        }
    }
}

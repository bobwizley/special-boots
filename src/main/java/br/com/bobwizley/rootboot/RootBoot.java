package br.com.bobwizley.rootboot;

import br.com.bobwizley.rootboot.config.RootBootConfig;
import br.com.bobwizley.rootboot.feature.FeatureRegistry;
import br.com.bobwizley.rootboot.feature.dayannouncement.DayAnnouncementFeature;
import br.com.bobwizley.rootboot.feature.deathitemprotection.DeathItemProtectionFeature;
import br.com.bobwizley.rootboot.feature.homingexperienceorb.HomingExperienceOrbFeature;
import br.com.bobwizley.rootboot.feature.levelmilestone.LevelMilestoneFeature;
import br.com.bobwizley.rootboot.feature.timeoffset.TimeOffsetFeature;
import br.com.bobwizley.rootboot.feature.timeoffset.TimeOffsetWorldInitializer;
import java.nio.file.Path;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RootBoot implements ModInitializer {

    public static final String MOD_ID = "rootboot";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static RootBootConfig config;

    public static RootBootConfig config() {
        if (config == null) {
            config = RootBootConfig.load(configPath());
        }
        return config;
    }

    public static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json");
    }

    @Override
    public void onInitialize() {
        RootBootConfig cfg = config();

        FeatureRegistry registry = new FeatureRegistry(List.of(
                new TimeOffsetFeature(),
                new DayAnnouncementFeature(),
                new LevelMilestoneFeature(),
                new HomingExperienceOrbFeature(),
                new DeathItemProtectionFeature()));
        if (cfg.ensureKeys(registry.featureIds())) {
            cfg.save(configPath());
        }

        List<String> registered = registry.registerEnabled(cfg);
        LOGGER.info("RootBoot registered {} behavior feature(s): {}", registered.size(), registered);

        // Persistence exception: even when Time Offset is disabled, a world's first
        // initialization must be recorded as evaluated so that enabling the feature later
        // never retroactively shifts an already-created world.
        if (!cfg.isEnabled(TimeOffsetFeature.ID)) {
            ServerLifecycleEvents.SERVER_STARTED.register(server ->
                    TimeOffsetWorldInitializer.firstInit(server, false));
        }
    }
}

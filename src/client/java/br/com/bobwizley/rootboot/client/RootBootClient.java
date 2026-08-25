package br.com.bobwizley.rootboot.client;

import br.com.bobwizley.rootboot.RootBoot;
import br.com.bobwizley.rootboot.client.feature.jukeboxmusicoverride.JukeboxMusicOverrideFeature;
import br.com.bobwizley.rootboot.client.feature.localdeathsound.LocalDeathSoundFeature;
import br.com.bobwizley.rootboot.client.feature.lowhealthsound.LowHealthSoundFeature;
import br.com.bobwizley.rootboot.client.feature.stopmusicondeath.StopMusicOnDeathFeature;
import br.com.bobwizley.rootboot.config.RootBootConfig;
import br.com.bobwizley.rootboot.feature.FeatureRegistry;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;

public final class RootBootClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RootBootConfig config = RootBoot.config();

        FeatureRegistry registry = new FeatureRegistry(List.of(
                new JukeboxMusicOverrideFeature(),
                new LocalDeathSoundFeature(),
                new StopMusicOnDeathFeature(),
                new LowHealthSoundFeature()));
        if (config.ensureKeys(registry.featureIds())) {
            config.save(RootBoot.configPath());
        }

        List<String> registered = registry.registerEnabled(config);
        RootBoot.LOGGER.info("RootBoot registered {} client feature(s): {}",
                registered.size(), registered);
    }
}

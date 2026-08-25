package br.com.bobwizley.rootboot.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.com.bobwizley.rootboot.client.feature.jukeboxmusicoverride.JukeboxMusicOverrideFeature;
import br.com.bobwizley.rootboot.client.feature.localdeathsound.LocalDeathSoundFeature;
import br.com.bobwizley.rootboot.client.feature.lowhealthsound.LowHealthSoundFeature;
import br.com.bobwizley.rootboot.client.feature.stopmusicondeath.StopMusicOnDeathFeature;
import br.com.bobwizley.rootboot.config.RootBootConfig;
import br.com.bobwizley.rootboot.feature.Feature;
import br.com.bobwizley.rootboot.feature.FeatureRegistry;
import java.util.List;
import org.junit.jupiter.api.Test;

class LocalAudioFeaturesTest {

    private static List<Feature> features() {
        return List.of(
                new JukeboxMusicOverrideFeature(),
                new LocalDeathSoundFeature(),
                new StopMusicOnDeathFeature(),
                new LowHealthSoundFeature());
    }

    @Test
    void featuresHaveCorrectIds() {
        assertEquals(
                List.of("jukebox_music_override", "local_death_sound", "stop_music_on_death",
                        "low_health_sound"),
                new FeatureRegistry(features()).featureIds());
    }

    @Test
    void featuresAreEnabledByDefault() {
        assertEquals(
                new FeatureRegistry(features()).featureIds(),
                new FeatureRegistry(features()).registerEnabled(new RootBootConfig()));
    }

    @Test
    void eachToggleControlsOnlyItsOwnRegistration() {
        List<String> ids = new FeatureRegistry(features()).featureIds();

        for (String disabled : ids) {
            RootBootConfig config = new RootBootConfig();
            config.setEnabled(disabled, false);

            assertEquals(
                    ids.stream().filter(id -> !id.equals(disabled)).toList(),
                    new FeatureRegistry(features()).registerEnabled(config));
        }
    }
}

package br.com.bobwizley.rootboot.feature.dayannouncement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.sounds.SoundSource;
import org.junit.jupiter.api.Test;

class DayAnnouncementFeatureTest {

    @Test
    void typingClickUsesTheReferenceAudioSettings() {
        DayAnnouncementFeature.SoundSettings sound =
                DayAnnouncementFeature.soundSettings(DayAnnouncementFrame.Sound.CLICK);

        assertEquals(SoundSource.MASTER, sound.source());
        assertEquals(0.4F, sound.volume());
        assertEquals(2.0F, sound.pitch());
    }
}

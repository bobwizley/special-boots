package br.com.bobwizley.rootboot.client.feature.lowhealthsound;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LowHealthSoundTest {

    @Test
    void beatsWhenANonFatalHitLeavesTheThresholdOrLess() {
        assertTrue(LowHealthSound.beats(20.0F, LowHealthSound.THRESHOLD));
        assertTrue(LowHealthSound.beats(LowHealthSound.THRESHOLD, 1.0F));
    }

    @Test
    void staysSilentWhenTheHitLeavesMoreThanTheThreshold() {
        assertFalse(LowHealthSound.beats(20.0F, LowHealthSound.THRESHOLD + 1.0F));
    }

    @Test
    void staysSilentWhenTheHitIsFatal() {
        assertFalse(LowHealthSound.beats(4.0F, 0.0F));
    }

    @Test
    void staysSilentWhenTheRealHealthDidNotDrop() {
        assertFalse(LowHealthSound.beats(4.0F, 4.0F));
        assertFalse(LowHealthSound.beats(4.0F, 6.0F));
    }
}

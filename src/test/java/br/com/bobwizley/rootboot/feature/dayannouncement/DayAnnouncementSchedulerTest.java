package br.com.bobwizley.rootboot.feature.dayannouncement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DayAnnouncementSchedulerTest {

    private static final UUID PLAYER = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID RECONNECTING_PLAYER =
            UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void announcesAnOverworldDayTransitionForTwoSeconds() {
        DayAnnouncementScheduler scheduler = new DayAnnouncementScheduler();

        assertTrue(scheduler.tick(7, Set.of(PLAYER)).isEmpty());

        for (int tick = 0; tick < 40; tick++) {
            DayAnnouncementFrame frame = scheduler.tick(8, Set.of(PLAYER)).get(PLAYER);
            assertEquals(8, frame.day());
            assertEquals(DayAnnouncementFrame.Kind.COMMON, frame.kind());
        }

        assertTrue(scheduler.tick(8, Set.of(PLAYER)).isEmpty());
    }

    @Test
    void celebratesHundredDayMilestonesWithColorsAndSoundsForElevenSeconds() {
        DayAnnouncementScheduler scheduler = new DayAnnouncementScheduler();
        scheduler.tick(99, Set.of(PLAYER));
        Set<Integer> colors = new HashSet<>();
        Set<DayAnnouncementFrame.Sound> sounds = new HashSet<>();

        for (int tick = 0; tick < 220; tick++) {
            DayAnnouncementFrame frame = scheduler.tick(100, Set.of(PLAYER)).get(PLAYER);
            assertEquals(DayAnnouncementFrame.Kind.MILESTONE, frame.kind());
            assertTrue(frame.bold());
            colors.add(frame.color());
            sounds.addAll(frame.sounds());
        }

        assertTrue(scheduler.tick(100, Set.of(PLAYER)).isEmpty());
        assertTrue(colors.size() >= 3);
        assertTrue(sounds.size() >= 3);
    }

    @Test
    void reconnectingDuringAMilestoneReceivesOnlyTheCommonAnnouncement() {
        DayAnnouncementScheduler scheduler = new DayAnnouncementScheduler();
        scheduler.tick(99, Set.of(PLAYER));
        assertEquals(
                DayAnnouncementFrame.Kind.MILESTONE,
                scheduler.tick(100, Set.of(PLAYER)).get(PLAYER).kind());

        scheduler.playerJoined(RECONNECTING_PLAYER);
        var frames = scheduler.tick(100, Set.of(PLAYER, RECONNECTING_PLAYER));

        assertEquals(DayAnnouncementFrame.Kind.MILESTONE, frames.get(PLAYER).kind());
        assertEquals(DayAnnouncementFrame.Kind.COMMON, frames.get(RECONNECTING_PLAYER).kind());
    }

    @Test
    void milestoneSupersedesAJoinAnnouncementAlreadyInProgress() {
        DayAnnouncementScheduler scheduler = new DayAnnouncementScheduler();
        scheduler.playerJoined(PLAYER);
        assertEquals(
                DayAnnouncementFrame.Kind.COMMON,
                scheduler.tick(99, Set.of(PLAYER)).get(PLAYER).kind());

        assertEquals(
                DayAnnouncementFrame.Kind.MILESTONE,
                scheduler.tick(100, Set.of(PLAYER)).get(PLAYER).kind());
    }
}

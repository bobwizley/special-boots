package br.com.bobwizley.rootboot.feature.dayannouncement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        advance(scheduler, 8, 100);

        for (int tick = 0; tick < 41; tick++) {
            DayAnnouncementFrame frame = scheduler.tick(8, Set.of(PLAYER)).get(PLAYER);
            assertEquals(8, frame.day());
            assertEquals(DayAnnouncementFrame.Kind.COMMON, frame.kind());
            assertNull(frame.partialTranslationKey());
        }

        advance(scheduler, 8, 59);
        assertTrue(scheduler.tick(8, Set.of(PLAYER)).isEmpty());
    }

    @Test
    void commonAnnouncementMatchesTheReferenceTypingTimeline() {
        DayAnnouncementScheduler scheduler = new DayAnnouncementScheduler();
        scheduler.tick(7, Set.of(PLAYER));

        for (int tick = 0; tick < 40; tick++) {
            assertTrue(scheduler.tick(8, Set.of(PLAYER)).isEmpty());
        }
        assertTypingFrame(
                scheduler.tick(8, Set.of(PLAYER)).get(PLAYER),
                "message.rootboot.day_announcement.typing.dash");
        advance(scheduler, 8, 4);
        assertTypingFrame(
                scheduler.tick(8, Set.of(PLAYER)).get(PLAYER),
                "message.rootboot.day_announcement.typing.double_dash");
        advance(scheduler, 8, 24);
        assertTypingFrame(
                scheduler.tick(8, Set.of(PLAYER)).get(PLAYER),
                "message.rootboot.day_announcement.typing.empty");
        advance(scheduler, 8, 4);
        assertTypingFrame(
                scheduler.tick(8, Set.of(PLAYER)).get(PLAYER),
                "message.rootboot.day_announcement.typing.d");
        advance(scheduler, 8, 4);
        assertTypingFrame(
                scheduler.tick(8, Set.of(PLAYER)).get(PLAYER),
                "message.rootboot.day_announcement.typing.da");
        advance(scheduler, 8, 4);
        assertTypingFrame(
                scheduler.tick(8, Set.of(PLAYER)).get(PLAYER),
                "message.rootboot.day_announcement.typing.day");
        advance(scheduler, 8, 14);
        assertNull(scheduler.tick(8, Set.of(PLAYER)).get(PLAYER).partialTranslationKey());
    }

    @Test
    void joiningMidDayWaitsThreeSecondsBeforeTyping() {
        DayAnnouncementScheduler scheduler = new DayAnnouncementScheduler();
        scheduler.playerJoined(PLAYER);

        for (int tick = 0; tick < 60; tick++) {
            assertTrue(scheduler.tick(8, Set.of(PLAYER)).isEmpty());
        }

        assertTypingFrame(
                scheduler.tick(8, Set.of(PLAYER)).get(PLAYER),
                "message.rootboot.day_announcement.typing.dash");
    }

    @Test
    void celebratesHundredDayMilestonesWithColorsAndSoundsForElevenSeconds() {
        DayAnnouncementScheduler scheduler = new DayAnnouncementScheduler();
        scheduler.tick(99, Set.of(PLAYER));
        Set<Integer> colors = new HashSet<>();
        Set<DayAnnouncementFrame.Sound> sounds = new HashSet<>();
        advance(scheduler, 100, 100);

        for (int tick = 0; tick < 220; tick++) {
            DayAnnouncementFrame frame = scheduler.tick(100, Set.of(PLAYER)).get(PLAYER);
            if (frame != null) {
                assertEquals(DayAnnouncementFrame.Kind.MILESTONE, frame.kind());
                colors.add(frame.color());
                sounds.addAll(frame.sounds());
            }
        }

        assertTrue(scheduler.tick(100, Set.of(PLAYER)).isEmpty());
        assertTrue(colors.size() >= 3);
        assertTrue(sounds.size() >= 3);
    }

    @Test
    void milestoneAnimationTypesOutBeforeRevealingTheDayCount() {
        DayAnnouncementScheduler scheduler = new DayAnnouncementScheduler();
        scheduler.tick(99, Set.of(PLAYER));

        for (int tick = 0; tick < 40; tick++) {
            assertTrue(scheduler.tick(100, Set.of(PLAYER)).isEmpty());
        }

        assertTypingFrame(
                scheduler.tick(100, Set.of(PLAYER)).get(PLAYER),
                "message.rootboot.day_announcement.typing.dash");
    }

    @Test
    void reconnectingDuringAMilestoneReceivesOnlyTheCommonAnnouncement() {
        DayAnnouncementScheduler scheduler = new DayAnnouncementScheduler();
        scheduler.tick(99, Set.of(PLAYER));
        advance(scheduler, 100, 40);
        assertEquals(
                DayAnnouncementFrame.Kind.MILESTONE,
                scheduler.tick(100, Set.of(PLAYER)).get(PLAYER).kind());

        scheduler.playerJoined(RECONNECTING_PLAYER);
        for (int tick = 0; tick < 60; tick++) {
            scheduler.tick(100, Set.of(PLAYER, RECONNECTING_PLAYER));
        }
        var frames = scheduler.tick(100, Set.of(PLAYER, RECONNECTING_PLAYER));

        assertEquals(DayAnnouncementFrame.Kind.COMMON, frames.get(RECONNECTING_PLAYER).kind());
    }

    @Test
    void milestoneSupersedesAJoinAnnouncementAlreadyInProgress() {
        DayAnnouncementScheduler scheduler = new DayAnnouncementScheduler();
        scheduler.playerJoined(PLAYER);
        advance(scheduler, 99, 10);
        scheduler.tick(100, Set.of(PLAYER));
        advance(scheduler, 100, 39);

        assertEquals(
                DayAnnouncementFrame.Kind.MILESTONE,
                scheduler.tick(100, Set.of(PLAYER)).get(PLAYER).kind());
    }

    private static void advance(DayAnnouncementScheduler scheduler, long day, int ticks) {
        for (int tick = 0; tick < ticks; tick++) {
            scheduler.tick(day, Set.of(PLAYER));
        }
    }

    private static void assertTypingFrame(DayAnnouncementFrame frame, String translationKey) {
        assertEquals(translationKey, frame.partialTranslationKey());
        assertEquals(Set.of(DayAnnouncementFrame.Sound.CLICK), frame.sounds());
    }
}

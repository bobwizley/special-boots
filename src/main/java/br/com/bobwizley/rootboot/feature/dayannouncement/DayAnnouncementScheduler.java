package br.com.bobwizley.rootboot.feature.dayannouncement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class DayAnnouncementScheduler {

    public static final int COMMON_DURATION_TICKS = 200;
    public static final int MILESTONE_DURATION_TICKS = 320;
    private static final int JOIN_START_TICK = -20;
    private static final int MILESTONE_SPECIAL_START_TICK = 100;

    private long currentDay = Long.MIN_VALUE;
    private int remainingTicks;
    private int elapsedTicks;
    private DayAnnouncementFrame.Kind kind;
    private final Set<UUID> recipients = new HashSet<>();
    private final Map<UUID, Integer> joinedPlayers = new HashMap<>();

    public void playerJoined(UUID playerId) {
        recipients.remove(playerId);
        joinedPlayers.put(playerId, JOIN_START_TICK);
    }

    public Map<UUID, DayAnnouncementFrame> tick(long overworldDay, Set<UUID> connectedPlayers) {
        if (currentDay == Long.MIN_VALUE) {
            currentDay = overworldDay;
        } else if (overworldDay != currentDay) {
            currentDay = overworldDay;
            kind = isMilestone(overworldDay)
                    ? DayAnnouncementFrame.Kind.MILESTONE
                    : DayAnnouncementFrame.Kind.COMMON;
            remainingTicks = kind == DayAnnouncementFrame.Kind.MILESTONE
                    ? MILESTONE_DURATION_TICKS
                    : COMMON_DURATION_TICKS;
            elapsedTicks = 0;
            recipients.clear();
            recipients.addAll(connectedPlayers);
            joinedPlayers.keySet().removeAll(connectedPlayers);
        }

        Map<UUID, DayAnnouncementFrame> frames = new HashMap<>();
        if (remainingTicks > 0) {
            remainingTicks--;
            DayAnnouncementFrame frame = frame();
            elapsedTicks++;
            if (frame != null) {
                for (UUID recipient : recipients) {
                    if (connectedPlayers.contains(recipient)) {
                        frames.put(recipient, frame);
                    }
                }
            }
        }
        Iterator<Map.Entry<UUID, Integer>> joined = joinedPlayers.entrySet().iterator();
        while (joined.hasNext()) {
            Map.Entry<UUID, Integer> entry = joined.next();
            int joinElapsedTicks = entry.getValue();
            if (connectedPlayers.contains(entry.getKey())) {
                DayAnnouncementFrame frame =
                        commonFrame(DayAnnouncementFrame.Kind.COMMON, joinElapsedTicks);
                if (frame != null) {
                    frames.put(entry.getKey(), frame);
                }
            }
            int nextElapsedTicks = joinElapsedTicks + 1;
            if (nextElapsedTicks >= COMMON_DURATION_TICKS) {
                joined.remove();
            } else {
                entry.setValue(nextElapsedTicks);
            }
        }
        return Map.copyOf(frames);
    }

    private static boolean isMilestone(long day) {
        return day > 0 && day % 100 == 0;
    }

    private DayAnnouncementFrame frame() {
        if (kind == DayAnnouncementFrame.Kind.COMMON) {
            return commonFrame(kind, elapsedTicks);
        }

        if (elapsedTicks < MILESTONE_SPECIAL_START_TICK) {
            return commonTypingFrame(kind, elapsedTicks);
        }
        return milestoneFrame(elapsedTicks - MILESTONE_SPECIAL_START_TICK);
    }

    private DayAnnouncementFrame milestoneFrame(int specialElapsedTicks) {
        String partialTranslationKey = switch (specialElapsedTicks) {
            case 0 -> "message.rootboot.day_announcement.typing.day";
            case 30 -> "message.rootboot.day_announcement.typing.double_dash";
            default -> null;
        };
        boolean displaysFullMessage = specialElapsedTicks == 50
                || specialElapsedTicks == 55
                || specialElapsedTicks == 60
                || specialElapsedTicks == 62
                || specialElapsedTicks == 65
                || specialElapsedTicks == 70
                || specialElapsedTicks == 74
                || specialElapsedTicks >= 75 && specialElapsedTicks <= 140;
        Set<DayAnnouncementFrame.Sound> sounds = switch (specialElapsedTicks) {
            case 0 -> Set.of(DayAnnouncementFrame.Sound.CLICK);
            case 30 -> Set.of(DayAnnouncementFrame.Sound.LODESTONE);
            case 50 -> Set.of(DayAnnouncementFrame.Sound.CLICK, DayAnnouncementFrame.Sound.AMETHYST);
            case 62, 74 -> Set.of(DayAnnouncementFrame.Sound.AMETHYST);
            case 90, 100 -> Set.of(DayAnnouncementFrame.Sound.CHIME);
            default -> Set.of();
        };
        if (partialTranslationKey == null && !displaysFullMessage && sounds.isEmpty()) {
            return null;
        }

        int color = switch (specialElapsedTicks) {
            case 0 -> 0xD6D6BC;
            case 30 -> 0xB0B086;
            case 50 -> 0xFFFCBF;
            case 60 -> 0xFFFF9E;
            case 70 -> 0xFFFF80;
            default -> 0xFFFF00;
        };
        return new DayAnnouncementFrame(
                currentDay,
                kind,
                color,
                specialElapsedTicks >= 50,
                sounds,
                partialTranslationKey);
    }

    private DayAnnouncementFrame commonFrame(
            DayAnnouncementFrame.Kind frameKind, int elapsedTicks) {
        DayAnnouncementFrame typingFrame = commonTypingFrame(frameKind, elapsedTicks);
        if (typingFrame != null) {
            return typingFrame;
        }
        if (elapsedTicks < 100 || elapsedTicks > 140) {
            return null;
        }
        return new DayAnnouncementFrame(
                currentDay,
                frameKind,
                0xFFFFFF,
                false,
                elapsedTicks == 100
                        ? Set.of(DayAnnouncementFrame.Sound.CLICK)
                        : Set.of(),
                null);
    }

    private DayAnnouncementFrame commonTypingFrame(
            DayAnnouncementFrame.Kind frameKind, int elapsedTicks) {
        String partialTranslationKey = switch (elapsedTicks) {
            case 40 -> "message.rootboot.day_announcement.typing.dash";
            case 45 -> "message.rootboot.day_announcement.typing.double_dash";
            case 70 -> "message.rootboot.day_announcement.typing.empty";
            case 75 -> "message.rootboot.day_announcement.typing.d";
            case 80 -> "message.rootboot.day_announcement.typing.da";
            case 85 -> "message.rootboot.day_announcement.typing.day";
            default -> null;
        };
        if (partialTranslationKey == null) {
            return null;
        }
        return new DayAnnouncementFrame(
                currentDay,
                frameKind,
                0xFFFFFF,
                false,
                Set.of(DayAnnouncementFrame.Sound.CLICK),
                partialTranslationKey);
    }
}

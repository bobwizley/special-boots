package br.com.bobwizley.rootboot.feature.dayannouncement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class DayAnnouncementScheduler {

    public static final int COMMON_DURATION_TICKS = 40;
    public static final int MILESTONE_DURATION_TICKS = 220;

    private long currentDay = Long.MIN_VALUE;
    private int remainingTicks;
    private int elapsedTicks;
    private DayAnnouncementFrame.Kind kind;
    private final Set<UUID> recipients = new HashSet<>();
    private final Map<UUID, Integer> joinedPlayers = new HashMap<>();

    public void playerJoined(UUID playerId) {
        recipients.remove(playerId);
        joinedPlayers.put(playerId, COMMON_DURATION_TICKS);
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
            for (UUID recipient : recipients) {
                if (connectedPlayers.contains(recipient)) {
                    frames.put(recipient, frame);
                }
            }
        }
        Iterator<Map.Entry<UUID, Integer>> joined = joinedPlayers.entrySet().iterator();
        while (joined.hasNext()) {
            Map.Entry<UUID, Integer> entry = joined.next();
            if (connectedPlayers.contains(entry.getKey())) {
                frames.put(entry.getKey(), commonFrame());
            }
            int nextRemainingTicks = entry.getValue() - 1;
            if (nextRemainingTicks == 0) {
                joined.remove();
            } else {
                entry.setValue(nextRemainingTicks);
            }
        }
        return Map.copyOf(frames);
    }

    private static boolean isMilestone(long day) {
        return day > 0 && day % 100 == 0;
    }

    private DayAnnouncementFrame frame() {
        if (kind == DayAnnouncementFrame.Kind.COMMON) {
            return commonFrame();
        }

        int color = switch (elapsedTicks / 25) {
            case 0 -> 0xD6D6BC;
            case 1 -> 0xB0B086;
            case 2 -> 0xFFFCBF;
            case 3 -> 0xFFFF9E;
            default -> 0xFFFF00;
        };
        Set<DayAnnouncementFrame.Sound> sounds = switch (elapsedTicks) {
            case 0 -> Set.of(DayAnnouncementFrame.Sound.CLICK);
            case 30 -> Set.of(DayAnnouncementFrame.Sound.LODESTONE);
            case 50, 62, 74 -> Set.of(DayAnnouncementFrame.Sound.AMETHYST);
            case 90, 100 -> Set.of(DayAnnouncementFrame.Sound.CHIME);
            default -> Set.of();
        };
        return new DayAnnouncementFrame(currentDay, kind, color, true, sounds);
    }

    private DayAnnouncementFrame commonFrame() {
        return new DayAnnouncementFrame(
                currentDay, DayAnnouncementFrame.Kind.COMMON, 0xFFFFFF, false, Set.of());
    }
}

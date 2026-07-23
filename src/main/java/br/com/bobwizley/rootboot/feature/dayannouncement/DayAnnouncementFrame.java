package br.com.bobwizley.rootboot.feature.dayannouncement;

import java.util.Set;

public record DayAnnouncementFrame(
        long day, Kind kind, int color, boolean bold, Set<Sound> sounds) {

    public DayAnnouncementFrame {
        sounds = Set.copyOf(sounds);
    }

    public enum Kind {
        COMMON,
        MILESTONE
    }

    public enum Sound {
        CLICK,
        LODESTONE,
        AMETHYST,
        CHIME
    }
}

package br.com.bobwizley.rootboot.feature.dayannouncement;

import br.com.bobwizley.rootboot.feature.Feature;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public final class DayAnnouncementFeature implements Feature {

    public static final String ID = "day_announcement";
    private static final long TICKS_PER_DAY = 24_000L;

    private final DayAnnouncementScheduler scheduler = new DayAnnouncementScheduler();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                scheduler.playerJoined(handler.player.getUUID()));
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        long day = Math.floorDiv(server.overworld().getOverworldClockTime(), TICKS_PER_DAY);
        Map<UUID, ServerPlayer> players = server.getPlayerList().getPlayers().stream()
                .collect(Collectors.toMap(ServerPlayer::getUUID, player -> player));
        Map<UUID, DayAnnouncementFrame> frames = scheduler.tick(day, players.keySet());

        for (Map.Entry<UUID, DayAnnouncementFrame> entry : frames.entrySet()) {
            present(players.get(entry.getKey()), entry.getValue());
        }
    }

    private static void present(ServerPlayer player, DayAnnouncementFrame frame) {
        Style style = Style.EMPTY.withColor(frame.color()).withBold(frame.bold());
        player.sendOverlayMessage(
                Component.translatable("message.rootboot.day_announcement", frame.day()).withStyle(style));
        for (DayAnnouncementFrame.Sound sound : frame.sounds()) {
            player.playSound(soundEvent(sound), 1.0F, 1.0F);
        }
    }

    private static SoundEvent soundEvent(DayAnnouncementFrame.Sound sound) {
        return switch (sound) {
            case CLICK -> SoundEvents.UI_BUTTON_CLICK.value();
            case LODESTONE -> SoundEvents.VAULT_PLACE;
            case AMETHYST -> SoundEvents.AMETHYST_BLOCK_STEP;
            case CHIME -> SoundEvents.AMETHYST_BLOCK_CHIME;
        };
    }
}

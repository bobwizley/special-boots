package br.com.bobwizley.rootboot.client.mixin;

import br.com.bobwizley.rootboot.client.feature.jukeboxmusicoverride.JukeboxMusicOverride;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.JukeboxSong;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The client is told which jukeboxes play only through these two level events; the block entity
 * itself carries no song on this side. Both are broadcast within 64 blocks and neither is
 * repeated, so a disc that started while the client was out of that radius is inaudible in
 * vanilla too and is deliberately not suppressing anything here.
 */
@Mixin(LevelEventHandler.class)
abstract class LevelEventHandlerMixin {

    @Shadow
    @Final
    private ClientLevel level;

    @Inject(method = "playJukeboxSong", at = @At("RETURN"))
    private void rootboot$trackJukeboxStart(
            Holder<JukeboxSong> song, BlockPos pos, CallbackInfo ci) {
        JukeboxMusicOverride.discStarted(pos, song.value(), level);
    }

    @Inject(method = "stopJukeboxSong", at = @At("HEAD"))
    private void rootboot$trackJukeboxStop(BlockPos pos, CallbackInfo ci) {
        JukeboxMusicOverride.discStopped(pos, level);
    }
}

package br.com.bobwizley.rootboot.client.mixin;

import br.com.bobwizley.rootboot.client.feature.jukeboxmusicoverride.JukeboxMusicOverride;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
abstract class MusicManagerMixin {

    /**
     * Cancelling the tick freezes the countdown to the next track instead of silencing it, so
     * nothing starts while a jukebox is audible and the vanilla flow resumes untouched
     * afterwards.
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void rootboot$suppressWhileJukeboxIsAudible(CallbackInfo ci) {
        if (JukeboxMusicOverride.parksAmbientMusicScheduler((MusicManager) (Object) this)) {
            ci.cancel();
        }
    }
}

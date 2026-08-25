package br.com.bobwizley.rootboot.client.mixin;

import br.com.bobwizley.rootboot.client.feature.lowhealthsound.LowHealthSound;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
abstract class LocalPlayerMixin {

    /**
     * Every server-side health change reaches the local player through this one call, and it
     * still holds the previous health, so one hit produces exactly one comparison.
     */
    @Inject(method = "hurtTo", at = @At("HEAD"))
    private void rootboot$beatOnLowHealth(float newHealth, CallbackInfo ci) {
        LowHealthSound.healthChanged(((LocalPlayer) (Object) this).getHealth(), newHealth);
    }
}

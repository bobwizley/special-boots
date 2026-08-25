package br.com.bobwizley.rootboot.client.mixin;

import br.com.bobwizley.rootboot.client.feature.stopmusicondeath.StopMusicOnDeath;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class StopMusicOnDeathMixin {

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void rootboot$stopMusic(byte event, CallbackInfo ci) {
        if (event == EntityEvent.DEATH) {
            StopMusicOnDeath.died((LivingEntity) (Object) this);
        }
    }
}

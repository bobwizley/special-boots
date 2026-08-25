package br.com.bobwizley.rootboot.client.mixin;

import br.com.bobwizley.rootboot.client.feature.localdeathsound.LocalDeathSound;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class LocalDeathSoundMixin {

    /**
     * The damage event packet is the only place the client learns a damage type; the death event
     * that follows it carries none.
     */
    @Inject(method = "handleDamageEvent", at = @At("HEAD"))
    private void rootboot$recordDeathCause(DamageSource source, CallbackInfo ci) {
        LocalDeathSound.damageTaken((LivingEntity) (Object) this, source);
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void rootboot$playDeathSound(byte event, CallbackInfo ci) {
        if (event == EntityEvent.DEATH) {
            LocalDeathSound.died((LivingEntity) (Object) this);
        }
    }
}

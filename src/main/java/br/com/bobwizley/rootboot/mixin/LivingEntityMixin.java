package br.com.bobwizley.rootboot.mixin;

import br.com.bobwizley.rootboot.feature.deathitemprotection.DeathItemProtection;
import br.com.bobwizley.rootboot.feature.deathitemprotection.DeathDroppingPlayer;
import br.com.bobwizley.rootboot.feature.halfhealthbabies.HalfHealthBabies;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void rootboot$updateBabyHealthReduction(CallbackInfo ci) {
        HalfHealthBabies.applyCurrentPolicy((LivingEntity) (Object) this);
    }

    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("RETURN"))
    private void rootboot$protectDeathDrop(CallbackInfoReturnable<ItemEntity> cir) {
        ItemEntity item = cir.getReturnValue();
        if ((Object) this instanceof Player player
                && item != null
                && player.level() instanceof ServerLevel
                && ((DeathDroppingPlayer) player).rootboot$isDroppingDeathItems()) {
            DeathItemProtection.protect(item);
        }
    }
}

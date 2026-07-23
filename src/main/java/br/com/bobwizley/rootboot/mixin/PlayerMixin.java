package br.com.bobwizley.rootboot.mixin;

import br.com.bobwizley.rootboot.feature.deathitemprotection.DeathDroppingPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
abstract class PlayerMixin implements DeathDroppingPlayer {

    @Unique
    private boolean rootboot$droppingDeathItems;

    @Override
    public boolean rootboot$isDroppingDeathItems() {
        return rootboot$droppingDeathItems;
    }

    @Override
    public void rootboot$setDroppingDeathItems(boolean droppingDeathItems) {
        rootboot$droppingDeathItems = droppingDeathItems;
    }

    @Inject(method = "dropEquipment", at = @At("HEAD"))
    private void rootboot$startDroppingDeathItems(ServerLevel level, CallbackInfo ci) {
        rootboot$droppingDeathItems = true;
    }

    @Inject(method = "dropEquipment", at = @At("TAIL"))
    private void rootboot$finishDroppingDeathItems(ServerLevel level, CallbackInfo ci) {
        rootboot$droppingDeathItems = false;
    }
}

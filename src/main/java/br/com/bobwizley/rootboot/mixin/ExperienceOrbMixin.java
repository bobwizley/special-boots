package br.com.bobwizley.rootboot.mixin;

import br.com.bobwizley.rootboot.feature.homingexperienceorb.HomingExperienceOrbMovement;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExperienceOrb.class)
abstract class ExperienceOrbMixin {

    @Unique
    private int rootboot$pursuitTicks;

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ExperienceOrb;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"))
    private void rootboot$applyHomingMovement(
            ExperienceOrb orb, MoverType moverType, Vec3 vanillaMovement) {
        if (!HomingExperienceOrbMovement.isEnabled(orb)) {
            orb.move(moverType, vanillaMovement);
            return;
        }

        Player target = HomingExperienceOrbMovement.nearestTarget(orb);
        Vec3 movement =
                HomingExperienceOrbMovement.movement(orb, target, rootboot$pursuitTicks);
        orb.setPos(orb.position().add(movement));
        orb.setDeltaMovement(movement);
        if (target != null) {
            rootboot$pursuitTicks++;
        }
    }
}

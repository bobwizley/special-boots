package br.com.bobwizley.rootboot.mixin;

import br.com.bobwizley.rootboot.feature.lightfoot.Lightfoot;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Vanilla's farmland is named {@code FarmlandBlock} in 26.2. Wrapping the single
 * {@code turnToDirt} call inside {@code fallOn} suspends only the conversion: the fall itself,
 * its damage and every other caller of {@code turnToDirt} keep working, so farmland never becomes
 * globally immune.
 *
 * <p>{@code @WrapOperation} rather than {@code @Redirect} because a redirect claims the call site
 * exclusively, breaking any other mod that also alters farmland trampling.
 */
@Mixin(FarmlandBlock.class)
abstract class FarmlandBlockMixin {

    @WrapOperation(
            method = "fallOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/FarmlandBlock;turnToDirt"
                            + "(Lnet/minecraft/world/entity/Entity;"
                            + "Lnet/minecraft/world/level/block/state/BlockState;"
                            + "Lnet/minecraft/world/level/Level;"
                            + "Lnet/minecraft/core/BlockPos;)V"))
    private void rootboot$keepFarmlandUnderLightfoot(
            Entity entity,
            BlockState state,
            Level level,
            BlockPos pos,
            Operation<Void> turnToDirt) {
        if (!Lightfoot.preventsTrampling(level, entity)) {
            turnToDirt.call(entity, state, level, pos);
        }
    }
}

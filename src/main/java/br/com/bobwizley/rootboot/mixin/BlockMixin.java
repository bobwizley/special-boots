package br.com.bobwizley.rootboot.mixin;

import br.com.bobwizley.rootboot.feature.cropsexperience.CropsExperience;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@code playerDestroy} is the only vanilla path a player's own harvest takes — environmental
 * block changes and other break paths never reach it — and it already carries the tool copy
 * taken before the break damaged it, which is what the Silk Touch check must read.
 */
@Mixin(Block.class)
abstract class BlockMixin {

    @Inject(method = "playerDestroy", at = @At("TAIL"))
    private void rootboot$awardCropExperience(
            Level level,
            Player player,
            BlockPos pos,
            BlockState state,
            BlockEntity blockEntity,
            ItemStack destroyedWith,
            CallbackInfo ci) {
        CropsExperience.harvest(level, pos, state, destroyedWith);
    }
}

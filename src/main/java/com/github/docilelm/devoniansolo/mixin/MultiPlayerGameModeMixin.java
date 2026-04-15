package com.github.docilelm.devoniansolo.mixin;

import com.github.docilelm.devoniansolo.features.FiftyPingDB;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(
            method = "startDestroyBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/tutorial/Tutorial;onDestroyBlock(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;F)V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void devonianDoogan$onBlockStartBreak(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
        FiftyPingDB.INSTANCE.onBreak(blockPos, blockState, blockState.getBlock());
    }
}

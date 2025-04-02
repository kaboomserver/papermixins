package pw.kaboom.papermixins.mixin.perf.command_block_optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CommandBlock.class)
public abstract class CommandBlockMixin {
    @WrapOperation(method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;" +
            "scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"))
    private void tick$scheduleTick(final ServerLevel instance, final BlockPos blockPos,
                                   final Block block, final int delay,
                                   final Operation<Void> original) {
        // Don't schedule the tick here
    }
}
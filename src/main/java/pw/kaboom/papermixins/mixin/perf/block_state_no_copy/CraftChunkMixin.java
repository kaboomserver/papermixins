package pw.kaboom.papermixins.mixin.perf.block_state_no_copy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftChunk.class)
public abstract class CraftChunkMixin {
    @WrapOperation(
            method = "getTileEntities()[Lorg/bukkit/block/BlockState;",
            at = @At(value = "INVOKE", target = "Lorg/bukkit/craftbukkit/CraftChunk;getTileEntities(Z)[Lorg/bukkit/block/BlockState;"))
    private BlockState[] getTileEntities$overload(final CraftChunk instance,
                                                  final boolean snapshot,
                                                  final Operation<BlockState[]> original) {
        return original.call(instance, false);
    }
}

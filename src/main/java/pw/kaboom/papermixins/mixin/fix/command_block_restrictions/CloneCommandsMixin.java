package pw.kaboom.papermixins.mixin.fix.command_block_restrictions;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.CloneCommands;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CloneCommands.class)
public abstract class CloneCommandsMixin {
    @ModifyExpressionValue(method = "clone",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;saveCustomOnly" +
                    "(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/CompoundTag;"))
    private static CompoundTag clone$saveCustomOnly(final CompoundTag original,
                                                    final @Local(ordinal = 0) BlockEntity blockEntity) {
        if (!(blockEntity instanceof CommandBlockEntity)) {
            return original;
        }

        original.remove("Command");
        return original;
    }
}

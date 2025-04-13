package pw.kaboom.papermixins.mixin.fix.command_block_restrictions;

import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.world.level.block.CommandBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FillCommand.class)
public abstract class FillCommandMixin {
    @ModifyVariable(method = "fillBlocks", at = @At("HEAD"), argsOnly = true)
    private static BlockInput fillBlocks(final BlockInput block) {
        // Only modify command blocks with NBT data
        if (block.tag == null ||
                !(block.getState().getBlock() instanceof CommandBlock)) {
            return block;
        }

        // We remove Command instead of auto here, else people can just power the command blocks with another /fill.
        block.tag.remove("Command");
        return block;
    }
}

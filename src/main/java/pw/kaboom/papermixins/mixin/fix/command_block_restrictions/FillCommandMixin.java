package pw.kaboom.papermixins.mixin.fix.command_block_restrictions;

import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.server.commands.FillCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pw.kaboom.papermixins.util.RestrictionUtil;

@Mixin(FillCommand.class)
public abstract class FillCommandMixin {
    @ModifyVariable(method = "fillBlocks", at = @At("HEAD"), argsOnly = true)
    private static BlockInput fillBlocks(final BlockInput block) {
        RestrictionUtil.applyCopyRestrictions(block.getState().getBlock(), block.tag);
        return block;
    }
}

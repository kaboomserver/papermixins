package pw.kaboom.papermixins.mixin.perf.command_block_optimization;

import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.kaboom.papermixins.mixin.perf.command_block_optimization.accessor.CommandBlockEntityAccessor;

@Mixin(targets = "net.minecraft.world.level.block.entity.CommandBlockEntity$1")
public abstract class CommandBlockEntityBaseCommandBlockImplMixin {
    @Shadow
    @Final
    CommandBlockEntity this$0;

    @Inject(method = "setCommand", at = @At("TAIL"))
    private void onSetCommand(final String command, final CallbackInfo ci) {
        if (StringUtil.isNullOrEmpty(command)
                || (!this$0.isAutomatic() || this$0.getMode() != CommandBlockEntity.Mode.AUTO)) {
            return;
        }
        ((CommandBlockEntityAccessor) this$0).invokeScheduleTick();
    }
}

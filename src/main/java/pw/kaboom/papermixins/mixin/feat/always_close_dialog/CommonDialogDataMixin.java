package pw.kaboom.papermixins.mixin.feat.always_close_dialog;

import net.minecraft.server.dialog.CommonDialogData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommonDialogData.class)
public abstract class CommonDialogDataMixin {
    @Inject(method = "canCloseWithEscape", at = @At("HEAD"), cancellable = true)
    private void onCanCloseWithEscape(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}

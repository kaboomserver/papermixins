package pw.kaboom.papermixins.mixin.feat.limit_dialog;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.dialog.Dialog;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.kaboom.papermixins.util.DialogUtil;

@Mixin(ClickEvent.ShowDialog.class)
public abstract class ShowDialogClickEventMixin {
    @Shadow
    @Final
    @Mutable
    private Holder<Dialog> dialog;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(final Holder<Dialog> dialog, final CallbackInfo ci) {
        this.dialog = Holder.direct(DialogUtil.modifyDialog(dialog.value()));
    }
}

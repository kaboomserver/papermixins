package pw.kaboom.papermixins.mixin.feat.always_close_dialog;

import net.minecraft.core.Holder;
import net.minecraft.server.commands.DialogCommand;
import net.minecraft.server.dialog.Dialog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pw.kaboom.papermixins.util.RestrictionUtil;

@Mixin(DialogCommand.class)
public abstract class DialogCommandMixin {
    @ModifyVariable(method = "showDialog", at = @At("HEAD"), name = "dialog", argsOnly = true)
    private static Holder<Dialog> showDialog(final Holder<Dialog> dialog) {
        if (dialog.value().common().canCloseWithEscape()) {
            return dialog;
        }
        return Holder.direct(RestrictionUtil.applyDialogCloseable(dialog.value()));
    }
}

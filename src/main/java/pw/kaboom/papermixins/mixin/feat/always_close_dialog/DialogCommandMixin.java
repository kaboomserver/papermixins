package pw.kaboom.papermixins.mixin.feat.always_close_dialog;

import net.minecraft.core.Holder;
import net.minecraft.server.commands.DialogCommand;
import net.minecraft.server.dialog.Dialog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pw.kaboom.papermixins.util.DialogUtil;

@Mixin(DialogCommand.class)
public abstract class DialogCommandMixin {
    @ModifyVariable(method = "showDialog", at = @At("HEAD"), name = "dialog", argsOnly = true)
    private static Holder<Dialog> modifyShowDialog(final Holder<Dialog> dialog) {
        return Holder.direct(DialogUtil.modifyDialog(dialog.value()));
    }
}

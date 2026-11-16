package pw.kaboom.papermixins.mixin.feat.always_close_dialog;

import net.minecraft.core.Holder;
import net.minecraft.server.commands.DialogCommand;
import net.minecraft.server.dialog.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DialogCommand.class)
public abstract class DialogCommandMixin {
    @ModifyVariable(method = "showDialog", at = @At("HEAD"), name = "dialog", argsOnly = true)
    private static Holder<Dialog> showDialog(Holder<Dialog> dialog) {
        return Holder.direct(papermixins$editDialog(dialog.value()));
    }

    @Unique
    private static Dialog papermixins$editDialog(Dialog old) {
        CommonDialogData oldCommon = old.common();

        CommonDialogData newCommon = new CommonDialogData(
            oldCommon.title(),
            oldCommon.externalTitle(),
            true,
            oldCommon.pause(),
            oldCommon.afterAction(),
            oldCommon.body(),
            oldCommon.inputs()
        );

        return switch (old) {
            case ConfirmationDialog d -> new ConfirmationDialog(newCommon, d.yesButton(), d.noButton());
            case NoticeDialog d -> new NoticeDialog(newCommon, d.action());
            case DialogListDialog d -> new DialogListDialog(
                newCommon,
                d.dialogs(),
                d.exitAction(),
                d.columns(),
                d.buttonWidth()
            );
            case MultiActionDialog d -> new MultiActionDialog(
                newCommon,
                d.actions(),
                d.exitAction(),
                d.columns()
            );
            case ServerLinksDialog d -> new ServerLinksDialog(
                newCommon,
                d.exitAction(),
                d.columns(),
                d.buttonWidth()
            );
            default -> throw new IllegalArgumentException("dialog type not supported " + old.getClass().getSimpleName());
        };
    }
}

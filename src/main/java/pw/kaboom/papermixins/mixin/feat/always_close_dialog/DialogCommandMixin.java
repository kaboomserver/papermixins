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
    private static Holder<Dialog> showDialog(final Holder<Dialog> dialog) {
        return Holder.direct(papermixins$editDialog(dialog.value()));
    }

    @Unique
    private static Dialog papermixins$editDialog(final Dialog old) {
        final CommonDialogData oldCommon = old.common();

        final CommonDialogData newCommon = new CommonDialogData(
            oldCommon.title(),
            oldCommon.externalTitle(),
            true,
            oldCommon.pause(),
            oldCommon.afterAction(),
            oldCommon.body(),
            oldCommon.inputs()
        );

        return switch (old) {
            case final ConfirmationDialog d -> new ConfirmationDialog(newCommon, d.yesButton(), d.noButton());
            case final NoticeDialog d -> new NoticeDialog(newCommon, d.action());
            case final DialogListDialog d -> new DialogListDialog(
                newCommon,
                d.dialogs(),
                d.exitAction(),
                d.columns(),
                d.buttonWidth()
            );
            case final MultiActionDialog d -> new MultiActionDialog(
                newCommon,
                d.actions(),
                d.exitAction(),
                d.columns()
            );
            case final ServerLinksDialog d -> new ServerLinksDialog(
                newCommon,
                d.exitAction(),
                d.columns(),
                d.buttonWidth()
            );
            default -> throw new IllegalArgumentException("dialog type not supported " + old.getClass().getSimpleName());
        };
    }
}

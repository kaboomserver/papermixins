package pw.kaboom.papermixins.mixin.feat.always_close_dialog;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.dialog.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClickEvent.ShowDialog.class)
public abstract class ShowDialogClickEventMixin {
    @Shadow
    @Final
    @Mutable
    private Holder<Dialog> dialog;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(final Holder<Dialog> dialog, final CallbackInfo ci) {
        if (dialog.value().common().canCloseWithEscape()) {
            return;
        }
        this.dialog = Holder.direct(papermixins$editDialog(dialog.value()));
    }

    // this should probably not be duped across 2 classes, but, egh
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

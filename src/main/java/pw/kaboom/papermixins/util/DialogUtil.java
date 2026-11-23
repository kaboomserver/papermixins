package pw.kaboom.papermixins.util;

import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.*;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.PlainMessage;

import java.util.ArrayList;
import java.util.List;

public final class DialogUtil {
    private static final DialogBody ANTI_PHISH = new PlainMessage(
        Component.empty()
            .append(Component.object(new AtlasSprite(AtlasIds.GUI, Identifier.withDefaultNamespace("icon/unseen_notification"))))
            .append(Component.literal(" This dialog is not created by the server. Do not enter credentials or personal information.")),
        PlainMessage.DEFAULT_WIDTH
    );

    private DialogUtil() {
    }

    public static Dialog modifyDialog(final Dialog old) {
        return injectAntiPhish(applyDialogCloseable(old));
    }

    private static Dialog applyDialogCloseable(final Dialog old) {
        final CommonDialogData oldCommon = old.common();
        if (oldCommon.canCloseWithEscape()) {
            return old;
        }

        final CommonDialogData newCommon = new CommonDialogData(
            oldCommon.title(),
            oldCommon.externalTitle(),
            true,
            oldCommon.pause(),
            oldCommon.afterAction(),
            oldCommon.body(),
            oldCommon.inputs()
        );

        return DialogUtil.mutateCommon(old, newCommon);
    }

    private static Dialog injectAntiPhish(final Dialog old) {
        final CommonDialogData oldCommon = old.common();
        final List<DialogBody> newBody = new ArrayList<>(oldCommon.body().size() + 1);
        newBody.add(ANTI_PHISH);
        newBody.addAll(oldCommon.body());
        final CommonDialogData newCommon = new CommonDialogData(
            oldCommon.title(),
            oldCommon.externalTitle(),
            oldCommon.canCloseWithEscape(),
            oldCommon.pause(),
            oldCommon.afterAction(),
            newBody,
            oldCommon.inputs()
        );
        return DialogUtil.mutateCommon(old, newCommon);
    }

    private static Dialog mutateCommon(final Dialog old, final CommonDialogData newCommon) {
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

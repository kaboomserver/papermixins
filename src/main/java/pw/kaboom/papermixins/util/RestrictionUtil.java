package pw.kaboom.papermixins.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.dialog.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.StructureBlock;

public final class RestrictionUtil {
    private RestrictionUtil() {
    }

    public static void applyCopyRestrictions(final Block block, final CompoundTag compoundTag) {
        if (compoundTag == null) return; // Only modify blocks with NBT data

        if (block instanceof CommandBlock) {
            // We remove Command instead of auto here, else people can just power the command blocks with another /fill.
            compoundTag.remove("Command");
        } else if (block instanceof StructureBlock) {
            // Prevent people from making recursive structures
            compoundTag.remove("name");
            compoundTag.remove("sizeX");
            compoundTag.remove("sizeY");
            compoundTag.remove("sizeZ");
        }
    }

    /**
     * create a closeable {@link Dialog}
     *
     * @param old the {@link Dialog} to make closeable
     * @return a new {@link Dialog} if it's not closeable, otherwise {@code old}
     */
    public static Dialog applyDialogCloseable(final Dialog old) {
        if (old.common().canCloseWithEscape()) {
            return old;
        }

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

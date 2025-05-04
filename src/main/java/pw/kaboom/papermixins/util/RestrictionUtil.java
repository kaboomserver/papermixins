package pw.kaboom.papermixins.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.StructureBlock;

public final class RestrictionUtil {
    private RestrictionUtil() {}

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
}

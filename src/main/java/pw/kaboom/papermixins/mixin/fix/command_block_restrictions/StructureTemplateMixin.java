package pw.kaboom.papermixins.mixin.fix.command_block_restrictions;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.util.RestrictionUtil;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
    @ModifyExpressionValue(method = "fillFromWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;" +
                    "saveWithId(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/CompoundTag;"))
    private CompoundTag fillFromWorld$saveWithId(final CompoundTag original,
                                                 final @Local(ordinal = 0) BlockEntity blockEntity) {
        RestrictionUtil.applyCopyRestrictions(blockEntity.getBlockState().getBlock(), original);
        return original;
    }
}

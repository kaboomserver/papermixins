package pw.kaboom.papermixins.mixin.feat.disable_username_validation;

import com.mojang.serialization.DataResult;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ExtraCodecs.class)
public abstract class ExtraCodecsMixin {
    /**
     * @author amyavi
     * @reason Filter text instead of throwing error to prevent server crashes
     */
    @Overwrite
    private static DataResult<String> lambda$static$58(final String string) {
        return DataResult.success(StringUtil.filterText(string));
    }
}

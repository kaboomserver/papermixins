package pw.kaboom.papermixins.mixin.feat.disable_username_validation;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.serialization.DataResult;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ExtraCodecs.class)
public abstract class ExtraCodecsMixin {
    /**
     * @author amyavi
     * @reason Filter text instead of throwing error to prevent server crashes
     */
    @WrapMethod(method = "lambda$static$63")
    private static DataResult<String> lambda$static$63(final String string, final Operation<DataResult<String>> original) {
        return DataResult.success(StringUtil.filterText(string));
    }
}

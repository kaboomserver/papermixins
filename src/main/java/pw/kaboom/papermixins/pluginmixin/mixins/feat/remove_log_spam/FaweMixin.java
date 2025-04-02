package pw.kaboom.papermixins.pluginmixin.mixins.feat.remove_log_spam;

import com.fastasyncworldedit.core.Fawe;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.pluginmixin.PluginMixin;

@PluginMixin("FastAsyncWorldEdit")
@Mixin(Fawe.class)
public abstract class FaweMixin {
    @WrapOperation(method = "lambda$setupMemoryListener$2",
            at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V"),
            require = 0)
    private static void setupMemoryListener$warnAboutLowMemory(final Logger instance, final String s,
                                                               final Operation<Void> original) {

    }
}

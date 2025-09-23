package pw.kaboom.papermixins.pluginmixin.mixins.fix.unmangle_join_messages;

import com.earth2me.essentials.EssentialsPlayerListener;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.pluginmixin.PluginMixin;

import java.util.function.Consumer;

@PluginMixin("Essentials")
@Mixin(EssentialsPlayerListener.class)
public abstract class EssentialsPlayerListenerMixin {
    @WrapOperation(
            method = "joinFlow",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
    private void joinFlow$accept(final Consumer<?> instance, final Object t, final Operation<Void> original,
                                 @Local(argsOnly = true) Consumer<String> joinMessageConsumer) {
        if (joinMessageConsumer == instance) return;
        original.call(instance, t);
    }
}

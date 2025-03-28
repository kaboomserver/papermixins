package pw.kaboom.papermixins.pluginmixin.mixins;

import com.earth2me.essentials.commands.Commandgc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.kaboom.papermixins.pluginmixin.PluginMixin;

@PluginMixin("Essentials")
@Mixin(Commandgc.class)
public abstract class CommandgcMixin {
    @Inject(method = "run", at = @At("HEAD"))
    private void run(CallbackInfo ci) {
        System.out.println("Hello from Commandgc!");
    }
}
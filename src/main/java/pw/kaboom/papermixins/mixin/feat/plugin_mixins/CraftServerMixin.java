package pw.kaboom.papermixins.mixin.feat.plugin_mixins;

import org.bukkit.craftbukkit.CraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.kaboom.papermixins.pluginmixin.PluginMixinLoader;

@Mixin(CraftServer.class)
public abstract class CraftServerMixin {
    @Inject(method = "loadPlugins", at = @At("TAIL"))
    private void loadPlugins(CallbackInfo ci) {
        PluginMixinLoader.finishedApplying();
    }
}

package pw.kaboom.papermixins.mixin.feat.plugin_mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.bukkit.plugin.java.LibraryLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.pluginmixin.PluginMixinSeparationClassLoader;

@Mixin(LibraryLoader.class)
public abstract class LibraryLoaderMixin {
    @WrapOperation(
            method = "createLoader(Lorg/bukkit/plugin/PluginDescriptionFile;Ljava/util/List;)Ljava/lang/ClassLoader;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Class;getClassLoader()Ljava/lang/ClassLoader;"))
    private ClassLoader createLoader$getClassLoader(final Class<?> instance, final Operation<ClassLoader> original) {
        return new PluginMixinSeparationClassLoader(original.call(instance), null);
    }
}

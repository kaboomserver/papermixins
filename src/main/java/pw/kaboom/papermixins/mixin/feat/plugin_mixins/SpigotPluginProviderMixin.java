package pw.kaboom.papermixins.mixin.feat.plugin_mixins;

import io.papermc.paper.plugin.provider.type.spigot.SpigotPluginProvider;
import org.bukkit.plugin.PluginDescriptionFile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import pw.kaboom.papermixins.pluginmixin.PluginMixinLoader;
import pw.kaboom.papermixins.pluginmixin.PluginMixinSeparationClassLoader;
import pw.kaboom.papermixins.pluginmixin.interop.LoadedPluginMixin;

import java.util.List;

@Mixin(SpigotPluginProvider.class)
public abstract class SpigotPluginProviderMixin {
    @Shadow
    @Final
    private PluginDescriptionFile description;

    @ModifyArg(method = "createInstance()Lorg/bukkit/plugin/java/JavaPlugin;",
            at = @At(value = "INVOKE", target = "Lorg/bukkit/plugin/java/PluginClassLoader;<init>" +
                    "(Ljava/lang/ClassLoader;Lorg/bukkit/plugin/PluginDescriptionFile;Ljava/io/File;Ljava/io/File;" +
                    "Ljava/lang/ClassLoader;Ljava/util/jar/JarFile;" +
                    "Lio/papermc/paper/plugin/provider/entrypoint/DependencyContext;)V"),
            index = 0)
    private ClassLoader createInstance$newPluginClassLoader(final ClassLoader parent) {
        final String pluginName = description.getName();
        final List<LoadedPluginMixin> pluginMixins = PluginMixinLoader.getPluginMixins(pluginName);
        if (pluginMixins == null) return parent;

        PluginMixinLoader.LOGGER.info("Modifying plugin class loader instantiation for {}", pluginName);
        return new PluginMixinSeparationClassLoader(parent, pluginMixins);
    }
}

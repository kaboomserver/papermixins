package pw.kaboom.papermixins.mixin.feat.plugin_mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.papermc.paper.plugin.provider.entrypoint.DependencyContext;
import io.papermc.paper.plugin.provider.type.spigot.SpigotPluginProvider;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.PluginClassLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.pluginmixin.PluginMixinSeparationClassLoader;
import pw.kaboom.papermixins.pluginmixin.PluginMixinLoader;
import pw.kaboom.papermixins.pluginmixin.interop.LoadedPluginMixin;

import java.io.File;
import java.util.List;
import java.util.jar.JarFile;

@Mixin(SpigotPluginProvider.class)
public abstract class SpigotPluginProviderMixin {
    @WrapOperation(
            method = "createInstance()Lorg/bukkit/plugin/java/JavaPlugin;",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/ClassLoader;Lorg/bukkit/plugin/PluginDescriptionFile;" +
                            "Ljava/io/File;Ljava/io/File;Ljava/lang/ClassLoader;Ljava/util/jar/JarFile;" +
                            "Lio/papermc/paper/plugin/provider/entrypoint/DependencyContext;)" +
                            "Lorg/bukkit/plugin/java/PluginClassLoader;"))
    private PluginClassLoader createInstance$newPluginClassLoader(ClassLoader parentClassLoader,
                                                                  PluginDescriptionFile description,
                                                                  File pluginFile,
                                                                  File pluginDataFolder,
                                                                  ClassLoader libraryClassLoader,
                                                                  JarFile jarFile,
                                                                  DependencyContext dependencyContext,
                                                                  Operation<PluginClassLoader> original) {
        final List<LoadedPluginMixin> pluginMixins = PluginMixinLoader.getPluginMixins(description.getName());
        if (pluginMixins == null) {
            return original.call(parentClassLoader,
                    description,
                    pluginFile,
                    pluginDataFolder,
                    libraryClassLoader,
                    jarFile,
                    dependencyContext);
        }

        PluginMixinLoader.LOGGER.info("Modifying plugin class loader instantiation for {}", description.getName());
        return original.call(new PluginMixinSeparationClassLoader(parentClassLoader, pluginMixins),
                description,
                pluginFile,
                pluginDataFolder,
                libraryClassLoader,
                jarFile,
                dependencyContext);
    }
}

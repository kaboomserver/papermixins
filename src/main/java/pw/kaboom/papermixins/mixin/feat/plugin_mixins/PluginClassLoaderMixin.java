package pw.kaboom.papermixins.mixin.feat.plugin_mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.papermc.paper.plugin.provider.entrypoint.DependencyContext;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.PluginClassLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.kaboom.papermixins.pluginmixin.PluginMixinLoader;
import pw.kaboom.papermixins.pluginmixin.PluginMixinSeparationClassLoader;
import pw.kaboom.papermixins.pluginmixin.interop.IPluginMixinBootstrapper;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.JarFile;

@Mixin(PluginClassLoader.class)
public abstract class PluginClassLoaderMixin extends URLClassLoader {
    PluginClassLoaderMixin(final URL[] urls) {
        super(urls);
    }

    @Unique
    private IPluginMixinBootstrapper papermixins$pluginMixinBootstrapper;

    @Inject(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Class;forName(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;"))
    private void init$afterSuperInit(final ClassLoader parent,
                                     final PluginDescriptionFile description,
                                     final File dataFolder,
                                     final File file,
                                     final ClassLoader libraryLoader,
                                     final JarFile jarFile,
                                     final DependencyContext dependencyContext,
                                     final CallbackInfo ci) {
        if (!(parent instanceof final PluginMixinSeparationClassLoader ourClassLoader)) return;

        try {
            final Class<?> bootstrapperClass = Class.forName("pw.kaboom.papermixins.pluginmixin.bootstrap.Bootstrapper", true, this);
            final Method initMethod = bootstrapperClass.getDeclaredMethod("init", URLClassLoader.class, URL.class, List.class);
            this.papermixins$pluginMixinBootstrapper =
                    (IPluginMixinBootstrapper) initMethod.invoke(null,
                            this,
                            file.toURI().toURL(),
                            ourClassLoader.mixins);
        } catch (final Exception e) {
            PluginMixinLoader.LOGGER.error("Failed to invoke PluginMixin bootstrapper, not applying mixins", e);
        }
    }

    @WrapOperation(
            method = "findClass",
            at = @At(value = "INVOKE",
                    target = "Lcom/google/common/io/ByteStreams;toByteArray(Ljava/io/InputStream;)[B"))
    private byte[] findClass$toByteArray(final InputStream in,
                                         final Operation<byte[]> original,
                                         @Local(argsOnly = true) final String binaryName) {
        final byte[] asByteArray = original.call(in);
        if (this.papermixins$pluginMixinBootstrapper == null) return asByteArray;
        return this.papermixins$pluginMixinBootstrapper.transformClassBytes(binaryName, asByteArray);
    }
}

package pw.kaboom.papermixins.pluginmixin;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import pw.kaboom.papermixins.pluginmixin.interop.LoadedPluginMixin;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public final class PluginMixinSeparationClassLoader extends ClassLoader {
    static {
        registerAsParallelCapable();
    }

    private static final String[] DELEGATED_PACKAGES = {
            "org.spongepowered.asm.",
            "com.llamalad7.mixinextras.",
            "pw.kaboom.papermixins.pluginmixin.bootstrap.",
            "META-INF/services/org.spongepowered.asm.service."
    };

    private static final String[] INHERITED_PACKAGES = {
            "pw.kaboom.papermixins.pluginmixin.interop"
    };

    public final List<LoadedPluginMixin> mixins;

    public PluginMixinSeparationClassLoader(final ClassLoader parent, final List<LoadedPluginMixin> mixins) {
        super(parent);
        this.mixins = mixins;
    }

    private static boolean shouldDelegate(final String attemptedLoad) {
        for (final String pkg : DELEGATED_PACKAGES) {
            if (attemptedLoad.startsWith(pkg)) return true;
        }

        return false;
    }

    private static boolean shouldInherit(final String attemptedLoad) {
        for (final String pkg : INHERITED_PACKAGES) {
            if (attemptedLoad.startsWith(pkg)) return true;
        }

        return false;
    }

    private Class<?> reloadFromRoot(final String name) throws ClassNotFoundException {
        final Class<?> parentClass = this.getClass().getClassLoader().loadClass(name);

        final String internalName = parentClass.getTypeName()
                .substring(parentClass.getPackageName().length() + 1);
        final URL parentResource = parentClass.getResource(internalName + ".class");
        if (parentResource == null) throw new ClassNotFoundException(name); // Pray this doesn't happen

        final byte[] classBytes;
        try {
            classBytes = IOUtils.toByteArray(parentResource);
        } catch (final IOException e) {
            throw new ClassNotFoundException(name); // Maybe this could happen if the jar is corrupted?
        }

        return defineClass(parentClass.getName(), classBytes, 0, classBytes.length);
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        final Class<?> alreadyLoaded = this.findLoadedClass(name);
        if (alreadyLoaded != null) return alreadyLoaded;

        if (shouldInherit(name)) return this.getClass().getClassLoader().loadClass(name);

        return shouldDelegate(name) ? reloadFromRoot(name)
                : super.loadClass(name, resolve);
    }

    @Override
    public @Nullable URL getResource(final String name) {
        return shouldDelegate(name) ? this.getClass().getClassLoader().getResource(name)
                : super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(final String name) throws IOException {
        return shouldDelegate(name) ? this.getClass().getClassLoader().getResources(name)
                : super.getResources(name);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Package getPackage(final String name) {
        return shouldDelegate(name) ? null : super.getPackage(name);
    }

    @Override
    protected Package[] getPackages() {
        return Arrays.stream(super.getPackages())
                .filter(pkg -> !shouldDelegate(pkg.getName()))
                .toArray(Package[]::new);
    }
}

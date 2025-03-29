package pw.kaboom.papermixins.pluginmixin;

import org.jetbrains.annotations.Nullable;
import pw.kaboom.papermixins.pluginmixin.interop.LoadedPluginMixin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

public final class PluginMixinSeparationClassLoader extends ClassLoader {
    static {
        registerAsParallelCapable();
    }

    public final List<LoadedPluginMixin> mixins;

    public PluginMixinSeparationClassLoader(final ClassLoader parent, final List<LoadedPluginMixin> mixins) {
        super(parent);
        this.mixins = mixins;
    }

    private static boolean isIllegal(final String attemptedLoad) {
        return attemptedLoad.contains("org/spongepowered/asm") || attemptedLoad.contains("org.spongepowered.asm")
                || attemptedLoad.contains("com/llamalad7/mixinextras") || attemptedLoad.contains("com.llamalad7.mixinextras");
    }

    private static void separateWithClassNotFound(final String attemptedLoad) throws ClassNotFoundException {
        if (isIllegal(attemptedLoad)) throw new ClassNotFoundException();
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        separateWithClassNotFound(name);
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        separateWithClassNotFound(name);
        return this.getParent().loadClass(name);
    }

    @Override
    public @Nullable URL getResource(final String name) {
        return isIllegal(name) ? null : super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(final String name) throws IOException {
        return isIllegal(name) ? EmptyEnumeration.empty() : super.getResources(name);
    }

    @Override
    public Stream<URL> resources(final String name) {
        return isIllegal(name) ? Stream.empty() : super.resources(name);
    }

    @Override
    protected URL findResource(final String name) {
        return isIllegal(name) ? null : super.findResource(name);
    }

    @Override
    protected Enumeration<URL> findResources(final String name) throws IOException {
        return isIllegal(name) ? EmptyEnumeration.empty() : super.findResources(name);
    }

    @Override
    public @Nullable InputStream getResourceAsStream(final String name) {
        return isIllegal(name) ? null : super.getResourceAsStream(name);
    }

    @Override
    protected Package definePackage(final String name,
                                    final String specTitle,
                                    final String specVersion,
                                    final String specVendor,
                                    final String implTitle,
                                    final String implVersion,
                                    final String implVendor,
                                    final URL sealBase) {
        return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Package getPackage(final String name) {
        return isIllegal(name) ? null : super.getPackage(name);
    }

    @Override
    protected Package[] getPackages() {
        return Arrays.stream(super.getPackages())
                .filter(pkg -> !isIllegal(pkg.getName()))
                .toArray(Package[]::new);
    }
}
